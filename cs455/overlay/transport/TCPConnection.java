package cs455.overlay.transport;


import cs455.overlay.wireformats.Event;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

public class TCPConnection implements Runnable {
  private Thread threadRecv;
  private Thread threadSend;
  private Socket sock;

  private LinkedBlockingQueue<Event> queueRecv = new LinkedBlockingQueue<>();
  private LinkedBlockingQueue<Event> queueSend = new LinkedBlockingQueue<>();

  public TCPConnection(String host, int port) {
    try {
      this.sock = new Socket(host, port);
    } catch (IOException e) {
      System.err.println(e);
    }

    threadRecv = new Thread(new TCPReceiverThread(queueRecv, sock));
    threadSend = new Thread(new TCPSenderThread(queueSend, sock));
  }

  public TCPConnection(Socket sock) {
    this.sock = sock;
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

  public String getHost () {
    return sock.getInetAddress().getCanonicalHostName();
  }

  @Override
  public boolean equals (Object obj) {
    if (obj instanceof String) {
      String other = (String) obj;
      return getHost().equals(other);
    }
    return false;
  }

  public Socket getSock () {
    return sock;
  }

  @Override
  public int hashCode() {
    return getHost().hashCode();
  }

  public synchronized void sendMessage (Event e) {
    queueSend.add(e);
  }

  public synchronized Event receiveMessage () {
    return queueRecv.poll();
  }
}
