package cs455.overlay.routing;

import java.util.Vector;

public class RoutingTable {
  public Vector<RoutingEntry> table = new Vector<>();

  public void add(RoutingEntry entry) {
    table.add(entry);
  }
  public int size() {
    return table.size();
  }

}
