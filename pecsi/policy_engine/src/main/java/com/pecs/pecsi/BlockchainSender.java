package com.pecs.pecsi;

import java.io.*;
import org.apache.commons.io.FileUtils;
// import org.apache.commons.lang3.StringEscapeUtils;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.cert.CertificateException;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

import java.io.*;
import java.security.*;

@SuppressWarnings("unused")
public final class BlockchainSender {
    private static String signature;
    private static PublicKey publicKey;
    private static PrivateKey privateKey;

    // Gateway peer end point.

    private static void keys_init(){
        try {
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA", "SUN");

            keyGen.initialize(1024, random);

            KeyPair pair = keyGen.generateKeyPair();
            PrivateKey priv = pair.getPrivate();
            PublicKey pub = pair.getPublic();

            privateKey = priv;
            publicKey = pub;
        }

        catch (Exception e) {
            System.err.println("Caught exception " + e.toString());
        }

    }

    public static void sendPolicy(File policy) throws Exception {
        keys_init();
        
        String privacyPolicy = "";
        String stringHash = "";

        try {
            //Path path = Path.of("textfile.txt").toAbsolutePath();
            //System.out.println("Trying to open file " + path);
            
            privacyPolicy = FileUtils.readFileToString(policy);
            String privacyPolicyEncoded = Base64.getEncoder().encodeToString(privacyPolicy.getBytes()); 

            /*MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(xmlToString.getBytes(StandardCharsets.UTF_8));
            //String s = new String(hash, StandardCharsets.UTF_8);

            String encodedHash = Base64.getEncoder().encodeToString(hash);*/
        
            System.out.println("Submitting transaction to blockchain");

            securing_policy(policy.toString());

            String signature = get_signature();
            String pubKey = get_publicKey();

            // System.out.println("Signature: " + signature + "\n" + "Public Key: " + pubKey + "Encoded policy: " + privacyPolicyEncoded);

    } catch (Exception e) {e.printStackTrace();}

    }


    
    public static String get_signature(){
        return signature;
    }

    public static String get_publicKey(){
        byte[] byte_pubkey = publicKey.getEncoded();
        String str_key = Base64.getEncoder().encodeToString(byte_pubkey);

        return str_key;
    }

    public static void securing_policy(String path){
        try {
            Signature dsa = Signature.getInstance("SHA1withDSA", "SUN"); 
            dsa.initSign(privateKey);
            
            FileInputStream fis = new FileInputStream(path);
            BufferedInputStream bufin = new BufferedInputStream(fis);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = bufin.read(buffer)) >= 0) {
                dsa.update(buffer, 0, len);
            };
            bufin.close();

            byte[] realSig = dsa.sign();

            String s = new String(realSig, StandardCharsets.UTF_8);
            
            signature = s;

            // verify signature...

            } 
            catch (Exception e) {
                System.err.println("Caught exception " + e.toString());
            }
            
        }
}
