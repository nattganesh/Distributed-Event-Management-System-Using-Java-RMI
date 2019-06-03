/**
 * CONCORDIA UNIVERSITY
 * DEPARTMENT OF COMPUTER SCIENCE AND SOFTWARE ENGINEERING
 * COMP 6231, Summer 2019 Instructor: Sukhjinder K. Narula
 * ASSIGNMENT 1
 * Issued: May 14, 2019 Due: Jun 3, 2019
 */
package Client;

import static CommonUtils.CommonUtils.*;
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
    private static final Scanner scanner = new Scanner(System.in);

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        System.out.print("Enter Your ID Number: ");
        String id = scanner.next();
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
            if ((clientType.equals(CUSTOMER_ClientType) || clientType.equals(EVENT_MANAGER_ClientType))
                    && (serverId.equals(TORONTO) || serverId.equals(MONTREAL) || serverId.equals(OTTAWA)))
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
            addFileHandler(LOGGER, customerID);
            server = (ServerInterface) registry.lookup(getServerName(serverId));

            runCustomerMenu(server, customerID);
        }
        catch (SecurityException | IOException | NotBoundException ex)
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
            System.out.println("Welcome Manager " + clientID);
            Registry registry = LocateRegistry.getRegistry(getServerPort(serverId));
            LOGGER = Logger.getLogger(getServerClassName(serverId));
            addFileHandler(LOGGER, customerID);
            server = (ServerInterface) registry.lookup(getServerName(serverId));
            runManagerMenu(server, customerID);
        }
        catch (SecurityException | IOException | NotBoundException ex)
        {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void runClientService(String clientType, String serverId, String clientID)
    {
        switch (clientType)
        {
            case CUSTOMER_ClientType:
                customerService(serverId, clientID);
                break;
            case EVENT_MANAGER_ClientType:
                eventManagerService(serverId, clientID);
                break;
        }
    }

    private static int getServerPort(String serverId)
    {
        switch (serverId)
        {
            case TORONTO: return TORONTO_SERVER_PORT;
            case MONTREAL: return MONTREAL_SERVER_PORT;
            case OTTAWA: return OTTAWA_SERVER_PORT;
            default: return -1;
        }
    }

    private static String getServerName(String serverId)
    {
        switch (serverId)
        {
            case TORONTO: return TORONTO_SERVER_NAME;
            case MONTREAL: return MONTREAL_SERVER_NAME;
            case OTTAWA: return OTTAWA_SERVER_NAME;
            default: return "Server Does Not Exist";
        }
    }

    private static String getServerClassName(String serverId)
    {
        switch (serverId)
        {
            case TORONTO: return TorontoServerImpl.class.getName();
            case MONTREAL: return MontrealServerImpl.class.getName();
            case OTTAWA: return OttawaServerImpl.class.getName();
            default: return "Server Does Not Exist";
        }
    }

    private static void runCustomerMenu(ServerInterface server, String customerID) throws RemoteException
    {
        String itemNum = "";
        while (!itemNum.equals("0"))
        {
            System.out.println("============================");
            System.out.println("Customer Menu");
            System.out.println("0: Quit");
            System.out.println("1: Book Event");
            System.out.println("2: Get Booking Schedule");
            System.out.println("3: Cancel Event");
            System.out.println("============================");

            itemNum = scanner.next();
            switch (itemNum.trim())
            {
                case "0":
                    System.out.println("Good Bye !!!");
                    break;
                case "1":
                    runBookEvent(server, customerID);
                    break;
                case "2":
                    runBookingSchedule(server, customerID);
                    break;
                case "3":
                    System.out.println("Enter Event ID to Cancel: ");
                    String eventID = scanner.next();
                    server.cancelEvent(customerID, eventID);
                    break;
                default:
                    System.out.println("Invalid Choice !!!");
                    break;
            }
        }
        scanner.close();
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

            itemNum = scanner.nextInt();
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
        scanner.close();
    }

    private static void managerListEvents(ServerInterface server, String customerID)
    {
        try
        {
            String eventType = scanner.next();
            switch (eventType.toUpperCase())
            {
                case "A":
                    eventType = CONFERENCE;
                    break;
                case "B":
                    eventType = TRADESHOW;
                    break;
                case "C":
                    eventType = SEMINAR;
                    break;
                default:
                    System.out.println("Invalid Choice !!!");
                    eventType = "";
                    break;
            }

            String str = server.listEventAvailability(eventType, customerID);
            System.out.println(str);
            LOGGER.log(Level.INFO, "Response of Server: {0}", str);
        }
        catch (RemoteException ex)
        {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void managerAddEvent(ServerInterface server, String managerID)
    {
        String eventID;
        String eventType;
        String bookingCapacity;
        try
        {
            System.out.print("Please enter Event id: ");
            eventID = enterEventID();
            System.out.println();
            System.out.print("Please enter Event Type: (Available Options: A: CONFERENCE, B: TRADESHOW, C: SEMINAR) ");
            eventType = scanner.nextLine();
            switch (eventType.toUpperCase())
            {
                case "A":
                    eventType = CONFERENCE;
                    break;
                case "B":
                    eventType = TRADESHOW;
                    break;
                case "C":
                    eventType = SEMINAR;
                    break;
                default:
                    System.out.println("Invalid Choice !!!");
                    eventType = "";
                    break;
            }
            System.out.println();
            System.out.print("Please enter Booking Capacity: ");
            bookingCapacity = scanner.nextLine();
            LOGGER.log(Level.INFO, "Manager: {0} adding a new Event with Event id: {1} ,Event Type: {2} and Booking Capacity: {3}", new Object[]{managerID, eventID, eventType, bookingCapacity});
            String string = server.addEvent(eventID, eventType, bookingCapacity, managerID);
            LOGGER.log(Level.INFO, "Response of server: {0}", string);
            System.out.println("Response of server: " + string);
        }
        catch (RemoteException ex)
        {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static String enterEventID()
    {
        String s;
        s = scanner.nextLine();
        if (s.length() != 10)
        {
            return "Invalid Event ID";
        }
        else
        {
            String serverId = s.substring(0, 3).toUpperCase();
            String time = s.substring(3, 4).toUpperCase();
            String eventID = s.substring(4, 10).toUpperCase();
            if ((time.equals(MORNING) || time.equals(EVENING) || time.equals(AFTERNOON))
                    && (serverId.equals(TORONTO) || serverId.equals(MONTREAL) || serverId.equals(OTTAWA)))
            {
                return s;
            }
            else
            {
                System.out.println("Please enter valid Event id: ");
                return enterEventID();
            }
        }
    }

    private static void managerRemoveEvent(ServerInterface server, String managerID)
    {
        //We need to check if the event is booked by a client before
        String eventID;
        String eventType;
        try
        {
            System.out.print("Please enter Event id: ");
            eventID = enterEventID();
            System.out.println();
            System.out.print("Please enter Event Type: ");
            eventType = scanner.nextLine();
            switch (eventType.toUpperCase())
            {
                case "A":
                    eventType = CONFERENCE;
                    break;
                case "B":
                    eventType = TRADESHOW;
                    break;
                case "C":
                    eventType = SEMINAR;
                    break;
                default:
                    System.out.println("Invalid Choice !!!");
                    eventType = "";
                    break;
            }
            LOGGER.log(Level.INFO, "Manager {0} removing Event with Event ID {1} of type: {2}", new Object[]{managerID, eventID, eventType});
            String string = server.removeEvent(eventID, eventType, managerID);
            LOGGER.log(Level.INFO, "Response of server: {0}", string);
        }
        catch (RemoteException ex)
        {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void runBookingSchedule(ServerInterface server, String customerID) throws RemoteException
    {
        LOGGER.log(Level.INFO, "Booking Schedule Requested by {0}", customerID);
        System.out.println(customerID + "'s Bookings Schedule");
        String booking = server.getBookingSchedule(customerID);
        System.out.println(booking);

        if (!booking.equalsIgnoreCase(OPERATIONFAILURE))
        {
            LOGGER.log(Level.INFO, "Operation Sucessful. Records for {0} have been found", customerID);
            LOGGER.info(booking);
        }
        else
        {
            LOGGER.log(Level.INFO, "Operation Failure. Records for {0} do not exist.", customerID);
        }
    }

    private static void runBookEvent(ServerInterface server, String customerID) throws RemoteException
    {
        System.out.println("What type of event do you wish to book? (Available Options: A: CONFERENCE, B: TRADESHOW, C: SEMINAR)");
        String eventType = scanner.next();
        switch (eventType.toUpperCase())
        {
            case "A":
                eventType = CONFERENCE;
                break;
            case "B":
                eventType = TRADESHOW;
                break;
            case "C":
                eventType = SEMINAR;
                break;
            default:
                System.out.println("Invalid Choice !!!");
                eventType = "";
                break;
        }
        if (!eventType.equals(""))
        {
            System.out.println("Enter Event ID: ");
            String eventID = scanner.next();
            System.out.println("Enter the number of people attending: ");
            Integer booking = scanner.nextInt();
            if (booking > 0)
            {
                String book = booking.toString();
                String msg = server.bookEvent(customerID, eventID, eventType, book);
                LOGGER.info(msg);
                System.out.println(msg);
            }
            else
            {
                System.out.println("Invalid Number !!! number of people attending > 0");
            }
        }
    }
}
