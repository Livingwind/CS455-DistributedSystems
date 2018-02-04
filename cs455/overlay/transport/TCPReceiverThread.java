package cs455.overlay.transport;

import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.EventFactory;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.LinkedBlockingQueue;

// Receives messages sent to the socket
public class TCPReceiverThread extends Thread {
  private LinkedBlockingQueue<Event> queue;
  private Socket sock;
  private DataInputStream din;

  public TCPReceiverThread (LinkedBlockingQueue<Event> queue, Socket sock) {
    this.queue = queue;
    this.sock = sock;
    try {
      this.din = new DataInputStream(this.sock.getInputStream());
    } catch (IOException e) {
      System.err.println(e);
    }
  }

  @Override
  public void run() {
    int dataLength;
    byte[] data;
    Event createdEvent;

    try {
      do {
        dataLength = din.readInt();

        data = new byte[dataLength];
        din.readFully(data);

        createdEvent = EventFactory.createEvent(data);
        System.out.println("RECEIVED: \n" + createdEvent);
        queue.put(createdEvent);

      } while (!sock.isClosed());
    } catch (Exception e) {
      System.err.println("CONNECTION CLOSED");
    }

    try {
      sock.close();
    } catch (Exception e) {
      System.err.println(e);
    }
  }

  public synchronized void killMe () {
    try {
      sock.close();
    } catch (IOException e) {
      System.err.println(e);
    }
  }
}
