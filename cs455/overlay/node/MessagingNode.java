package cs455.overlay.node;

import cs455.overlay.transport.TCPConnection;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.OverlayNodeSendsRegistration;

import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class MessagingNode extends Node {
  private TCPConnection connRegistry;

  MessagingNode (String host, int port) {
    super();
    startThreads();

    sendRegistration (host, port);
    System.out.println("STARTING MESSAGING NODE");

    do {
      acceptCommands();
    } while (!exit);

    connRegistry.interrupt();
    try {
      connRegistry.join();
    } catch (InterruptedException e) {
      System.err.println(e);
    }
    stopAllThreads();
  }

  private void sendRegistration (String host, int port) {
    System.out.println("TRYING TO CONNECT TO: " + host + ":" + port);
    connRegistry = new TCPConnection(host, port);
    connRegistry.start();
    int localPort = server.getServerSocket().getLocalPort();

    String localHostname = "";
    try {
      localHostname = InetAddress.getLocalHost().getHostAddress();
    } catch (UnknownHostException e) {
      System.err.println(e);
    }

    OverlayNodeSendsRegistration event = new OverlayNodeSendsRegistration(localHostname, localPort);
    connRegistry.sendMessage(event);
  }

  private void acceptCommands () {
    String msgCommand = command.getMessage();
    if (msgCommand == null)
      return;

    switch (msgCommand) {
      case "exit-overlay":
        exit = true;
        break;
    }
  }

  @Override
  protected void onEvent (Event event) {

  }

  // MAIN

  public static void main (String[] args) {
    new MessagingNode(args[0], Integer.parseInt(args[1]));
  }
}
