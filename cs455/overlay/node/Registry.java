package cs455.overlay.node;

import cs455.overlay.routing.RoutingEntry;
import cs455.overlay.routing.RoutingTable;
import cs455.overlay.transport.TCPConnection;
import cs455.overlay.routing.RegistryEntry;
import cs455.overlay.wireformats.*;

import java.util.Collections;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

public class Registry extends Node {
  private Random rand = new Random();
  private Vector<RegistryEntry> entries = new Vector<>();

  Registry (int port) {
    super(port);
  }

  private void programLoop () {
    startThreads();

    do {
      checkBrokenConnections();
      checkForRegistrationRequests();
      checkForEvents();
      acceptCommand();
    } while (!exit);

    stopAllThreads();
  }

  private void checkBrokenConnections () {
    Iterator<RegistryEntry> iter = entries.iterator();

    while (iter.hasNext()) {
      RegistryEntry entry = iter.next();
      if (!entry.conn.isAlive()) {
        System.out.println(String.format("!REMOVING BROKEN CONNECTION FROM REGISTRY:\n\t%s", entry.hostname));
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
        handleSetup(Integer.parseInt(cmd[1]));
        return;
      case "list-routing-tables":
        handleRoutingTables();
        return;
      case "start":
        handleStart(Integer.parseInt(cmd[1]));
        return;
      default:
        System.out.println("Command [" + cmd[0] + "] not recognized.");
    }
  }

  // HANDLE COMMANDS

  private void handleListNodes () {
    if (entries.isEmpty()) {
      System.out.println("Registry is currently empty.");
      return;
    }

    int index = 0;
    for(RegistryEntry entry: entries) {
      System.out.println(String.format("REGISTRY ENTRY %d:", ++index));
      System.out.println(entry + "\n");
    }
  }

  private void handleSetup(int tableSize) {
    System.out.println("SETTING UP OVERLAY WITH TABLE SIZE " + tableSize);
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
    }
  }

  private void handleRoutingTables() {
  }

  private void handleStart(int size) {
  }

  // CHECKING FOR NEW REGISTRATIONS

  private void checkForRegistrationRequests () {
    Vector<TCPConnection> cacheCopy = new Vector<TCPConnection>(cache.getCache());

    for (TCPConnection conn: cacheCopy) {
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
    }
    else {
      conn.sendMessage(new RegistryReportsRegistrationStatus(-1, "MessengerNode already in Registry."));
    }
  }

  // CHECKING FOR EVENTS

  @Override
  protected void checkForEvents () {
    Vector<RegistryEntry> copyEntries = new Vector<>(entries);

    for (RegistryEntry entry: copyEntries) {
      byte type = entry.conn.checkMessage();
      if (type == 0)
        return;

      switch (type) {
        case Protocol.OVERLAY_NODE_SENDS_DEREGISTRATION:
          handleDeregister(entry);
          return;
        case Protocol.NODE_REPORTS_OVERLAY_SETUP_STATUS:
          return;
        case Protocol.OVERLAY_NODE_REPORTS_TASK_FINISHED:
          return;
        case Protocol.OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY:
          return;
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

  // MAIN

  public static void main(String[] args){
    int port = 33000;
    if(args.length == 1)
      port = Integer.parseInt(args[0]);
    else
      System.out.println("PORT NOT SPECIFIED");
    System.out.println("Starting registry on port " + port);
    Registry reg = new Registry(port);
    reg.programLoop();
  }
}