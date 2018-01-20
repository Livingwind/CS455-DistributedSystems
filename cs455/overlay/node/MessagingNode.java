package cs455.overlay.node;

import java.io.IOException;
import java.net.Socket;

public class MessagingNode {
  public static void main (String[] args) {
    new MessagingNode(args[0], Integer.parseInt(args[1]));
  }

  private Socket sock;

  MessagingNode (String host, int port) {
    System.out.println("STARTING MESSAGING NODE");
    try {
      sock = new Socket(host, port);
    } catch (IOException e) {
      System.err.println(e);
    }
  }
}
