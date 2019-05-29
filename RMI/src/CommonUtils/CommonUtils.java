package CommonUtils;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Natheepan Ganeshamoorthy
 */
public class CommonUtils {

    public static final String CUSTOMER_ClientType = "C";
    public static final String EVENT_MANAGER_ClientType = "M";

    public static final String TORONTO = "TOR";
    public static final String MONTREAL = "MTL";
    public static final String OTTAWA = "OTW";

    public static final String TORONTO_SERVER_NAME = "TORONTO";
    public static final String MONTREAL_SERVER_NAME = "MONTREAL";
    public static final String OTTAWA_SERVER_NAME = "OTTAWA";

    public static final int TORONTO_SERVER_PORT = 6666;
    public static final int MONTREAL_SERVER_PORT = 5555;
    public static final int OTTAWA_SERVER_PORT = 4444;

    public static void addFileHandler(Logger log, String fileName) throws SecurityException, IOException
    {
        FileHandler fileHandler = new FileHandler(System.getProperty("user.dir") + "/LoggedFiles/" + fileName + "/" + fileName + ".log", true);
        log.addHandler(fileHandler);
        fileHandler.setFormatter(new SimpleFormatter());
    }

}
