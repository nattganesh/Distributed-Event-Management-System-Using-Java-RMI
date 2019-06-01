/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Gursimran Singh
 */
public class MontrealServerImpl extends UnicastRemoteObject implements ServerInterface {


    private static HashMap<String, HashMap< String, Object>> databaseMontreal = new HashMap<>();
    private static final  HashMap<String, HashMap< String, Integer>> customerEventsMapping = new HashMap<>();
    private static  Logger logger;
    {

        //item1
        databaseMontreal.put(CONFERENCE, new HashMap<>());
        databaseMontreal.get(CONFERENCE).put("OTWA100519", "5");
        //item2
        databaseMontreal.put(SEMINAR, new HashMap<>());
        databaseMontreal.get(SEMINAR).put("TORM100519", "8");

        //item3
        databaseMontreal.put(TRADESHOW, new HashMap<>());
        databaseMontreal.get(TRADESHOW).put("MTLE100519", "9");

        //item4
        databaseMontreal.put(CONFERENCE, new HashMap<>());
        databaseMontreal.get(CONFERENCE).put("MTLE100519", "3");
        databaseMontreal.put(CONFERENCE, new HashMap<>());
        databaseMontreal.get(CONFERENCE).put("MTLA12345", "12");

        //item5
        databaseMontreal.put(SEMINAR, new HashMap<>());
        databaseMontreal.get(SEMINAR).put("TORM100519", "2");

        //item6
        databaseMontreal.put(TRADESHOW, new HashMap<>());
        databaseMontreal.get(TRADESHOW).put("OTWA100519", "9");

    }

    public MontrealServerImpl() throws RemoteException {
        super();
        logger = Logger.getLogger(MontrealServerImpl.class.getName());
        try
        {
            addFileHandler(logger, "Montreal_Server");
        }
        catch (SecurityException | IOException ex)
        {
            Logger.getLogger(MontrealServerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static int serverPortSelection(String str) {
        str = str.substring(0, 3);
        if (str.equals(TORONTO)) {
            return TORONTO_SERVER_PORT;
        } else if (str.equals(OTTAWA)) {
            return OTTAWA_SERVER_PORT;
        } else if (str.equals(MONTREAL)) {
            return MONTREAL_SERVER_PORT;
        }
        return 0;
    }

    @Override
    public String addEvent(String eventID, String eventType, String bookingCapacity, String managerID) throws RemoteException {
        String message = null;
        logger.info("Received request from " + managerID + " to add an event with event id " + eventID + " , Event Type" + eventType +
                " & Booking Capacity " + bookingCapacity);
        if (!databaseMontreal.get(eventType).containsKey(eventID))
        {
            databaseMontreal.get(eventType).put(eventID,bookingCapacity);
            message = "Operations Successful!. Event Added in Montreal Server by Manager: " + managerID + " for Event ID: "
                    + eventID + " Event Type: " + eventType + " Booking Capacity: " + bookingCapacity;
            logger.info(message);

            return message;
        } else {
            databaseMontreal.get(eventType).replace(eventID, bookingCapacity);
            message = "Operations Unsuccessful!. Event Not Added in Montreal Server by Manager: " + managerID + " f" +
                    "or Event ID: " + eventID + " Event Type: " + eventType + " because the Event ID: " + eventID + "" +
                    " is already added for the Event Type: " + eventType + ". But, the Booking Capacity is updated to " + bookingCapacity;
            logger.info(message);

            return message;
        }

    }

    @Override
    public String removeEvent(String eventID, String eventType, String managerID) throws RemoteException {
        String message = null;
        if (databaseMontreal.get(eventType).containsKey(eventID)) {
            databaseMontreal.get(eventType).remove(eventID);
            message = "Operations Successful!. Event Removed in Montreal Server by Manager: " + managerID + " for Event ID: "
                    + eventID + " Event Type: " + eventType;
            logger.info(message);
            return message;
        } else {
            message = "Operations Unsuccessful!. Event Not Removed in Montreal Server by Manager: " + managerID + " f" +
                    "or Event ID: " + eventID + " Event Type: " + eventType + " because the Event ID: " + eventID +
                    " does not exist";
            logger.info(message);
            return message;
        }

    }

    @Override
    public String listEventAvailability(String eventType, String managerID) throws RemoteException {
        //Eg: Seminars - MTLE130519 3, OTWA060519 6, TORM180519 0, MTLE190519 2.
        String message = null;
        String returnMessage = null;
        ArrayList<String> events = new ArrayList<>();
        if (!databaseMontreal.get(eventType).isEmpty()) {
            for (Map.Entry<String, Object> entry : databaseMontreal.get(eventType).entrySet()) {
                events.add("EventID: " + entry.getKey() + " Booking Capacity " + entry.getValue());
            }
            for (String str : events) {
                returnMessage += str + ",";
            }
            message = "Operation Successful, List of events retrieved for Event Type: " + eventType + " by Manager: " + managerID;
            logger.info(message);

            return returnMessage;
        } else {
            message = "Operation UnSuccessful, List of events not retrieved for Event Type: " + eventType + " by Manager: " + managerID;

        }


        return null;
    }

    @Override
    public String bookEvent(String customerID, String eventID, String eventType) throws RemoteException {
        return null;
    }

    @Override
    public String getBookingSchedule(String customerID) throws RemoteException
    {
        String returnMsg = "";
        customerEventsMapping.put("MTLC1234", new HashMap<>());
        customerEventsMapping.get("MTLC1234").put("MTLdyfg1234",5);
        customerEventsMapping.get("MTLC1234").put("MTLdyfg3444",5);
        customerEventsMapping.get("MTLC1234").put("MTLdyfg1666",2);
        customerEventsMapping.get("MTLC1234").put("MTLdyfg1777",5);
        customerEventsMapping.get("MTLC1234").put("MTLdyfg1888",5);
        logger.info("Booking Schedule Requested by " + customerID);
        HashMap< String, Integer> customerEvents = customerEventsMapping.get(customerID);
        if (customerEvents != null && !customerEvents.isEmpty())
        {
            for (String event : customerEvents.keySet())
            {
                returnMsg += "\nEvent ID: " + event + "Booking for " + customerEvents.get(event);
            }
            logger.info("Operation Sucessful. Records for " + customerID + " have been found");
        }
        else
        {
            logger.info("Operation Failure. Records for " + customerID + " do not exist.");
            returnMsg += OPERATIONFAILURE;
        }
        return returnMsg;
    }

    @Override
    public String cancelEvent(String customerID, String eventID) throws RemoteException {
        return null;
    }


    public String requestToOtherServers(String userID, String eventID, Object bookingCapacity, int serverNumber, String eventType, int serPort) {
        logger.info("Requesting other server from montreal");
        int serverPort;
        if (eventID != null) {
            serverPort = serverPortSelection(eventID);
        } else {
            serverPort = serPort;
        }
        String stringServer = Integer.toString(serverNumber);
        DatagramSocket aSocket = null;
        String response = null;
        String eventTypeName = eventType != null ? eventType : "Default";
        String eventIDName = eventID != null ? eventID : "Default";
        String bookingCap = bookingCapacity.toString();

        try {
            aSocket = new DatagramSocket();
            String message = userID.concat(" ").concat(eventIDName).concat(" ").concat(stringServer).concat(" ").concat(eventTypeName).concat(bookingCap);
            InetAddress host = InetAddress.getByName("localhost");
            DatagramPacket sendPacket = new DatagramPacket(message.getBytes(), message.length(), host, serverPort);
            aSocket.send(sendPacket);
            logger.info("Request send " + sendPacket.getData());
            byte[] receiveBuffer = new byte[1500];
            DatagramPacket recievedPacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            aSocket.receive(recievedPacket);
            response = new String(recievedPacket.getData());
            logger.info("Reply received" + response);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (aSocket != null)
                aSocket.close();
        }
        return response;
    }
    
}
