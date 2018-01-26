package cs455.overlay.transport;

import cs455.overlay.wireformats.Event;

import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;

// Receives messages sent to the socket
public class TCPReceiverThread implements Runnable {
  private ArrayBlockingQueue<Event> queue;
  private Socket sock;

  public TCPReceiverThread (ArrayBlockingQueue<Event> queue, Socket sock) {
    this.queue = queue;
    this.sock = sock;
  }

  @Override
  public void run() {
  }
}
