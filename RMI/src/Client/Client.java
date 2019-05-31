/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import CommonUtils.CommonUtils;
import ServerImpl.MontrealServerImpl;
import ServerImpl.OttawaServerImpl;
import ServerImpl.TorontoServerImpl;
import ServerInterface.ServerInterface;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Natheepan Ganeshamoorthy
 */
public class Client {

    private static Logger LOGGER;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        Scanner in = new Scanner(System.in);
        // TODO code application logic here
        System.out.print("Enter Your ID Number: ");
        String id = in.next();
        System.out.println();
        if (id.length() != 8)
        {
            System.out.println("Invalid ID !!!");
        }
        else
        {
            String serverId = id.substring(0, 3).toUpperCase();
            String clientType = id.substring(3, 4).toUpperCase();
            String clientID = id.substring(4, 8).toUpperCase();
            if ((clientType.equals(CommonUtils.CUSTOMER_ClientType) || clientType.equals(CommonUtils.EVENT_MANAGER_ClientType))
                    && (serverId.equals(CommonUtils.TORONTO) || serverId.equals(CommonUtils.MONTREAL) || serverId.equals(CommonUtils.OTTAWA)))
            {
                runClientService(clientType, serverId, clientID);
            }
            else
            {
                System.out.println("Invalid ID !!!");
            }
        }
        in.close();
    }

    private static void customerService(String serverId, String clientID)
    {
        ServerInterface server;
        try
        {
            System.out.println("Customer Service");
            Registry registry = LocateRegistry.getRegistry(getServerPort(serverId));

            LOGGER = Logger.getLogger(getServerClassName(serverId));
            CommonUtils.addFileHandler(LOGGER, serverId + "C" + clientID);

            server = (ServerInterface) registry.lookup(getServerName(serverId));
            System.out.println(server.getClass().getName());
            String out = server.addEvent("OTWA100519", "Conferences", 3, clientID);
            System.out.println(out);
        }
        catch (SecurityException | IOException ex)
        {
            //Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (NotBoundException ex)
        {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void eventManagerService(String serverId, String clientID)
    {
        ServerInterface server;
        try
        {
            System.out.println("Welcome Manager");
            Registry registry = LocateRegistry.getRegistry(getServerPort(serverId));
            LOGGER = Logger.getLogger(getServerClassName(serverId));
            CommonUtils.addFileHandler(LOGGER, serverId + "M" + clientID);
            server = (ServerInterface) registry.lookup(getServerName(serverId));
        }
        catch (SecurityException | IOException ex)
        {
            //Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (NotBoundException ex)
        {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void runClientService(String clientType, String serverId, String clientID)
    {
        if (clientType.equals(CommonUtils.CUSTOMER_ClientType))
        {
            customerService(serverId, clientID);
        }
        else if (clientType.equals(CommonUtils.EVENT_MANAGER_ClientType))
        {
            eventManagerService(serverId, clientID);
        }
    }

    private static String getServerName(String serverId)
    {
        if (serverId.equals(CommonUtils.TORONTO))
        {
            return CommonUtils.TORONTO_SERVER_NAME;
        }
        if (serverId.equals(CommonUtils.MONTREAL))
        {
            return CommonUtils.MONTREAL_SERVER_NAME;
        }
        if (serverId.equals(CommonUtils.OTTAWA))
        {
            return CommonUtils.OTTAWA_SERVER_NAME;
        }
        return "Server Does Not Exist";
    }

    private static String getServerClassName(String serverId)
    {
        if (serverId.equals(CommonUtils.TORONTO))
        {
            return TorontoServerImpl.class.getName();
        }
        if (serverId.equals(CommonUtils.MONTREAL))
        {
            return MontrealServerImpl.class.getName();
        }
        if (serverId.equals(CommonUtils.OTTAWA))
        {
            return OttawaServerImpl.class.getName();
        }
        return "Server Does Not Exist";
    }

    private static int getServerPort(String serverId)
    {
        if (serverId.equals(CommonUtils.TORONTO))
        {
            return CommonUtils.TORONTO_SERVER_PORT;
        }
        if (serverId.equals(CommonUtils.MONTREAL))
        {
            return CommonUtils.MONTREAL_SERVER_PORT;
        }
        if (serverId.equals(CommonUtils.OTTAWA))
        {
            return CommonUtils.OTTAWA_SERVER_PORT;
        }
        return -1;
    }

}
