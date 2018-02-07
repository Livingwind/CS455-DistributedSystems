package cs455.overlay.routing;

import cs455.overlay.transport.TCPConnection;

public class RegistryEntry implements Comparable<RegistryEntry>{
  public TCPConnection conn;
  public String hostname;
  public int receivingPort;
  public int id;
  public RoutingTable routes = new RoutingTable();

  public boolean ready = false;
  public boolean finished = false;

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
      return hostname.equals(s);
    }
    else if (obj instanceof RegistryEntry) {
      RegistryEntry other = (RegistryEntry)obj;
      if (hostname.equals(other.hostname) && receivingPort == other.receivingPort &&
          id == other.id)
        return true;
    }
    return false;
  }

  @Override
  public String toString () {
    return String.format("NodeID: %d\nHOSTNAME: %s\nPORT NUMBER: %d",
      id, hostname, receivingPort);
  }

  @Override
  public int compareTo(RegistryEntry o) {
    return id - o.id;
  }
}
