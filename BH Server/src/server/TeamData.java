package server;

import chessboard.ChessBoard;

import java.io.OutputStream;
import java.net.Socket;
import java.util.HashSet;

/**
 * Created by firefly431 on 5/24/14.
 */
public class TeamData {
    HashSet<Message> listeners;
    public int x;

    public TeamData() {
        x = 0;
        listeners = new HashSet<Message>();
    }

    @Override
    public String toString() {
        return "TeamData with " + listeners.size() + " listeners";
    }

    public HashSet<Message> getListeners() {
        return listeners;
    }
}
