package defaultPackage;

public class Payment {

	private String webshopId;
	private String customerId;
	
	//csak 'card' vagy 'transfer'
	private String paymentType;
	
	//max. 10 hosszú
	private String amount;
	
	//max 16 hosszú
	private String accountNumber;
	
	//max 16 hosszú
	private String cardNumber;
	private String paymentDate;
	
	
	
	public Payment(String webshopId, String customerId, String paymentType, String amount, String accountNumber, String cardNumber, String paymentDate) {

		this.webshopId = webshopId;
		this.customerId = customerId;
		this.paymentType = paymentType;
		this.amount = amount;
		this.accountNumber = accountNumber;
		this.cardNumber = cardNumber;
		this.paymentDate = paymentDate;
	}
	
	public String getWebshopId() {
		return webshopId;
	}

	public String getCustomerId() {
		return customerId;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public String getAmount() {
		return amount;
	}

	public String getAccountNumber() {
		return accountNumber;
	}
	
	public String getCardNumber() {
		return cardNumber;
	}
	
	public String getPaymentDate() {
		return paymentDate;
	}
	
}
