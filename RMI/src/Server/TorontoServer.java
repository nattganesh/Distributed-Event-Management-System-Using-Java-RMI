/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import CommonUtils.CommonUtils;
import ServerImpl.MontrealServerImpl;
import ServerImpl.TorontoServerImpl;

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
        System.out.println("Server Started");

        TorontoServerImpl torontoServerStub = new TorontoServerImpl();

        Registry registry = LocateRegistry.createRegistry(CommonUtils.TORONTO_SERVER_PORT);

        try {
            registry.bind(CommonUtils.TORONTO_SERVER_NAME, torontoServerStub);
        }catch (RemoteException e){
            e.printStackTrace();
        } catch (AlreadyBoundException e) {
            e.printStackTrace();
        }

    }
}