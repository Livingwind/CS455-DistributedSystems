package cs455.overlay.node;


import cs455.overlay.transport.TCPConnectionsCache;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.util.EventWithConn;
import cs455.overlay.util.InteractiveCommandParser;

import java.net.ServerSocket;
import java.util.Vector;

public abstract class Node {

  protected boolean exit = false;
  protected Vector<Thread> threads = new Vector<>();
  protected InteractiveCommandParser command = new InteractiveCommandParser();
  protected TCPConnectionsCache cache;
  protected TCPServerThread server;

  public Node () {
    server = new TCPServerThread();
    cache = new TCPConnectionsCache(server);
    addThreads();
  }

  public Node (int port) {
    server = new TCPServerThread(port);
    cache = new TCPConnectionsCache(server);
    addThreads();
  }

  private void addThreads () {
    threads.add(new Thread(command));
    threads.add(new Thread(cache));
    threads.add(new Thread(server));
  }

  protected void startThreads () {
    for (Thread thread : threads) {
      thread.start();
    }
  }

  protected void stopAllThreads () {
    System.out.println("SENDING INTERRUPTS");

    server.killMe();

    int num_threads = threads.size();
    while (num_threads != 0) {
      for (Thread thread : threads) {
        try {
          thread.interrupt();
          thread.join();
        } catch (InterruptedException e) {
          System.err.println(e);
        }
        num_threads--;
      }
    }
  }

  protected abstract void onEvent (EventWithConn event);
}
