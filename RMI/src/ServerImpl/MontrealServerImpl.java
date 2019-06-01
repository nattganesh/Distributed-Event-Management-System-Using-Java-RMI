/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ServerImpl;

import CommonUtils.CommonUtils;
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
    private static  HashMap<String, ArrayList<String>> customerEventsMapping = new HashMap<>();
    private static  Logger logger;
    {

        //item1
        databaseMontreal.put(CommonUtils.CONFERENCE, new HashMap<>());
        databaseMontreal.get(CommonUtils.CONFERENCE).put("MTLA100519", "5");
        databaseMontreal.get(CommonUtils.CONFERENCE).put("MTLE100519", "3");
        databaseMontreal.get(CommonUtils.CONFERENCE).put("MTLA12345", "12");

        //item2
        databaseMontreal.put(CommonUtils.SEMINAR, new HashMap<>());
        databaseMontreal.get(CommonUtils.SEMINAR).put("MTLM100519", "8");
        databaseMontreal.get(CommonUtils.SEMINAR).put("MTLA100519", "2");



        //item6
        databaseMontreal.put(CommonUtils.TRADESHOW, new HashMap<>());
        databaseMontreal.get(CommonUtils.TRADESHOW).put("MTLA100519", "9");
        databaseMontreal.get(CommonUtils.TRADESHOW).put("MTLE100519", "9");

    }

    public MontrealServerImpl() throws RemoteException {
        super();
        logger = Logger.getLogger(MontrealServerImpl.class.getName());
        try
        {
            CommonUtils.addFileHandler(logger, "Montreal_Server");
        }
        catch (SecurityException | IOException ex)
        {
            Logger.getLogger(MontrealServerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static int serverPortSelection(String str) {
        str = str.substring(0, 3);
        if (str.equals(CommonUtils.TORONTO)) {
            return CommonUtils.TORONTO_SERVER_PORT;
        } else if (str.equals(CommonUtils.OTTAWA)) {
            return CommonUtils.OTTAWA_SERVER_PORT;
        } else if (str.equals(CommonUtils.MONTREAL)) {
            return CommonUtils.MONTREAL_SERVER_PORT;
        }
        return 0;
    }

    @Override
    public String addEvent(String eventID, String eventType, String bookingCapacity, String managerID) throws RemoteException {
        String message = null;
        logger.info("Received request to add an event with event id " + eventID + " , Event Type" + eventType +
                " & Booking Capacity " + bookingCapacity);
        if (!databaseMontreal.get(eventType).containsKey(eventID))
        {
            databaseMontreal.get(eventType).put(eventID,bookingCapacity);
            message = "Operations Successful!. Event Added in Montreal Server for Event ID: "
                    + eventID + " Event Type: " + eventType + " Booking Capacity: " + bookingCapacity;
            logger.info(message);

            return message;
        } else {
            databaseMontreal.get(eventType).replace(eventID, bookingCapacity);
            message = "Operations Unsuccessful!. Event Not Added in Montreal Server " +
                    "for Event ID: " + eventID + " Event Type: " + eventType + " because the Event ID: " + eventID + "" +
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
        StringBuilder returnMessage = new StringBuilder();
        if (managerID.substring(0, 3).equals(CommonUtils.MONTREAL)) {
            logger.info("Requesting other server from Server: " + CommonUtils.TORONTO_SERVER_NAME);
            String torrontoEvents = requestToOtherServers(null, null, null, 3, eventType, CommonUtils.TORONTO_SERVER_PORT);
            logger.info("Requesting other server from Server: " + CommonUtils.OTTAWA_SERVER_NAME);
            String ottawaEvents = requestToOtherServers(null, null, null, 3, eventType, CommonUtils.OTTAWA_SERVER_PORT);
            returnMessage.append(torrontoEvents).append("\n\n").append(ottawaEvents).append("\n\n");

        }
        if (managerID.substring(0, 3).equals(CommonUtils.TORONTO)) {
            logger.info("Requesting other server from Server: " + CommonUtils.MONTREAL_SERVER_NAME);
            String montrealEvents = requestToOtherServers(null, null, null, 3, eventType, CommonUtils.MONTREAL_SERVER_PORT);
            logger.info("Requesting other server from Server: " + CommonUtils.OTTAWA_SERVER_NAME);
            String ottawaEvents = requestToOtherServers(null, null, null, 3, eventType, CommonUtils.OTTAWA_SERVER_PORT);

            returnMessage.append(ottawaEvents).append("\n\n").append(montrealEvents).append("\n\n");
        }
        if (managerID.substring(0, 3).equals(CommonUtils.OTTAWA)) {
            logger.info("Requesting other server from Server: " + CommonUtils.MONTREAL_SERVER_NAME);
            String montrealEvents = requestToOtherServers(null, null, null, 3, eventType, CommonUtils.MONTREAL_SERVER_PORT);
            logger.info("Requesting other server from Server: " + CommonUtils.TORONTO_SERVER_NAME);
            String torrontoEvents = requestToOtherServers(null, null, null, 3, eventType, CommonUtils.TORONTO_SERVER_PORT);

            returnMessage.append(torrontoEvents).append("\n\n").append(montrealEvents).append("\n\n");
        }


        if (!databaseMontreal.get(eventType).isEmpty()) {
            for (Map.Entry<String, Object> entry : databaseMontreal.get(eventType).entrySet()) {
                returnMessage.append("EventID: " + entry.getKey() + "| Booking Capacity " + entry.getValue() + "\n");
            }
            message = "Operation Successful, List of events retrieved for Event Type: " + eventType + " by Manager: " + managerID + "in server";
            logger.info(message);

            return returnMessage.toString();
        } else {
            message = "Operation UnSuccessful, List of events not retrieved for Event Type: " + eventType + " by Manager: " + managerID + " in server ";
            logger.info(message);
            return message;
        }



    }

    @Override
    public String bookEvent(String customerID, String eventID, String eventType) throws RemoteException {
        return null;
    }

    @Override
    public String getBookingSchedule(String customerID) throws RemoteException {
        return null;
    }

    @Override
    public String cancelEvent(String customerID, String eventID) throws RemoteException {
        return null;
    }


    public String requestToOtherServers(String userID, String eventID, String bookingCapacity, int serverNumber, String eventType, int serPort) {
        int serverPort;
        if (eventID != null) {
            serverPort = serverPortSelection(eventID);
        } else {
            serverPort = serPort;
        }
        String stringServer = Integer.toString(serverNumber);
        DatagramSocket aSocket = null;
        String response = null;
        String userIDName = userID != null ? userID : "Default";
        String eventTypeName = eventType != null ? eventType : "Default";
        String eventIDName = eventID != null ? eventID : "Default";
        String bookingCap = bookingCapacity != null ? bookingCapacity : "Default";

        try {
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
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (aSocket != null)
                aSocket.close();
        }
        return response;
    }
    
}
