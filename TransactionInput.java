
public class TransactionInput {
	
	public String transactionOutputID; //reference to TransactionOutputs -> transactionID
	public TransactionOutput UTXO; //contains the unspent transaction output
	
	public TransactionInput(String transactionOutputID) {
		this.transactionOutputID = transactionOutputID;
	}
}
