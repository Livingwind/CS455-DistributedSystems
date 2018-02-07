package cs455.overlay.node;

import cs455.overlay.routing.RoutingEntry;
import cs455.overlay.routing.RoutingTable;
import cs455.overlay.transport.TCPConnection;
import cs455.overlay.wireformats.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class MessagingNode extends Node {
  private String registryHost;
  private int registryPort;

  private TCPConnection connRegistry;
  private String localHostname = "";
  private int localPort;
  private int registryId;

  private RoutingTable routing = new RoutingTable();
  private int[] nodes;

  MessagingNode (String host, int port) {
    super();
    registryHost = host;
    registryPort = port;
  }

  private void programLoop () {
    startThreads();

    if(sendRegistration (registryHost, registryPort)) {
      System.out.println("STARTING NODE");
      while (!exit) {
        checkRegistryStatus();
        checkForEvents();
        acceptCommand();
      }
    }

    connRegistry.interrupt();
    try {
      connRegistry.join();
    } catch (InterruptedException e) {
      System.err.println(e);
    }

    stopAllThreads();
  }

  private void checkRegistryStatus () {
    if (!connRegistry.isAlive()) {
      exit = true;
      System.out.println("LOST CONNECTION TO THE REGISTRY. EXITING");
    }
  }

  @Override
  protected void parseCommand (String msg) {
    switch (msg) {
      case "print-counters-and-diagnostics":
        break;
      case "exit-overlay":
        waitForDeregistrationStatus();
        exit = true;
        break;
      default:
        System.out.println("Command [" + msg + "] not recognized.");
    }
  }

  // REGISTRATION

  private boolean sendRegistration (String host, int port) {
    System.out.println("TRYING TO CONNECT TO: " + host + ":" + port);
    try {
      connRegistry = new TCPConnection(host, port);
    } catch (IOException ioe) {
      System.err.println("COULD NOT CONNECT TO THE REGISTRY.");
      return false;
    }
    connRegistry.start();

    localPort = server.getServerSocket().getLocalPort();

    try {
      localHostname = InetAddress.getLocalHost().getHostAddress();
    } catch (UnknownHostException e) {
      System.err.println(e);
    }

    return waitForRegistrationStatus();
  }

  private boolean waitForRegistrationStatus () {
    boolean success = false;
    connRegistry.sendMessage(new OverlayNodeSendsRegistration(localHostname, localPort));
    do {
      Event event = connRegistry.receiveMessage();
      if (event != null && event.getType() == Protocol.REGISTRY_REPORTS_REGISTRATION_STATUS) {
        RegistryReportsRegistrationStatus status = (RegistryReportsRegistrationStatus) event;
        registryId = status.getStatus();
        System.out.println(status.getInfo());

        if (registryId != -1)
          success = true;
        else
          success = false;
        break;
      }
    } while (connRegistry.isAlive());

    return success;
  }

  // DEREGISTRATION

  private void waitForDeregistrationStatus () {
    connRegistry.sendMessage(new OverlayNodeSendsDeregistration(localHostname, localPort, registryId));

    do {
      Event event = connRegistry.receiveMessage();
      if (event != null) {
        return;
      }
    } while (true);
  }

  // CHECKING FOR EVENTS

  @Override
  protected void checkForEvents () {
    checkForRegistryEvents ();
    checkForMessages ();
  }

  private void checkForRegistryEvents () {
    byte type = connRegistry.checkMessage();
    if (type == 0)
      return;

    switch(type) {
      case Protocol.REGISTRY_SENDS_NODE_MANIFEST:
        handleManifest();
        break;
      case Protocol.REGISTRY_REQUESTS_TASK_INITIATE:
        handleInitiate();
        break;
      case Protocol.REGISTRY_REQUESTS_TRAFFIC_SUMMARY:
        handleSummary();
        break;
    }
  }

  private void checkForMessages () {
    for (RoutingEntry entry: routing.table) {
      byte type = entry.getConn().checkMessage();

      if (type == Protocol.OVERLAY_NODE_SENDS_DATA) {
        handleData();
      }
    }
  }

  // HANDLE EVENTS

  private void handleManifest () {
    RegistrySendsNodeManifest event = (RegistrySendsNodeManifest) connRegistry.receiveMessage();
    routing = event.getTable();
    nodes = event.getNodes();

    try {
      for (RoutingEntry entry : routing.table)
        entry.createTCPConnection();
    } catch (IOException ioe) {
      connRegistry.sendMessage(new NodeReportsOverlaySetupStatus(-1,
      "Couldn't establish connection to all routing nodes."));
      System.err.println("ERROR: Could not communicate to all routing table nodes.");
      return;
    }

    connRegistry.sendMessage(new NodeReportsOverlaySetupStatus(registryId, "Setup successful."));
    System.out.println("ALERT: Successfully connected to all routing table nodes.");
  }

  private void handleInitiate () {
  }

  private void handleSummary () {
  }

  private void handleData () {
  }


  // MAIN

  public static void main (String[] args) {
    MessagingNode node = new MessagingNode(args[0], Integer.parseInt(args[1]));
    node.programLoop();
  }
}
