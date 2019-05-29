/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import java.util.Scanner;

/**
 *
 * @author Natheepan Ganeshamoorthy
 */
public class Client {

    enum ClientType {
        CUSTOMER, EVENT_MANAGER
    };

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
            String serverId = id.substring(0, 3);
            String userType = id.substring(3, 4);
            String userId = id.substring(4, 8);
            if (userType.equalsIgnoreCase("C") || userType.equalsIgnoreCase("M"))
            {
                if (serverId.equalsIgnoreCase("TOR"))
                {
                }
                else if (serverId.equalsIgnoreCase("OTW"))
                {
                }
                else if (serverId.equalsIgnoreCase("MTL"))
                {
                }
                else
                {
                    System.out.println("Invalid ID !!!");
                }
            }
            else
            {
                System.out.println("Invalid ID !!!");
            }
        }
        in.close();
    }

}
