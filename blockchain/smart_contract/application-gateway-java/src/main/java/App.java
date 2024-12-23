import java.io.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import io.grpc.Grpc;
import io.grpc.ManagedChannel;
import io.grpc.TlsChannelCredentials;
import org.hyperledger.fabric.client.CommitException;
import org.hyperledger.fabric.client.CommitStatusException;
import org.hyperledger.fabric.client.Contract;
import org.hyperledger.fabric.client.EndorseException;
import org.hyperledger.fabric.client.Gateway;
import org.hyperledger.fabric.client.GatewayException;
import org.hyperledger.fabric.client.SubmitException;
import org.hyperledger.fabric.client.identity.Identities;
import org.hyperledger.fabric.client.identity.Identity;
import org.hyperledger.fabric.client.identity.Signer;
import org.hyperledger.fabric.client.identity.Signers;
import org.hyperledger.fabric.client.identity.X509Identity;

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
import java.security.cert.Certificate;

import java.util.Timer;
import java.util.TimerTask;
import java.nio.file.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public final class App {
	private static final String MSP_ID = System.getenv().getOrDefault("MSP_ID", "Org1MSP");
	private static final String CHANNEL_NAME = System.getenv().getOrDefault("CHANNEL_NAME", "mychannel");
	private static final String CHAINCODE_NAME = System.getenv().getOrDefault("CHAINCODE_NAME", "basic");
	
	// Path to crypto materials.
	private static final Path CRYPTO_PATH = Paths.get("../../network/organizations/peerOrganizations/org1.example.com");
	// Path to user certificate.
	private static final Path CERT_DIR_PATH = CRYPTO_PATH.resolve(Paths.get("users/User1@org1.example.com/msp/signcerts"));
	// Path to user private key directory.
	private static final Path KEY_DIR_PATH = CRYPTO_PATH.resolve(Paths.get("users/User1@org1.example.com/msp/keystore"));
	// Path to peer tls certificate.
	private static final Path TLS_CERT_PATH = CRYPTO_PATH.resolve(Paths.get("peers/peer0.org1.example.com/tls/ca.crt"));

	// Gateway peer end point.
	private static final String PEER_ENDPOINT = "localhost:7051";
	private static final String OVERRIDE_AUTH = "peer0.org1.example.com";

	private final Contract contract;
	private final String hashPrivacyPolicy = "asset" + Instant.now().toEpochMilli();
	private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

	public static void openConnection(Path privacyPolicyFileName) throws Exception{
		// The gRPC client connection should be shared by all Gateway connections to
		// this endpoint.
		var channel = newGrpcConnection();

		var builder = Gateway.newInstance().identity(newIdentity()).signer(newSigner()).connection(channel)
				// Default timeouts for different gRPC calls
				.evaluateOptions(options -> options.withDeadlineAfter(5, TimeUnit.SECONDS))
				.endorseOptions(options -> options.withDeadlineAfter(15, TimeUnit.SECONDS))
				.submitOptions(options -> options.withDeadlineAfter(5, TimeUnit.SECONDS))
				.commitStatusOptions(options -> options.withDeadlineAfter(1, TimeUnit.MINUTES));

		try (var gateway = builder.connect()) {
			new App(gateway).run(privacyPolicyFileName);
		} finally {
			channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
		}
	}

	public static void watcher() throws Exception{
		WatchService watcher = FileSystems.getDefault().newWatchService();

		Path dir = Paths.get("."); //Modify with directory of FTP Server where privacy policies are stored
		try {
   	 		WatchKey key = dir.register(watcher,
            	StandardWatchEventKinds.ENTRY_CREATE);
		} 
		catch (IOException x) {
    		System.err.println(x);
		}		

		for (;;) {
			// wait for key to be signaled
			WatchKey key;
			try {
				key = watcher.take();
			} catch (InterruptedException x) {
				return;
			}
		
			for (WatchEvent<?> event: key.pollEvents()) {
				WatchEvent.Kind<?> kind = event.kind();
		
				if (kind == StandardWatchEventKinds.OVERFLOW) {
					continue;
				}
		
				WatchEvent<Path> ev = (WatchEvent<Path>)event;
				Path privacyPolicyFileName = ev.context();

				System.out.println("New file " + privacyPolicyFileName);
		
				// Verify that the new file is a json file
				try {					
					Path child = dir.resolve(privacyPolicyFileName);
					if (!Files.probeContentType(child).equals("application/json")) {
						System.err.format("New file '%s'" +
							" is not a privacy policy.%n", privacyPolicyFileName);
						continue;
					}
				} catch (IOException x) {
					System.err.println(x);
					continue;
				}
		
				System.out.format("Creating new asset on blockchain %n");
				openConnection(privacyPolicyFileName);
			}
		
			// Reset the key for receiving further watch events
			boolean valid = key.reset();
			if (!valid) {
				break;
			}
		}
	}

	public static void main(final String[] args) throws Exception {
		watcher();
	}

	private static ManagedChannel newGrpcConnection() throws IOException {
		var credentials = TlsChannelCredentials.newBuilder()
				.trustManager(TLS_CERT_PATH.toFile())
				.build();
		return Grpc.newChannelBuilder(PEER_ENDPOINT, credentials)
				.overrideAuthority(OVERRIDE_AUTH)
				.build();
	}

	private static Identity newIdentity() throws IOException, CertificateException {
		try (var certReader = Files.newBufferedReader(getFirstFilePath(CERT_DIR_PATH))) {
			var certificate = Identities.readX509Certificate(certReader);
			return new X509Identity(MSP_ID, certificate);
		}
	}

	private static Signer newSigner() throws IOException, InvalidKeyException {
		try (var keyReader = Files.newBufferedReader(getFirstFilePath(KEY_DIR_PATH))) {
			var privateKey = Identities.readPrivateKey(keyReader);
			return Signers.newPrivateKeySigner(privateKey);
		}
	}

	private static Path getFirstFilePath(Path dirPath) throws IOException {
		try (var keyFiles = Files.list(dirPath)) {
			return keyFiles.findFirst().orElseThrow();
		}
	}

	public App(final Gateway gateway) {
		// Get a network instance representing the channel where the smart contract is
		// deployed.
		var network = gateway.getNetwork(CHANNEL_NAME);

		// Get the smart contract from the network.
		contract = network.getContract(CHAINCODE_NAME);
	}

	public void run(Path privacyPolicyFileName) throws GatewayException, CommitException {
		// Create a new asset on the ledger.
		createAsset(privacyPolicyFileName);

		// Return all the current assets on the ledger.
		getAllAssets();

		// Get the asset details by assetID.
		//readAssetById();
	}
		
	/**
	 * Evaluate a transaction to query ledger state.
	 */
	private void getAllAssets() throws GatewayException {
		System.out.println("\n--> Retrieve Assets...");

		var result = contract.evaluateTransaction("GetAllAssets");
		System.out.println("*** Result: " + prettyJson(result));
	}

	private String prettyJson(final byte[] json) {
		return prettyJson(new String(json));
	}

	private String prettyJson(final String json) {
		var parsedJson = JsonParser.parseString(json);
		return gson.toJson(parsedJson);
	}

	/**
	 * Submit a transaction synchronously, blocking until it has been committed to
	 * the ledger.
	 */
	private void createAsset(Path privacyPolicyFileName) throws EndorseException, SubmitException, CommitStatusException, CommitException {
		String privacyPolicy = "";
		
		String privacyPolicyEncoded = ""; 
		String signature = ""; 
		String pubKey = ""; 

		try {
			File privacyPolicyFile = new File(privacyPolicyFileName.toString());
			privacyPolicy = FileUtils.readFileToString(privacyPolicyFile, StandardCharsets.UTF_8);
			
			System.out.println("\n--> Submit Transaction: CreateAsset, submit new privacy asset to the blockchain");
			
			JSONParser jsonParser = new JSONParser();
      		try {

         		//Parsing the contents of the JSON file
         		JSONObject jsonObject = (JSONObject) jsonParser.parse(new FileReader("./"+privacyPolicyFileName));
         		privacyPolicy = (String) jsonObject.get("privacy_policy");
         		signature = (String) jsonObject.get("digital_signature");
         		pubKey = (String) jsonObject.get("pubkey");
         
        	} catch (ParseException | IOException e) {
            	e.printStackTrace();
      		} 

			contract.submitTransaction("CreateAsset", privacyPolicy, signature, pubKey);

			System.out.println("*** Transaction committed successfully");	
			
			privacyPolicyFile.delete();	
		}

		catch(IOException e) {
			System.out.println("File not present");
			}
		}

	private void readAssetById() throws GatewayException {
		System.out.println("\n--> Evaluate Transaction: ReadAsset, function returns asset attributes");

		var evaluateResult = contract.evaluateTransaction("ReadAsset", hashPrivacyPolicy);
		
		System.out.println("*** Result:" + prettyJson(evaluateResult));
	}
}