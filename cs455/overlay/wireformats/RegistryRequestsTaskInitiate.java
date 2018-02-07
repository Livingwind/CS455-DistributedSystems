package cs455.overlay.wireformats;

import java.io.IOException;

public class RegistryRequestsTaskInitiate extends Event {
  private int numPackets;

  public RegistryRequestsTaskInitiate (int numPackets) {
    this.type = Protocol.REGISTRY_REQUESTS_TASK_INITIATE;
    this.numPackets = numPackets;
  }

  public RegistryRequestsTaskInitiate (byte[] bytes) {
    super(bytes);
  }

  @Override
  protected void readBytes () throws IOException {
    type = din.readByte();
    numPackets = din.readInt();
  }

  @Override
  protected void writeBytes () throws IOException {
    dout.writeByte(type);
    dout.writeInt(numPackets);
  }

  @Override
  public String toString () {
    return String.format("TYPE: %d\nNUM PACKETS: %d", type, numPackets);
  }
}