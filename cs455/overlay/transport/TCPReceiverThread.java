package cs455.overlay.transport;

import cs455.overlay.wireformats.Event;

import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

// Receives messages sent to the socket
public class TCPReceiverThread implements Runnable {
  private LinkedBlockingQueue<Event> queue;
  private Socket sock;

  public TCPReceiverThread (LinkedBlockingQueue<Event> queue, Socket sock) {
    this.queue = queue;
    this.sock = sock;
  }

  @Override
  public void run() {
  }
}
