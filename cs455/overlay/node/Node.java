package cs455.overlay.node;

import cs455.overlay.transport.TCPConnectionsCache;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.util.InteractiveCommandParser;

import java.util.Random;

public abstract class Node {
  protected Random rand = new Random();
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

  protected abstract void programLoop ();

  public void start () {
    startThreads();
    programLoop();
    stopAllThreads();
  }

  protected void startThreads () {
    command.start();
    cache.start();
    server.start();
  }

  protected void stopAllThreads () {
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
    System.out.println("Termination: All threads cleanly interrupted and joined.");
  }

  protected abstract void parseCommand(String msg);

  protected void acceptCommand () {
    String msgCommand = command.getMessage();
    if (msgCommand == null)
      return;

    parseCommand(msgCommand);
  }

  protected abstract void checkForEvents ();
}
