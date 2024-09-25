package com.pecs.pecsi;

import java.io.StringReader;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.xml.sax.SAXException;
import java.io.IOException;

/**
 * Privacy Policy validator engine
 */
@SuppressWarnings("unused")
public class PolicyValidator {

    /**
     * Default constructor to instantiate a policy validator engine
     */
    public PolicyValidator() {}
    
    /**
     * Method to validate a XACML 3.0 Privacy Policy in front of a XSD schema
     * @param xsdPath the path of XSD schema file
     * @param policy XML policy input
     * @return true if the provided privacy policy is valid, false otherwise
    */
    public boolean validate(String xsdPath, String policy) {
        try {
            SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
            Schema schema = factory.newSchema(new StreamSource(xsdPath));
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new StringReader(policy)));
        } catch (IOException | SAXException e) {
            System.out.println("Exception during XML validation: " + e.getMessage());

            return false;
        }

        return true;
    }
}
