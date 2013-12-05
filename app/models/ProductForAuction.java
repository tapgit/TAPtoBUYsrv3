package models;

public class ProductForAuction extends Product {
	private double startinBidPrice;//para el history y el MYSellingItems del lado del seller.
	private double currentBidPrice;
	private int totalBids;
	//private double bidRate;//para que el MySellingItems del lado del seller.
	public ProductForAuction(int id, String title, String timeRemaining,
			double shippingPrice, String imgLink, String sellerUsername,
			double sellerRate, double startinBidPrice, double currentBidPrice,
			int totalBids) {
		super(id, title, timeRemaining, shippingPrice, imgLink, sellerUsername,
				sellerRate);
		this.startinBidPrice = startinBidPrice;
		this.currentBidPrice = currentBidPrice;
		this.totalBids = totalBids;
	}
	public double getStartinBidPrice() {
		return startinBidPrice;
	}
	public void setStartinBidPrice(double startinBidPrice) {
		this.startinBidPrice = startinBidPrice;
	}
	public double getCurrentBidPrice() {
		return currentBidPrice;
	}
	public void setCurrentBidPrice(double currentBidPrice) {
		this.currentBidPrice = currentBidPrice;
	}
	public int getTotalBids() {
		return totalBids;
	}
	public void setTotalBids(int totalBids) {
		this.totalBids = totalBids;
	}
}
