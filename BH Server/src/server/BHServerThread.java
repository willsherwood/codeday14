package server;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;

/**
 * Thread for each client accepted
 */
public class BHServerThread extends Thread {
    PrintWriter out;
    BufferedReader in;
    HashMap<String, TeamData> data;
    Socket s;

    public BHServerThread(Socket s) throws IOException {
        // here's where you would handle this
        this.s = s;
        BufferedWriter w = new BufferedWriter(new OutputStreamWriter(s
            .getOutputStream()));
        in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        out = new PrintWriter(w);
        data = new HashMap<String, TeamData>();
    }
    @Override
    public void run() {
        String team;
        try {
            String rtype = in.readLine();
            if (rtype.equals("X-APPLICATION")) {
                // special
                try {
                    String command = in.readLine();
                    if (command.equals("Listen")) {
                        team = in.readLine();
                        System.out.println("Listener for team " + team);
                        if (data.get(team) == null)
                            data.put(team, new TeamData());
                        data.get(team).getListeners().add(s);
                        // wait for data
                        try {
                            synchronized (s) {
                                s.wait();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        data.get(team).getListeners().remove(s);
                        final String ateam = team;
                        if (data.get(team).getListeners().isEmpty())
                            new Thread() {
                                @Override
                                public void run() {
                                    try {
                                        sleep(30000);
                                    } catch (Exception e) {
                                        // lol
                                    }
                                    if (data.get(ateam) != null && data.get
                                        (ateam).getListeners().isEmpty())
                                        data.remove(ateam);
                                }
                            }.start();
                    } else if (command.equals("Move")) {
                        team = in.readLine();
                        String move = in.readLine();
                        System.out.println("Moving " + move + " for " + team);
                        TeamData d = data.get(team);
                        if (d != null)
                            for (Socket socket : d.getListeners()) {
                                new PrintWriter(socket.getOutputStream())
                                    .print(move);
                                synchronized (socket) {
                                    socket.notify();
                                }
                            }
                        ;
                    } else {
                        out.println("Invalid command.");
                        System.out.println(command);
                    }
                } catch (IOException e) {
                    // can't read anymore
                }
            } else {
                if (rtype.startsWith("GET ")) {
                    System.out.println("Getting " + rtype);
                    String message = "Hello, World!";
                    try {
                        message += "\nFrom: " + rtype.split("[ \r\n\t]+")[1];
                    } catch (ArrayIndexOutOfBoundsException e) {
                        message = "Invalid request!";
                    }
                    out.print("HTTP/1.1 200 OK\r\n");
                    out.print("Date: " + BHServer.getServerTime() + "\r\n");
                    out.print("Allow: GET\r\n");
                    out.print("Connection: Close\r\n");
                    out.print("Content-Type: text/plain\r\n");
                    out.print("Content-Length: " + message.length() +
                        "\r\n\r\n");
                    out.print(message);
                } else {
                    out.print("HTTP/1.1 405 Method Not Allowed\r\n");
                    out.print("Date: " + BHServer.getServerTime() + "\r\n");
                    out.print("Allows: GET\r\n");
                    out.print("Connection: Close\r\n");
                    out.print("Content-Type: text/plain\r\n");
                    out.print("Content-Length: 0\r\n\r\n");
                }
            }
            out.flush();
            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            // uh oh
            out.print("HTTP/1.1 500 Internal Server Error\r\n\r\n");
        }
    }
}
