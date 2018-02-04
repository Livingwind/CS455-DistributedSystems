package cs455.overlay.transport;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.LinkedBlockingQueue;

// Starts the server to establish connections (ServerSocket)
public class TCPServerThread extends Thread {
  protected LinkedBlockingQueue<Socket> incomingSockets =
      new LinkedBlockingQueue<>();
  protected ServerSocket sock;
  protected int port;

  public TCPServerThread () {
    this.port = 0;
  }

  public TCPServerThread (int port) {
    this.port = port;
  }

  @Override
  public void run() {
    try {
      sock = new ServerSocket(port);
      startServer();
    }
    catch (Exception e) {
      System.out.println("CLOSING SERVERSOCKET");
    }
  }

  private void startServer () {
    try {
      do {
        Socket incoming = sock.accept();
        incomingSockets.add(incoming);
      } while (true);
    } catch (SocketException e) {
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  public ServerSocket getServerSocket () {
    return sock;
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
