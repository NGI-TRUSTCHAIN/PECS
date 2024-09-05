package com.pecs.pecsi;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.Set;
import java.util.HashSet;
import org.json.*;
import java.util.Map;
import java.util.HashMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pecs.pecsi.Preferences.UserPreferences.AppSpecific;

@SuppressWarnings("unused")
public class PermissionsChecker implements Runnable {

    @Override
    public void run() {
        HashMap<String, String> alertMap = new HashMap<String, String>();
        setupCanInterface();

        while (true) {
            try {Thread.sleep(5000);}
            catch (InterruptedException e) {e.printStackTrace();}
            List<String> packages = new ArrayList<>();
            List<List<String>> totPerms = new ArrayList<>();
            Administration pap = new Administration();
            // check if we haven't an active policy yet
            Preferences p = null;
            if ((p = pap.getEnforcedPolicy()) == null) {
                System.out.println("[INFO] No Privacy Policy enforced right now, skipping permission check round");
                continue;
            }
            String[] packagesComm = {"/system/bin/sh", "-c", "pm list packages -3"};
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            ObjectNode root = mapper.createObjectNode();
            ArrayNode responses = mapper.createArrayNode();
    
            try {
                ProcessBuilder processBuilder = new ProcessBuilder(packagesComm);
                Process proc = processBuilder.start();
    
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()))) {
                    String line;
    
                    while ((line = reader.readLine()) != null) {
                        line = line.trim();
                        String[] splits = line.split("[:]");
    
                        if (splits.length > 0) {
                            String pack = splits[splits.length - 1];
                            // System.out.println("adding " + perm + "...");
                            packages.add(pack);
                        }
                    }
                }
    
                int exitCode = 0;
                try {exitCode = proc.waitFor();}
                catch (Exception e) {e.printStackTrace();}
                if (exitCode != 0) System.err.println("Error while command executing: " + exitCode);
            } catch (IOException e) {e.printStackTrace();}
    
            HashMap<String, String> checkerMap = new HashMap<String, String>(alertMap);
            alertMap.clear();
            for (String pack : packages) {
                String comm = "echo \"dumpsys package " + pack + " | grep -E 'android.permission.[A-Z_]+:'\" | su";
                String[] permsComm = {"/system/bin/sh", "-c", comm};
                List<String> packPerms = new ArrayList<>();
    
                try {
                    ProcessBuilder processBuilder = new ProcessBuilder(permsComm);
                    Process proc = processBuilder.start();
        
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()))) {
                        String line;
        
                        while ((line = reader.readLine()) != null) {
                            line = line.trim();
                            line = clean(line);
                            String[] splits = line.split("[.]");
        
                            if (splits.length > 0) {
                                String perm = splits[splits.length - 1];
                                // System.out.println("adding " + perm + "...");
                                perm = adjust(perm);
    
                                if (Permissions.getDataList().contains(perm)) packPerms.add(perm);
                            }
                        }
                    }
        
                    int exitCode = 0;
                    try {exitCode = proc.waitFor();}
                    catch (Exception e) {e.printStackTrace();}
    
                    ObjectNode resp = mapper.createObjectNode();
                    resp.put("name", pack);
                    ArrayNode alerts = mapper.createArrayNode();
                    List<Preferences.UserPreferences.AppSpecific> appSpecificsPrefs = pap.getEnforcedPolicy().getPreferences().getAppSpecificPrefs();
                    Map<String, Boolean> appPrefs = null;
    
                    if (appSpecificsPrefs != null) {
                        for (Preferences.UserPreferences.AppSpecific pref : appSpecificsPrefs) {
                            if (pref.getAppName().equalsIgnoreCase(pack)) appPrefs = pref.getPrefs();
                        }
                    }

                    if (appPrefs == null) appPrefs = pap.getEnforcedPolicy().getPreferences().getGlobals().getPrefs();
    
                    // use appPrefs to do the right checks
                    boolean hasAlerts = false;
                    List<String> checked = new ArrayList<>();
                    for (String perm : packPerms) {
                        perm = adjust(perm);
                        // System.out.println("checking " + perm + "...");
                        if (!checked.contains(perm) && appPrefs.get(perm) != null && appPrefs.get(perm) == false) {
                            hasAlerts = true;
                            ObjectNode alert = mapper.createObjectNode();
                            alert.put("data", perm);
                            long timestamp = System.currentTimeMillis() / 1000L;
                            Date date = new Date((long) timestamp * 1000);
                            alert.put("timestamp", timestamp);
                            alert.put("date", date.toString());
                            // System.out.println("adding alert...");
                            checked.add(perm);
                            alerts.add(alert);
                            alertMap.put(pack, perm);
                        }
                    }
                    
                    resp.set("alerts", alerts);
                    if (hasAlerts) responses.add(resp);
                } catch (IOException e) {e.printStackTrace();}
            }
    
            root.set("responses", responses);
            // check if there are any changes
            if (!checkerMap.equals(alertMap)) {
                System.out.println("[INFO] New privacy violation detected");
                sendResponseToFrontend(root);
                hapticFeedback();
            }
            saveResponse(root);
            System.out.println("[INFO] Apps permissions check round completed");
        }
    }

    private static void sendResponseToFrontend(ObjectNode root) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            File file = new File("/data/data/com.termux/files/home/storage/downloads/response.json");
            mapper.writeValue(file, root);
        }
        catch (IOException e) {e.printStackTrace();}
    }

    private static void saveResponse(ObjectNode root) {
        File responsesDir = new File("/data/data/com.termux/files/home/PECS/pecsi/responses/");

        if (!responsesDir.exists()) responsesDir.mkdir();

        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            File file = new File("/data/data/com.termux/files/home/PECS/pecsi/responses/response_" + UUID.randomUUID().toString() + ".json");
            mapper.writeValue(file, root);
        }
        catch (IOException e) {e.printStackTrace();}
    }

    private static String adjust(String s) {
        String[] parts = s.split("[_]");
        
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            result.append(parts[i].toLowerCase());
            
            if (i < parts.length - 1) {
                result.append("-");
            }
        }

        return result.toString();
    }

    private static String clean(String s) {
        int i = s.indexOf(':');
        if (i != -1) return s.substring(0, i);
        
        return s;
    }

    private static void setupCanInterface() {
        // eventually insert echo *
        String[] comm = {"/system/bin/sh", "-c", "ip link set can0 type can bitrate 50000 | su && ip link set dev can0 txqueuelen 10000 | su && ip link set up can0 | su"};

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(comm);
            Process proc = processBuilder.start();

            int exitCode = 0;
            try {exitCode = proc.waitFor();}
            catch (Exception e) {e.printStackTrace();}
        } catch (IOException e) {e.printStackTrace();}

        System.out.println("[INFO] CAN interface setup completed");
    }

    private static void hapticFeedback() {
        String[] comm = {"/system/bin/sh", "-c", "cansend can0 0D5#5649425241"};

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(comm);
            Process proc = processBuilder.start();

            int exitCode = 0;
            try {exitCode = proc.waitFor();}
            catch (Exception e) {e.printStackTrace();}
        } catch (IOException e) {e.printStackTrace();}

        System.out.println("[INFO] Triggered haptic feedback actuators");
    }

    // before, check if map.get() != null (if exists)
    // pdp.evaluate() diventa PermissionChecker
}
