package models;
public class Address {
	private int id;
	private String country;
	private String contact_name;
	private String street;
	private String city;
	private String state;
	private String zip_code;
	private String telephone;
	public Address(int id, String country, String contact_name, String street,
			String city, String state, String zip_code, String telephone) {
		super();
		this.id = id;
		this.country = country;
		this.contact_name = contact_name;
		this.street = street;
		this.city = city;
		this.state = state;
		this.zip_code = zip_code;
		this.telephone = telephone;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getContact_name() {
		return contact_name;
	}
	public void setContact_name(String contact_name) {
		this.contact_name = contact_name;
	}
	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getZip_code() {
		return zip_code;
	}
	public void setZip_code(String zip_code) {
		this.zip_code = zip_code;
	}
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
}