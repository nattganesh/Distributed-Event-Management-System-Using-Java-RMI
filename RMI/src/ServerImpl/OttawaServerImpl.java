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
 * @author 
 */
public class OttawaServerImpl extends UnicastRemoteObject implements ServerInterface {



    private static HashMap<String, HashMap< String, Object>> databaseOttawa = new HashMap<>();
    private static  HashMap<String, ArrayList<String>> customerEventsMapping = new HashMap<>();
    private static  Logger logger;
    {

        //item1
        databaseOttawa.put("Conferences", new HashMap<>());
        databaseOttawa.get("Conferences").put("OTWA100519", "5");
        //item2
        databaseOttawa.put("Seminars", new HashMap<>());
        databaseOttawa.get("Seminars").put("TORM100519", "8");

        //item3
        databaseOttawa.put("TradeShows", new HashMap<>());
        databaseOttawa.get("TradeShows").put("MTLE100519", "9");

        //item4
        databaseOttawa.put("Conferences", new HashMap<>());
        databaseOttawa.get("Conferences").put("MTLE100519", "3");

        //item5
        databaseOttawa.put("Seminars", new HashMap<>());
        databaseOttawa.get("Seminars").put("TORM100519", "2");

        //item6
        databaseOttawa.put("TradeShows", new HashMap<>());
        databaseOttawa.get("TradeShows").put("OTWA100519", "9");

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
    public String addEvent(String eventID, String eventType, int bookingCapacity, String managerID) throws RemoteException {
        return null;
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
