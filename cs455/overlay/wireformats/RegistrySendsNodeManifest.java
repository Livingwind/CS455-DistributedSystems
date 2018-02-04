package cs455.overlay.wireformats;

import cs455.overlay.routing.RoutingEntry;
import cs455.overlay.routing.RoutingTable;

import java.io.IOException;
import java.util.Vector;

public class RegistrySendsNodeManifest extends Event {
  private RoutingTable entries;
  private int[] nodes;

  public RegistrySendsNodeManifest (RoutingTable table, int[] nodes) {
    this.type = Protocol.REGISTRY_SENDS_NODE_MANIFEST;
    this.entries = table;
    this.nodes = nodes;
  }

  // MESSAGE PROTOCOL
  /*
  byte: REGISTRY_SENDS_NODE_MANIFEST
  byte: Routing table size
  int: NodeID of 1 hop
  byte: Length of hostname
  byte[^^]: Hostname
  int: Port number of node 1
  ...
  int: NodeID of 4 hops
  byte: Length of hostname
  byte[^^]: Hostname
  int: Port number of node 4

  byte: Number of nodes
  int[^^]: List of all node IDs
   */

  public RegistrySendsNodeManifest (byte[] bytes) {
    super(bytes);
  }

  @Override
  protected void readBytes() throws IOException {
    entries = new RoutingTable();
    type = din.readByte();

    byte num = din.readByte();
    for (int i = 0; i < num; i++) {
      int id = din.readInt();

      byte hostLen = din.readByte();
      byte[] hostBytes = new byte[hostLen];
      din.readFully(hostBytes);
      String hostname = new String(hostBytes);

      int port = din.readInt();
      entries.add(new RoutingEntry(hostname, port, id));
    }

    byte idsSize = din.readByte();
    nodes = new int[idsSize];
    for (int i = 0; i < idsSize; i++) {
      nodes[i] = din.readInt();
    }

  }

  @Override
  protected void writeBytes() throws IOException {
    dout.writeByte(type);
    dout.write(entries.size());
    for (RoutingEntry entry: entries.table) {
      dout.writeInt(entry.nodeId());
      dout.writeByte(entry.getHostname().length());
      dout.writeBytes(entry.getHostname());
      dout.writeInt(entry.getPort());
    }
    dout.writeByte(nodes.length);
    for (int i = 0; i < nodes.length; i++) {
      dout.writeInt(nodes[i]);
    }
  }

  public RoutingTable getTable () {
    return entries;
  }
  public int[] getNodes () {
    return nodes;
  }

  @Override
  public String toString () {
    StringBuilder s = new StringBuilder("Type: " + type + "\nTABLE SIZE: " + entries.size() + "\n");
    for (RoutingEntry entry: entries.table) {
      s.append(entry.toString() + "\n");
    }
    s.append("\nNODES: [");
    for (int x: nodes) {
      s.append(x + ",");
    }
    s.append("]");

    return s.toString();
  }
}
