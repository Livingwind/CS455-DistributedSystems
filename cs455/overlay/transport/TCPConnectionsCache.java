package cs455.overlay.transport;

import cs455.overlay.util.EventWithConn;
import cs455.overlay.wireformats.Event;

import java.net.Socket;
import java.util.Vector;
import java.util.concurrent.LinkedBlockingQueue;

// Contains all the sockets for each established connection
public class TCPConnectionsCache implements Runnable {
  private TCPServerThread server;
  private Vector<TCPConnection> cache = new Vector<>();
  private Vector<Thread> threads = new Vector<>();
  private LinkedBlockingQueue<EventWithConn> events = new LinkedBlockingQueue<>();

  public TCPConnectionsCache (TCPServerThread server) {
    this.server = server;
  }

  public synchronized void add (TCPConnection conn) {
    cache.add(conn);
  }

  public synchronized boolean contains (String hostname) {
    return cache.contains(hostname);
  }

  public int size () {
    return cache.size();
  }

  public synchronized EventWithConn getEvent () {
    return events.poll();
  }

  private void collectEvents () {
    Event event;
    for (TCPConnection conn: cache) {
      event = conn.receiveMessage();
      if (event != null) {
        events.add(new EventWithConn(conn, event));
      }
    }
  }

  private void closeAllConnections () {
    for (Thread thread: threads) {
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
    Thread thread = new Thread(temp);
    cache.add(temp);
    threads.add(thread);
    thread.start();
  }

  @Override
  public void run() {
    do {
      Socket sock = server.getSocket();
      if (sock != null)
        createTCPConnection(sock);

      collectEvents();
    } while (!Thread.interrupted());
    closeAllConnections();
  }
}