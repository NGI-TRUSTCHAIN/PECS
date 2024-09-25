package com.pecs.pecsi;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Utility class to upload new policies to blockchain FTP server
 */
@SuppressWarnings("unused")
public class FtpUploader {

    /**
     * Upload privacy policy, digital signature and public key to FTP server
     * @param encodedPolicy base64 encoding of privacy policy
     * @param ds Digital signature
     * @param pubKey Public key (its string representation)
     */
    public void send(String encodedPolicy, String ds, String pubKey) {
        String serverUrl = ""; // insert FTP server URL here
        int port = 0; // insert port here
        String username = ""; // insert username
        String pass = ""; // insert password -> or port it as an env variable

        String tmpFilePath = getTempPath();
        FTPClient client = new FTPClient();

        try {
            client.connect(serverUrl, port);
            client.login(username, pass);
            client.enterLocalPassiveMode();
            client.setFileType(FTP.BINARY_FILE_TYPE);
            File file = new File("/data/data/com.termux/files/home/PECS/pecsi/" + tmpFilePath);
            InputStream istream = new FileInputStream(file);
            boolean check = client.storeFile(tmpFilePath, istream);
            istream.close();

            if (check) System.out.println("[INFO] Privacy Policy successfully uploaded to blockchain FTP server");

        } catch (IOException e) {e.printStackTrace();}
        finally {
            try {
                if (client.isConnected()) {
                    client.logout();
                    client.disconnect();
                }
            } catch (IOException e) {e.printStackTrace();}
        }
    }

    private String getTempPath() {
        return null;
    }
}
