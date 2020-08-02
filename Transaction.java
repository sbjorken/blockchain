import java.security.*;
import java.util.ArrayList;
//import org.bouncycastle.*;

public class Transaction {
	
	public String transactionId; //this is also the hash of the transaction
	public PublicKey sender;
	public PublicKey recipient;
	public float value;
	public byte[] signature;
	
	public ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
	public ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();
	
	private static int sequence = 0; //counter of rough amount of transactions that have been generated
	
	public Transaction(PublicKey from, PublicKey to, float value, ArrayList<TransactionInput> inputs) {
		this.sender = from;
		this.recipient = to;
		this.value = value;
		this.inputs = inputs;
	}
	
	//calculates the transaction hash that will be used as its ID
	private String calculateHash() {
		sequence++;
		return StringUtil.applySha256(
				StringUtil.getStringFromKey(sender) +
				StringUtil.getStringFromKey(recipient) +
				Float.toString(value) +
				sequence);
	}
	
	//signs all the data
	public void generateSignature(PrivateKey privateKey) {
		String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(recipient) + Float.toString(value);
		signature = StringUtil.applyECDSASig(privateKey, data);
	}
	
	//verifies the signed data
	public boolean verifySignature() {
		String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(recipient) + Float.toString(value);
		return StringUtil.verifyECDSASig(sender, data, signature);
	}

	//returns true if new transaction could be created
	public boolean processTransaction() {
		
		if(verifySignature() == false) {
			System.out.println("#Transaction signature failed to verify");
			return false;
		}
		
		//gather transaction inputs (make sure they are unspent)
		for(TransactionInput i : inputs) {
			i.UTXO = Main.UTXOs.get(i.transactionOutputID);
		}
		
		//check if transaction is valid
		if(getInputsValue() < Main.minimumTransaction) {
			System.out.println("#Transaction inputs too small: " + getInputsValue());
			return false;
		}
		
		//generate transaction outputs
		float leftOver = getInputsValue() - value;
		transactionId = calculateHash();
		outputs.add(new TransactionOutput(this.recipient, value, transactionId)); //send value to the recipient
		outputs.add(new TransactionOutput(this.sender, leftOver, transactionId)); //send the left overs back to the sender
	
		//add outputs to unspent list
		for(TransactionOutput o : outputs) {
			Main.UTXOs.put(o.id, o);
		}
		
		//remove transaction inputs from UTXOs lists as spent
		for(TransactionInput i : inputs) {
			Main.UTXOs.remove(i.UTXO.id);
		}
		
		return true;
	}

	//returns sum of inputs (UTXOs) values
	public float getInputsValue() {
		float total = 0;
		for(TransactionInput i : inputs) {
			if(i.UTXO == null) continue; //if transaction cannot be found skip it
			total += i.UTXO.value;
		}
		return total;
	}
	
	//returns sum of outputs
	public float getOutputsValue() {
		float total = 0;
		for(TransactionOutput o : outputs) {
			total += o.value;
		}
		return total;
	}
	
}