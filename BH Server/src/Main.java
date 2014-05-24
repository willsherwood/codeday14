import com.sun.net.httpserver.*;

import java.io.IOException;
import java.net.*;

public class Main {
    public static void main(String argv[]) throws Exception {
        HttpServer s = HttpServer.create(new InetSocketAddress(8080), 5);
        s.createContext("/", new HttpHandler() {
            public void handle(HttpExchange x) {
                try {
                    if (!x.getRequestMethod().equals("GET")) {
                        x.getRequestBody().close();
                        x.sendResponseHeaders(400, -1);
                    }
                } catch (IOException e) {
                    // oh no
                }
            }
        });
    }
}
