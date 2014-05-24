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
    HashMap<String, HashSet<Socket>> listeners;
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
                        team = in.readLine();
                        if (listeners.get(team) == null)
                            listeners.put(team, new HashSet<Socket>());
                        listeners.get(team).add(s);
                        // wait for data
                    } else {
                        out.println("Invalid command.");
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
