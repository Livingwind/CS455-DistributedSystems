package cs455.overlay.transport;

import cs455.overlay.wireformats.Event;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

// Sends messages over a queue
public class TCPSenderThread extends Thread {
  private LinkedBlockingQueue<Event> queue;
  private Socket sock;
  private DataOutputStream dout;

  public TCPSenderThread (LinkedBlockingQueue<Event> queue, Socket sock) {
    this.queue = queue;
    this.sock = sock;
    try {
      dout = new DataOutputStream(this.sock.getOutputStream());
    } catch (IOException e) {
      System.err.println(e);
    }
  }

  @Override
  public void run() {
    Event event;
    byte[] msgInBytes;
    int msgByteLength;

    do {
      event = queue.poll();
      if (event != null) {
        msgInBytes = event.getBytes();
        msgByteLength = msgInBytes.length;

        try {
          dout.writeInt(msgByteLength);
          dout.write(msgInBytes, 0, msgByteLength);
          dout.flush();
        } catch (IOException e) {
          System.err.println(e);
        }

      }

    } while(!Thread.interrupted());
  }
}
