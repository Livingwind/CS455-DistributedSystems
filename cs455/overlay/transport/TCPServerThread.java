package cs455.overlay.transport;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.LinkedBlockingQueue;

// Starts the server to establish connections (ServerSocket)
public class TCPServerThread implements Runnable {
  private LinkedBlockingQueue<Socket> incomingSockets =
      new LinkedBlockingQueue<>();
  private ServerSocket sock;

  public TCPServerThread () {
    try {
      sock = new ServerSocket(0);
    } catch (IOException e) {
      System.err.println(e);
    }
  }

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
      do {
        Socket incoming = sock.accept();
        incomingSockets.add(incoming);
      } while (true);
    }
    catch (SocketException e) {
      System.out.println("CLOSING SERVERSOCKET");
    }
    catch (Exception e) {
      System.err.println(e);
    }
  }

  public synchronized Socket getSocket () {
    return incomingSockets.poll();
  }

  public synchronized void killMe () {
    try {
      sock.close();
    } catch (IOException e) {
      System.err.println(e);
    }
  }
}
