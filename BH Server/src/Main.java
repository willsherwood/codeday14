import com.sun.net.httpserver.*;
import java.net.*;

public class Main() {
    public static void main(String argv[]) {
        HttpServer s = HttpServer.create(new InetSocketAddress(8080), 5);
        s.createContext("/", new HttpHandler() {
            public void handle(HttpExchange x) {
                if (x.getRequestMethod() != "GET") {
                    x.getRequestBody().close();
                    x.sendResponseHeaders(400, -1);
                }
            }
        });
    }
}
