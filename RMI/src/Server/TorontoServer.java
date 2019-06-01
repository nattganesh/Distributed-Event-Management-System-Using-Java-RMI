/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import CommonUtils.CommonUtils;
import ServerImpl.TorontoServerImpl;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 *
 * @author 
 */
public class TorontoServer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws RemoteException {
        // TODO code application logic here

        TorontoServerImpl torontoServerStub = new TorontoServerImpl();
        Runnable runnable = () -> {
            receiveRequestsFromOthers(torontoServerStub);
        };

        Thread thread = new Thread(runnable);
        thread.start();

        Registry registry = LocateRegistry.createRegistry(CommonUtils.TORONTO_SERVER_PORT);

        try {
            registry.bind(CommonUtils.TORONTO_SERVER_NAME, torontoServerStub);
        }catch (RemoteException e){
            e.printStackTrace();
        } catch (AlreadyBoundException e) {
            e.printStackTrace();
        }

    }

    private static void receiveRequestsFromOthers(TorontoServerImpl torontoServer) {
        DatagramSocket aSocket = null;
        try {
            aSocket = new DatagramSocket(CommonUtils.TORONTO_SERVER_PORT);
            byte[] buffer = new byte[1000];
            System.out.println("Torronto server started.....");
            //Server waits for the request
            while (true) {
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(request);
                String response = requestsFromOthers(new String(request.getData()), torontoServer);
                DatagramPacket reply = new DatagramPacket(response.getBytes(), response.length(), request.getAddress(),
                        request.getPort());
                //reply sent
                aSocket.send(reply);
            }
        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        } finally {
            if (aSocket != null)
                aSocket.close();
        }
    }

    //clientudp
    public static String requestsFromOthers(String data, TorontoServerImpl torontoServer) {
        try {
            String[] receivedDataString = data.split(" ");
            String userId = receivedDataString[0];
            String eventID = receivedDataString[1];
            String methodNumber = receivedDataString[2].trim();
            String eventType = receivedDataString[3].trim();
            String bookingCapacity = receivedDataString[4].trim();

            switch (methodNumber) {
                case "1":
                    return torontoServer.addEvent(eventID, eventType, bookingCapacity, userId);
                case "2":
                    return torontoServer.removeEvent(eventID, eventType, userId);
                case "3":
                    return torontoServer.listEventAvailability(eventType, userId);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return "Incorrect";
    }
}