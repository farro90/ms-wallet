package com.nttdata.bc19.mswallet.util;

import java.util.HashMap;
import java.util.Map;

public class LogMessage {

    public static String idNotFound = "Id not found";
    public static Map<String, String> logMessage;

    static {
        logMessage = new HashMap<>();
        logMessage.put("SAVESUCCESS", "Successfully registered");
        logMessage.put("UPDATESUCCESS", "Successfully updated");
        logMessage.put("DELETESUCCESS", "Successfully deleted");
        logMessage.put("ERROR", "Error: ");
        logMessage.put("VALIDATION", "Validation: ");
    }

}
