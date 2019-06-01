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
 * @author 
 */
public class OttawaServerImpl extends UnicastRemoteObject implements ServerInterface {



    private static HashMap<String, HashMap< String, Object>> databaseOttawa = new HashMap<>();
    private static  HashMap<String, ArrayList<String>> customerEventsMapping = new HashMap<>();
    private static  Logger logger;
    {

        //item1
        databaseOttawa.put(CommonUtils.CONFERENCE, new HashMap<>());
        databaseOttawa.get(CommonUtils.CONFERENCE).put("OTWM100519", "5");
        databaseOttawa.get(CommonUtils.CONFERENCE).put("OTWA100519", "3");
        databaseOttawa.get(CommonUtils.CONFERENCE).put("OTWE12345", "12");

        //item2
        databaseOttawa.put(CommonUtils.SEMINAR, new HashMap<>());
        databaseOttawa.get(CommonUtils.SEMINAR).put("OTWA100519", "8");
        databaseOttawa.get(CommonUtils.SEMINAR).put("OTWM100519", "2");



        //item6
        databaseOttawa.put(CommonUtils.TRADESHOW, new HashMap<>());
        databaseOttawa.get(CommonUtils.TRADESHOW).put("OTWA100519", "9");
        databaseOttawa.get(CommonUtils.TRADESHOW).put("OTWE100519", "9");

    }

    public OttawaServerImpl() throws RemoteException {
        super();
        logger = Logger.getLogger(OttawaServerImpl.class.getName());
        try
        {
            CommonUtils.addFileHandler(logger, "Ottawa_Server");
        }
        catch (SecurityException | IOException ex)
        {
            Logger.getLogger(MontrealServerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public String addEvent(String eventID, String eventType, String bookingCapacity, String managerID) throws RemoteException {
        return null;
    }

    @Override
    public String removeEvent(String eventID, String eventType, String managerID) throws RemoteException {
        return null;
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

    @Override
    public String listEventAvailability(String eventType, String managerID) throws RemoteException {
        //Eg: Seminars - MTLE130519 3, OTWA060519 6, TORM180519 0, MTLE190519 2.
        String message = null;
        StringBuilder returnMessage = new StringBuilder();

        if (managerID.substring(0, 3).equals(CommonUtils.MONTREAL)) {
            String torrontoEvents = requestToOtherServers(null, null, null, 3, eventType, CommonUtils.TORONTO_SERVER_PORT);
            String ottawaEvents = requestToOtherServers(null, null, null, 3, eventType, CommonUtils.OTTAWA_SERVER_PORT);
            returnMessage.append(torrontoEvents).append("\n\n").append(ottawaEvents).append("\n\n");

        }
        if (managerID.substring(0, 3).equals(CommonUtils.TORONTO)) {
            String montrealEvents = requestToOtherServers(null, null, null, 3, eventType, CommonUtils.MONTREAL_SERVER_PORT);
            String ottawaEvents = requestToOtherServers(null, null, null, 3, eventType, CommonUtils.OTTAWA_SERVER_PORT);

            returnMessage.append(ottawaEvents).append("\n\n").append(montrealEvents).append("\n\n");
        }
        if (managerID.substring(0, 3).equals(CommonUtils.OTTAWA)) {
            String montrealEvents = requestToOtherServers(null, null, null, 3, eventType, CommonUtils.MONTREAL_SERVER_PORT);
            String torrontoEvents = requestToOtherServers(null, null, null, 3, eventType, CommonUtils.TORONTO_SERVER_PORT);

            returnMessage.append(torrontoEvents).append("\n\n").append(montrealEvents).append("\n\n");
        }

        if (!databaseOttawa.get(eventType).isEmpty()) {
            for (Map.Entry<String, Object> entry : databaseOttawa.get(eventType).entrySet()) {
                returnMessage.append("EventID: " + entry.getKey() + "| Booking Capacity " + entry.getValue() + "\n");
            }
            message = "Operation Successful, List of events retrieved for Event Type: " + eventType + " by Manager: " + managerID + "in server" + CommonUtils.OTTAWA_SERVER_NAME;
            logger.info(message);

            return returnMessage.toString();
        } else {
            message = "Operation UnSuccessful, List of events not retrieved for Event Type: " + eventType + " by Manager: " + managerID + " in server " + CommonUtils.OTTAWA_SERVER_NAME;
            logger.info(message);
            return message;
        }


    }

    public String requestToOtherServers(String userID, String eventID, String bookingCapacity, int serverNumber, String eventType, int serPort) {
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
