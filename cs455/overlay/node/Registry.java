package cs455.overlay.node;

public class Registry {
  public static void main(String[] args){
    if (args[0].equals("server"))
      System.out.println("SERVER");
    else if (args[0].equals("client"))
      System.out.println("CLIENT");
  }
}
