package models;

public class MyHistoryProduct {
	private int id;
	private int order_id;
	private String title;
	private double paidPrice;
	private double paidShippingPrice;
	private String imgLink;
	private String sellerUsername;
	private double sellerRate;
	public MyHistoryProduct(int id, int order_id, String title,
			double paidPrice, double paidShippingPrice, String imgLink,
			String sellerUsername, double sellerRate) {
		super();
		this.id = id;
		this.order_id = order_id;
		this.title = title;
		this.paidPrice = paidPrice;
		this.paidShippingPrice = paidShippingPrice;
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
	public int getOrder_id() {
		return order_id;
	}
	public void setOrder_id(int order_id) {
		this.order_id = order_id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public double getPaidPrice() {
		return paidPrice;
	}
	public void setPaidPrice(double paidPrice) {
		this.paidPrice = paidPrice;
	}
	public double getPaidShippingPrice() {
		return paidShippingPrice;
	}
	public void setPaidShippingPrice(double paidShippingPrice) {
		this.paidShippingPrice = paidShippingPrice;
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
