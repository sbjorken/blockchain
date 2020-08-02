import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;

public class Wallet {
	public PrivateKey privateKey;
	public PublicKey publicKey;
	
	public HashMap<String, TransactionOutput> UTXOs = new HashMap<String, TransactionOutput>();
	
	public Wallet() {
		generateKeyPair();
	}

	//generate pair of public & private keys
	public void generateKeyPair() {
		try {
			//Elliptic Curve Digital Signature Algorithm and provider Bouncy Castle
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA", "BC");
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
			keyGen.initialize(ecSpec, random);
			KeyPair keyPair = keyGen.generateKeyPair();
			privateKey = keyPair.getPrivate();
			publicKey = keyPair.getPublic();
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	//returns balance and stores the UTXOs owned by this wallet in this.UTXOs
	public float getBalance() {
		float total = 0;
		for(HashMap.Entry<String, TransactionOutput> item : Main.UTXOs.entrySet()) {
			TransactionOutput UTXO = item.getValue();
			if(UTXO.isMine(publicKey)) { //if output (i.e. coins) belongs to me
				UTXOs.put(UTXO.id, UTXO); //add to list of unspent transactions
				total += UTXO.value;
			}
		}
		return total;
	}
	
	//generates and returns a new transaction from this wallet
	public Transaction sendFunds(PublicKey _recipient, float value) {
		if(getBalance() < value) { //gather balance and check funds
			System.out.println("#Not enough funds to send transaction. Transaction discarded.");
			return null;
		}
		//create array list of inputs
		ArrayList<TransactionInput> inputs  = new ArrayList<TransactionInput>();
		float total = 0;
		for(HashMap.Entry<String, TransactionOutput> item : UTXOs.entrySet()) {
			TransactionOutput UTXO = item.getValue();
			total += UTXO.value;
			inputs.add(new TransactionInput(UTXO.id));
			if(total > value) break;
		}
		
		Transaction newTransaction = new Transaction(publicKey, _recipient, value, inputs);
		newTransaction.generateSignature(privateKey);
		
		for(TransactionInput input : inputs) {
				UTXOs.remove(input.transactionOutputID);
		}
		return newTransaction;
	}
}

