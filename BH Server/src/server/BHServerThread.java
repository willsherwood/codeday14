package server;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread for each client accepted
 */
public class BHServerThread extends Thread {
    PrintWriter out;
    BufferedReader in;
    static ConcurrentHashMap<String, TeamData> data = new
        ConcurrentHashMap<String,
        TeamData>();
    Socket s;

    public BHServerThread(Socket s) throws IOException {
        // here's where you would handle this
        this.s = s;
        BufferedWriter w = new BufferedWriter(new OutputStreamWriter(s
            .getOutputStream()));
        in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        out = new PrintWriter(w);
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
                        team = in.readLine().trim();
                        System.out.println("Listener for team " + team);
                        if (data.get(team) == null)
                            data.put(team, new TeamData());
                        Message m = new Message(s);
                        data.get(team).getListeners().add(m);
                        // print listeners
                        System.out.println(data.get(team));
                        System.out.println(data);
                        // wait for data
                        final Message mm = m;
                        final BufferedReader rr = in;
                        new Thread(() -> {
                            try {
                                rr.readLine();
                            } catch (IOException e) {
                                //
                            } finally {
                                synchronized (mm) {
                                    mm.notify();
                                }
                            }
                        }).start();
                        try {
                            synchronized (m) {
                                m.wait();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        out.println(m.str);
                        System.out.println("Received move (notified)");
                        data.get(team).getListeners().remove(m);
                        final String ateam = team;
                        if (data.get(team).getListeners().isEmpty())
                            new Thread() {
                                @Override
                                public void run() {
                                    try {
                                        sleep(10000);
                                    } catch (Exception e) {
                                        // lol
                                    }
                                    if (data.get(ateam) != null && data.get
                                        (ateam).getListeners().isEmpty()) {
                                        System.out.println("Culling " + ateam);
                                        data.remove(ateam);
                                    }
                                }
                            }.start();
                    } else if (command.equals("Move")) {
                        System.out.println(data);
                        team = in.readLine().trim();
                        String move = in.readLine();
                        System.out.println("Moving " + move + " for " + team);
                        TeamData d = data.get(team);
                        System.out.println("Notifying " + d);
                        if (d != null) {
                            for (Message m : d.getListeners()) {
                                Socket socket = m.s;
                                m.str = move;
                                synchronized (m) {
                                    m.notify();
                                }
                            }
                        }
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
                    String message = "Error!";
                    try {
                        String uri = rtype.split("[ \r\n\t]")[1];
                        if (uri.equals("/")) {
                            // index
                            BufferedReader buff = new BufferedReader(new
                                FileReader(new File("index.html")));
                            message = "";
                            String line = "";
                            while ((line = buff.readLine()) != null)
                                message += line + "\n";
                            buff.close();
                        } else {
                            BufferedReader buff = new BufferedReader(new
                                FileReader(new File("room.html")));
                            message = "";
                            String line = "";
                            while ((line = buff.readLine()) != null)
                                message += line + "\n";
                            buff.close();
                            String roomname;
                            if (uri.startsWith("/game?roomname="))
                                roomname = uri.substring("/game?roomname="
                                    .length()+1);
                            else
                                roomname = uri.substring(1);
                            message = message.replace("<!--?ROOMNAME?-->",
                                roomname);
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        message = htmlwrap("Invalid request!");
                    }
                    out.print("HTTP/1.1 200 OK\r\n");
                    out.print("Date: " + BHServer.getServerTime() + "\r\n");
                    out.print("Allow: GET\r\n");
                    out.print("Connection: Close\r\n");
                    out.print("Content-Type: text/html\r\n");
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

    private String htmlwrap(String s) {
        return "<!DOCTYPE html><html><body>" + s + "</body></html>";
    }
}
