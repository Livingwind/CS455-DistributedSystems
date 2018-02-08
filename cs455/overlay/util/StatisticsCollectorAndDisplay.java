package cs455.overlay.util;

import cs455.overlay.wireformats.OverlayNodeReportsTrafficSummary;

import java.util.ArrayList;
import java.util.Collections;

public class StatisticsCollectorAndDisplay {
  private ArrayList<StatisticsEntry> entries = new ArrayList<>();

  private final int INDEX_WIDTH = 8;
  private final int SENT_WIDTH = 8;
  private final int RECV_WIDTH = 8;
  private final int RELAY_WIDTH = 10;
  private final int SSUM_WIDTH = 15;
  private final int RSUM_WIDTH = 15;

  public void add (OverlayNodeReportsTrafficSummary event) {
    int id = event.getId();
    int sTotal = event.getSentTotal();
    long sSum = event.getSentSum();
    int relayed = event.getRelayed();
    int rTotal = event.getRecvTotal();
    long rSum = event.getRecvSum();

    entries.add(
      new StatisticsEntry(id, sTotal, sSum, relayed, rTotal, rSum)
    );
  }

  public void clear () {
    entries.clear();
  }

  @Override
  public String toString () {
    String fields =
      "\u2503%-"+INDEX_WIDTH+"s\u2503%"+SENT_WIDTH+"s\u2503" +
      "%"+RECV_WIDTH+"s\u2503%"+RELAY_WIDTH+"s\u2503" +
      "%"+SSUM_WIDTH+"s\u2503%"+RSUM_WIDTH+"s\u2503\n";

    StringBuilder s = new StringBuilder();
    s.append(
      String.format(fields, "", "Packets", "Packets", "Packets",
        "Sum Values", "Sum Values")
    );

    s.append(
      String.format(fields, "", "Sent", "Recv'ed", "Relayed", "Sent", "Recv'ed")
    );

    Collections.sort(entries);
    int totalSent = 0;
    int totalRecv = 0;
    int totalRelayed = 0;
    long totalSumSent =0;
    long totalSumRecv = 0;

    for (StatisticsEntry entry: entries) {
      s.append(String.format(
        fields, "Node " + entry.id, entry.sentTotal, entry.recvTotal,
        entry.relayed, entry.sentSum, entry.recvSum
      ));

      totalSent += entry.sentTotal;
      totalRecv += entry.recvTotal;
      totalRelayed += entry.relayed;
      totalSumSent += entry.sentSum;
      totalSumRecv += entry.recvSum;
    }

    s.append(String.format(
      fields, "Sum", totalSent, totalRecv, totalRelayed, totalSumSent, totalSumRecv
    ));

    return s.toString();
  }
}
