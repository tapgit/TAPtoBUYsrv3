package models;

public class ProductForSale extends Product {
	//private int startingQuantity;
	private int remainingQuantity;
	private double instantPrice;
	
	public ProductForSale(int id, String title, String timeRemaining,
			double shippingPrice, String imgLink, String sellerUsername,
			double sellerRate, int remainingQuantity, double instantPrice) {
		super(id, title, timeRemaining, shippingPrice, imgLink, sellerUsername,
				sellerRate);
		this.remainingQuantity = remainingQuantity;
		this.instantPrice = instantPrice;
	}
	public int getRemainingQuantity() {
		return remainingQuantity;
	}
	public void setRemainingQuantity(int remainingQuantity) {
		this.remainingQuantity = remainingQuantity;
	}
	public double getInstantPrice() {
		return instantPrice;
	}
	public void setInstantPrice(double instantPrice) {
		this.instantPrice = instantPrice;
	}
}
