package models;

public class Rating {
	
	private String buyerUN;
	private int buyerRate;
	
	public Rating (String buyerUsername,int buyerRate){
		buyerUN = buyerUsername;
		this.buyerRate = buyerRate;
	}

	public String getBuyerUN() {
		return buyerUN;
	}

	public int getBuyerRate() {
		return buyerRate;
	}
	
	public void setBuyerUN(String username){
		this.buyerUN = username;
	}
	public void setBuyerRate(int rate){
		this.buyerRate = rate;
	}
	

}
