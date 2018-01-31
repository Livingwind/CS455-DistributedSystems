package cs455.overlay.node;

import cs455.overlay.transport.TCPConnection;
import cs455.overlay.routing.RegistryEntry;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.OverlayNodeSendsRegistration;
import cs455.overlay.wireformats.Protocol;
import cs455.overlay.wireformats.RegistryReportsRegistrationStatus;

import java.util.Random;
import java.util.Vector;

public class Registry extends Node {
  private Random rand = new Random();
  private Vector<RegistryEntry> entries = new Vector<>();

  Registry (int port) {
    super(port);


    System.out.println("STARTING REGISTRY...");
    startThreads();

    String msgCommand;

    do {
        msgCommand = command.getMessage();
        checkForRegistrationRequests();
        checkForEvents();

        if (msgCommand != null && msgCommand.equals("exit")) {
          System.out.println("RECEIVED EXIT");
          break;
        }
    } while (true);

    stopAllThreads();
  }

  // CHECKING FOR NEW REGISTRATIONS

  private void checkForRegistrationRequests () {
    for (TCPConnection conn: cache.getCache()) {
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

    if (!entries.contains(hostname)) {
      entries.add(new RegistryEntry(conn, hostname, port, id));
      conn.sendMessage(new RegistryReportsRegistrationStatus(id, "Registration successful."));
    }
    else {
      conn.sendMessage(new RegistryReportsRegistrationStatus(-1, "MessengerNode already in Registry."));
    }
  }

  // CHECKING FOR EVENTS

  private void checkForEvents () {
    Event event;
    for (RegistryEntry entry: entries) {
      event = entry.conn.receiveMessage();
      if (event != null) {
        byte type = event.getType();

        switch (type) {
          case Protocol.OVERLAY_NODE_SENDS_DEREGISTRATION:
            handleDeregister(entry);
            break;
          case Protocol.NODE_REPORTS_OVERLAY_SETUP_STATUS:
            break;
          case Protocol.OVERLAY_NODE_REPORTS_TASK_FINISHED:
            break;
          case Protocol.OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY:
            break;
        }
      }
    }
  }

  // HANDLE EVENTS

  private void handleDeregister (RegistryEntry entry) {

  }

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


  private void deregisterNode(Event bundle) {
  }

  // MAIN

  public static void main(String[] args){
    int port = 33000;
    if(args.length == 1)
      port = Integer.parseInt(args[0]);
    else
      System.out.println("PORT NOT SPECIFIED");
    System.out.println("STARTING SERVER ON PORT " + port);
    new Registry(port);
  }
}