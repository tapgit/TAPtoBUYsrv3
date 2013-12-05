package models;

public class CreditCard {
	
	//private int id;
	private String number;
	private String holders_name;
	private String exp_date;
	private Address billing_address;
	public CreditCard(String number, String holders_name, String exp_date,
			Address billing_address) {
		super();
		this.number = number;
		this.holders_name = holders_name;
		this.exp_date = exp_date;
		this.billing_address = billing_address;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getHolders_name() {
		return holders_name;
	}
	public void setHolders_name(String holders_name) {
		this.holders_name = holders_name;
	}
	public String getExp_date() {
		return exp_date;
	}
	public void setExp_date(String exp_date) {
		this.exp_date = exp_date;
	}
	public Address getBilling_address() {
		return billing_address;
	}
	public void setBilling_address(Address billing_address) {
		this.billing_address = billing_address;
	}
	
}
