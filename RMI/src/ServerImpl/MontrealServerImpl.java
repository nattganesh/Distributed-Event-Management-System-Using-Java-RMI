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
public class MontrealServerImpl extends UnicastRemoteObject implements ServerInterface {

    private static HashMap<String, HashMap< String, String>> databaseMontreal = new HashMap<>();
    private static final HashMap<String, HashMap< String, Integer>> customerEventsMapping = new HashMap<>();
    private static Logger logger;    
    {
        //item1
        databaseMontreal.put(CONFERENCE, new HashMap<>());
        databaseMontreal.get(CONFERENCE).put("MTLM030619", "50");
        databaseMontreal.get(CONFERENCE).put("MTLE030619", "50");
        databaseMontreal.get(CONFERENCE).put("MTLA030619", "50");

        //item2
        databaseMontreal.put(SEMINAR, new HashMap<>());
        databaseMontreal.get(CONFERENCE).put("MTLM050619", "50");
        databaseMontreal.get(CONFERENCE).put("MTLE050619", "50");
        databaseMontreal.get(CONFERENCE).put("MTLA050619", "50");

        //item6
        databaseMontreal.put(TRADESHOW, new HashMap<>());
        databaseMontreal.get(CONFERENCE).put("MTLM090619", "50");
        databaseMontreal.get(CONFERENCE).put("MTLE090619", "50");
        databaseMontreal.get(CONFERENCE).put("MTLA090619", "50");
    }

    public MontrealServerImpl() throws RemoteException
    {
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
    public String addEvent(String eventID, String eventType, String bookingCapacity, String managerID) throws RemoteException
    {
        String message = null;
        logger.info("Received request to add an event with event id " + eventID + " , Event Type" + eventType
                + " & Booking Capacity " + bookingCapacity);
        if (!databaseMontreal.get(eventType).containsKey(eventID))
        {
            databaseMontreal.get(eventType).put(eventID, bookingCapacity);
            message = "Operations Successful!. Event Added in Montreal Server for Event ID: "
                    + eventID + " Event Type: " + eventType + " Booking Capacity: " + bookingCapacity;
            logger.info(message);

            return message;
        }
        else
        {
            databaseMontreal.get(eventType).replace(eventID, bookingCapacity);
            message = "Operations Unsuccessful!. Event Not Added in Montreal Server "
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
        if (databaseMontreal.get(eventType).containsKey(eventID))
        {
            databaseMontreal.get(eventType).remove(eventID);
            message = "Operations Successful!. Event Removed in Montreal Server by Manager: " + managerID + " for Event ID: "
                    + eventID + " Event Type: " + eventType;
            logger.info(message);
            return message;
        }
        else
        {
            message = "Operations Unsuccessful!. Event Not Removed in Montreal Server by Manager: " + managerID + " f"
                    + "or Event ID: " + eventID + " Event Type: " + eventType + " because the Event ID: " + eventID
                    + " does not exist";
            logger.info(message);
            return message;
        }

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

        if (!databaseMontreal.get(eventType).isEmpty())
        {
            for (Map.Entry<String, String> entry : databaseMontreal.get(eventType).entrySet())
            {
                returnMessage.append("EventID: ").append(entry.getKey()).append("| Booking Capacity ").append(entry.getValue()).append("\n");
            }
            message = "Operation Successful, List of events retrieved for Event Type: " + eventType + " by Manager: " + managerID + "in server";
            logger.info(message);

            return returnMessage.toString();
        }
        else
        {
            message = "Operation UnSuccessful, List of events not retrieved for Event Type: " + eventType + " by Manager: " + managerID + " in server ";
            logger.info(message);
            return message;
        }

    }

    @Override
    public String bookEvent(String customerID, String eventID, String eventType, String bookingAmount) throws RemoteException
    {
//        if(customerID.substring(0, 3).equals(MONTREAL)){}
        if (eventID.substring(0, 3).equals(MONTREAL))
        {
            logger.log(Level.INFO, "Book Event Requested by {0} for Event Type {1} with Event ID {2}", new Object[]
            {
                customerID, eventType, eventID
            });
            HashMap< String, String> event = databaseMontreal.get(eventType);
            if (event.containsKey(eventID))
            {
                if (customerEventsMapping.containsKey(customerID))
                {
                    if (customerEventsMapping.get(customerID).containsKey(eventID))
                    {
                        logger.log(Level.INFO, "Operation Unsuccessful, Book Event Requested by {0} for Event Type {1} with Event ID {2} cannot be booked. Customer already booked for this event.", new Object[]
                        {
                            customerID, eventType, eventID
                        });
                        return "Operation Unsuccessful, Book Event Requested by " + customerID + " for Event Type " + eventType + " with Event ID " + eventID + " cannot be booked. Customer already booked for this event.";
                    }
                    else if (!customerID.substring(0, 3).equals(MONTREAL))
                    {
                        int customerBookingsCurrent = Integer.parseInt(this.nonOriginCustomerBooking(customerID));
                        int customerBookingsOther = customerID.substring(0, 3).equals(OTTAWA) ? Integer.parseInt(requestToOtherServers(customerID, null, null, 7, null, TORONTO_SERVER_PORT).trim())
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

                    if (customerEventsMapping.containsKey(customerID))
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
        if (eventID.substring(0, 3).equals(TORONTO))
        {
            return requestToOtherServers(customerID, eventID, bookingAmount, 4, eventType, TORONTO_SERVER_PORT);
        }
        if (eventID.substring(0, 3).equals(OTTAWA))
        {
            return requestToOtherServers(customerID, eventID, bookingAmount, 4, eventType, OTTAWA_SERVER_PORT);
        }
        return "";
    }

    @Override
    public String getBookingSchedule(String customerID) throws RemoteException
    {
        String returnMsg = "";
        logger.log(Level.INFO, "Booking Schedule Requested by {0}", customerID);
        HashMap< String, Integer> customerEvents = customerEventsMapping.get(customerID);

        if (customerID.substring(0, 3).equals(MONTREAL))
        {
            returnMsg += requestToOtherServers(customerID, null, null, 5, null, TORONTO_SERVER_PORT);
            returnMsg += requestToOtherServers(customerID, null, null, 5, null, OTTAWA_SERVER_PORT);
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
            if (customerID.substring(0, 3).equals(MONTREAL))
            {
                returnMsg += OPERATIONFAILURE;
            }
        }

        return returnMsg;
    }

    @Override
    public String cancelEvent(String customerID, String eventID) throws RemoteException
    {
        if (eventID.substring(0, 3).equals(MONTREAL))
        {
            if (customerEventsMapping.containsKey(customerID))
            {
                if (customerEventsMapping.get(customerID).containsKey(eventID))
                {
                    Integer bookValue = customerEventsMapping.get(customerID).remove(eventID);
                    Integer currentValue = 0;
                    Integer sum = 0;

                    if (databaseMontreal.get(CONFERENCE).containsKey(eventID))
                    {
                        currentValue = Integer.parseInt(databaseMontreal.get(CONFERENCE).get(eventID));
                        sum = currentValue + bookValue;
                        databaseMontreal.get(CONFERENCE).put(eventID, sum.toString());
                    }
                    else if (databaseMontreal.get(SEMINAR).containsKey(eventID))
                    {
                        currentValue = Integer.parseInt(databaseMontreal.get(SEMINAR).get(eventID));
                        sum = currentValue + bookValue;
                        databaseMontreal.get(SEMINAR).put(eventID, sum.toString());
                    }
                    else if (databaseMontreal.get(TRADESHOW).containsKey(eventID))
                    {
                        currentValue = Integer.parseInt(databaseMontreal.get(TRADESHOW).get(eventID));
                        sum = currentValue + bookValue;
                        databaseMontreal.get(TRADESHOW).put(eventID, sum.toString());
                    }
                    logger.log(Level.INFO, "This event has been removed from customer record.");
                    return "This event has been removed from customer record.";
                }
            }
            logger.log(Level.INFO, "This event does not exist in customer record.");
            return "This event does not exist in customer record.";
        }
        else if (eventID.substring(0, 3).equals(TORONTO))
        {
            return requestToOtherServers(customerID, eventID, null, 6, null, TORONTO_SERVER_PORT);
        }
        else if (eventID.substring(0, 3).equals(OTTAWA))
        {
            return requestToOtherServers(customerID, eventID, null, 6, null, OTTAWA_SERVER_PORT);
        }
        return null;
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
