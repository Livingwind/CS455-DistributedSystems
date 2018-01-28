package cs455.overlay.node;

import cs455.overlay.transport.TCPConnection;
import cs455.overlay.util.EventWithConn;

import java.net.Socket;

public class MessagingNode extends Node {
  TCPConnection registrySocket;

  public static void main (String[] args) {
    new MessagingNode(args[0], Integer.parseInt(args[1]));
  }

  private Socket sock;

  MessagingNode (String host, int port) {
    super();
    sendRegistration (host, port);

    System.out.println("STARTING MESSAGING NODE");

    String msgCommand;
    EventWithConn eventReceive;

    startThreads();

    do {
      msgCommand = command.getMessage();
      eventReceive = cache.getEvent();
      if (eventReceive != null)
        onEvent(eventReceive);

      if (msgCommand != null) {
        acceptCommand(msgCommand);
      }
    } while (!exit);

    stopAllThreads();
  }

  private void sendRegistration (String host, int port) {
    registrySocket = new TCPConnection(host, port);
  }

  private void acceptCommand (String msg) {
    switch (msg) {
      case "exit":
        exit = true;
    }
  }

  @Override
  protected void onEvent (EventWithConn event) {

  }
}
