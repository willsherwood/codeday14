package server;

import java.io.*;
import java.net.Socket;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
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
    BufferedOutputStream bout;
    static ConcurrentHashMap<String, TeamData> data = new ConcurrentHashMap<>
        ();
    Socket s;
    public static final HashMap<String, String> mimeTypes = new HashMap<>();

    static {
        mimeTypes.put("html", "text/html");
        mimeTypes.put("jar", "application/java-archive");
        mimeTypes.put("class", "application/java-byte-code");
        mimeTypes.put("png", "image/png");
        mimeTypes.put("jpg", "image/jpeg");
        mimeTypes.put("jpeg", "image/jpeg");
        mimeTypes.put("gif", "image/gif");
        mimeTypes.put("bmp", "image/bmp");
        mimeTypes.put("js", "text/javascript");
        mimeTypes.put("css", "text/css");
    }

    public BHServerThread(Socket s) throws IOException {
        // here's where you would handle this
        this.s = s;
        OutputStream o = s.getOutputStream();
        BufferedWriter w = new BufferedWriter(new OutputStreamWriter(o));
        in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        out = new PrintWriter(w);
        bout = new BufferedOutputStream(o);
    }
    @Override
    public void run() {
        String team;
        try {
            String rtype = in.readLine();
            if (rtype == null) return;
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
                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    rr.readLine();
                                } catch (IOException e) {
                                    //
                                } finally {
                                    synchronized (mm) {
                                        mm.notify();
                                    }
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
                    } else if (command.equals("Login")) {
                        team = in.readLine().trim();
                        if (data.get(team) == null)
                            data.put(team, new TeamData());
                        out.println(data.get(team).x);
                        data.get(team).x = (data.get(team).x + 1) % 4;
                        out.flush();
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
                        } else if (uri.substring(1).contains("/")) {
                            out.print("HTTP/1.1 200 OK\r\n");
                            out.print("Date: " + BHServer.getServerTime() + "\r\n");
                            out.print("Allow: GET\r\n");
                            out.print("Connection: Close\r\n");
                            out.print("Content-Type: " +
                                getMimeType(uri.substring(1+uri.lastIndexOf('.'))) +
                                "\r\n");
                            out.print("Last-Modified: " + BHServer
                                .getServerTime() + "\r\n\r\n");
                            out.flush();
                            // bout is a buffered outputstream
                            // read from Client.jar into bout
                            Files.copy(FileSystems.getDefault().getPath
                                (uri.substring(1)), bout);
                            bout.flush();
                            out.close();
                            bout.close();
                            in.close();
                            return;
                        } else {
                            message = htmlwrap("404 Not Found");
                            out.print("HTTP/1.1 404 Not Found\r\n");
                            out.print("Date: " + BHServer.getServerTime() + "\r\n");
                            out.print("Allow: GET\r\n");
                            out.print("Connection: Close\r\n");
                            out.print("Content-Type: text/html\r\n");
                            out.print("Content-Length: " + message.length() +
                                "\r\n\r\n");
                            out.print(message);
                            out.flush();
                            in.close();
                            out.close();
                            bout.close();
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
            bout.close();
        } catch (IOException e) {
            e.printStackTrace();
            // uh oh
            out.print("HTTP/1.1 500 Internal Server Error\r\n\r\n");
        }
    }

    private String getMimeType(String t) {
        String s = mimeTypes.get(t);
        if (s != null)
            return s;
        return "text/plain";
    }

    private String htmlwrap(String s) {
        return "<!DOCTYPE html><html><body>" + s + "</body></html>";
    }
}
