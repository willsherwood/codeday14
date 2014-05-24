package server;

import chessboard.ChessBoard;

import java.net.Socket;
import java.util.HashSet;

/**
 * Created by firefly431 on 5/24/14.
 */
public class TeamData {
    HashSet<Socket> listeners;
    ChessBoard a, b;

    public TeamData() {
        listeners = new HashSet<Socket>();
        a = new ChessBoard(0);
        b = new ChessBoard(1);
    }

    public HashSet<Socket> getListeners() {
        return listeners;
    }
}
