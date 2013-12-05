package models;


public class Product {
	private int id;
	private String title;
	private String timeRemaining; //need to convert to xd yh, xh ym, xm ys(use timer for seconds)
	//private boolean timeEnded;
	private double shippingPrice; //free=> 0
	//info
	private String imgLink;
	
	private String sellerUsername;
	private double sellerRate;
	public Product(int id, String title, String timeRemaining,
			double shippingPrice, String imgLink, String sellerUsername,
			double sellerRate) {
		super();
		this.id = id;
		this.title = title;
		this.timeRemaining = timeRemaining;
		this.shippingPrice = shippingPrice;
		this.imgLink = imgLink;
		this.sellerUsername = sellerUsername;
		this.sellerRate = sellerRate;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getTimeRemaining() {
		return timeRemaining;
	}
	public void setTimeRemaining(String timeRemaining) {
		this.timeRemaining = timeRemaining;
	}
	public double getShippingPrice() {
		return shippingPrice;
	}
	public void setShippingPrice(double shippingPrice) {
		this.shippingPrice = shippingPrice;
	}
	public String getImgLink() {
		return imgLink;
	}
	public void setImgLink(String imgLink) {
		this.imgLink = imgLink;
	}
	public String getSellerUsername() {
		return sellerUsername;
	}
	public void setSellerUsername(String sellerUsername) {
		this.sellerUsername = sellerUsername;
	}
	public double getSellerRate() {
		return sellerRate;
	}
	public void setSellerRate(double sellerRate) {
		this.sellerRate = sellerRate;
	}
}
