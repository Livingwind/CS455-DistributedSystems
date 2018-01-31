package cs455.overlay.wireformats;

import cs455.overlay.routing.RoutingTable;

import java.util.Vector;

public class RegistrySendsNodeManifest {
  private byte type;
  private RoutingTable table;
  private Vector<Integer> nodes;

  public RegistrySendsNodeManifest (RoutingTable table, Vector<Integer> nodes) {
    this.type = Protocol.REGISTRY_SENDS_NODE_MANIFEST;
    this.table = table;
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

  }
}
