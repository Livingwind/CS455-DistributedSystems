package cs455.overlay.transport;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ConcurrentHashMap;

// Starts the server to establish connections (ServerSocket)
public class TCPServerThread implements Runnable {
  private ConcurrentHashMap<String, Socket> pooledConnections;
  private ServerSocket sock;

  public TCPServerThread (int port) {
    pooledConnections = new ConcurrentHashMap<>();

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
        String hostName = incoming.getInetAddress().getCanonicalHostName();
        pooledConnections.put(hostName, incoming);
      } while (true);
    }
    catch (SocketException e) {
      System.out.println("CLOSING SERVERSOCKET");
    }
    catch (Exception e) {
      System.err.println(e);
    }
  }

  public synchronized Socket getSocketByHostname (String host) {
    return pooledConnections.get(host);
  }

  public synchronized void killMe () {
    try {
      sock.close();
    } catch (IOException e) {
      System.err.println(e);
    }
  }
}
