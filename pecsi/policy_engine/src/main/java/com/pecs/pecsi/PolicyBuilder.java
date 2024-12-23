package com.pecs.pecsi;

import java.util.Map;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Privacy Policy builder engine (XACML 3.0 compliant)
 */
@SuppressWarnings("unused")
public class PolicyBuilder {

    /**
     * Build a new XACML 3.0 compliant privacy policy based on user preferences
     * @param prefs Preferences mapped object representing user choices
     * @param id UUIDv4 assigned to the policy
     * @return XML generated policy
     */
    public String buildPolicy(Preferences prefs, String id) {
        StringBuilder policy = new StringBuilder();
        Map<String, Boolean> globals = prefs.getPreferences().getGlobals().getPrefs();
        Map<String, Boolean> enginePrefs = prefs.getPreferences().getEngineDataPrefs();
        List<String> dataList = Permissions.getDataList();
        List<String> engineDataList = Permissions.getEngineDataList();

        policy.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        policy.append("<Policy xmlns=\"urn:oasis:names:tc:xacml:3.0:core:schema:wd-17\" PolicyId=\"");
        policy.append(id);
        policy.append("\"");
        policy.append(" RuleCombiningAlgId=\"urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:first-applicable\" Version=\"1.0\">\n");
        policy.append("\t<Description></Description>\n");
        policy.append("\t<Target>\n\t\t<AnyOf>\n\t\t\t<AllOf>\n\t\t\t\t");
        policy.append("<Match MatchId=\"urn:oasis:names:tc:xacml:1.0:function:string-equal\">\n\t\t\t\t\t");
        policy.append("<AttributeValue DataType=\"http://www.w3.org/2001/XMLSchema#string\">global</AttributeValue>\n\t\t\t\t\t");
        policy.append("<AttributeDesignator Category=\"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\" AttributeId=\"urn:oasis:names:tc:xacml:1.0:resource:resource-id\" DataType=\"http://www.w3.org/2001/XMLSchema#string\"/>\n\t\t\t\t");
        policy.append("</Match>\n\t\t\t</AllOf>\n\t\t</AnyOf>\n\t</Target>\n\t");

        for (String data : dataList) {
            policy.append("<Rule RuleId=\"global-");
            policy.append(data);
            policy.append("\" Effect=");
            
            if (globals.get(data) == true) policy.append("\"Permit\">");
            else policy.append("\"Deny\">");

            policy.append("\n\t\t");
            policy.append("<Target>\n\t\t\t<AnyOf>\n\t\t\t\t<AllOf>\n\t\t\t\t\t");
            policy.append("<Match MatchId=\"urn:oasis:names:tc:xacml:1.0:function:string-equal\">\n\t\t\t\t\t\t");
            policy.append("<AttributeValue DataType=\"http://www.w3.org/2001/XMLSchema#string\">" + data + "</AttributeValue>\n\t\t\t\t\t\t");
            policy.append("<AttributeDesignator Category=\"urn:oasis:names:tc:xacml:3.0:attribute-category:action\" AttributeId=\"urn:oasis:names:tc:xacml:1.0:action:action-id\" DataType=\"http://www.w3.org/2001/XMLSchema#string\"/>\n\t\t\t\t\t");
            policy.append("</Match>\n\t\t\t\t</AllOf>\n\t\t\t</AnyOf>\n\t\t</Target>\n\t");
            policy.append("</Rule>\n\n\t");
        }

        for (String data : engineDataList) {
            policy.append("\t<Rule RuleId=\"engine-");
            policy.append(data);
            policy.append("\" Effect=");
            
            if (enginePrefs.get(data) == true) policy.append("\"Permit\">");
            else policy.append("\"Deny\">");

            policy.append("\n\t\t");
            policy.append("<Target>\n\t\t\t<AnyOf>\n\t\t\t\t<AllOf>\n\t\t\t\t\t");
            policy.append("<Match MatchId=\"urn:oasis:names:tc:xacml:1.0:function:string-equal\">\n\t\t\t\t\t\t");
            policy.append("<AttributeValue DataType=\"http://www.w3.org/2001/XMLSchema#string\">" + data + "</AttributeValue>\n\t\t\t\t\t\t");
            policy.append("<AttributeDesignator Category=\"urn:oasis:names:tc:xacml:3.0:attribute-category:action\" AttributeId=\"urn:oasis:names:tc:xacml:1.0:action:action-id\" DataType=\"http://www.w3.org/2001/XMLSchema#string\"/>\n\t\t\t\t\t");
            policy.append("</Match>\n\t\t\t\t</AllOf>\n\t\t\t</AnyOf>\n\t\t</Target>\n\t");
            policy.append("</Rule>\n\n");
        }

        policy.append("</Policy>\n");

        return policy.toString();
    }
}
