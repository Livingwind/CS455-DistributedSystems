package cs455.overlay.routing;

import cs455.overlay.transport.TCPConnection;

public class RoutingEntry {
  private TCPConnection conn;
  private String hostname;
  private int port;
  private int nodeId;

  public RoutingEntry (String hostname, int port, int nodeId) {
    this.hostname = hostname;
    this.port = port;
    this.nodeId = nodeId;
  }

  public RoutingEntry (TCPConnection conn, String hostname, int port, int nodeId) {
    this(hostname, port, nodeId);
    this.conn = conn;
  }

  public void addConn (TCPConnection conn) {
    this.conn = conn;
  }

  public TCPConnection getConn () {
    return conn;
  }
  public String getHostname () {
    return hostname;
  }
  public int getPort () {
    return port;
  }
  public int nodeId () {
    return nodeId;
  }

  @Override
  public String toString () {
    return String.format("NODE ID: %d\nHOSTNAME: %s\nPORT: %d",
            nodeId, hostname, port);
  }
}
