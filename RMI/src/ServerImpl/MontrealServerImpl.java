/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ServerImpl;

import ServerInterface.ServerInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 *
 * @author 
 */
public class MontrealServerImpl extends UnicastRemoteObject implements ServerInterface {

    /**
     * @param args the command line arguments
     */
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
        logger = Logger.getLogger(OttawaServerImpl.class.getName());
    }

    @Override
    public String addEvent(String eventID, String eventType, int bookingCapacity) throws RemoteException {
        return null;
    }

    @Override
    public String removeEvent(String eventID, String eventType) throws RemoteException {
        return null;
    }

    @Override
    public String listEventAvailability(String eventType) throws RemoteException {
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
