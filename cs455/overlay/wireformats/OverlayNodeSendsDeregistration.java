package cs455.overlay.wireformats;

import java.io.*;

public class OverlayNodeSendsDeregistration extends Event{
  private String hostname;
  private int port;
  private int id;

  public OverlayNodeSendsDeregistration (String hostname, int port, int id) {
    this.type = Protocol.OVERLAY_NODE_SENDS_DEREGISTRATION;
    this.hostname = hostname;
    this.port = port;
    this.id = id;
  }

  // MESSAGE PROTOCOL
  /*
  byte: OVERLAY_NODE_SENDS_DEREGISTRATION
  byte: Length of hostname
  byte[^^]: Hostname
  int: Port number
  int: Node Id
   */

  public OverlayNodeSendsDeregistration (byte[] bytes) {
    super(bytes);
  }

  @Override
  protected void readBytes () throws IOException {
    type = din.readByte();

    byte hostLen = din.readByte();
    byte[] hostBytes = new byte[hostLen];
    din.readFully(hostBytes);
    hostname = new String(hostBytes);

    port = din.readInt();
    id = din.readInt();
  }

  @Override
  protected void writeBytes () throws IOException {
    dout.writeByte(type);
    dout.writeByte(hostname.length());
    dout.writeBytes(hostname);
    dout.writeInt(port);
    dout.writeInt(id);
  }

  public String getHostname () {
    return hostname;
  }
  public int getPort () {
    return port;
  }
  public int getId () {
    return id;
  }

  @Override
  public String toString () {
    return String.format("TYPE: %d\n" + "HOSTNAME: %s\n" + "PORT: %d\n" + "NODE_ID: %d",
                          type, hostname, port, id);
  }
}
