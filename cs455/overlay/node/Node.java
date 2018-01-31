package cs455.overlay.node;


import cs455.overlay.transport.TCPConnectionsCache;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.util.InteractiveCommandParser;
import cs455.overlay.wireformats.Event;

public abstract class Node {
  protected boolean exit = false;

  protected InteractiveCommandParser command = new InteractiveCommandParser();
  protected TCPConnectionsCache cache;
  protected TCPServerThread server;

  public Node () {
    server = new TCPServerThread();
    cache = new TCPConnectionsCache(server);
  }

  public Node (int port) {
    server = new TCPServerThread(port);
    cache = new TCPConnectionsCache(server);
  }

  protected void startThreads () {
    command.start();
    cache.start();
    server.start();
  }

  protected void stopAllThreads () {
    System.out.println("SENDING INTERRUPTS");

    server.killMe();
    command.interrupt();
    cache.interrupt();

    try {
      server.join();
      command.join();
      cache.join();
    } catch (InterruptedException e) {
      System.err.println(e);
    }
    System.out.println("JOINED ALL THREADS. EXITING");
  }

  protected abstract void onEvent (Event event);
}
