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
public class TorontoServerImpl extends UnicastRemoteObject implements ServerInterface {

    /**
     * @param args the command line arguments
     */

    private static HashMap<String, HashMap< String, Object>> databaseToronto = new HashMap<>();
    private static  HashMap<String, ArrayList<String>> customerEventsMapping = new HashMap<>();
    private static  Logger logger;
    {

        //item1
        databaseToronto.put("Conferences", new HashMap<>());
        databaseToronto.get("Conferences").put("OTWA100519", "5");
        //item2
        databaseToronto.put("Seminars", new HashMap<>());
        databaseToronto.get("Seminars").put("TORM100519", "8");

        //item3
        databaseToronto.put("TradeShows", new HashMap<>());
        databaseToronto.get("TradeShows").put("MTLE100519", "9");

        //item4
        databaseToronto.put("Conferences", new HashMap<>());
        databaseToronto.get("Conferences").put("MTLE100519", "3");

        //item5
        databaseToronto.put("Seminars", new HashMap<>());
        databaseToronto.get("Seminars").put("TORM100519", "2");

        //item6
        databaseToronto.put("TradeShows", new HashMap<>());
        databaseToronto.get("TradeShows").put("OTWA100519", "9");

    }

    public TorontoServerImpl() throws RemoteException {
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