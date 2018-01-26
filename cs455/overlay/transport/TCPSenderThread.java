package cs455.overlay.transport;

import cs455.overlay.wireformats.Event;

import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;

// Sends messages over a queue
public class TCPSenderThread implements Runnable {
  private ArrayBlockingQueue<Event> queue;
  private Socket sock;

  public TCPSenderThread (ArrayBlockingQueue<Event> queue, Socket sock) {
    this.queue = queue;
    this.sock = sock;
  }

  @Override
  public void run() {
  }
}
