package cs455.overlay.util;

import java.util.Scanner;
import java.util.concurrent.BlockingQueue;

public class InteractiveCommandParser implements Runnable {
  private BlockingQueue<String> queue;
  public InteractiveCommandParser (BlockingQueue<String> queue) {
    this.queue = queue;
  }

  @Override
  public void run() {
    boolean exit = false;
    try (Scanner reader = new Scanner(System.in)) {
      while (true) {
        String input = reader.nextLine();
        try {
          queue.put(input);
        } catch (InterruptedException e){
          System.err.println(e);
        }

        if(input.equals("TERMINATE")) {
          System.out.println("SENDING TERMINATE");
          break;
        }
      }
    }
  }
}
