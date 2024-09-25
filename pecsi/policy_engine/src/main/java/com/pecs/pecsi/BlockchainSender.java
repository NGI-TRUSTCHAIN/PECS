package com.pecs.pecsi;

import java.io.*;
import org.apache.commons.io.FileUtils;

import java.util.Base64;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.cert.CertificateException;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import java.security.cert.Certificate;

import java.io.*;
import java.security.*;

/**
 * Component for blockchain data preparation
 */
@SuppressWarnings("unused")
public final class BlockchainSender {
    private static String signatureString;
    private static PublicKey publicKey;
    private static PrivateKey privateKey;

    // Gateway peer end point.

    /**
     * Initializes keys
     */
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

    /**
     * Send privacy policy data to prepare them for blockchain uploading
     * @param policy Privacy policy XML file path
     * @throws Exception If errors occur while reading policy file
     */
    public static void sendPolicy(File policy) throws Exception {
        keys_init();
        
        String privacyPolicy = "";
        String stringHash = "";

        try {            
            privacyPolicy = FileUtils.readFileToString(policy);
            String privacyPolicyEncoded = Base64.getEncoder().encodeToString(privacyPolicy.getBytes()); 
        
            System.out.println("Preparing data for blockchain...");

            pecs_ds(policy.toString());

            String signature = get_signature();
            String pubKey = get_publicKey();

            // System.out.println("Signature: " + signature + "\n" + "Public Key: " + pubKey + "Encoded policy: " + privacyPolicyEncoded);

    } catch (Exception e) {e.printStackTrace();}

    }


    /**
     * 
     * @return String representation of digital signature
     */
    public static String get_signature(){
        return signatureString;
    }

    /**
     * 
     * @return String representation of public key
     */
    public static String get_publicKey(){
        byte[] byte_pubkey = publicKey.getEncoded();
        String str_key = Base64.getEncoder().encodeToString(byte_pubkey);

        return str_key;
    }

    public static void pecs_ds(String policyPath){
		System.out.println("Digital signature on " + policyPath);
		char[] pass = ("pecsToBlockchain").toCharArray();

		try{
			KeyStore keyStore = KeyStore.getInstance("JKS");
			keyStore.load(new FileInputStream("/data/data/com.termux/files/home/PECS/pecs-ds/sender_keystore.jks"), pass);
			
			privateKey = (PrivateKey) keyStore.getKey("senderKeyPair", pass);
			
			keyStore.load(new FileInputStream("/data/data/com.termux/files/home/PECS/pecs-ds/receiver_keystore.jks"), pass);

			Certificate certificate = keyStore.getCertificate("receiverKeyPair");
			publicKey = certificate.getPublicKey();

			Signature signature = Signature.getInstance("MD5withRSA");
			signature.initSign(privateKey);

			byte[] messageBytes = Files.readAllBytes(Paths.get(policyPath));
			
			signature.update(messageBytes);

			byte[] digitalSignature = signature.sign();
			String s = new String(digitalSignature, StandardCharsets.UTF_8);

			signatureString = Base64.getEncoder().encodeToString(digitalSignature);
			
			System.out.println("digital signature " + signatureString);
			System.out.println("public key " + get_publicKey());

			//verification

            /*
			signature.initVerify(publicKey);
			signature.update(messageBytes);
			boolean isCorrect = signature.verify(digitalSignature);

			System.out.println("Verification " + isCorrect);
            */

		}
		catch (Exception e) {e.printStackTrace(System.out);}
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
            
            signatureString = s;

            // verify signature...

            } 
            catch (Exception e) {
                System.err.println("Caught exception " + e.toString());
            }
            
        }
}
