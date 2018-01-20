package cs455.overlay.node;

import cs455.overlay.transport.TCPSenderThread;
import cs455.overlay.transport.TCPServerThread;

import java.io.IOException;
import java.util.Vector;

public class Registry extends Node {
  public static void main(String[] args){
    int port = 33000;
    if(args.length == 1)
      port = Integer.parseInt(args[0]);
    else
      System.out.println("PORT NOT SPECIFIED");
    System.out.println("STARTING SERVER ON PORT " + port);
    new Registry(port);
  }

  Registry (int port) {
    System.out.println("STARTING REGISTRY...");
    Vector<Thread> threads = new Vector<>();
    try {
      threads.add(new Thread(new TCPServerThread(port)));
    }
    catch (IOException e){
      System.err.println(e);
    }

    for (Thread thread : threads) {
      thread.start();
    }

    try {
      while (threads.size() != 0) {
        for (Thread thread : threads) {
          thread.join();
        }
      }
    }
    catch (InterruptedException e) {
      System.err.println(e);
    }
  }
}
