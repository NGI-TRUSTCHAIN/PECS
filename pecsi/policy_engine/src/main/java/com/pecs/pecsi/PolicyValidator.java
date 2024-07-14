package com.pecs.pecsi;

import java.io.StringReader;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.xml.sax.SAXException;
import java.io.IOException;

@SuppressWarnings("unused")
public class PolicyValidator {
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
