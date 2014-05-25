package server;

import java.net.Socket;

/**
 * Created by firefly431 on 5/24/14.
 */
public class Message {
    public Socket s;
    public String str;
    public Message(Socket s) {this.s = s;}
}
