package cs455.overlay.wireformats;

import java.io.*;

public class OverlayNodeSendsDeregistration implements Event{
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
  byte: Length of hostname
  byte[^^]: Hostname
  int: Port number
  int: Node Id
   */

  public OverlayNodeSendsDeregistration (byte[] bytes) {
    try (ByteArrayInputStream instream = new ByteArrayInputStream(bytes);
         DataInputStream din
         = new DataInputStream(new BufferedInputStream(instream))) {

      type = din.readByte();

      byte hostLen = din.readByte();
      byte[] hostBytes = new byte[hostLen];
      din.readFully(hostBytes);
      hostname = new String(hostBytes);

      port = din.readInt();
      id = din.readInt();
    } catch (IOException e) {
      System.err.println(e);
    }
  }

  @Override
  public byte[] getBytes () {
    byte[] bytes = null;
    try (ByteArrayOutputStream outstream = new ByteArrayOutputStream();
         DataOutputStream dout
         = new DataOutputStream(new BufferedOutputStream(outstream))) {

      dout.writeByte(type);

      dout.writeByte(hostname.length());
      dout.writeBytes(hostname);

      dout.writeInt(port);
      dout.writeInt(id);

      dout.flush();
      bytes = outstream.toByteArray();
    } catch (IOException e) {
      System.err.println(e);
    }

    return bytes;
  }

  @Override
  public byte getType () {
    return type;
  }

  @Override
  public String toString () {
    return String.format("TYPE: %d\n" + "HOSTNAME: %s\n" + "PORT: %d\n" + "NODE_ID: %d",
                          type, hostname, port, id);
  }
}
