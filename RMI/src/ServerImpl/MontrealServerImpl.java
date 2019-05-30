/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ServerImpl;

import CommonUtils.CommonUtils;
import ServerInterface.ServerInterface;
import java.io.IOException;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
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
        databaseMontreal.put("Conferences", new HashMap<>());
        databaseMontreal.get("Conferences").put("OTWA100519", "5");
        //item2
        databaseMontreal.put("Seminars", new HashMap<>());
        databaseMontreal.get("Seminars").put("TORM100519", "8");

        //item3
        databaseMontreal.put("TradeShows", new HashMap<>());
        databaseMontreal.get("TradeShows").put("MTLE100519", "9");

        //item4
        databaseMontreal.put("Conferences", new HashMap<>());
        databaseMontreal.get("Conferences").put("MTLE100519", "3");

        //item5
        databaseMontreal.put("Seminars", new HashMap<>());
        databaseMontreal.get("Seminars").put("TORM100519", "2");

        //item6
        databaseMontreal.put("TradeShows", new HashMap<>());
        databaseMontreal.get("TradeShows").put("OTWA100519", "9");

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

    @Override
    public String addEvent(String eventID, String eventType, int bookingCapacity) throws RemoteException {
        System.out.println("Add Event Called in Montreal");
        if (!databaseMontreal.get(eventType).containsKey(eventID))
        {
            databaseMontreal.get(eventType).put(eventID,bookingCapacity);
            logger.info("Add Event Called in Montreal Server for Event ID: " + eventID + " Event Type: " + eventType + " Booking Capacity: " + bookingCapacity);
           // System.out.println("Event added "+eventID);
        }
        
        return eventID+"BALLE";
    }

    @Override
    public String removeEvent(String eventID, String eventType) throws RemoteException {
        return null;
    }

    @Override
    public ArrayList listEventAvailability(String eventType) throws RemoteException {
        return null;
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

    
}
