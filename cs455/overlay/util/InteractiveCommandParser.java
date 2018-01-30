package cs455.overlay.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class InteractiveCommandParser implements Runnable {
  private LinkedBlockingQueue<String> queue;
  public InteractiveCommandParser () {
    queue = new LinkedBlockingQueue<>();
  }

  @Override
  public void run() {
    System.out.println("STARTING COMMAND PARSER");

    try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))){
      do {
        while (!br.ready()) {
          Thread.sleep(1);
        };
        queue.put(br.readLine());
      } while (true);
    } catch (InterruptedException e) {
      System.out.println("STOPPPING PARSER");
    } catch (Exception e) {
      System.err.println(e);
    }
  }

  public synchronized String getMessage () {
    return queue.poll();
  }
}
