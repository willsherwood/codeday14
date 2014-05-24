package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Server class
 */
public class BHServer {
    private ServerSocket ss;
    private boolean running;

    public BHServer() throws IOException {
        this(80);
    }

    public BHServer(int port) throws IOException
    {
        this(port, 5);
    }

    public BHServer(int port, int backlog) throws IOException
    {
        ss = new ServerSocket(port, backlog);
        running = true;
    }

    public static String getServerTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat(
            "EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(calendar.getTime());
    }

    public void start() {
        System.out.println("Listening on port " + ss.getLocalPort());
        while (running) {
            try {
                Socket s = ss.accept();
                new BHServerThread(s).start();
            } catch (IOException e) {
                running = false;
                e.printStackTrace();
                break;
            }
        }
    }

    public static void main(String argv[]) {
        try {
            new BHServer(8080).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
