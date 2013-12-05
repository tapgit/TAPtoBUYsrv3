package models;

public class Order {
	private int id;
	private int date;
	private String sellerUsername;
	private String buyerUsername;
	private Address shippingAddress;
	private CreditCard creditCard;
	private String paypalEmail;
	private double paidPrice;
	private double shippingPrice;
	public Order(int id, int date, String sellerUsername, String buyerUsername,
			Address shippingAddress, CreditCard creditCard, String paypalEmail,
			double paidPrice, double shippingPrice) {
		super();
		this.id = id;
		this.date = date;
		this.sellerUsername = sellerUsername;
		this.buyerUsername = buyerUsername;
		this.shippingAddress = shippingAddress;
		this.creditCard = creditCard;
		this.paypalEmail = paypalEmail;
		this.paidPrice = paidPrice;
		this.shippingPrice = shippingPrice;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getDate() {
		return date;
	}
	public void setDate(int date) {
		this.date = date;
	}
	public String getSellerUsername() {
		return sellerUsername;
	}
	public void setSellerUsername(String sellerUsername) {
		this.sellerUsername = sellerUsername;
	}
	public String getBuyerUsername() {
		return buyerUsername;
	}
	public void setBuyerUsername(String buyerUsername) {
		this.buyerUsername = buyerUsername;
	}
	public Address getShippingAddress() {
		return shippingAddress;
	}
	public void setShippingAddress(Address shippingAddress) {
		this.shippingAddress = shippingAddress;
	}
	public CreditCard getCreditCard() {
		return creditCard;
	}
	public void setCreditCard(CreditCard creditCard) {
		this.creditCard = creditCard;
	}
	public String getPaypalEmail() {
		return paypalEmail;
	}
	public void setPaypalEmail(String paypalEmail) {
		this.paypalEmail = paypalEmail;
	}
	public double getPaidPrice() {
		return paidPrice;
	}
	public void setPaidPrice(double paidPrice) {
		this.paidPrice = paidPrice;
	}
	public double getShippingPrice() {
		return shippingPrice;
	}
	public void setShippingPrice(double shippingPrice) {
		this.shippingPrice = shippingPrice;
	}
}
