package cs455.overlay.transport;

import cs455.overlay.wireformats.Event;

import java.net.Socket;
import java.util.Vector;

// Contains all the sockets for each established connection
public class TCPConnectionsCache extends Thread {
  private TCPServerThread server;
  private Vector<TCPConnection> cache = new Vector<>();

  public TCPConnectionsCache (TCPServerThread server) {
    this.server = server;
  }

  public synchronized void add (TCPConnection conn) {
    cache.add(conn);
  }

  public synchronized Vector<TCPConnection> getCache () {
    return cache;
  }

  public int size () {
    return cache.size();
  }

  private void closeAllConnections () {
    for (Thread thread: cache) {
      thread.interrupt();
      try {
        thread.join();
      } catch (InterruptedException e) {
        System.err.println(e);
      }
    }
  }

  private synchronized void createTCPConnection (Socket sock) {
    TCPConnection temp = new TCPConnection(sock);
    temp.start();
    cache.add(temp);
  }

  @Override
  public void run() {
    do {
      Socket sock = server.getSocket();
      if (sock != null)
        createTCPConnection(sock);
    } while (!Thread.interrupted());
    closeAllConnections();
  }
}