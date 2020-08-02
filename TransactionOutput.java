import java.security.PublicKey;

public class TransactionOutput {

	public String id;
	public PublicKey recipient; //a.k.a. the owner of the coins
	public float value; //amount of coins owned
	public String parentTransactionID; //id of the transaction this output was created in
	
	public TransactionOutput(PublicKey recipient, float value, String parentTransactionID) {
		this.recipient = recipient;
		this.value = value;
		this.parentTransactionID = parentTransactionID;
		this.id = StringUtil.applySha256(StringUtil.getStringFromKey(recipient)+Float.toString(value)+parentTransactionID);
	}
	
	//check if coins belong to owner
	public boolean isMine(PublicKey publicKey) {
		return (publicKey == recipient);
	}
}