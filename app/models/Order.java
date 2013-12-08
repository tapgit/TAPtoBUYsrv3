package models;

import java.util.ArrayList;

public class Order {
	private int id;
	private String date;
	private String shippingAddressStr;
	private String paymentMethod; //CreditCard for purchased order & Paypal email for sold order
	private double paidPrice;
	private double shippingPrice;
	public Order(int id, String date, String shippingAddressStr,
			String paymentMethod, double paidPrice, double shippingPrice) {
		super();
		this.id = id;
		this.date = date;
		this.shippingAddressStr = shippingAddressStr;
		this.paymentMethod = paymentMethod;
		this.paidPrice = paidPrice;
		this.shippingPrice = shippingPrice;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getShippingAddressStr() {
		return shippingAddressStr;
	}
	public void setShippingAddressStr(String shippingAddressStr) {
		this.shippingAddressStr = shippingAddressStr;
	}
	public String getPaymentMethod() {
		return paymentMethod;
	}
	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}
	public double getPaidPrice() {
		return paidPrice;
	}
	public void setPaidPrice(double paidPrice) {
		this.paidPrice = paidPrice;
	}
	public double getShippingPrice() {
		return shippingPrice;
	}
	public void setShippingPrice(double shippingPrice) {
		this.shippingPrice = shippingPrice;
	}
}
