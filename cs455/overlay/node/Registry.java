package cs455.overlay.node;

import cs455.overlay.util.EventWithConn;
import cs455.overlay.util.RegistryEntry;
import cs455.overlay.wireformats.OverlayNodeSendsRegistration;
import cs455.overlay.wireformats.Protocol;
import cs455.overlay.wireformats.RegistryReportsRegistrationStatus;

import java.util.Random;
import java.util.Vector;

public class Registry extends Node {
  private Random rand = new Random();
  private Vector<RegistryEntry> entries;

  Registry (int port) {
    super(port);

    entries = new Vector<>();

    System.out.println("STARTING REGISTRY...");
    startThreads();

    String msgCommand;
    EventWithConn eventReceive;

    do {
        msgCommand = command.getMessage();
        eventReceive = cache.getEvent();
        if (eventReceive != null)
          onEvent(eventReceive);

        if (msgCommand != null && msgCommand.equals("exit")) {
          System.out.println("RECEIVED EXIT");
          break;
        }
    } while (!exit);

    stopAllThreads();
  }

  public static void main(String[] args){
    int port = 33000;
    if(args.length == 1)
      port = Integer.parseInt(args[0]);
    else
      System.out.println("PORT NOT SPECIFIED");
    System.out.println("STARTING SERVER ON PORT " + port);
    new Registry(port);
  }

  @Override
  protected void onEvent (EventWithConn bundle) {
    byte type = bundle.event.getType();

    switch (type) {
      case Protocol.OVERLAY_NODE_SENDS_REGISTRATION:
        registerNode(bundle);
      case Protocol.OVERLAY_NODE_SENDS_DEREGISTRATION:
        deregisterNode(bundle);
    }
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

  private void registerNode(EventWithConn bundle) {
    int id;
    try {
      id = randomUnpickedId();
    } catch (IllegalStateException e) {
      bundle.conn.sendMessage(new RegistryReportsRegistrationStatus(-1, "Registry full."));
      return;
    }

    OverlayNodeSendsRegistration request = (OverlayNodeSendsRegistration)bundle.event;
    String hostname = request.getHostname();
    int port = request.getPort();

    if (!entries.contains(hostname)) {
      entries.add(new RegistryEntry(hostname, port, id));
      bundle.conn.sendMessage(new RegistryReportsRegistrationStatus(id, "Registration successful."));
    }

    bundle.conn.sendMessage(new RegistryReportsRegistrationStatus(-1, "MessengerNode already in Registry."));
  }

  private void deregisterNode(EventWithConn bundle) {
  }
}