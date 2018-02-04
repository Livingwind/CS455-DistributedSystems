package cs455.overlay.wireformats;

import java.io.*;

public abstract class Event {
  protected byte type;
  protected DataInputStream din;
  protected DataOutputStream dout;

  public Event () {}

  public Event (byte[] bytes) {
    ByteArrayInputStream instream = new ByteArrayInputStream(bytes);
    din = new DataInputStream(new BufferedInputStream(instream));

    try {
      readBytes();

      instream.close();
      din.close();
    } catch (IOException e) {
      System.err.println(e);
    }
  }

  public byte[] getBytes () {
    byte[] bytes = null;

    ByteArrayOutputStream outstream = new ByteArrayOutputStream();
    dout = new DataOutputStream(new BufferedOutputStream(outstream));

    try {
      writeBytes();

      dout.flush();
      bytes = outstream.toByteArray();

      outstream.close();
      dout.close();
    } catch (IOException e) {
      System.err.println(e);
    }

    return bytes;
  }

  public byte getType () {
    return type;
  }

  protected abstract void readBytes () throws IOException;
  protected abstract void writeBytes () throws IOException;
}
