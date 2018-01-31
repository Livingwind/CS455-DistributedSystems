package cs455.overlay.routing;

public class RoutingEntry {
  private String hostname;
  private int port;
  private int nodeId;

  public RoutingEntry (String hostname, int port, int nodeId) {
    this.hostname = hostname;
    this.port = port;
    this.nodeId = nodeId;
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
}
