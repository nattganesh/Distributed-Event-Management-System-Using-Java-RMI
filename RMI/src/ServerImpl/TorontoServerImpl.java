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
public class TorontoServerImpl extends UnicastRemoteObject implements ServerInterface {

    /**
     * @param args the command line arguments
     */

    private static HashMap<String, HashMap< String, Object>> databaseToronto = new HashMap<>();
    private static  HashMap<String, ArrayList<String>> customerEventsMapping = new HashMap<>();
    private static  Logger logger;
    {

        //item1
        databaseToronto.put(CommonUtils.CONFERENCE, new HashMap<>());
        databaseToronto.get(CommonUtils.CONFERENCE).put("TORA100519", "52");
        databaseToronto.get(CommonUtils.CONFERENCE).put("TORE100519", "3");
        databaseToronto.get(CommonUtils.CONFERENCE).put("TORA12345", "12");

        //item2
        databaseToronto.put(CommonUtils.SEMINAR, new HashMap<>());
        databaseToronto.get(CommonUtils.SEMINAR).put("TORE100519", "8");
        databaseToronto.get(CommonUtils.SEMINAR).put("TORM100519", "2");



        //item6
        databaseToronto.put(CommonUtils.TRADESHOW, new HashMap<>());
        databaseToronto.get(CommonUtils.TRADESHOW).put("TORA100519", "9");
        databaseToronto.get(CommonUtils.TRADESHOW).put("TORE100519", "9");

    }

    public TorontoServerImpl() throws RemoteException {
        super();
        logger = Logger.getLogger(TorontoServerImpl.class.getName());
        try
        {
            CommonUtils.addFileHandler(logger, "Toronto_Server");
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
        if (!databaseToronto.get(eventType).isEmpty()) {
            for (Map.Entry<String, Object> entry : databaseToronto.get(eventType).entrySet()) {
                returnMessage.append("EventID: " + entry.getKey() + "| Booking Capacity " + entry.getValue() + "\n");
            }
            message = "Operation Successful, List of events retrieved for Event Type: " + eventType + " by Manager: " + managerID + "in server" + CommonUtils.TORONTO_SERVER_NAME;
            logger.info(message);

            return returnMessage.toString();
        } else {
            message = "Operation UnSuccessful, List of events not retrieved for Event Type: " + eventType + " by Manager: " + managerID + " in server " + CommonUtils.TORONTO_SERVER_NAME;
            logger.info(message);
            return message;
        }


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