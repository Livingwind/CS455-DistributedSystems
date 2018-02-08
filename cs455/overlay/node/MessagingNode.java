package cs455.overlay.node;

import cs455.overlay.routing.RoutingEntry;
import cs455.overlay.routing.RoutingTable;
import cs455.overlay.transport.TCPConnection;
import cs455.overlay.wireformats.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;

public class MessagingNode extends Node {
  private String registryHost;
  private int registryPort;

  private TCPConnection connRegistry;
  private String localHostname = "";
  private int localPort;
  private int registryId;

  private RoutingTable routing = new RoutingTable();
  private int[] nodes;

  private int remaining = 0;
  private int sendTracker = 0;
  private long sendSummation = 0;
  private int receiveTracker = 0;
  private long receiveSummation = 0;
  private int relayedTracker = 0;

  MessagingNode (String host, int port) {
    super();
    registryHost = host;
    registryPort = port;
  }

  private void programLoop () {
    startThreads();

    if(sendRegistration (registryHost, registryPort)) {
      System.out.println("ALERT: Successfully connected to the registry and given id " +
          registryId + ". Starting...");
      while (!exit) {
        checkRegistryStatus();
        checkForEvents();
        createMessages();
        acceptCommand();
      }
    }

    connRegistry.interrupt();
    try {
      connRegistry.join();
    } catch (InterruptedException e) {
      System.err.println(e);
    }

    killTableConnections();
    stopAllThreads();
  }

  private void killTableConnections () {
    for (RoutingEntry entry: routing.table) {
      entry.conn.interrupt();
      try {
        entry.conn.join();
      } catch (InterruptedException e){
        System.err.println(e);
      }
    }
  }

  private void checkRegistryStatus () {
    if (!connRegistry.isAlive()) {
      exit = true;
      System.out.println("ERROR: Lost connection to the registry. Terminating.");
    }
  }

  private void printCounters () {
    System.out.println(String.format(
      "\nMessages:\n\tSent - %d\n\tReceived - %d\n\tRelayed - %d\nTotal Received: %d\n",
      sendTracker, receiveTracker, relayedTracker, receiveSummation
    ));
  }

  @Override
  protected void parseCommand (String msg) {
    switch (msg) {
      case "print-counters-and-diagnostics":
        printCounters();
        break;
      case "exit-overlay":
        waitForDeregistrationStatus();
        exit = true;
        break;
      default:
        System.out.println("CMD ERROR: Command [" + msg + "] not recognized.");
    }
  }

  private void forwardMessage (OverlayNodeSendsData data) {
    int dest = data.getDest();

    int target = routing.size()-1;
    for (int i = 0; i < routing.size() - 1; i++) {
      if (dest >= routing.table.get(i).nodeId() &&
          dest < routing.table.get(i + 1).nodeId()) {
        target = i;
      }
    }


    int[] newHops = new int[data.getHops().length+1];
    newHops[newHops.length-1] = registryId;
    Event event = new OverlayNodeSendsData(data.getDest(), data.getSrc(), data.getPayload(), newHops);

    routing.table.get(target).conn.sendMessage(event);
  }

  private void createMessages () {
    if (remaining <= 0)
      return;

    int dest;
    do {
      dest = rand.nextInt(nodes.length);
    } while (nodes[dest] == registryId);
    int i = rand.nextInt();

    OverlayNodeSendsData event = new OverlayNodeSendsData(nodes[dest], registryId, i,  new int[0]);
    forwardMessage(event);
    sendTracker++;
    sendSummation += i;
    remaining--;
    if (remaining <= 0)
      handleTaskFinished();
  }

  // REGISTRATION

  private boolean sendRegistration (String host, int port) {
    try {
      connRegistry = new TCPConnection(host, port);
    } catch (IOException ioe) {
      System.err.println("ERROR: Could not establish a connection to the registry.");
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
    checkForRegistryEvents();
    checkForMessages();
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
    for (String key: cache.getCache().keySet()) {
      TCPConnection conn = cache.getCache().get(key);
      byte type = conn.checkMessage();
      if (type == Protocol.OVERLAY_NODE_SENDS_DATA) {
        handleData(conn);
      }
    }
  }

  // HANDLE EVENTS

  private void handleManifest () {
    RegistrySendsNodeManifest event = (RegistrySendsNodeManifest) connRegistry.receiveMessage();
    routing = event.getTable();
    nodes = event.getNodes();
    Collections.sort(routing.table);

    try {
      for (RoutingEntry entry : routing.table)
        entry.createTCPConnection();
    } catch (IOException ioe) {
      connRegistry.sendMessage(new NodeReportsOverlaySetupStatus(-1,
      "Couldn't establish connection to all routing nodes."));
      System.err.println("ERROR: Could not communicate to all routing table nodes.");
      return;
    }
    zeroTotals();

    connRegistry.sendMessage(new NodeReportsOverlaySetupStatus(registryId, "Setup successful."));
    System.out.println("ALERT: Successfully connected to all routing table nodes.");
  }

  private void handleInitiate () {
    RegistryRequestsTaskInitiate event = (RegistryRequestsTaskInitiate) connRegistry.receiveMessage();
    System.out.println(String.format("ALERT: Registry requested %d packets be sent.\n" +
      "\tStarting relay...", event.getNumPackets()));
    remaining = event.getNumPackets();
  }

  private void handleData (TCPConnection conn) {
    OverlayNodeSendsData event = (OverlayNodeSendsData) conn.receiveMessage();

    if (event.getDest() == registryId) {
      receiveTracker++;
      receiveSummation += event.getPayload();
      return;
    }

    relayedTracker++;
    forwardMessage(event);
  }

  private void handleSummary () {
    RegistryRequestsTrafficSummary event = (RegistryRequestsTrafficSummary) connRegistry.receiveMessage();
    System.out.println("ALERT: Registry requested traffic summary. Responding...");
    connRegistry.sendMessage(
      new OverlayNodeReportsTrafficSummary(registryId, sendTracker, sendSummation,
        relayedTracker, receiveTracker, receiveSummation)
    );
    zeroTotals();
  }

  private void handleTaskFinished () {
    System.out.println("ALERT: Finished creating messages. Reporting to registry.");
    connRegistry.sendMessage(new OverlayNodeReportsTaskFinished(localHostname, localPort, registryId));
  }

  private void zeroTotals () {
    remaining = 0;
    sendTracker = 0;
    sendSummation = 0;
    receiveTracker = 0;
    receiveSummation = 0;
    relayedTracker = 0;
  }

  // MAIN

  public static void main (String[] args) {
    MessagingNode node = new MessagingNode(args[0], Integer.parseInt(args[1]));
    node.programLoop();
  }
}
