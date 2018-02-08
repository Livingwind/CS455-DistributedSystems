package cs455.overlay.util;

public class StatisticsEntry implements Comparable<StatisticsEntry> {
  public int id;
  public int sentTotal;
  public long sentSum;
  public int relayed;
  public int recvTotal;
  public long recvSum;

  public StatisticsEntry (int id, int sTotal, long sSum,
                          int relayed, int rTotal, long rSum) {
    this.id = id;
    this.sentTotal = sTotal;
    this.sentSum = sSum;
    this.relayed = relayed;
    this.recvTotal = rTotal;
    this.recvSum = rSum;
  }


  @Override
  public int compareTo(StatisticsEntry o) {
    return id - o.id;
  }
}
