package models;

public class MyHistoryProductForSale extends MyHistoryProduct{
	private int quantity;

	public MyHistoryProductForSale(int id, int order_id, String title,
			double paidPrice, double paidShippingPrice, String imgLink,
			String sellerUsername, double sellerRate, int quantity) {
		super(id, order_id, title, paidPrice, paidShippingPrice, imgLink,
				sellerUsername, sellerRate);
		this.quantity = quantity;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}


}
