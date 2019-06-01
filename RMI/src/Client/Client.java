/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import CommonUtils.CommonUtils;
import ServerImpl.MontrealServerImpl;
import ServerImpl.OttawaServerImpl;
import ServerImpl.TorontoServerImpl;
import ServerInterface.ServerInterface;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Natheepan Ganeshamoorthy
 */
public class Client {

    private static Logger LOGGER;
    private static Scanner in = new Scanner(System.in);
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        System.out.print("Enter Your ID Number: ");
        String id = in.next();
        System.out.println();
        if (id.length() != 8)
        {
            System.out.println("Invalid ID !!!");
        }
        else
        {
            String serverId = id.substring(0, 3).toUpperCase();
            String clientType = id.substring(3, 4).toUpperCase();
            String clientID = id.substring(4, 8).toUpperCase();
            if ((clientType.equals(CommonUtils.CUSTOMER_ClientType) || clientType.equals(CommonUtils.EVENT_MANAGER_ClientType))
                    && (serverId.equals(CommonUtils.TORONTO) || serverId.equals(CommonUtils.MONTREAL) || serverId.equals(CommonUtils.OTTAWA)))
            {
                runClientService(clientType, serverId, clientID);
            }
            else
            {
                System.out.println("Invalid ID !!!");
            }
        }
    }

    private static void customerService(String serverId, String clientID)
    {
        ServerInterface server;
        try
        {
            String customerID = serverId + "C" + clientID;
            System.out.println("Welcome Customer " + customerID);
            Registry registry = LocateRegistry.getRegistry(getServerPort(serverId));
            LOGGER = Logger.getLogger(getServerClassName(serverId));
            CommonUtils.addFileHandler(LOGGER, customerID);
            server = (ServerInterface) registry.lookup(getServerName(serverId));
            
            runCustomerMenu(server,customerID);
        }
        catch (SecurityException | IOException ex)
        {
            //Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (NotBoundException ex)
        {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void eventManagerService(String serverId, String clientID)
    {
        ServerInterface server;
        try
        {
            String customerID = serverId + "M" + clientID;
            System.out.println("Welcome Manager " + customerID);
            Registry registry = LocateRegistry.getRegistry(getServerPort(serverId));
            LOGGER = Logger.getLogger(getServerClassName(serverId));
            CommonUtils.addFileHandler(LOGGER, customerID);
            server = (ServerInterface) registry.lookup(getServerName(serverId));

            String out = server.listEventAvailability("Conferences", clientID);
            System.out.println(out);
        }
        catch (SecurityException | IOException ex)
        {
            //Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (NotBoundException ex)
        {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void runClientService(String clientType, String serverId, String clientID)
    {
        switch (clientType)
        {
            case CommonUtils.CUSTOMER_ClientType:
                customerService(serverId, clientID);
                break;
            case CommonUtils.EVENT_MANAGER_ClientType:
                eventManagerService(serverId, clientID);
                break;
        }
    }

    private static int getServerPort(String serverId)
    {
        switch (serverId)
        {
            case CommonUtils.TORONTO:  return CommonUtils.TORONTO_SERVER_PORT;
            case CommonUtils.MONTREAL: return CommonUtils.MONTREAL_SERVER_PORT;
            case CommonUtils.OTTAWA:   return CommonUtils.OTTAWA_SERVER_PORT;
            default: return -1;
        }
    }
    
    private static String getServerName(String serverId)
    {
        switch (serverId)
        {
            case CommonUtils.TORONTO:  return CommonUtils.TORONTO_SERVER_NAME;
            case CommonUtils.MONTREAL: return CommonUtils.MONTREAL_SERVER_NAME;
            case CommonUtils.OTTAWA:   return CommonUtils.OTTAWA_SERVER_NAME;
            default: return "Server Does Not Exist";
        }
    }
    
    private static String getServerClassName(String serverId)
    {
        switch (serverId)
        {
            case CommonUtils.TORONTO:  return TorontoServerImpl.class.getName();
            case CommonUtils.MONTREAL: return MontrealServerImpl.class.getName();
            case CommonUtils.OTTAWA:   return OttawaServerImpl.class.getName();
            default: return "Server Does Not Exist";
        }
    }
    
    private static void runCustomerMenu(ServerInterface server, String customerID) throws RemoteException
    {
        int itemNum = -1;       
        while (itemNum != 0)
        {
            System.out.println("============================");
            System.out.println("Customer Menu");
            System.out.println("0: Quit");
            System.out.println("1: Book Event");
            System.out.println("2: Get Booking Schedule");
            System.out.println("3: Cancel Event");
            System.out.println("============================");

            itemNum = in.nextInt();
            switch (itemNum)
            {
                case 0:
                    System.out.println("Good Bye !!!");
                    break;
                case 1:
                    System.out.println("What type of event do you wish to book? (Available Options: A: CONFERENCE, B: TRADESHOW, C: SEMINAR)");
                    String eventType = in.next();
                    switch (eventType.toUpperCase())
                    {
                        case "A":
                            eventType = CommonUtils.CONFERENCE;
                            break;
                        case "B":
                            eventType = CommonUtils.TRADESHOW;
                            break;
                        case "C":
                            eventType = CommonUtils.SEMINAR;
                            break;
                        default:
                            System.out.println("Invalid Choice !!!");
                            eventType = "";
                            break;
                    }
                    if (!eventType.equals(""))
                    {
                        System.out.println("Enter Event ID: ");
                        String eventID = in.next();
                        server.bookEvent(customerID, eventID, eventType);
                    }
                    break;
                case 2:
                    server.getBookingSchedule(customerID);
                    break;
                case 3:
                    System.out.println("Enter Event ID to Cancel: ");
                    String eventID = in.next();
                    server.cancelEvent(customerID, eventID);
                    break;
                default:
                    System.out.println("Invalid Choice !!!");
                    break;
            }

        }
        in.close();
    }
    
    private static void runManagerMenu(ServerInterface server, String customerID) throws RemoteException
    {
        int itemNum = -1;       
        while (itemNum != 0)
        {
            System.out.println("============================");
            System.out.println("Customer Menu");
            System.out.println("0: Quit");
            System.out.println("1: Add Event");
            System.out.println("2: Remove Event");
            System.out.println("3: List Event Availability");
            System.out.println("============================");

            itemNum = in.nextInt();
            switch (itemNum)
            {
                case 0:
                    System.out.println("Good Bye !!!");
                    break;
                case 1:
                    System.out.println("What event do you wish to add?");                   
                    break;
                case 2:
                    System.out.println("What event do you wish to remove?");
                    break;
                case 3:
                    break;
                default:
                    System.out.println("Invalid Choice !!!");
                    break;
            }

        }
        in.close();
    }

}
