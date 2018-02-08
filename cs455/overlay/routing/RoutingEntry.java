package cs455.overlay.routing;

import cs455.overlay.transport.TCPConnection;

import java.io.IOException;

public class RoutingEntry implements Comparable<RoutingEntry> {
  public TCPConnection conn;
  private String hostname;
  private int port;
  private int nodeId;

  public RoutingEntry (String hostname, int port, int nodeId) {
    this.hostname = hostname;
    this.port = port;
    this.nodeId = nodeId;
  }

  public void createTCPConnection () throws IOException {
    conn = new TCPConnection(hostname, port);
    conn.start();
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

  @Override
  public int compareTo(RoutingEntry o) {
    return nodeId - o.nodeId;
  }
}
