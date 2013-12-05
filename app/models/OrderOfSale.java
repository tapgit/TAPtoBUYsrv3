package models;

public class OrderOfSale extends Order {
	private MyHistoryProductForSale[] productList;

	public OrderOfSale(int id, int date, String sellerUsername,
			String buyerUsername, Address shippingAddress,
			CreditCard creditCard, String paypalEmail, double paidPrice,
			double shippingPrice, MyHistoryProductForSale[] productList) {
		super(id, date, sellerUsername, buyerUsername, shippingAddress,
				creditCard, paypalEmail, paidPrice, shippingPrice);
		this.productList = productList;
	}

	public MyHistoryProductForSale[] getProductList() {
		return productList;
	}

	public void setProductList(MyHistoryProductForSale[] productList) {
		this.productList = productList;
	}
	
}
