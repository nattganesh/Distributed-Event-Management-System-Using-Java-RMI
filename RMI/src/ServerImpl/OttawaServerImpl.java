/**
 * CONCORDIA UNIVERSITY
 * DEPARTMENT OF COMPUTER SCIENCE AND SOFTWARE ENGINEERING
 * COMP 6231, Summer 2019 Instructor: Sukhjinder K. Narula
 * ASSIGNMENT 1
 * Issued: May 14, 2019 Due: Jun 3, 2019
 */
package ServerImpl;

import static CommonUtils.CommonUtils.*;
import ServerInterface.ServerInterface;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Gursimran Singh, Natheepan Ganeshamoorthy
 */
public class OttawaServerImpl extends UnicastRemoteObject implements ServerInterface {

    private static HashMap<String, HashMap< String, Object>> databaseOttawa = new HashMap<>();
    private static HashMap<String, HashMap<String, Integer>> customerEventsMapping = new HashMap<>();
    private static Logger logger;    
    {

        //item1
        databaseOttawa.put(CONFERENCE, new HashMap<>());
        databaseOttawa.get(CONFERENCE).put("OTWM100519", "5");
        databaseOttawa.get(CONFERENCE).put("OTWA100519", "3");
        databaseOttawa.get(CONFERENCE).put("OTWE12345", "12");

        //item2
        databaseOttawa.put(SEMINAR, new HashMap<>());
        databaseOttawa.get(SEMINAR).put("OTWA100519", "8");
        databaseOttawa.get(SEMINAR).put("OTWM100519", "2");

        //item6
        databaseOttawa.put(TRADESHOW, new HashMap<>());
        databaseOttawa.get(TRADESHOW).put("OTWA100519", "9");
        databaseOttawa.get(TRADESHOW).put("OTWE100519", "9");

    }

    public OttawaServerImpl() throws RemoteException
    {
        super();
        logger = Logger.getLogger(OttawaServerImpl.class.getName());
        try
        {
            addFileHandler(logger, "Ottawa_Server");
        }
        catch (SecurityException | IOException ex)
        {
            Logger.getLogger(MontrealServerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public String addEvent(String eventID, String eventType, String bookingCapacity, String managerID) throws RemoteException
    {
        String message = null;
        logger.info("Received request to add an event with event id " + eventID + " , Event Type" + eventType
                + " & Booking Capacity " + bookingCapacity);
        if (!databaseOttawa.get(eventType).containsKey(eventID))
        {
            databaseOttawa.get(eventType).put(eventID, bookingCapacity);
            message = "Operations Successful!. Event Added in Montreal Server for Event ID: "
                    + eventID + " Event Type: " + eventType + " Booking Capacity: " + bookingCapacity;
            logger.info(message);

            return message;
        }
        else
        {
            databaseOttawa.get(eventType).replace(eventID, bookingCapacity);
            message = "Operations Unsuccessful!. Event Not Added in Montreal Server "
                    + "for Event ID: " + eventID + " Event Type: " + eventType + " because the Event ID: " + eventID + ""
                    + " is already added for the Event Type: " + eventType + ". But, the Booking Capacity is updated to " + bookingCapacity;
            logger.info(message);

            return message;
        }
    }

    @Override
    public String removeEvent(String eventID, String eventType, String managerID) throws RemoteException
    {
        String message = null;
        if (databaseOttawa.get(eventType).containsKey(eventID))
        {
            databaseOttawa.get(eventType).remove(eventID);
            message = "Operations Successful!. Event Removed in Montreal Server by Manager: " + managerID + " for Event ID: "
                    + eventID + " Event Type: " + eventType;
            logger.info(message);
            return message;
        }
        else
        {
            message = "Operations Unsuccessful!. Event Not Removed in Montreal Server by Manager: " + managerID + " f"
                    + "or Event ID: " + eventID + " Event Type: " + eventType + " because the Event ID: " + eventID
                    + " does not exist";
            logger.info(message);
            return message;
        }
    }

    private static int serverPortSelection(String str)
    {
        str = str.substring(0, 3);
        if (str.equals(TORONTO))
        {
            return TORONTO_SERVER_PORT;
        }
        else if (str.equals(OTTAWA))
        {
            return OTTAWA_SERVER_PORT;
        }
        else if (str.equals(MONTREAL))
        {
            return MONTREAL_SERVER_PORT;
        }
        return 0;
    }

    @Override
    public String listEventAvailability(String eventType, String managerID) throws RemoteException
    {
        //Eg: Seminars - MTLE130519 3, OTWA060519 6, TORM180519 0, MTLE190519 2.
        String message = null;
        StringBuilder returnMessage = new StringBuilder();

        if (managerID.substring(0, 3).equals(MONTREAL))
        {
            logger.info("Requesting other server from Server: " + TORONTO_SERVER_NAME);
            String torrontoEvents = requestToOtherServers(null, null, null, 3, eventType, TORONTO_SERVER_PORT);
            logger.info("Requesting other server from Server: " + OTTAWA_SERVER_NAME);
            String ottawaEvents = requestToOtherServers(null, null, null, 3, eventType, OTTAWA_SERVER_PORT);
            returnMessage.append(torrontoEvents).append("\n\n").append(ottawaEvents).append("\n\n");

        }
        if (managerID.substring(0, 3).equals(TORONTO))
        {
            logger.info("Requesting other server from Server: " + MONTREAL_SERVER_NAME);
            String montrealEvents = requestToOtherServers(null, null, null, 3, eventType, MONTREAL_SERVER_PORT);
            logger.info("Requesting other server from Server: " + OTTAWA_SERVER_NAME);
            String ottawaEvents = requestToOtherServers(null, null, null, 3, eventType, OTTAWA_SERVER_PORT);

            returnMessage.append(ottawaEvents).append("\n\n").append(montrealEvents).append("\n\n");
        }
        if (managerID.substring(0, 3).equals(OTTAWA))
        {
            logger.info("Requesting other server from Server: " + MONTREAL_SERVER_NAME);
            String montrealEvents = requestToOtherServers(null, null, null, 3, eventType, MONTREAL_SERVER_PORT);
            logger.info("Requesting other server from Server: " + TORONTO_SERVER_NAME);
            String torrontoEvents = requestToOtherServers(null, null, null, 3, eventType, TORONTO_SERVER_PORT);

            returnMessage.append(torrontoEvents).append("\n\n").append(montrealEvents).append("\n\n");
        }

        if (!databaseOttawa.get(eventType).isEmpty())
        {
            for (Map.Entry<String, Object> entry : databaseOttawa.get(eventType).entrySet())
            {
                returnMessage.append("EventID: " + entry.getKey() + "| Booking Capacity " + entry.getValue() + "\n");
            }
            message = "Operation Successful, List of events retrieved for Event Type: " + eventType + " by Manager: " + managerID + "in server" + OTTAWA_SERVER_NAME;
            logger.info(message);

            return returnMessage.toString();
        }
        else
        {
            message = "Operation UnSuccessful, List of events not retrieved for Event Type: " + eventType + " by Manager: " + managerID + " in server " + OTTAWA_SERVER_NAME;
            logger.info(message);
            return message;
        }

    }

    @Override
    public String bookEvent(String customerID, String eventID, String eventType) throws RemoteException
    {
        return null;
    }

    @Override
    public String getBookingSchedule(String customerID) throws RemoteException
    {
        String returnMsg = "";
        customerEventsMapping.put("MTLC1234", new HashMap<>());
        customerEventsMapping.get("MTLC1234").put("OTWdyfg1234", 5);
        customerEventsMapping.get("MTL1234").put("OTWdyfg3444", 5);
        customerEventsMapping.get("MTLC1234").put("OTWdyfg1666", 2);
        customerEventsMapping.get("MTLC1234").put("OTWdyfg1777", 5);
        customerEventsMapping.get("MTLC1234").put("OTWdyfg1888", 5);
        logger.log(Level.INFO, "Booking Schedule Requested by {0}", customerID);
        HashMap< String, Integer> customerEvents = customerEventsMapping.get(customerID);
        if (customerEvents != null && !customerEvents.isEmpty())
        {
            for (String event : customerEvents.keySet())
            {
                returnMsg += "\nEvent ID: " + event + "Booking for " + customerEvents.get(event);
            }
            logger.log(Level.INFO, "Operation Sucessful. Records for {0} have been found", customerID);
        }
        else
        {
            logger.log(Level.INFO, "Operation Failure. Records for {0} do not exist.", customerID);
            returnMsg += OPERATIONFAILURE;
        }
        return returnMsg;
    }

    @Override
    public String cancelEvent(String customerID, String eventID) throws RemoteException
    {
        return null;
    }

    public String requestToOtherServers(String userID, String eventID, String bookingCapacity, int serverNumber, String eventType, int serPort)
    {
        int serverPort;
        if (eventID != null)
        {
            serverPort = serverPortSelection(eventID);
        }
        else
        {
            serverPort = serPort;
        }
        String stringServer = Integer.toString(serverNumber);
        DatagramSocket aSocket = null;
        String response = null;
        String userIDName = userID != null ? userID : "Default";
        String eventTypeName = eventType != null ? eventType : "Default";
        String eventIDName = eventID != null ? eventID : "Default";
        String bookingCap = bookingCapacity != null ? bookingCapacity : "Default";

        try
        {
            aSocket = new DatagramSocket();
            String message = userIDName.concat(" ").concat(eventIDName).concat(" ").concat(stringServer).concat(" ").concat(eventTypeName).concat(" ").concat(bookingCap);
            InetAddress host = InetAddress.getByName("localhost");
            DatagramPacket sendPacket = new DatagramPacket(message.getBytes(), message.length(), host, serverPort);
            aSocket.send(sendPacket);
            logger.info("Request send " + sendPacket.getData());
            byte[] receiveBuffer = new byte[1500];
            DatagramPacket recievedPacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            aSocket.receive(recievedPacket);
            response = new String(recievedPacket.getData());
            logger.info("Reply received" + response);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (aSocket != null)
            {
                aSocket.close();
            }
        }
        return response;
    }
}
