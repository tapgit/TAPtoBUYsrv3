package controllers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;

import man.Manager;
import models.Address;
import models.CreditCard;
import models.Rating;
import models.User;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

public class UserAccountController extends Controller {

	//DONE
	public static Result checkLogin(){
		JsonNode json = request().body().asJson();
		if(json == null) {
			return badRequest("Expecting Json data");//400
		} 
		else {
			try{
				Class.forName(Manager.driver);
				Connection connection = DriverManager.getConnection(Manager.db,Manager.user,Manager.pass);
				Statement statement = connection.createStatement();

				String username = json.findPath("username").getTextValue();//json.get("username").getTextValue();
				String password = json.findPath("password").getTextValue();
				ObjectNode respJson = Json.newObject();

				ResultSet rset = statement.executeQuery("select aid " + 
						"from admins " +
						"where username = '"+username+"' and pass = '" + password +"';");
				if(rset.next()){
					respJson.put("admin", true);
					respJson.put("id", rset.getInt(1));
					connection.close();
					return ok(respJson);//200 (send client the userId of the user that's being signed in)
				}
				else{
					rset = statement.executeQuery("select uid " + 
							"from users " +
							"where username = '"+username+"' and pass = '" + password +"';");
					if(rset.next()){
						respJson.put("admin", false);
						int userId =rset.getInt(1);
						respJson.put("id", userId);
						//Get won bid
						
						ArrayNode array = respJson.arrayNode();
						ObjectNode tmpJson = Json.newObject();
						
						rset = statement.executeQuery("select iid from bid natural join item where uid = "+ userId +" and winningbid = true and item.available = true;");
						while(rset.next()){
							tmpJson.put("iid", rset.getInt("iid"));
							array.add(tmpJson);
						}
						respJson.put("wonBids",array);
						
						connection.close();
						return ok(respJson);//200 (send client the userId of the user that's being signed in)
					}
					else{
						connection.close();
						return unauthorized("Bad username or password");//401
					}
				}
			}
			catch (Exception e) {
				Logger.info("EXCEPTION ON SIGNIN");
				e.printStackTrace();
				return notFound();
			}
		}
	}
	public static Result register(){
		JsonNode json = request().body().asJson();
		if(json == null) {
			return badRequest("Expecting Json data");//400
		} 
		else {
			try{
				Class.forName(Manager.driver);
				Connection connection = DriverManager.getConnection(Manager.db,Manager.user,Manager.pass);
				Statement statement = connection.createStatement();
				String firstname = json.get("firstname").getTextValue();
				String lastname = json.get("lastname").getTextValue();
				String username = json.get("username").getTextValue();
				String password = json.get("password").getTextValue();
				String email = json.get("email").getTextValue();
				//Shipping Address
				JsonNode shippingAddress = json.get("shipping_address");
				String shipCountry = shippingAddress.get("country").getTextValue();
				String shipContactName = shippingAddress.get("contact_name").getTextValue();
				String shipStreet = shippingAddress.get("street").getTextValue();
				String shipCity = shippingAddress.get("city").getTextValue();
				String shipState = shippingAddress.get("state").getTextValue();
				String shipZipCode = shippingAddress.get("zip_code").getTextValue();
				String shipTelephone = shippingAddress.get("telephone").getTextValue();
				//Billing Address
				JsonNode billingAddress = json.get("billing_address");
				String billCountry = billingAddress.get("country").getTextValue();
				String billContactName = billingAddress.get("contact_name").getTextValue();
				String billStreet = billingAddress.get("street").getTextValue();
				String billCity = billingAddress.get("city").getTextValue();
				String billState = billingAddress.get("state").getTextValue();
				String billZipCode = billingAddress.get("zip_code").getTextValue();
				String billTelephone = billingAddress.get("telephone").getTextValue();
				//Credit Card
				JsonNode creditCard = json.get("credit_card");
				String creditCardNumber = creditCard.get("number").getTextValue();
				String creditCardHoldersName = creditCard.get("holders_name").getTextValue();
				String creditCardExpDate = creditCard.get("exp_date").getTextValue();	

				ResultSet rset = statement.executeQuery("insert into users(uid,username,pass,fname,lname,email) " +
						"values (DEFAULT,'" + username + "','" + password + "','" + firstname + "','" + lastname + "','"+ email + "') " +
						"returning uid;");
				rset.next();
				int last_uid = rset.getInt("uid");
				statement.executeUpdate("insert into paypal(payId,pay_email,seller_uId) values (DEFAULT,''," + last_uid +");");

				rset = statement.executeQuery("insert into billing_address(billAddr_Id,country,contact_name,street,city,state,zip_code,telephone) " +
						"values (DEFAULT,'" + billCountry + "','" + billContactName + "','" + billStreet + "','" + billCity + "','"+ billState + "','"+ billZipCode + "','"+ billTelephone + "') " + 
						"returning billAddr_Id; ");
				rset.next();
				int last_billAddr_Id = rset.getInt("billAddr_Id");
				statement.executeUpdate("insert into shipping_address(shipAddr_Id,country,contact_name,street,city,state,zip_code,telephone,uid,available) " +
						"values (DEFAULT,'" + shipCountry + "','" + shipContactName + "','" +shipStreet + "','" + shipCity + "','"+ shipState + "','"+ shipZipCode + "','"+ shipTelephone + "',"+ last_uid +",true); " + 
						"insert into credit_card(crCard_id,sec_number,holders_name,exp_date,billAddr_Id,uid,available) " + 
						"values (DEFAULT,'" + creditCardNumber + "','" + creditCardHoldersName + "','" +creditCardExpDate + "'," + last_billAddr_Id + ","+ last_uid + ",true);");
				ObjectNode respJson = Json.newObject();
				respJson.put("id", last_uid);
				connection.close();
				return created(respJson);//201
			}
			catch (Exception e) {
				Logger.info("EXCEPTION ON REGISTER");
				e.printStackTrace();
				return notFound();
			}
		}
	}
	//DONE
	public static Result getUserAccountInfo(int userId){
		try{
			Class.forName(Manager.driver);
			Connection connection = DriverManager.getConnection(Manager.db,Manager.user,Manager.pass);
			Statement statement = connection.createStatement();

			//Get user info
			ResultSet rset = statement.executeQuery("select username, pass,fname,lname,email,pay_email,count(distinct shipAddr_Id) as shipCount, count(distinct crCard_Id) as crCardCount " + 
					"from users natural join shipping_address natural join credit_card,paypal " +
					"where paypal.seller_uId = uid and uid = " + userId + " and shipping_address.available = true and credit_card.available = true " + 
					"group by username, pass, fname, lname, email,pay_email;");

			if(rset.next()){
				String username = rset.getString("username");
				String pass = rset.getString("pass");
				String fname = rset.getString("fname");
				String lname = rset.getString("lname");
				String email = rset.getString("email");
				String paypalEmail = rset.getString("pay_email");
				Address[] shippingAddresses = new Address[rset.getInt("shipCount")];
				CreditCard[] creditCards = new CreditCard[rset.getInt("crCardCount")];

				//Get shipping addresses:
				rset = statement.executeQuery("select shipaddr_id,country, contact_name, street, city, state, zip_code, telephone " +
						"from shipping_address " +
						"where uid = " + userId + " and shipping_address.available = true ;"); 
				int i=0;
				while(rset.next()){
					shippingAddresses[i++] = new Address(rset.getInt(1), rset.getString(2), rset.getString(3), 
							rset.getString(4), rset.getString(5), rset.getString(6), rset.getString(7), rset.getString(8));
				}
				//Get CreditCard-BillingAddress pair

				rset = statement.executeQuery("select billaddr_id,country,contact_name,street,city,state,zip_code,telephone, " +
						"sec_number,holders_name,exp_date " +
						"from billing_address natural join credit_card " +
						"where uid = " + userId + " and credit_card.available = true;");
				Address tempBillingAddress = null;
				i=0;
				while(rset.next()){
					tempBillingAddress =  new Address(rset.getInt(1), rset.getString(2), rset.getString(3), 
							rset.getString(4), rset.getString(5), rset.getString(6), rset.getString(7), rset.getString(8));

					creditCards[i++] = new CreditCard(rset.getString(9), rset.getString(10), rset.getString(11), tempBillingAddress);
					
				}
				User user = new User(userId,fname, lname, username, pass, email, shippingAddresses, creditCards, paypalEmail);
				connection.close();
				return ok(Json.toJson(user));//200 respond with user data
			}
			else{
				connection.close();
				return notFound("User not found");//404
			}
		}
		catch (Exception e) {
			Logger.info("EXCEPTION ON ACCOUNT INFO");
			e.printStackTrace();
			return notFound();
		}
	}

	//DONE
	public static Result getRatingList(int productId){
		try{
			Class.forName(Manager.driver);
			Connection connection = DriverManager.getConnection(Manager.db,Manager.user,Manager.pass);
			Statement statement = connection.createStatement();
			ResultSet rset = statement.executeQuery("select username, stars " +
					"from item_for_sale natural left outer join ranks as rnk(b_uid,uid,stars),users " +
					"where iid = " + productId + " and users.uid = b_uid");
			ObjectNode buyersAndStars = null;
			ObjectNode respJson = Json.newObject();
			ArrayNode array = respJson.arrayNode();
			Rating rating = null;
			while(rset.next()){
				buyersAndStars = Json.newObject();
				rating = new Rating(rset.getString("username"), rset.getInt("stars"));
				array.add(Json.toJson(rating));
			}
			rset = statement.executeQuery("select username, stars " +
					"from item_for_auction natural left outer join ranks as rnk(b_uid,uid,stars),users " +
					"where iid = " + productId + " and users.uid = b_uid");
			while(rset.next()){
				buyersAndStars = Json.newObject();
				rating = new Rating(rset.getString("username"), rset.getInt("stars"));
				array.add(Json.toJson(rating));
			}
			respJson.put("ratingslist",array);
			connection.close();
			return ok(respJson);
		}
		catch (Exception e) {
			Logger.info("EXCEPTION ON RATING LIST");
			e.printStackTrace();
			return notFound();
		}
	}

	public static Result updateUserAccount(int userId){
		JsonNode json = request().body().asJson();
		if(json == null) {
			return badRequest("Expecting Json data");//400
		} 
		else{
			try{
				Class.forName(Manager.driver);
				Connection connection = DriverManager.getConnection(Manager.db,Manager.user,Manager.pass);
				
				JSONObject userJson = new JSONObject(json.toString());
				JSONArray shippingAddressesArray = userJson.getJSONArray("shipping_addresses");
				JSONArray creditCardsArray = userJson.getJSONArray("credit_cards");

				JSONObject jsonAddress = null;
				JSONObject jsonCreditCard = null;

				//Get shipping addresses
				Address[] shippingAddresses = new Address[shippingAddressesArray.length()];
				for(int i=0;i<shippingAddressesArray.length();i++){
					jsonAddress = shippingAddressesArray.getJSONObject(i);
					shippingAddresses[i] = new Address(jsonAddress.getInt("id"),jsonAddress.getString("country"), jsonAddress.getString("contact_name"), 
							jsonAddress.getString("street"), jsonAddress.getString("city"), jsonAddress.getString("state"), 
							jsonAddress.getString("zip_code"), jsonAddress.getString("telephone"));
				}
				//Get credit cards
				CreditCard[] creditCards = new CreditCard[creditCardsArray.length()];
				for(int i=0;i<creditCardsArray.length();i++){
					jsonCreditCard = creditCardsArray.getJSONObject(i);
					//get the billing address from this credit card		
					jsonAddress = jsonCreditCard.getJSONObject("billing_address");
					Address aBillingAddress = new Address(jsonAddress.getInt("id"),jsonAddress.getString("country"), jsonAddress.getString("contact_name"), 
							jsonAddress.getString("street"), jsonAddress.getString("city"), jsonAddress.getString("state"), 
							jsonAddress.getString("zip_code"), jsonAddress.getString("telephone"));

					creditCards[i] = new CreditCard(jsonCreditCard.getString("number"), jsonCreditCard.getString("holders_name"), 
							jsonCreditCard.getString("exp_date"), aBillingAddress);
				}
				ArrayList<Integer> receivedShippingIds = new ArrayList<Integer>();
				ArrayList<Integer> receivedBillingIds = new ArrayList<Integer>();
				ArrayList<String> receivedCreditCardNums = new ArrayList<String>();
				for(Address ship:shippingAddresses){
					receivedShippingIds.add(ship.getId());
				}
				for(CreditCard card:creditCards){
					receivedBillingIds.add(card.getBilling_address().getId());
					receivedCreditCardNums.add(card.getNumber());
				}
				String firstname = userJson.getString("firstname");
				String lastname = userJson.getString("lastname");
				String username = userJson.getString("username");
				String password = userJson.getString("password");
				String email = userJson.getString("email");
				String paypalEmail = userJson.getString("payEmail");
				
				//Update users
				Statement statement = connection.createStatement();
				statement.executeUpdate("update users set (username,pass,fname,lname,email) = ('" + username + "','" + password + "','" + firstname + "','" + lastname + "','"+ email + "') " +
															"where uid = " + userId + ";");
				statement.executeUpdate("update paypal set pay_email = '"+ paypalEmail +"' where seller_uId = "+ userId +";");
				//Update Shipping Addresses
				//get shipAddr_Id's de este user
				ResultSet rset = statement.executeQuery("select shipAddr_id from shipping_address where uid=" + userId + " and available = true");
				ArrayList<Integer> foundShipIds = new ArrayList<Integer>();
				while (rset.next()){
					foundShipIds.add(rset.getInt(1));
				}
				for(Address ship: shippingAddresses){
					if(!foundShipIds.contains(ship.getId())){//si hay un shippAddress nuevo..lo anadimos al DB
						statement.executeUpdate("insert into shipping_address(shipAddr_Id,country,contact_name,street,city,state,zip_code,telephone,uid,available) " +
								"values (DEFAULT,'" + ship.getCountry() + "','" + ship.getContact_name() + "','" +ship.getStreet() + "','" + ship.getCity() + "','"+ ship.getState() + "','"+ ship.getZip_code() + "','"+ ship.getTelephone() + "',"+ userId +",true); ");
					}
				}
				for(Integer shipId: foundShipIds){
					if(!receivedShippingIds.contains(shipId)){//si en las q recibimos no esta este shipId(del DB), lo removemos del DB.
						statement.executeUpdate("update shipping_address set available = false where shipAddr_Id = " + shipId.intValue() + ";");
					}
				}
				rset = statement.executeQuery("select billaddr_id,sec_number from credit_card where uid="+ userId+" and available = true");
				ArrayList<Integer> foundBillIds = new ArrayList<Integer>();
				ArrayList<String> foundCrCardNums = new ArrayList<String>();
				while (rset.next()){
					foundBillIds.add(rset.getInt(1));
					foundCrCardNums.add(rset.getString(2));
				}
				//Update credit Cards
				for(CreditCard c: creditCards){
					if(!foundCrCardNums.contains(c.getNumber())){//Si este creditCard no esta en el DB, la anadimos al DB.
						Address b = c.getBilling_address();//anadimos primero el billing address
						rset = statement.executeQuery("insert into billing_address(billAddr_Id,country,contact_name,street,city,state,zip_code,telephone) " +
								"values (DEFAULT,'" + b.getCountry() + "','" + b.getContact_name() + "','" + b.getStreet() + "','" + b.getCity() + "','"+ b.getState() + "','"+ b.getZip_code() + "','"+ b.getTelephone() + "') " + 
								"returning billAddr_Id; ");
						rset.next();
						int added_billAddr_Id = rset.getInt("billAddr_Id");
						//luego anadimos el credit_card
						statement.executeUpdate("insert into credit_card(crCard_id,sec_number,holders_name,exp_date,billAddr_Id,uid,available) " + 
								"values (DEFAULT,'" + c.getNumber() + "','" + c.getHolders_name() + "','" +c.getExp_date() + "'," + added_billAddr_Id + ","+ userId + ",true);");
					}
				}
				for(String s: foundCrCardNums){
					if(!receivedCreditCardNums.contains(s)){//si en las q recibimos no esta este credit card(del DB), lo removemos del DB.
						statement.executeUpdate("update credit_card set available = false where sec_number = '" + s + "' and uid = " + userId +";");
					}
				}
				
				connection.close();
				return ok("User account has been updated");//200
			}
			catch(JSONException e){
				Logger.info("JSON EXCEPTION ON UPDATE USER ACCOUNT");
				e.printStackTrace();
				return notFound();
			}
			catch (Exception e) {
				Logger.info("EXCEPTION ON UPDATE USER ACCOUNT");
				e.printStackTrace();
				return notFound();
			}
		}
	}
	public static Result deleteUserAccount(int userId){
		if(userId!=16){
			return notFound("User not found");//404
		}
		else{


			return noContent();//204 (user account deleted successfully)
		}
	}
}
