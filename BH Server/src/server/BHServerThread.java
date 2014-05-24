package server;

import java.io.*;
import java.net.Socket;

/**
 * Thread for each client accepted
 */
public class BHServerThread extends Thread {
    PrintWriter out;
    BufferedReader in;
    ObjectInputStream oin;
    ObjectOutputStream oout;

    public BHServerThread(Socket s) throws IOException {
        // here's where you would handle this
        BufferedWriter w = new BufferedWriter(new OutputStreamWriter(s
            .getOutputStream()));
        in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        out = new PrintWriter(w);
        oin = new ObjectInputStream(s.getInputStream());
        oout = new ObjectOutputStream(s.getOutputStream());
    }
    @Override
    public void run() {
        try {
            String rtype = in.readLine();
            if (rtype.equals("X-APPLICATION")) {
                // special
                System.out.print("");
            } else {
                if (rtype.startsWith("GET ")) {
                    String message = "Hello, World!";
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
            in.close();
            out.close();
            oin.close();
            oout.close();
        } catch (IOException e) {
            // uh oh
            out.print("HTTP/1.1 500 Internal Server Error\r\n\r\n");
        }
    }
}
