package cs455.overlay.wireformats;

import java.io.*;

public class RegistryReportsRegistrationStatus extends Event {
  private byte type;
  private int status;   // ID if successful, -1 otherwise
  private String info;

  public RegistryReportsRegistrationStatus (int status, String info) {
    this.type = Protocol.REGISTRY_REPORTS_REGISTRATION_STATUS;
    this.status = status;
    this.info = info;
  }

  // MESSAGE PROTOCOL
  /*
  byte: REGISTRY_REPORTS_REGISTRATION_STATUS
  int: Success status
  byte: Length of information field
  byte[^^]: Information string
   */

  public RegistryReportsRegistrationStatus(byte[] bytes) {
    super(bytes);
  }

  @Override
  protected void readBytes () throws IOException {
    type = din.readByte();
    status = din.readInt();

    byte infoLen = din.readByte();
    byte[] infoBytes = new byte[infoLen];
    din.readFully(infoBytes);
    info = new String(infoBytes);
  }

  @Override
  protected void writeBytes () throws IOException {
    dout.writeByte(type);
    dout.writeInt(status);
    dout.writeByte(info.length());
    dout.writeBytes(info);
  }

  public int getStatus () {
    return status;
  }
  public String getInfo () {
    return info;
  }

  @Override
  public byte getType() {
    return type;
  }

  @Override
  public String toString () {
    return String.format("TYPE: %d\n" + "STATUS: %d\n" + "INFO: %s", type, status, info);
  }
}