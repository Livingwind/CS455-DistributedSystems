package cs455.overlay.routing;

import cs455.overlay.transport.TCPConnection;

public class RegistryEntry {
  public TCPConnection conn;
  public String hostname;
  public int receivingPort;
  public int id;

  public RegistryEntry (TCPConnection conn, String hostname, int port, int nodeId) {
    this.conn = conn;
    this.hostname = hostname;
    this.receivingPort = port;
    this.id = nodeId;
  }

  @Override
  public boolean equals (Object obj) {
    if (obj instanceof String) {
      String s = (String)obj;
      return this.hostname.equals(s);
    }
    return false;
  }
}
