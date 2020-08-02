import java.security.Security;
import java.util.HashMap;

//import org.bouncycastle.*;

public class Main {
	
	public static HashMap<String,TransactionOutput> UTXOs = new HashMap<String,TransactionOutput>(); //list of unspent transactions
	public static float minimumTransaction = 0.1f;
	public static Wallet walletA;
	public static Wallet walletB;
	public static Transaction genesisTransaction;

	public static void main(String[] args) {
		//setup bouncy castle as a security provider
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		
		//create new wallets
		walletA = new Wallet();
		walletB = new Wallet();
		Wallet firstwallet = new Wallet();
		
		//test public and private keys
		System.out.println("Private and public keys for Wallet A:");
		System.out.println(StringUtil.getStringFromKey(walletA.privateKey));
		System.out.println(StringUtil.getStringFromKey(walletA.publicKey));
		
		//create genesis transaction sending 100 coins to walletA
		genesisTransaction = new Transaction(firstwallet.publicKey, walletA.publicKey, 10f, null);
		genesisTransaction.generateSignature(firstwallet.privateKey); //manually sign the genesis transaction
		genesisTransaction.transactionId = "0"; //manually set the transaction id
		genesisTransaction.outputs.add(new TransactionOutput(genesisTransaction.recipient, genesisTransaction.value, 
				genesisTransaction.transactionId)); //manually add the transactions output
		UTXOs.put(genesisTransaction.outputs.get(0).id,  genesisTransaction.outputs.get(0)); //it is important to store our first transaction in the UTXOs list
		
		/*
		//create a test transaction from walletA to walletB
		Transaction transaction = new Transaction(walletA.publicKey, walletB.publicKey, 5, null);
		transaction.generateSignature(walletA.privateKey);
		*/
		
		/*
		//verify the signature works and verify it from the public key
		System.out.println("Is signature verified");
		System.out.println(genesisTransaction.verifySignature());
		*/
		
		//create the blockchain including the genesis block
		Blockchain blockchain = new Blockchain(5);
		
		//add new blocks
		blockchain.addBlock(blockchain.newBlock("Sixtens block"));
		blockchain.addBlock(blockchain.newBlock("Satoshi Nakamoto"));
		blockchain.addBlock(blockchain.newBlock("Tredje lang"));
		blockchain.addBlock(blockchain.newBlock("Fjarde gang"));
		
		System.out.println("Blockchain valid? " + blockchain.isBlockChainValid() + "\n");
		
		System.out.println("Total blockchain: \n" + blockchain);
	}
	
}
