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
public class TorontoServerImpl extends UnicastRemoteObject implements ServerInterface {

    private static HashMap<String, HashMap< String, String>> databaseToronto = new HashMap<>();
    private static HashMap<String, HashMap<String, Integer>> customerEventsMapping = new HashMap<>();
    private static Logger logger;
    {

        //item1
        databaseToronto.put(CONFERENCE, new HashMap<>());
        databaseToronto.get(CONFERENCE).put("TORA100519", "52");
        databaseToronto.get(CONFERENCE).put("TORE100519", "3");
        databaseToronto.get(CONFERENCE).put("TORA123452", "12");

        //item2
        databaseToronto.put(SEMINAR, new HashMap<>());
        databaseToronto.get(SEMINAR).put("TORE100519", "8");
        databaseToronto.get(SEMINAR).put("TORM100519", "2");

        //item6
        databaseToronto.put(TRADESHOW, new HashMap<>());
        databaseToronto.get(TRADESHOW).put("TORA100519", "9");
        databaseToronto.get(TRADESHOW).put("TORE100519", "9");

        
        //to test
//                customerEventsMapping.put("MTLC1234", new HashMap<>());
//        customerEventsMapping.get("MTLC1234").put("TORdyfg1234", 5);
//        customerEventsMapping.get("MTLC1234").put("TORdyfg3444", 5);
//        customerEventsMapping.get("MTLC1234").put("TORdyfg1666", 2);
//        customerEventsMapping.get("MTLC1234").put("TORdyfg1777", 5);
//        customerEventsMapping.get("MTLC1234").put("TORdyfg1888", 5);
    }

    public TorontoServerImpl() throws RemoteException
    {
        super();
        logger = Logger.getLogger(TorontoServerImpl.class.getName());
        try
        {
            addFileHandler(logger, "Toronto_Server");
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
        logger.log(Level.INFO, "Received request to add an event with event id {0} , Event Type{1} & Booking Capacity {2}", new Object[]{eventID, eventType, bookingCapacity});
        if (!databaseToronto.get(eventType).containsKey(eventID))
        {
            databaseToronto.get(eventType).put(eventID, bookingCapacity);
            message = "Operations Successful!. Event Added in Toronto Server for Event ID: "
                    + eventID + " Event Type: " + eventType + " Booking Capacity: " + bookingCapacity;
            logger.info(message);

            return message;
        }
        else
        {
            databaseToronto.get(eventType).replace(eventID, bookingCapacity);
            message = "Operations Unsuccessful!. Event Not Added in Toronto Server "
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
        if (databaseToronto.get(eventType).containsKey(eventID))
        {
            databaseToronto.get(eventType).remove(eventID);
            message = "Operations Successful!. Event Removed in Toronto Server by Manager: " + managerID + " for Event ID: "
                    + eventID + " Event Type: " + eventType;
            logger.info(message);
            return message;
        }
        else
        {
            message = "Operations Unsuccessful!. Event Not Removed in Toronto Server by Manager: " + managerID + " f"
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

        if (!databaseToronto.get(eventType).isEmpty())
        {
            for (Map.Entry<String, String> entry : databaseToronto.get(eventType).entrySet())
            {
                returnMessage.append("EventID: " + entry.getKey() + "| Booking Capacity " + entry.getValue() + "\n");
            }
            message = "Operation Successful, List of events retrieved for Event Type: " + eventType + " by Manager: " + managerID + "in server" + TORONTO_SERVER_NAME;
            logger.info(message);

            return returnMessage.toString();
        }
        else
        {
            message = "Operation UnSuccessful, List of events not retrieved for Event Type: " + eventType + " by Manager: " + managerID + " in server " + TORONTO_SERVER_NAME;
            logger.info(message);
            return message;
        }

    }

    @Override
    public String bookEvent(String customerID, String eventID, String eventType, String bookingAmount) throws RemoteException
    {
//        if(customerID.substring(0, 3).equals(TORONTO)){}
        if(eventID.substring(0, 3).equals(TORONTO))
        {
            logger.log(Level.INFO, "Book Event Requested by {0} for Event Type {1} with Event ID {2}", new Object[]{customerID, eventType ,eventID});
            HashMap< String, String> event = databaseToronto.get(eventType);
            if(event.containsKey(eventID))
            {
                if (customerEventsMapping.containsKey(customerID))
                {
                    if (customerEventsMapping.get(customerID).containsKey(eventID))
                    {
                        logger.log(Level.INFO, "Operation Unsuccessful, Book Event Requested by {0} for Event Type {1} with Event ID {2} cannot be booked. Customer already booked for this event.", new Object[]{customerID, eventType ,eventID});
                        return "Operation Unsuccessful, Book Event Requested by "+ customerID + " for Event Type "+ eventType + " with Event ID "+ eventID + " cannot be booked. Customer already booked for this event.";
                    }
                    else if (!customerID.substring(0, 3).equals(TORONTO))
                    {
                        int customerBookingsCurrent = Integer.parseInt(this.nonOriginCustomerBooking(customerID));
                        int customerBookingsOther = customerID.substring(0, 3).equals(OTTAWA) ? Integer.parseInt(requestToOtherServers(customerID, null, null, 7, null, MONTREAL_SERVER_PORT).trim()) 
                                                                                                : Integer.parseInt(requestToOtherServers(customerID, null, null, 7, null, OTTAWA_SERVER_PORT).trim());

                        if (customerBookingsCurrent + customerBookingsOther >= 3)
                        {
                            logger.log(Level.INFO, "Operation Unsuccessful, Book Event Requested by {0} for Event Type {1} with Event ID {2} cannot be booked. Customer can book as many events in his/her own city, but only at most 3 events from other cities overall in a month", new Object[]
                            {
                                customerID, eventType, eventID
                            });
                            return "Operation Unsuccessful, Book Event Requested by " + customerID + " for Event Type " + eventType + " with Event ID " + eventID + " cannot be booked. Customer can book as many events in his/her own\n"
                                    + "city, but only at most 3 events from other cities overall in a month";
                        }
                    }
                }
                int bookingLeft = Integer.parseInt(event.get(eventID).trim());
                int bookingRequested = Integer.parseInt(bookingAmount);
                if (bookingLeft >= bookingRequested)
                {
                    bookingLeft -= bookingRequested;
                    event.put(eventID, "" + bookingLeft);
                    
                    if(customerEventsMapping.containsKey(customerID)) 
                    {
                        customerEventsMapping.get(customerID).put(eventID, bookingRequested);
                    }
                    else
                    {
                        customerEventsMapping.put(customerID, new HashMap<>());
                        customerEventsMapping.get(customerID).put(eventID, bookingRequested);
                    }

                    logger.log(Level.INFO, "Operation Successful, Book Event Requested by {0} for Event Type {1} with Event ID {2} has been booked.", new Object[]
                    {
                        customerID, eventType, eventID
                    });
                    return "Operation Successful, Book Event Requested by " + customerID + " for Event Type " + eventType + " with Event ID " + eventID + " has been booked.";
                }
                else
                {
                    logger.log(Level.INFO, "Operation Unsuccessful, Book Event Requested by {0} for Event Type {1} with Event ID {2} cannot be booked. Event Capacity < Booking Capacity Requested", new Object[]
                    {
                        customerID, eventType, eventID
                    });
                    return "Operation Unsuccessful, Book Event Requested by " + customerID + " for Event Type " + eventType + " with Event ID " + eventID + " cannot be booked. Event Capacity < Booking Capacity Requested";
                }
            }
            else
            {
                logger.log(Level.INFO, "Operation Unsuccessful, Book Event Requested by {0} for Event Type {1} with Event ID {2} cannot be booked. Evant Does Not Exist.", new Object[]
                {
                    customerID, eventType, eventID
                });
                return "Operation Unsuccessful, Book Event Requested by " + customerID + " for Event Type " + eventType + " with Event ID " + eventID + " cannot be booked. Evant Does Not Exist.";
            }
        }
        if(eventID.substring(0, 3).equals(MONTREAL)){return requestToOtherServers(customerID, eventID, bookingAmount, 4, eventType, MONTREAL_SERVER_PORT);}
        if(eventID.substring(0, 3).equals(OTTAWA)){return requestToOtherServers(customerID, eventID, bookingAmount, 4, eventType, OTTAWA_SERVER_PORT);}
        return "";
    }
    
    @Override
    public String nonOriginCustomerBooking(String customerID)
    {
        if (customerEventsMapping.containsKey(customerID))
        {
            return "" + customerEventsMapping.get(customerID).keySet().size();
        }
        return "" + 0;
    }
    
    @Override
    public String getBookingSchedule(String customerID) throws RemoteException
    {
        String returnMsg = "";
        logger.log(Level.INFO, "Booking Schedule Requested by {0}", customerID);
        HashMap< String, Integer> customerEvents = customerEventsMapping.get(customerID);
        
        if(customerID.substring(0, 3).equals(TORONTO))
        {
            returnMsg += requestToOtherServers(customerID, null, null, 5, null, OTTAWA_SERVER_PORT);
            returnMsg += requestToOtherServers(customerID, null, null, 5, null, MONTREAL_SERVER_PORT);           
        }
        if (customerEvents != null && !customerEvents.isEmpty())
        {
            for (String event : customerEvents.keySet())
            {
                returnMsg += "\nEvent ID: " + event + "Booking for " + customerEvents.get(event);
            }
            logger.log(Level.INFO, "Operation Sucessful. Records for {0} have been found", customerID);
        }
        if (returnMsg.trim().equals(""))
        {
            logger.log(Level.INFO, "Records for {0} do not exist.", customerID);
            if(customerID.substring(0, 3).equals(TORONTO)) returnMsg += OPERATIONFAILURE;
        }
        return returnMsg;
    }

    @Override
    public String cancelEvent(String customerID, String eventID) throws RemoteException
    {
        if(eventID.substring(0, 3).equals(TORONTO))
        {
            if(customerEventsMapping.containsKey(customerID))
            {
                if(customerEventsMapping.get(customerID).containsKey(eventID))
                {
                    Integer bookValue = customerEventsMapping.get(customerID).remove(eventID);
                    Integer currentValue = 0;
                    Integer sum = 0;
                    
                    if(databaseToronto.get(CONFERENCE).containsKey(eventID))
                    {
                        currentValue = Integer.parseInt(databaseToronto.get(CONFERENCE).get(eventID));
                        sum = currentValue + bookValue;
                        databaseToronto.get(CONFERENCE).put(eventID, sum.toString());
                    }
                    else if(databaseToronto.get(SEMINAR).containsKey(eventID))
                    {
                        currentValue = Integer.parseInt(databaseToronto.get(SEMINAR).get(eventID));
                        sum = currentValue + bookValue;
                        databaseToronto.get(SEMINAR).put(eventID, sum.toString());
                    }
                    else if(databaseToronto.get(TRADESHOW).containsKey(eventID))
                    {
                        currentValue = Integer.parseInt(databaseToronto.get(TRADESHOW).get(eventID));
                        sum = currentValue + bookValue;
                        databaseToronto.get(TRADESHOW).put(eventID, sum.toString());
                    }
                    logger.log(Level.INFO, "This event has been removed from customer record.");
                    return "This event has been removed from customer record.";
                }
            }
            logger.log(Level.INFO, "This event does not exist in customer record.");
            return "This event does not exist in customer record.";
        }
        else if(eventID.substring(0, 3).equals(MONTREAL))
        {
            return requestToOtherServers(customerID, eventID, null, 6, null, MONTREAL_SERVER_PORT);
        }
        else if(eventID.substring(0, 3).equals(OTTAWA))
        {
            return requestToOtherServers(customerID, eventID, null, 6, null, OTTAWA_SERVER_PORT);
        }
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
