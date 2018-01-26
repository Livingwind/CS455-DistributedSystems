package cs455.overlay.transport;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ArrayBlockingQueue;

// Starts the server to establish connections (ServerSocket)
public class TCPServerThread implements Runnable {
  private ServerSocket sock;

  public TCPServerThread (int port) {
    try {
      sock = new ServerSocket(port);
    } catch (IOException e) {
      System.err.println(e);
    }
  }

  @Override
  public void run() {
    System.out.println("STARTING TCP SERVER");
    try {
      Socket incoming = sock.accept();
      new TCPConnection(incoming);
    }
    catch (SocketException e) {
      System.out.println("CLOSING SERVERSOCKET");
    }
    catch (Exception e) {
      System.err.println(e);
    }
  }

  public synchronized void killMe () {
    try {
      sock.close();
    } catch (IOException e) {
      System.err.println(e);
    }
  }
}
