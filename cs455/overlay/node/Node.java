package cs455.overlay.node;


import cs455.overlay.transport.TCPConnection;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.util.InteractiveCommandParser;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;

public class Node {

  protected Vector<Thread> threads;
  protected InteractiveCommandParser command;
  protected TCPConnection conn;
  protected TCPServerThread server;

  protected ServerSocket sock;

  public Node (int port) {
    command = new InteractiveCommandParser();
    server = new TCPServerThread(port);

    threads = new Vector<>();
    threads.add(new Thread(command));
    threads.add(new Thread(conn));
    threads.add(new Thread(server));
  }

  protected void startThreads() {
    for (Thread thread : threads) {
      thread.start();
    }
  }

  protected void stopAllThreads() {
    System.out.println("SENDING INTERRUPTS");

    command.killMe();
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
}
