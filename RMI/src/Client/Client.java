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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
            //String clientID = id.substring(4, 8).toUpperCase();
            if ((clientType.equals(CommonUtils.CUSTOMER_ClientType) || clientType.equals(CommonUtils.EVENT_MANAGER_ClientType))
                    && (serverId.equals(CommonUtils.TORONTO) || serverId.equals(CommonUtils.MONTREAL) || serverId.equals(CommonUtils.OTTAWA)))
            {
                runClientService(clientType, serverId, id);
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

            runCustomerMenu(server, customerID);
        } catch (SecurityException | IOException ex)
        {
            //Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NotBoundException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void eventManagerService(String serverId, String clientID)
    {
        ServerInterface server;
        try
        {
            String customerID = serverId + "M" + clientID;
            System.out.println("Welcome Manager " + clientID);
            Registry registry = LocateRegistry.getRegistry(getServerPort(serverId));
            LOGGER = Logger.getLogger(getServerClassName(serverId));
            CommonUtils.addFileHandler(LOGGER, customerID);
            server = (ServerInterface) registry.lookup(getServerName(serverId));

//            String out = server.listEventAvailability(CommonUtils.CONFERENCE, clientID);
//            System.out.println(out);
            runManagerMenu(server, clientID);
        } catch (SecurityException | IOException ex)
        {
            //Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NotBoundException ex) {
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
            case CommonUtils.TORONTO:
                return CommonUtils.TORONTO_SERVER_PORT;
            case CommonUtils.MONTREAL:
                return CommonUtils.MONTREAL_SERVER_PORT;
            case CommonUtils.OTTAWA:
                return CommonUtils.OTTAWA_SERVER_PORT;
            default: return -1;
        }
    }

    private static String getServerName(String serverId)
    {
        switch (serverId)
        {
            case CommonUtils.TORONTO:
                return CommonUtils.TORONTO_SERVER_NAME;
            case CommonUtils.MONTREAL:
                return CommonUtils.MONTREAL_SERVER_NAME;
            case CommonUtils.OTTAWA:
                return CommonUtils.OTTAWA_SERVER_NAME;
            default: return "Server Does Not Exist";
        }
    }

    private static String getServerClassName(String serverId)
    {
        switch (serverId)
        {
            case CommonUtils.TORONTO:
                return TorontoServerImpl.class.getName();
            case CommonUtils.MONTREAL:
                return MontrealServerImpl.class.getName();
            case CommonUtils.OTTAWA:
                return OttawaServerImpl.class.getName();
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
                    switch (eventType.toUpperCase()) {
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
                    if (!eventType.equals("")) {
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
                    managerAddEvent(server, customerID);
                    break;
                case 2:
                    System.out.println("What event do you wish to remove?");
                    managerRemoveEvent(server, customerID);
                    break;
                case 3:
                    System.out.println("Which type of event you wish to list? (Available Options: A: CONFERENCE, B: TRADESHOW, C: SEMINAR)");
                    managerListEvents(server, customerID);
                    break;
                default:
                    System.out.println("Invalid Choice !!!");
                    break;
            }

        }
        in.close();
    }

    private static void managerListEvents(ServerInterface server, String customerID) {
        try {
            String eventType = in.next();
            switch (eventType.toUpperCase()) {
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

            String str = server.listEventAvailability(eventType, customerID);
            LOGGER.info("Response of Server: " + str);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void managerAddEvent(ServerInterface obj, String managerID) {
        String eventID;
        String eventType;
        String bookingCapacity;
        BufferedReader bfr = new BufferedReader(new InputStreamReader(System.in));
        try {
            System.out.print("Please enter Event id: ");
            eventID = enterEventID(bfr);
            System.out.println();
            System.out.print("Please enter Event Type: (Available Options: A: CONFERENCE, B: TRADESHOW, C: SEMINAR) ");
            eventType = bfr.readLine();
            switch (eventType.toUpperCase()) {
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
            System.out.println();
            System.out.print("Please enter Booking Capacity: ");
            bookingCapacity = bfr.readLine();
            LOGGER.info("Manager: " + managerID + " adding a new Event with Event id: " + eventID + " ,Event Type: " + eventType + " and Booking Capacity: " + bookingCapacity);
            String string = obj.addEvent(eventID, eventType, bookingCapacity, managerID);
            LOGGER.info("Response of server: " + string);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String enterEventID(BufferedReader bfr) {
        String s;
        try {
            s = bfr.readLine();
            if (s.length() != 10) {
                return "Invalid Event ID";
            } else {
                String serverId = s.substring(0, 3).toUpperCase();
                String time = s.substring(3, 4).toUpperCase();
                String eventID = s.substring(4, 10).toUpperCase();
                if ((time.equals(CommonUtils.MORNING) || time.equals(CommonUtils.EVENING) || time.equals(CommonUtils.AFTERNOON))
                        && (serverId.equals(CommonUtils.TORONTO) || serverId.equals(CommonUtils.MONTREAL) || serverId.equals(CommonUtils.OTTAWA))
                ) {
                    return s;
                } else {
                    System.out.println("Please enter valid Event id: ");
                    return enterEventID(bfr);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Invalid Event id";
    }

    private static void managerRemoveEvent(ServerInterface conObj, String managerID) {
        //We need to check if the event is booked by a client before
        String eventID;
        String eventType;
        BufferedReader bfr = new BufferedReader(new InputStreamReader(System.in));

        try {
            System.out.print("Please enter Event id: ");
            eventID = enterEventID(bfr);
            System.out.println();
            System.out.print("Please enter Event Type: ");
            eventType = bfr.readLine();
            switch (eventType.toUpperCase()) {
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
            LOGGER.info("Manager " + managerID + " removing Event with Event ID " + eventID + " of type: " + eventType);
            String string = conObj.removeEvent(eventID, eventType, managerID);
            LOGGER.info("Response of server: " + string);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
