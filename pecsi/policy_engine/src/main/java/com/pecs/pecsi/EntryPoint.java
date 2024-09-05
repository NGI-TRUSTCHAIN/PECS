package com.pecs.pecsi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.PlaceholderForType;
import com.pecs.pecsi.Request.AppPermission;

import net.sf.saxon.event.StreamWriterToReceiver;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.List;
import java.util.UUID;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.nio.file.*;
import static java.nio.file.StandardWatchEventKinds.*;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.*;

@SuppressWarnings("unused")
public class EntryPoint {
    public static void main(String[] args) {
        Administration pap = new Administration();
        Decision pdp = new Decision();
        PermissionsChecker checker = new PermissionsChecker();
        Thread checkerThread = new Thread(checker);
        checkerThread.start();
    
        ObjectMapper mapper = new ObjectMapper();
        try {
            watch = FileSystems.getDefault().newWatchService();
            prefsDir = Paths.get(prefsPath);
            prefsDir.register(watch, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);
        } catch (IOException e) {e.printStackTrace();}

        System.out.println("Monitoring " + prefsPath + " ...");
        try {
            while (true) {
                WatchKey key = watch.take();

                for (WatchEvent<?> e : key.pollEvents()) {
                    if (e.kind() == ENTRY_CREATE) {
                        String fileName = e.context().toString();

                        if (fileName.equalsIgnoreCase("choice.json")) {
                            PresetChoice choice = null;
                            File file = new File(prefsPath + fileName);
                            try {choice = mapper.readValue(file, PresetChoice.class);}
                            catch (IOException ex) {ex.printStackTrace();}
                            file.delete();

                            System.out.println("\n[INFO] Received Privacy Policy setting using preset: " + choice.getChoice().toUpperCase());
                            for (String preset : Presets.getList()) {
                                if (choice.getChoice().equalsIgnoreCase(preset)) {
                                    String presetPath = "/data/data/com.termux/files/home/PECS/pecsi/presets/" + preset + ".json";
                                    Preferences prefs = null;

                                    try {prefs = mapper.readValue(new File(presetPath), Preferences.class);}
                                    catch (IOException exc) {exc.printStackTrace();}

                                    pap.addPolicy(prefs, presetPath);

                                    break;
                                }
                            }
                        }
                        else if (fileName.equalsIgnoreCase("preferences.json")) {
                            System.out.println("\n[INFO] Received custom Privacy Policy settings");
                            Preferences prefs = null;
                            File file = new File(prefsPath + "preferences.json");

                            try {prefs = mapper.readValue(file, Preferences.class);}
                            catch (IOException exc) {exc.printStackTrace();}

                            pap.addPolicy(prefs, prefsPath + "preferences.json");
                            file.delete();
                        }
                    }
                }

                key.reset();
            }
        } catch (Exception e) {e.printStackTrace();}
    }

    private static String prefsPath = "/data/data/com.termux/files/home/storage/downloads/";
    private static WatchService watch = null;
    private static Path prefsDir = null;
}
