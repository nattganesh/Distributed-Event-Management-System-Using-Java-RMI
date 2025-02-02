/**
 * CONCORDIA UNIVERSITY
 * DEPARTMENT OF COMPUTER SCIENCE AND SOFTWARE ENGINEERING
 * COMP 6231, Summer 2019 Instructor: Sukhjinder K. Narula
 * ASSIGNMENT 1
 * Issued: May 14, 2019 Due: Jun 3, 2019
 */
package Server;

import ServerImpl.TorontoServerImpl;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import static CommonUtils.CommonUtils.TORONTO_SERVER_NAME;
import static CommonUtils.CommonUtils.TORONTO_SERVER_PORT;

/**
 *
 * @author Gursimran Singh
 */
public class TorontoServer {

    public static void main(String[] args) throws RemoteException
    {

        TorontoServerImpl torontoServerStub = new TorontoServerImpl();
        Runnable runnable = () ->
        {
            receiveRequestsFromOthers(torontoServerStub);
        };

        Thread thread = new Thread(runnable);
        thread.start();

        Registry registry = LocateRegistry.createRegistry(TORONTO_SERVER_PORT);

        try
        {
            registry.bind(TORONTO_SERVER_NAME, torontoServerStub);
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
        }
        catch (AlreadyBoundException e)
        {
            e.printStackTrace();
        }

    }

    private static void receiveRequestsFromOthers(TorontoServerImpl torontoServer)
    {
        DatagramSocket aSocket = null;
        try
        {
            aSocket = new DatagramSocket(TORONTO_SERVER_PORT);
            byte[] buffer = new byte[1000];
            System.out.println("Toronto server started.....");
            //Server waits for the request
            while (true)
            {
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(request);
                String response = requestsFromOthers(new String(request.getData()), torontoServer);
                DatagramPacket reply = new DatagramPacket(response.getBytes(StandardCharsets.UTF_8), response.length(), request.getAddress(),
                        request.getPort());
                //reply sent
                aSocket.send(reply);
            }
        }
        catch (SocketException e)
        {
            System.out.println("Socket: " + e.getMessage());
        }
        catch (IOException e)
        {
            System.out.println("IO: " + e.getMessage());
        }
        finally
        {
            if (aSocket != null)
            {
                aSocket.close();
            }
        }
    }

    //clientudp
    public static String requestsFromOthers(String data, TorontoServerImpl torontoServer)
    {
        try
        {
            String[] receivedDataString = data.split(" ");
            String userId = receivedDataString[0];
            String eventID = receivedDataString[1];
            String methodNumber = receivedDataString[2].trim();
            String eventType = receivedDataString[3].trim();
            String bookingCapacity = receivedDataString[4].trim();
            String managerID = receivedDataString[5].trim();

            switch (methodNumber)
            {
                case "1":
                    return torontoServer.addEvent(eventID, eventType, bookingCapacity, userId);
                case "2":
                    return torontoServer.removeEvent(eventID, eventType, userId);
                case "3":
                    return torontoServer.listEventAvailability(eventType, userId);
                case "4":
                    return torontoServer.bookEvent(userId, eventID, eventType, bookingCapacity);
                case "5":
                    return torontoServer.getBookingSchedule(userId,managerID);
                case "6":
                    return torontoServer.cancelEvent(userId, eventID, eventType);
                case "7":
                    return torontoServer.nonOriginCustomerBooking(userId);
            }
        }
        catch (RemoteException e)
        {
            
        }
        return "Incorrect";
    }
}
