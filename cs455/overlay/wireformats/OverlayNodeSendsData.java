package cs455.overlay.wireformats;

import java.io.IOException;
import java.util.Arrays;

public class OverlayNodeSendsData extends Event {
  private int dest;
  private int src;
  private int payload;
  private int[] hops;

  public OverlayNodeSendsData (int dest, int src, int payload, int[] hops) {
    this.type = Protocol.OVERLAY_NODE_SENDS_DATA;
    this.dest = dest;
    this.src = src;
    this.payload = payload;
    this.hops = hops;
  }

  public OverlayNodeSendsData (byte[] bytes) {
    super(bytes);
  }

  @Override
  protected void readBytes () throws IOException {
    type = din.readByte();
    dest = din.readInt();
    src = din.readInt();
    payload = din.readInt();

    int numHops = din.readInt();
    hops = new int[numHops];
    for (int i = 0; i < numHops; i++) {
      hops[i] = din.readInt();
    }
  }

  @Override
  protected void writeBytes () throws IOException {
    dout.writeByte(type);
    dout.writeInt(dest);
    dout.writeInt(src);
    dout.writeInt(payload);
    dout.writeInt(hops.length);
    for (int i = 0; i < hops.length; i++) {
      dout.writeInt(hops[i]);
    }
  }

  public int getDest () {
    return dest;
  }
  public int getSrc () {
    return src;
  }
  public int getPayload () {
    return payload;
  }
  public int[] getHops () {
    return hops;
  }


  @Override
  public String toString() {
    return String.format("TYPE: %d\nDESTINATION ID: %d\n" +
      "SOURCE ID: %d\nPAYLOAD: %d\nHOPS: %s",
      type, dest, src, payload, Arrays.toString(hops));
  }
}
