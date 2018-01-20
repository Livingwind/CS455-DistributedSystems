package cs455.overlay.transport;

import java.io.IOException;
import java.net.ServerSocket;

// Starts the server to establish connections (ServerSocket)
public class TCPServerThread implements Runnable {
  private ServerSocket sock;

  public TCPServerThread (int port) throws IOException{
    sock = new ServerSocket(port);
  }

  @Override
  public void run() {
    System.out.println("STARTING TCPSERVERTHREAD");
    try {
      sock.accept();
    }
    catch (IOException e) {
      System.err.println(e);
    }
    System.out.println("RECEIVED MSG");
  }
}
