package cs455.overlay.util;

public class RegistryEntry {
  public String hostname;
  public int receivingPort;
  public int id;

  public RegistryEntry (String hostname, int port, int nodeId) {
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
