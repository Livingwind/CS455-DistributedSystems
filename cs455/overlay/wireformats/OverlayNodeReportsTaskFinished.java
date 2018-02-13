package cs455.overlay.wireformats;

import java.io.IOException;

public class OverlayNodeReportsTaskFinished extends Event {
  private byte[] hostname;
  private int port;
  private int nodeId;

  public OverlayNodeReportsTaskFinished (byte[] hostname, int port, int nodeId) {
    this.type = Protocol.OVERLAY_NODE_REPORTS_TASK_FINISHED;
    this.hostname = hostname;
    this.port = port;
    this.nodeId = nodeId;
  }

  public OverlayNodeReportsTaskFinished (byte[] bytes) {
    super(bytes);
  }

  @Override
  protected void readBytes() throws IOException {
    type = din.readByte();

    byte hostLen = din.readByte();
    byte[] hostBytes = new byte[hostLen];
    din.readFully(hostBytes);
    hostname = hostBytes;

    port = din.readInt();
    nodeId = din.readInt();
  }

  @Override
  protected void writeBytes() throws IOException {
    dout.writeByte(type);
    dout.writeByte(hostname.length);
    dout.write(hostname);
    dout.writeInt(port);
    dout.writeInt(nodeId);
  }
}
