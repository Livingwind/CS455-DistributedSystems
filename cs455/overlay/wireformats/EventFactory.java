package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class EventFactory {
  private static EventFactory instance = null;
  protected EventFactory() {}

  public static synchronized EventFactory getInstance () {
    if (instance == null) {
      instance = new EventFactory();
    }
    return instance;
  }

  public static byte parseType (byte[] bytes) {
    byte type = -1;
    try (ByteArrayInputStream instream = new ByteArrayInputStream(bytes);
         DataInputStream din
             = new DataInputStream(new BufferedInputStream(instream))) {
      type = din.readByte();
    } catch (IOException e) {
      System.err.println("ERROR PARSING TYPE: " + e);
    }
    return type;
  }

  public static Event createEvent (byte[] bytes) {
    switch(parseType(bytes)) {
      case Protocol.OVERLAY_NODE_SENDS_REGISTRATION:
        return new OverlayNodeSendsRegistration(bytes);
      case Protocol.REGISTRY_REPORTS_REGISTRATION_STATUS:
        return new RegistryReportsRegistrationStatus(bytes);
      case Protocol.OVERLAY_NODE_SENDS_DEREGISTRATION:
        return new OverlayNodeSendsDeregistration(bytes);
      case Protocol.REGISTRY_REPORTS_DEREGISTRATION_STATUS:
        return new RegistryReportsDeregistrationStatus(bytes);
      case Protocol.REGISTRY_SENDS_NODE_MANIFEST:
        return new RegistrySendsNodeManifest(bytes);
      case Protocol.NODE_REPORTS_OVERLAY_SETUP_STATUS:
        return new NodeReportsOverlaySetupStatus(bytes);
      case Protocol.REGISTRY_REQUESTS_TASK_INITIATE:
        return new RegistryRequestsTaskInitiate(bytes);
      case Protocol.OVERLAY_NODE_SENDS_DATA:
        return new OverlayNodeSendsData(bytes);
      case Protocol.OVERLAY_NODE_REPORTS_TASK_FINISHED:
        return new OverlayNodeReportsTaskFinished(bytes);
    }
    return null;
  }
}
