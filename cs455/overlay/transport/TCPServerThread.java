package cs455.overlay.transport;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.concurrent.BlockingQueue;

// Starts the server to establish connections (ServerSocket)
public class TCPServerThread implements Runnable {
  private ServerSocket sock;
  private BlockingQueue<String> queue;

  public TCPServerThread (int port, ServerSocket sock) throws IOException{
    this.sock = sock;
    this.queue = queue;
  }

  @Override
  public void run() {
    System.out.println("STARTING TCPSERVERTHREAD");
    try {
      sock.accept();
    }
    catch (SocketException e) {
      System.out.println("CLOSING SERVERSOCKET");
    }
    catch (Exception e) {
      System.err.println(e);
    }
    System.out.println("RECEIVED MSG");
  }
}
