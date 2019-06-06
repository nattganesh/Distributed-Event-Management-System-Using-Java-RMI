package Client;

import CommonUtils.CommonUtils;
import ServerInterface.ServerInterface;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * @author gursimransingh
 */
public class MultiClient {
    public static void main(String[] args) throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry(CommonUtils.OTTAWA_SERVER_PORT);

        ServerInterface serverInterface = (ServerInterface) registry.lookup(CommonUtils.OTTAWA_SERVER_NAME);

        Runnable runnable1 = () ->
        {
            try {
                String response = serverInterface.bookEvent("OTWC3425", "OTWE030619", CommonUtils.CONFERENCE, "15");
                System.out.println("Thread 1: " + Thread.currentThread().getName() + "Respnse from server: " + response);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        };

        Thread thread1 = new Thread(runnable1);
        thread1.setName("Thread 1");
        Runnable runnable2 = () ->
        {
            try {
                String response = serverInterface.bookEvent("OTWC3425", "OTWE030619", CommonUtils.CONFERENCE, "15");
                System.out.println("Thread 2: " + Thread.currentThread().getName() + "Respnse from server: " + response);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        };

        Thread thread2 = new Thread(runnable2);
        thread2.setName("Thread 2");
        Runnable runnable3 = () ->
        {
            try {
                String response = serverInterface.bookEvent("OTWC3425", "OTWE030619", CommonUtils.CONFERENCE, "15");
                System.out.println("Thread 3: " + Thread.currentThread().getName() + "Respnse from server: " + response);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        };

        Thread thread3 = new Thread(runnable3);
        thread3.setName("Thread 3");

        thread1.start();
        thread2.start();
        thread3.start();

    }
}
