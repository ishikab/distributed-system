package DistSyslab0;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class Application
{
  public static void main(String[] args) throws IOException 
  {
    System.out.println("Distributed Systems 18-842 - lab0");
    BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
    System.out.println("Please enter local name::");
    String localName = input.readLine();
        
    System.out.println("Please enter the name of configuration file::");
    String configFileName = input.readLine();
 
    MessagePasser mp = new MessagePasser(localName, configFileName);
   
   while (true)
   {
     try
     {
       System.out.println("Please enter send/receive/exit::");
       String action = input.readLine();
       if (action.equals("exit"))
       {
         System.out.println("Bye!!!");
         System.exit(0);
       }
       else if (action.equals("send"))
       {
         System.out.println("Please enter the destination::");
         String dest = input.readLine();
         if (mp.NodeExists())
         {
           System.out.println("Please enter the kind of message::");
           String kind = input.readLine();
           System.out.println("Please enter the message::");
           String message = input.readLine();
           Message msg = new Message(dest, kind, message);
           mp.send(msg);
         }
         else
         {
           System.out.println("Destination does not exist");
         }
       }
       else if (action.equals("receive"))
       {
         Message msg = mp.receive();
         if (msg == null)
         {
           System.out.println("Sorry, no msg was received");
         }
         else
         {
           System.out.println(msg.toString());
         }
       }
       else
       {
         System.out.println("Invalid input");
       }
     }
     catch (IOException e)
     {
       e.printStackTrace();
     }
   }
  }
}

