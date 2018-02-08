package cs455.overlay.wireformats;

import java.io.IOException;

public class RegistryRequestsTrafficSummary extends Event {

  public RegistryRequestsTrafficSummary () {
    this.type = Protocol.REGISTRY_REQUESTS_TRAFFIC_SUMMARY;
  }

  public RegistryRequestsTrafficSummary (byte[] bytes) {
    super(bytes);
  }

  @Override
  protected void readBytes () throws IOException {
    type = din.readByte();
  }

  @Override
  protected void writeBytes () throws IOException {
    dout.writeByte(type);
  }

  @Override
  public String toString() {
    return "TYPE: " + type;
  }
}
