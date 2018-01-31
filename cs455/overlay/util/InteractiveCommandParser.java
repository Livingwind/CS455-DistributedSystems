package cs455.overlay.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class InteractiveCommandParser extends Thread {
  private LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>();
  public InteractiveCommandParser () {}

  @Override
  public void run() {
    System.out.println("STARTING COMMAND PARSER");

    try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))){
      do {
        if (br.ready())
          queue.put(br.readLine());
      } while (!Thread.interrupted());
    } catch (InterruptedException e) {
      System.out.println("STOPPING PARSER");
    } catch (Exception e) {
      System.err.println(e);
    }
  }

  public synchronized String getMessage () {
    return queue.poll();
  }
}
