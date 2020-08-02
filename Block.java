import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;

public class Block {

	private int index;
	private long timestamp;
	private String hash;
	private String previousHash;
	private String data;
	private String merkleRoot;
	private ArrayList<Transaction> transactions = new ArrayList<Transaction>();
	private int nonce;
	
	public Block(int index, long timestamp, String previousHash, String data) {
		this.index = index;
		this.timestamp = timestamp;
		this.previousHash = previousHash;
		this.data = data;
		nonce = 0;
		hash = Block.calculateHash(this); //make sure to do this after above values are set
	}

	public int getIndex() {
		return index;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public String getHash() {
		return hash;
	}

	public String getPreviousHash() {
		return previousHash;
	}

	public String getData() {
		return data;
	}
	
	public String str() {
		return index + timestamp + previousHash + data + nonce + merkleRoot;
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Block #").append(index).append(" [previousHash : ").append(previousHash).append(", ").
			append("timestamp: ").append(new Date(timestamp)).append(", ").append("data: ").append(data).append(", ").
			append("hash: ").append(hash).append(", ").append("Nonce: ").append(nonce).append("]");
		return builder.toString();
	}
	
	public static String calculateHash(Block block) {
		if (block != null) {
			MessageDigest digest = null;
			
			try {
				digest = MessageDigest.getInstance("SHA-256");
			} catch (NoSuchAlgorithmException e) {
				return null;
			}
			
			String txt = block.str();
			final byte bytes[] = digest.digest(txt.getBytes());
			final StringBuilder builder = new StringBuilder();
			
			for (final byte b : bytes) {
				String hex = Integer.toHexString(0xff & b);
				
				if (hex.length() == 1) {
					builder.append('0');
				}
				
				builder.append(hex);
			}
			
			return builder.toString();
		}
		
		return null;
	}
	
	public void mineBlock(int difficulty) {
		nonce = 0;
		merkleRoot = StringUtil.getMerkleRoot(transactions);
		// String target = StringUtil.getDifficultyString(difficulty); // create a string with difficulty * "0"
		while(!getHash().substring(0, difficulty).equals(Utils.zeros(difficulty))) {
			nonce++;
			hash = Block.calculateHash(this);
		}
		System.out.println("Block #" + index + " mined after " + nonce + " attempts.");
		System.out.println("Hash: " + getHash());
	}
	
	public boolean addTransaction(Transaction transaction) {
		//process transaction and check validity, unless block is genesis block then ignore
		if(transaction == null) return false;
		if(previousHash != "0") {
			if(transaction.processTransaction() != true) {
				System.out.println("Transaction failed to process. Discarded.");
				return false;
			}
		}
		transactions.add(transaction);
		System.out.println("Transaction Successfully added to block");
		return true;
	}
	
}