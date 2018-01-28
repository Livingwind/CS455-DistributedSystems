package cs455.overlay.wireformats;

import java.io.*;

public class OverlayNodeSendsRegistration implements Event {
  private byte type;
  private String hostname;
  private int port;

  public OverlayNodeSendsRegistration (String hostname, int port) {
    this.type = Protocol.OVERLAY_NODE_SENDS_REGISTRATION;
    this.hostname = hostname;
    this.port = port;
  }

  // MESSAGE PROTOCOL
  /*
  byte: OVERLAY_NODE_SENDS_REGISTRATION
  byte: Hostname length
  byte[^^]: Hostname
  int: Port
   */

  // Most of this marshalling code comes from the project slides.
  // I have taken the liberties of using DataOutputStream.writeBytes(String) vs
  //  DataOutputStream.write(byte[]) for simplicity
  public OverlayNodeSendsRegistration (byte[] bytes) {
    try (ByteArrayInputStream instream = new ByteArrayInputStream(bytes);
         DataInputStream din
          = new DataInputStream(new BufferedInputStream(instream))) {

      type = din.readByte();

      byte hostLength = din.readByte();
      byte[] hostBytes = new byte[hostLength];
      din.readFully(hostBytes);

      hostname = new String(hostBytes);
      port = din.readInt();

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

  @Override
  public byte getType() {
    return type;
  }

  @Override
  public String toString () {
    return String.format("TYPE: %1\n" + "HOSTNAME: %2\n" + "PORT: %3", type, hostname, port);
  }
}