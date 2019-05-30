/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import CommonUtils.CommonUtils;
import ServerImpl.MontrealServerImpl;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 *
 * @author 
 */
public class MontrealServer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws RemoteException {
        // TODO code application logic here
        System.out.println("Server Started");

        MontrealServerImpl montrealServerStub = new MontrealServerImpl();

        Registry registry = LocateRegistry.createRegistry(CommonUtils.MONTREAL_SERVER_PORT);

        try {
            registry.bind(CommonUtils.MONTREAL_SERVER_NAME, montrealServerStub);
        }catch (RemoteException e){
            e.printStackTrace();
        } catch (AlreadyBoundException e) {
            e.printStackTrace();
        }

    }
    
}
