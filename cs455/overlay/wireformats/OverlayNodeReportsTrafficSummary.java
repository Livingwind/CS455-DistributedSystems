package cs455.overlay.wireformats;

import java.io.IOException;

public class OverlayNodeReportsTrafficSummary extends Event {
  private int id;
  private int sentTotal;
  private int relayed;
  private long sentSum;
  private int recvTotal;
  private long recvSum;

  public OverlayNodeReportsTrafficSummary (int id, int sentTotal, long sentSum, int relayed,
                                           int recvTotal, long recvSum) {
    this.type = Protocol.OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY;
    this.id = id;
    this.sentTotal = sentTotal;
    this.sentSum = sentSum;
    this.relayed = relayed;
    this.recvTotal = recvTotal;
    this.recvSum = recvSum;
  }

  public OverlayNodeReportsTrafficSummary (byte[] bytes) {
    super(bytes);
  }

  @Override
  protected void readBytes () throws IOException {
    type = din.readByte();
    id = din.readInt();
    sentTotal = din.readInt();
    relayed = din.readInt();
    sentSum = din.readLong();
    recvTotal = din.readInt();
    recvSum = din.readLong();
  }

  @Override
  protected void writeBytes () throws IOException {
    dout.writeByte(type);
    dout.writeInt(id);
    dout.writeInt(sentTotal);
    dout.writeInt(relayed);
    dout.writeLong(sentSum);
    dout.writeInt(recvTotal);
    dout.writeLong(recvSum);
  }

  public int getId() {
    return id;
  }

  public int getSentTotal() {
    return sentTotal;
  }

  public long getSentSum() {
    return sentSum;
  }

  public int getRelayed() {
    return relayed;
  }

  public int getRecvTotal() {
    return recvTotal;
  }

  public long getRecvSum() {
    return recvSum;
  }

  @Override
  public String toString () {
    return String.format(
      "TYPE: %d\nNODEID: %d\nSENT: %d\nSENT SUM: %d\nRELAYED: %d\n" +
      "RECVED: %d\n RECVED SUM: %d",
      type, id, sentTotal, sentSum, relayed, recvTotal, recvSum);
  }
}
