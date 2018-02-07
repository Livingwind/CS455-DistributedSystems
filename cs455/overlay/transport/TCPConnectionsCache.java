package cs455.overlay.transport;

import cs455.overlay.wireformats.Event;

import java.net.Socket;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

// Contains all the sockets for each established connection
public class TCPConnectionsCache extends Thread {
  private TCPServerThread server;
  private ConcurrentHashMap<String, TCPConnection> cache = new ConcurrentHashMap<>();

  public TCPConnectionsCache (TCPServerThread server) {
    this.server = server;
  }

  public synchronized void add (TCPConnection conn) {
    cache.put(conn.getHost(), conn);
  }

  public ConcurrentHashMap<String, TCPConnection> getCache () {
    return cache;
  }

  public int size () {
    return cache.size();
  }

  private void closeAllConnections () {
    for (Thread thread: cache.values()) {
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
    add(temp);
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