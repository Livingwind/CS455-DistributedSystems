package cs455.overlay.wireformats;

import java.io.*;

public class OverlayNodeSendsRegistration extends Event {
  private byte[] hostname;
  private int port;

  public OverlayNodeSendsRegistration (byte[] hostname, int port) {
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
    super(bytes);
    try (ByteArrayInputStream instream = new ByteArrayInputStream(bytes);
         DataInputStream din
          = new DataInputStream(new BufferedInputStream(instream))) {


    } catch (IOException e) {
      System.err.println(e);
    }
  }

  @Override
  protected void readBytes () throws IOException {
    type = din.readByte();

    byte hostLength = din.readByte();
    byte[] hostBytes = new byte[hostLength];
    din.readFully(hostBytes);

    hostname = hostBytes;
    port = din.readInt();
  }

  @Override
  protected void writeBytes () throws IOException {
    dout.writeByte(type);
    dout.writeByte(hostname.length);
    dout.write(hostname);
    dout.writeInt(port);
  }

  public byte[] getHostname () {
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
    return String.format("TYPE: %d\n" + "HOSTNAME: %s\n" + "PORT: %d", type, hostname, port);
  }
}