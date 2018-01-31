package cs455.overlay.transport;

import cs455.overlay.wireformats.Event;

import java.net.Socket;
import java.util.Vector;
import java.util.concurrent.LinkedBlockingQueue;

// Contains all the sockets for each established connection
public class TCPConnectionsCache extends Thread {
  private TCPServerThread server;
  private Vector<TCPConnection> cache = new Vector<>();
  private LinkedBlockingQueue<Event> events = new LinkedBlockingQueue<>();

  public TCPConnectionsCache (TCPServerThread server) {
    this.server = server;
  }

  public synchronized void add (TCPConnection conn) {
    cache.add(conn);
  }

  public int size () {
    return cache.size();
  }

  public synchronized Vector<TCPConnection> getCache () {
    return cache;
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

  private void createTCPConnection (Socket sock) {
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