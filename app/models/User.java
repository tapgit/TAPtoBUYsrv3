package models;

public class User {
	private int id;
	private String firstname;
	private String lastname;
	private String username;
	private String password;
	private String email;
	
	private Address[] shipping_addresses;
	private CreditCard[] credit_cards;
	public User(int id, String firstname, String lastname, String username,
			String password, String email, Address[] shipping_addresses,
			CreditCard[] credit_cards) {
		super();
		this.id = id;
		this.firstname = firstname;
		this.lastname = lastname;
		this.username = username;
		this.password = password;
		this.email = email;
		this.shipping_addresses = shipping_addresses;
		this.credit_cards = credit_cards;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Address[] getShipping_addresses() {
		return shipping_addresses;
	}
	public void setShipping_addresses(Address[] shipping_addresses) {
		this.shipping_addresses = shipping_addresses;
	}
	public CreditCard[] getCredit_cards() {
		return credit_cards;
	}
	public void setCredit_cards(CreditCard[] credit_cards) {
		this.credit_cards = credit_cards;
	}
	
	
	
}
