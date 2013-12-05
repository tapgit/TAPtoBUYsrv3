package models;

public class Bid {
	//private int id;
	private int user_id;//bidder id
	private int product_id;
	private double amount;

	private String username;//bidder username
	
	public Bid(int user_id, int product_id, double amount) {
		super();
		this.user_id = user_id;
		this.product_id = product_id;
		this.amount = amount;
	}
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
	public int getProduct_id() {
		return product_id;
	}
	public void setProduct_id(int product_id) {
		this.product_id = product_id;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	
	

}
