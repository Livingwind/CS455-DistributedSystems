package cs455.overlay.node;


import cs455.overlay.transport.TCPConnection;
import cs455.overlay.util.InteractiveCommandParser;

import java.net.ServerSocket;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;

public class Node {

  protected Vector<Thread> threads;
  protected ArrayBlockingQueue<String> queue_command;
  protected Thread thread_command;
  protected ArrayBlockingQueue<String> queue_conn;
  protected Thread thread_conn;
  protected Thread thread_server;

  protected ServerSocket sock;

  public Node () {
    threads = new Vector<Thread>();
    queue_command = new ArrayBlockingQueue<String>(8);
    thread_command = new Thread(new InteractiveCommandParser(queue_command));
    thread_conn = new Thread(new TCPConnection());
  }

  private void addThreadsToPool() {
    threads.add(thread_command);
    threads.add(thread_conn);
    threads.add(thread_server);
  }

  protected void startThreads() {
    addThreadsToPool();
    for (Thread thread : threads) {
      System.out.println("STARTING " + thread);
      thread.start();
    }
  }
  protected void joinAllThreads() {
    int num_threads = threads.size();
    while (num_threads != 0) {
      for (Thread thread : threads) {
        try {
          thread.join();
        } catch (InterruptedException e) {
          System.err.println(e);
        }
        num_threads--;
      }
    }
  }
}
