package cs455.overlay.routing;

import cs455.overlay.transport.TCPConnection;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class RoutingEntry implements Comparable<RoutingEntry> {
  public TCPConnection conn;
  private byte[] hostname;
  private int port;
  private int nodeId;

  public RoutingEntry (byte[] hostname, int port, int nodeId) {
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
  public byte[] getHostname () {
    return hostname;
  }
  public String getHostString () {
    try {
      return InetAddress.getByAddress(hostname).getCanonicalHostName();
    } catch (UnknownHostException uhe) {
      uhe.printStackTrace();
    }
    return "";
  }
  public int getPort () {
    return port;
  }
  public int nodeId () {
    return nodeId;
  }

  @Override
  public String toString () {
    String host = "";
    try {
      host = InetAddress.getByAddress(hostname).getCanonicalHostName();
    } catch (UnknownHostException uhe) {
      uhe.printStackTrace();
    }

    return String.format("NODE ID: %d\nHOSTNAME: %s\nPORT: %d",
            nodeId, host, port);
  }

  @Override
  public int compareTo(RoutingEntry o) {
    return nodeId - o.nodeId;
  }
}
