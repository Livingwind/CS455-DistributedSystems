package cs455.overlay.util;

import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class InteractiveCommandParser implements Runnable {
  private ArrayBlockingQueue<String> queue;
  private Scanner reader;

  public InteractiveCommandParser () {
    queue = new ArrayBlockingQueue<String>(8);
  }

  @Override
  public void run() {
    System.out.println("STARTING COMMAND PARSER");

    String input;
    reader = new Scanner(System.in);

    try {
      do {
        if (reader.hasNext()) {
          input = reader.nextLine();
          queue.put(input);
        }
      } while (true);
    } catch (IllegalStateException e) {
      System.out.println("STOPPPING PARSER");
    } catch (Exception e) {
      System.out.println("STOPPING COMMAND PARSER");
    }
  }

  public synchronized String getMessage () {
    return queue.poll();
  }

  public synchronized void killMe () {
    reader.close();
  }
}
