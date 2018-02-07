package cs455.overlay.wireformats;

import java.io.IOException;

public class NodeReportsOverlaySetupStatus extends Event {
  private int status;
  private String info;


  public NodeReportsOverlaySetupStatus (int status, String info) {
    this.type = Protocol.NODE_REPORTS_OVERLAY_SETUP_STATUS;
    this.status = status;
    this.info = info;
  }

  public NodeReportsOverlaySetupStatus (byte[] bytes) {
    super(bytes);
  }

  @Override
  protected void readBytes() throws IOException {
    type = din.readByte();
    status = din.readInt();

    byte infoLen = din.readByte();
    byte[] infoBytes = new byte[infoLen];
    din.readFully(infoBytes);
    info = new String(infoBytes);
  }

  @Override
  protected void writeBytes() throws IOException {
    dout.writeByte(type);
    dout.writeInt(status);
    dout.writeByte(info.length());
    dout.writeBytes(info);
  }

  @Override
  public String toString() {
    return String.format("TYPE: %d\nSTATUS: %d\nINFO: %s", type, status, info);
  }
}
