package models;

public class OrderOfAuction extends Order {
	private MyHistoryProductForAuction product;

	public OrderOfAuction(int id, int date, String sellerUsername,
			String buyerUsername, Address shippingAddress,
			CreditCard creditCard, String paypalEmail, double paidPrice,
			double shippingPrice, MyHistoryProductForAuction product) {
		super(id, date, sellerUsername, buyerUsername, shippingAddress,
				creditCard, paypalEmail, paidPrice, shippingPrice);
		this.product = product;
	}

	public MyHistoryProductForAuction getProduct() {
		return product;
	}

	public void setProduct(MyHistoryProductForAuction product) {
		this.product = product;
	}
	
	
}
