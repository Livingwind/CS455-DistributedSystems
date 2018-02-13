package cs455.overlay.node;

import cs455.overlay.routing.RoutingEntry;
import cs455.overlay.routing.RoutingTable;
import cs455.overlay.transport.TCPConnection;
import cs455.overlay.routing.RegistryEntry;
import cs455.overlay.util.StatisticsCollectorAndDisplay;
import cs455.overlay.wireformats.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Registry extends Node {
  private Vector<RegistryEntry> entries = new Vector<>();
  private StatisticsCollectorAndDisplay stats = new StatisticsCollectorAndDisplay();
  private long timer;
  private int numMessages = 0;

  Registry (int port) {
    super(port);
  }

  @Override
  protected void programLoop () {
    do {
      checkBrokenConnections();
      checkForRegistrationRequests();
      checkForEvents();
      acceptCommand();
    } while (!exit);
  }

  private void checkBrokenConnections () {
    Iterator<RegistryEntry> iter = entries.iterator();

    while (iter.hasNext()) {
      RegistryEntry entry = iter.next();
      if (!entry.conn.isAlive()) {
        System.out.println(String.format("ERROR: Removing broken connection from registry:\n\t%s", entry.hostname));
        iter.remove();
      }
    }
  }

  @Override
  protected void parseCommand (String msg) {
    String[] cmd = msg.split(" ");
    System.out.println();
    switch (cmd[0]) {
      case "exit":
        exit = true;
        break;
      case "list-messaging-nodes":
        handleListNodes();
        return;
      case "setup-overlay":
        if (cmd[1] == null) {
          System.err.println("CMD ERROR: setup-overlay requires a routing size.");
          return;
        }
        handleSetup(Integer.parseInt(cmd[1]));
        return;
      case "list-routing-tables":
        handleRoutingTables();
        return;
      case "start":
        if (cmd[1] == null)
          System.err.println("CMD ERROR: start requires a number of messages.");
        else
          handleStart(Integer.parseInt(cmd[1]));
        return;
      case "test":
        System.out.println(stats);
        return;
      default:
        System.out.println("CMD ERROR: Command [" + cmd[0] + "] not recognized.");
    }
  }

  // HANDLE COMMANDS

  private void handleListNodes () {
    if (entries.isEmpty()) {
      System.out.println("Registry is currently empty.");
      return;
    }
    StringBuilder s = new StringBuilder();
    String tableFormat = "\u2503%10s\u2503%20s\u2503%12s\u2503\n";
    int index = 0;


    s.append(String.format(
      tableFormat, "NodeID", "Hostname", "Port Number"
    ));

    Collections.sort(entries);
    for(RegistryEntry entry: entries) {
      s.append(String.format(
        tableFormat, entry.id, entry.hostname, entry.receivingPort
      ));
    }
    System.out.println(s);
  }

  private void handleSetup(int tableSize) {
    if (Math.pow(2, tableSize) > entries.size() ) {
      System.err.println(String.format(
        "ERROR: Table size of %d is to large for %d registered nodes.\n\tEnter a smaller number.",
        tableSize, entries.size()
      ));
      return;
    }

    System.out.println("SIGNAL: Setting up overlay with routing table size " + tableSize);
    Collections.sort(entries);
    int[] allIds = new int[entries.size()];
    for (int i = 0; i < entries.size(); i++)
      allIds[i] = entries.get(i).id;

    Event event;
    for (int index = 0; index < entries.size(); index++) {
      RegistryEntry entry = entries.get(index);
      entry.ready = false;

      RoutingTable table = new RoutingTable();
      for (int i = 1; i <= (int)Math.pow(2, tableSize-1); i*=2) {
        int wrappedIndex = (index + i) % entries.size();
        RegistryEntry temp = entries.get(wrappedIndex);

        table.add(new RoutingEntry(temp.hostname, temp.receivingPort, temp.id));
      }

      event = new RegistrySendsNodeManifest(table, allIds);
      entry.conn.sendMessage(event);
      entry.routes = table;
    }
  }

  private void handleRoutingTables() {
    StringBuilder s = new StringBuilder();
    String tableFormat = "\u2503%8s\u2503%15s\u2503%7s\u2503%7s\u2503\n";
    s.append(String.format("All Nodes:\n  %s\n\n", getAllIds().toString()));

    for (RegistryEntry entry: entries) {
      s.append(String.format(
        tableFormat, "Node " + entry.id,
        "Hostname", "Port", "NodeID"
      ));

      int index = 1;
      for (RoutingEntry route: entry.routes.table) {
        s.append(String.format(
          tableFormat, index, route.getHostname(),
          route.getPort(), route.nodeId()
        ));
        index++;
      }
      s.append("\n");
    }


    System.out.println(s);
  }

  private void handleStart(int size) {
    for (RegistryEntry node: entries) {
      if (!node.ready) {
        System.err.println("ERROR: Node routing tables incomplete.");
        return;
      }
    }

    for (RegistryEntry entry: entries) {
      entry.finished = false;
      entry.conn.sendMessage(new RegistryRequestsTaskInitiate(size));
    }
    numMessages = size;
    timer = System.currentTimeMillis();
  }

  // CHECKING FOR NEW REGISTRATIONS

  private void checkForRegistrationRequests () {
    ConcurrentHashMap<String, TCPConnection> temp = cache.getCache();
    for (String key: temp.keySet()) {
      TCPConnection conn = temp.get(key);
      if (conn.checkMessage() == Protocol.OVERLAY_NODE_SENDS_REGISTRATION) {
        registerNode(conn);
      }
    }
  }

  private void registerNode(TCPConnection conn) {
    OverlayNodeSendsRegistration request = (OverlayNodeSendsRegistration) conn.receiveMessage();

    int id;
    try {
      id = randomUnpickedId();
    } catch (IllegalStateException e) {
      conn.sendMessage(new RegistryReportsRegistrationStatus(-1, "Registry full."));
      return;
    }

    String hostname = request.getHostname();
    int port = request.getPort();

    if (!entries.contains(new RegistryEntry(null, hostname, port, id))) {
      entries.add(new RegistryEntry(conn, hostname, port, id));
      conn.sendMessage(new RegistryReportsRegistrationStatus(id, "Registration successful."));
      System.out.println("ALERT: New registration with id " + id);
    }
    else {
      conn.sendMessage(new RegistryReportsRegistrationStatus(-1, "MessengerNode already in Registry."));
    }
  }

  // CHECKING FOR EVENTS

  @Override
  protected void checkForEvents () {
    for (int i = 0; i < entries.size(); i++) {
      RegistryEntry entry = entries.get(i);
      byte type = entry.conn.checkMessage();
      if (type != 0)
        switch (type) {
          case Protocol.OVERLAY_NODE_SENDS_DEREGISTRATION:
            handleDeregister(entry);
            break;
          case Protocol.NODE_REPORTS_OVERLAY_SETUP_STATUS:
            handleSetupStatus(entry);
            break;
          case Protocol.OVERLAY_NODE_REPORTS_TASK_FINISHED:
            handleTaskFinished(entry);
            break;
          case Protocol.OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY:
            handleTrafficSummary(entry);
            break;
        }
    }
  }

  // HANDLE EVENTS

  private void handleDeregister (RegistryEntry entry) {
    OverlayNodeSendsDeregistration event = (OverlayNodeSendsDeregistration) entry.conn.receiveMessage();
    int i = entries.indexOf(new RegistryEntry(null, event.getHostname(), event.getPort(), event.getId()));

    if (i != -1) {
      RegistryEntry found = entries.remove(i);
      found.conn.sendMessage(new RegistryReportsDeregistrationStatus(
        found.id, "Deregistration successful."
      ));
      System.out.println("ALERT: Deregistered node with id " + found.id);
    }
  }

  private void handleSetupStatus (RegistryEntry entry) {
    NodeReportsOverlaySetupStatus event = (NodeReportsOverlaySetupStatus) entry.conn.receiveMessage();
    if (event.getStatus() == -1) {
      System.err.println("ALERT: Node " + entry.id + "could not connection to it's routing table.");
      return;
    }

    entry.ready = true;

    for (RegistryEntry node: entries) {
      if (!node.ready)
        return;
    }
    System.out.println("ALERT: Registry now ready to initiate tasks.");
  }

  private void handleTaskFinished (RegistryEntry entry) {
    OverlayNodeReportsTaskFinished event = (OverlayNodeReportsTaskFinished) entry.conn.receiveMessage();
    entry.finished = true;

    for (RegistryEntry node: entries) {
      if (!node.finished)
        return;
    }

    System.out.println("ALERT: Nodes have finished communication in " + (System.currentTimeMillis() - timer) + "ms.\n");
    try {
      for (int i = (numMessages/10000)+1; i > 0; i--) {
        System.out.print(String.format("\tGenerating traffic summary in %d seconds...\r", i));
        Thread.sleep(1000);
      }
      requestTrafficSummary();
    } catch (InterruptedException ie) {
      System.err.println(ie);
    }
  }

  private void handleTrafficSummary (RegistryEntry entry) {
    OverlayNodeReportsTrafficSummary event = (OverlayNodeReportsTrafficSummary) entry.conn.receiveMessage();
    entry.waitingForSummary = false;
    stats.add(event);

    boolean fin = true;
    for (RegistryEntry node: entries) {
      if(node.waitingForSummary)
        fin = false;
    }

    if (fin) {
      System.out.println(stats);
      stats.clear();
    }
  }

  // HELPERS

  private Vector<Integer> getAllIds () {
    Vector<Integer> pickedInts = new Vector<>();
    for (RegistryEntry entry: entries) {
      pickedInts.add(entry.id);
    }
    return pickedInts;
  }

  private int randomUnpickedId () throws IllegalStateException{
    if (cache.size() > 128)
      throw new IllegalStateException();

    Vector<Integer> pickedInts = getAllIds();
    int value = rand.nextInt(128);
    while (pickedInts.contains(value)) {
      value++;
      value %= 128;
    }
    return value;
  }

  private void requestTrafficSummary () {
    for (RegistryEntry entry: entries) {
      entry.waitingForSummary = true;
      entry.conn.sendMessage(new RegistryRequestsTrafficSummary());
    }
  }

  // MAIN

  public static void main(String[] args){
    int port = 33000;
    if(args.length == 1)
      port = Integer.parseInt(args[0]);
    else
      System.out.println("PORT NOT SPECIFIED");
    System.out.println("Starting registry on port " + port);
    Registry reg = new Registry(port);
    reg.start();
  }
}