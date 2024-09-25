package com.pecs.pecsi;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.net.URL;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Policy Administration component (PAP)
 */
@SuppressWarnings("unused")
public class Administration {

    /**
     * Check if we are adding the first privacy policy in the system
     * @return true if is the first policy, false otherwise
     */
    private boolean isFirstPolicy() {
        File file = new File(this.LIST_PATH);
        return !file.exists();
    }

    /**
     * Method used privately to manage the enforcement of the first privacy policy in the system
     * @param prefs Preferences mapped object containing user preferences (see Preferences class)
     * @param path JSON path of the raw user preferences (ideally provided by a frontend application)
     */
    private void addFirstPolicy(Preferences prefs, String path) {
        PolicyBuilder policyBuilder = new PolicyBuilder();
        PolicyValidator validator = new PolicyValidator();

        File policiesDir = new File("/data/data/com.termux/files/home/PECS/pecsi/policies/");
        policiesDir.mkdir();

        String uuid = UUID.randomUUID().toString();
        String policy = policyBuilder.buildPolicy(prefs, uuid);
        if (validator.validate(this.XSD_PATH, policy)) {
            try {savePolicy(policy, "/data/data/com.termux/files/home/PECS/pecsi/policies/policy_" + uuid + ".xml");}
            catch (IOException e) {e.printStackTrace();}

            try {saveJson(path, uuid);}
            catch (Exception e) {e.printStackTrace();}
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        ObjectNode root = mapper.createObjectNode();
        ArrayNode policies = mapper.createArrayNode();
        ObjectNode policyNode = mapper.createObjectNode();
        long timestamp = System.currentTimeMillis() / 1000L;
        Date date = new Date((long) timestamp * 1000);

        policyNode.put("uuid", uuid);
        policyNode.put("file", "policy_" + uuid + ".xml");
        policyNode.put("json", "policy_" + uuid + ".json");
        policyNode.put("timestamp", timestamp);
        policyNode.put("date", date.toString());
        policyNode.put("enforced", true);
        policyNode.put("from-preset", false);

        policies.add(policyNode);
        root.set("policies", policies);

        try {
            File file = new File(this.LIST_PATH);
            mapper.writeValue(file, root);
        } catch (IOException e) {e.printStackTrace();}

        try {BlockchainSender.sendPolicy(new File("/data/data/com.termux/files/home/PECS/pecsi/policies/policy_" + uuid + ".xml"));}
        catch (Exception e) {e.printStackTrace();}
    }


    /**
     * Main component of the Administration component. It generates, validates and enforces the new privacy policy in the system
     * @param prefs Preferences mapped object containing user preferences (see Preferences class)
     * @param path JSON path of the raw user preferences (ideally provided by a frontend application)
     */
    public void addPolicy(Preferences prefs, String path) {
        if (isFirstPolicy()) {
            addFirstPolicy(prefs, path);
            return;
        }

        PolicyBuilder policyBuilder = new PolicyBuilder();
        PolicyValidator validator = new PolicyValidator();

        String uuid = UUID.randomUUID().toString();
        String policy = policyBuilder.buildPolicy(prefs, uuid);
        if (validator.validate(this.XSD_PATH, policy)) {
            try {
                savePolicy(policy, "/data/data/com.termux/files/home/PECS/pecsi/policies/policy_" + uuid + ".xml");
            }
            catch (IOException e) {e.printStackTrace();}

            try {saveJson(path, uuid);}
            catch (Exception e) {e.printStackTrace();}
        }

        ObjectMapper mapper = new ObjectMapper();
        PolicyList list = null;

        try {list = mapper.readValue(new File(this.LIST_PATH), PolicyList.class);}
        catch (IOException e) {e.printStackTrace();}

        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        ObjectNode root = mapper.createObjectNode();
        ArrayNode policies = mapper.createArrayNode();
        ObjectNode newPolicy = mapper.createObjectNode();
        long timestamp = System.currentTimeMillis() / 1000L;
        Date date = new Date((long) timestamp * 1000);

        newPolicy.put("uuid", uuid);
        newPolicy.put("file", "policy_" + uuid + ".xml");
        newPolicy.put("json", "policy_" + uuid + ".json");
        newPolicy.put("timestamp", timestamp);
        newPolicy.put("date", date.toString());
        newPolicy.put("enforced", true);
        newPolicy.put("from-preset", false);
        policies.add(newPolicy);

        for (PolicyList.Policy pol : list.getPolicies()) {
            ObjectNode node = mapper.createObjectNode();

            node.put("uuid", pol.getUuid());
            node.put("file", pol.getFilePath());
            node.put("json", pol.getJsonPath());
            node.put("timestamp", pol.getTimestamp());
            node.put("date", pol.getDate());
            node.put("enforced", false);
            node.put("from-preset", pol.isFromPreset());

            policies.add(node);
        }

        root.set("policies", policies);

        try {
            File file = new File(this.LIST_PATH);
            mapper.writeValue(file, root);
        } catch (IOException e) {e.printStackTrace();}

        try {BlockchainSender.sendPolicy(new File("/data/data/com.termux/files/home/PECS/pecsi/policies/policy_" + uuid + ".xml"));}
        catch (Exception e) {e.printStackTrace();}
    }

    /**
     * Get the enforced privacy policy in the system, if there is one
     * @return The Preferences object representing the enforced policy. Returns null if no privacy policy is enforced at the moment of method invocation
     */
    public Preferences getEnforcedPolicy() {
        Preferences prefs = null;

        ObjectMapper mapper = new ObjectMapper();
        PolicyList list = null;

        try {list = mapper.readValue(new File(this.LIST_PATH), PolicyList.class);}
        catch (IOException e) {return null;}

        List<PolicyList.Policy> policies = list.getPolicies();
        String policyPath = null;

        for (PolicyList.Policy pol : policies) {
            if (pol.isEnforced()) {
                policyPath = pol.getJsonPath();
                break;
            }
        }

        try {prefs = mapper.readValue(new File("/data/data/com.termux/files/home/PECS/pecsi/policies/" + policyPath), Preferences.class);}
        catch (IOException e) {e.printStackTrace();}

        return prefs;
    }

    /**
     * Save the privacy policy file
     * @param policy XML representation of the generated privacy policy
     * @param path Path where to save the policy file
     * @throws IOException If some problems occur while writing the file 
     */
    private static void savePolicy(String policy, String path) throws IOException {
        Files.write(Paths.get(path), policy.getBytes());
    }

    /**
     * Save the JSON representation of the privacy policy
     * @param path JSON path coming from frontend side
     * @param uuid Generated UUIDv4 assigned to the policy
     * @throws IOException If some problems occur while writing the file 
     */
    private static void saveJson(String path, String uuid) throws IOException {
        Path src = Paths.get(path);
        Path dst = Paths.get("/data/data/com.termux/files/home/PECS/pecsi/policies/policy_" + uuid + ".json");

        try {Files.copy(src, dst, StandardCopyOption.REPLACE_EXISTING);}
        catch (Exception e) {e.printStackTrace();}
    }

    /**
     * Path of the JSON-based list of the privacy policies history on the system (including the enforced one)
     */
    private final String LIST_PATH = "/data/data/com.termux/files/home/PECS/pecsi/policiesList.json";

    /**
     * Path of the XSD schema of the XACML 3.0 standard
     */
    private final String XSD_PATH = "/data/data/com.termux/files/home/PECS/pecsi/schema.xsd";
}
