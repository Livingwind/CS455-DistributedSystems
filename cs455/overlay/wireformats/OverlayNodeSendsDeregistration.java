package cs455.overlay.wireformats;

import java.io.*;

public class OverlayNodeSendsDeregistration implements Event {
  private byte type;
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
  byte: Hostname length
  byte[^^]: Hostname
  int: Port
  int: Assigned ID
   */
  public OverlayNodeSendsDeregistration (byte[] bytes) {
    try (ByteArrayInputStream instream = new ByteArrayInputStream(bytes);
         DataInputStream din
             = new DataInputStream(new BufferedInputStream(instream))) {

      type = din.readByte();

      byte hostLength = din.readByte();
      byte[] hostBytes = new byte[hostLength];
      din.readFully(hostBytes);

      hostname = new String(hostBytes);
      port = din.readInt();
      id = din.readInt();

    } catch (IOException e) {

    }
  }

  @Override
  public byte[] getBytes() {
    byte[] bytes = null;
    try (ByteArrayOutputStream outstream = new ByteArrayOutputStream();
         DataOutputStream dout
             = new DataOutputStream(new BufferedOutputStream(outstream))) {

      dout.writeByte(type);

      dout.writeByte(hostname.length());
      dout.writeBytes(hostname);

      dout.writeInt(port);
      dout.writeInt(id);

      bytes = outstream.toByteArray();

    } catch (IOException e) {
      System.err.println(e);
    }
    return bytes;
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
  public byte getType() {
    return type;
  }

  @Override
  public String toString () {
    return String.format("TYPE: %1\n" + "HOSTNAME: %2\n" + "PORT: %3\n" + "ID: %4",
        type, hostname, port, id);
  }
}