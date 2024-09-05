package com.pecs.pecsi;

import java.util.List;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Map;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@SuppressWarnings("unused")
public class Decision {
    public void evaluate(Preferences policy, Request req) {
        boolean check = false;
        List<String> dataList = Permissions.getDataList();
        List<Request.AppPermission> permList = req.getPermissions();
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        ObjectNode root = mapper.createObjectNode();
        ArrayNode responses = mapper.createArrayNode();

        for (Request.AppPermission app : permList) {
            Map<String, Boolean> perms = app.getAppPermissions();
            List<Preferences.UserPreferences.AppSpecific> prefs = policy.getPreferences().getAppSpecificPrefs();
            Map<String, Boolean> globals = policy.getPreferences().getGlobals().getPrefs();
            Map<String, Boolean> appPrefs = null;

            if (prefs != null) {
                for (Preferences.UserPreferences.AppSpecific pref : prefs) {
                    if (pref.getAppName().equalsIgnoreCase(app.getAppName())) {
                        appPrefs = pref.getPrefs();
                        break;
                    }
                }
            }

            if (appPrefs == null) appPrefs = globals;

            boolean hasAlerts = false;
            ObjectNode node = mapper.createObjectNode();
            node.put("name", app.getAppName());
            ArrayNode alerts = mapper.createArrayNode();

            for (String data : dataList) {
                if (appPrefs.get(data) == false && perms.get(data) == true) {
                    hasAlerts = true;
                    ObjectNode alertNode = mapper.createObjectNode();
                    long timestamp = System.currentTimeMillis() / 1000L;
                    Date date = new Date((long) timestamp * 1000);
                    alertNode.put("data", data);
                    alertNode.put("timestamp", timestamp);
                    alertNode.put("date", date.toString());

                    alerts.add(alertNode);
                }
            }

            if (hasAlerts) {
                node.set("alerts", alerts);
                responses.add(node);
            }
        }

        root.set("responses", responses);

        try {saveResponse(root);}
        catch (IOException e) {e.printStackTrace();}
    }

    private static void saveResponse(ObjectNode root) throws IOException {
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
}
