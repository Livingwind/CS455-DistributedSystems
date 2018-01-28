package cs455.overlay.util;

import cs455.overlay.transport.TCPConnection;
import cs455.overlay.wireformats.Event;

import java.net.Socket;

public class EventWithConn {
  public TCPConnection conn;
  public Event event;

  public EventWithConn(TCPConnection conn, Event event) {
    this.conn = conn;
    this.event = event;
  }
}
