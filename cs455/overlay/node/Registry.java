package cs455.overlay.node;

import cs455.overlay.transport.TCPSenderThread;
import cs455.overlay.transport.TCPServerThread;

import java.io.IOException;
import java.net.ServerSocket;
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

    try {
      sock = new ServerSocket(port);
      thread_server = new Thread(new TCPServerThread(port, sock));
    }
    catch (IOException e){
      System.err.println(e);
    }

    startThreads();

    String command;
    while (true) {
      try {
        command = queue_command.take();
        if (command.equals("TERMINATE")) {
          System.out.println("TERMINATE RECEIVED");
          break;
        }
      } catch (InterruptedException e) {
        System.err.println("Interrupt Caught");
        break;
      }
    }

    try {
      sock.close();
      joinAllThreads();
    } catch (IOException e){
      System.err.println(e);
    }
  }
}
