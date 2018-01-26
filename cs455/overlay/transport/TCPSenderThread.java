package cs455.overlay.transport;

import cs455.overlay.wireformats.Event;

import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

// Sends messages over a queue
public class TCPSenderThread implements Runnable {
  private LinkedBlockingQueue<Event> queue;
  private Socket sock;

  public TCPSenderThread (LinkedBlockingQueue<Event> queue, Socket sock) {
    this.queue = queue;
    this.sock = sock;
  }

  @Override
  public void run() {
  }
}
