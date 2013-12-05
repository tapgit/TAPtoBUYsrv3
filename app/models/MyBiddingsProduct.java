package models;

public class MyBiddingsProduct extends ProductForAuction{
	
	private boolean winningBid;

	public MyBiddingsProduct(int id, String title, String timeRemaining,
			double shippingPrice, String imgLink, String sellerUsername,
			double sellerRate, double startinBidPrice, double currentBidPrice,
			int totalBids, boolean winningBid) {
		super(id, title, timeRemaining, shippingPrice, imgLink, sellerUsername,
				sellerRate, startinBidPrice, currentBidPrice, totalBids);
		this.winningBid = winningBid;
	}
	
	public boolean isWinningBid() {
		return winningBid;
	}

	public void setWinningBid(boolean winningBid) {
		this.winningBid = winningBid;
	}
}
