package cs455.overlay.transport;

import java.util.Vector;

// Contains all the sockets for each established connection
public class TCPConnectionsCache {
  Vector<TCPConnection> cache;

  public synchronized void add(TCPConnection conn) {

    cache.add(conn);
  }
}
