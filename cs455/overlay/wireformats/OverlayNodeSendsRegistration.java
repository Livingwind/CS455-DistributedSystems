package cs455.overlay.wireformats;

import java.io.*;

public class OverlayNodeSendsRegistration {
  private int type;
  private String hostname;
  private int port;

  public OverlayNodeSendsRegistration (int type, String hostname, int port) {
    this.type = type;
    this.hostname = hostname;
    this.port = port;
  }

  // MESSAGE PROTOCOL
  /*
  byte: OVERLAY_NODE_SENDS_REGISTRATION
  byte: Length of host field
  byte[^^]: Hostname
  int: port
   */

  // Most of this marshalling code comes from the project slides.
  // I have taken the liberties of using DataOutputStream.writeBytes(String) vs
  //  DataOutputStream.write(byte[]) for simplicity
  public OverlayNodeSendsRegistration (byte[] bytes) {
    try (ByteArrayInputStream instream = new ByteArrayInputStream(bytes);
         DataInputStream din
          = new DataInputStream(new BufferedInputStream(instream))) {

      type = din.readInt();

      int hostLength = din.readInt();
      byte[] hostBytes = new byte[hostLength];
      din.readFully(hostBytes);

      hostname = new String(hostBytes);
      port = din.readInt();

    } catch (IOException e) {
      System.err.println(e);
    }
  }

  public byte[] getBytes () throws IOException {
    byte[] bytes = null;
    try (ByteArrayOutputStream outstream = new ByteArrayOutputStream();
         DataOutputStream dout
         = new DataOutputStream(new BufferedOutputStream(outstream))) {

      dout.writeInt(type);

      dout.writeInt(hostname.length());
      dout.writeBytes(hostname);

      dout.writeInt(port);

      bytes = outstream.toByteArray();

    } catch (IOException e) {
      System.err.println(e);
    }
    return bytes;
  }

  @Override
  public String toString () {
    return String.format("HOSTNAME: %2\n" + "TYPE: %1\n" + "PORT: %3", type, hostname, port);
  }
}
