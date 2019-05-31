/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ServerInterface;

import java.rmi.Remote;
import java.util.ArrayList;

/**
 *
 * @author Gursimran Singh
 */
public interface ServerInterface extends Remote {
    //Manager Operations
    String addEvent(String eventID, String eventType, int bookingCapacity, String managerID) throws java.rmi.RemoteException;

    String removeEvent(String eventID, String eventType, String managerID) throws java.rmi.RemoteException;
    ArrayList listEventAvailability (String eventType) throws java.rmi.RemoteException;

    //Customer Operations
    String bookEvent (String customerID, String eventID, String eventType) throws java.rmi.RemoteException;
    String getBookingSchedule (String customerID) throws java.rmi.RemoteException;
    String cancelEvent (String customerID, String eventID) throws java.rmi.RemoteException;

}
