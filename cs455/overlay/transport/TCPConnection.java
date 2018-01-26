package cs455.overlay.transport;


import cs455.overlay.wireformats.Event;

import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;

public class TCPConnection implements Runnable {
  private Thread threadRecv;
  private Thread threadSend;
  private Socket sock;

  ArrayBlockingQueue<Event> queueRecv;
  ArrayBlockingQueue<Event> queueSend;

  public TCPConnection(Socket sock) {
    this.sock = sock;
    queueRecv = new ArrayBlockingQueue<Event>(8);
    queueSend = new ArrayBlockingQueue<Event>(8);

    threadRecv = new Thread(new TCPReceiverThread(queueRecv, sock));
    threadSend = new Thread(new TCPSenderThread(queueSend, sock));
  }

  @Override
  public void run() {
    System.out.println("STARTING TCPCONNECTION");
    threadRecv.start();
    threadSend.start();

    while (!Thread.interrupted()) {}

    try {
      sock.close();
      threadRecv.interrupt();
      threadSend.interrupt();

      threadRecv.join();
      threadSend.join();
    } catch (Exception e) {
      System.err.println(e);
    }

    System.out.println("ENDING TCPCONNECTION");
  }

  public synchronized void sendMessage (Event e) {
    queueSend.add(e);
  }

  public synchronized Event receiveMessage () {
    return queueRecv.poll();
  }
}
