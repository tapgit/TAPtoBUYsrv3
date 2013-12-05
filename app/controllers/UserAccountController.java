package controllers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import models.Address;
import models.CreditCard;
import models.Rating;
import models.User;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import dbman.DBManager;

import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import testmodels.Test;

public class UserAccountController extends Controller {

	//DONE
	public static Result checkLogin(){
		JsonNode json = request().body().asJson();
		if(json == null) {
			return badRequest("Expecting Json data");//400
		} 
		else {
			try{
				Class.forName(DBManager.driver);
				Connection connection = DriverManager.getConnection(DBManager.db,DBManager.user,DBManager.pass);
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
					return ok(respJson);//200 (send client the userId of the user that's being signed in)
				}
				else{
					rset = statement.executeQuery("select uid " + 
							"from users " +
							"where username = '"+username+"' and pass = '" + password +"';");
					if(rset.next()){
						respJson.put("admin", false);
						respJson.put("id", rset.getInt(1));
						return ok(respJson);//200 (send client the userId of the user that's being signed in)
					}
					else{
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

			//			String firstname = json.get("firstname").getTextValue();
			//			String lastname = json.get("lastname").getTextValue();
			//			String username = json.get("username").getTextValue();
			//			String password = json.get("password").getTextValue();
			//			String email = json.get("email").getTextValue();
			//			//			//Shipping Address
			//			JsonNode shippingAddress = json.get("shipping_address");
			//			String shipCountry = shippingAddress.get("country").getTextValue();
			//			//FALTA		String shipContactName = shippingAddress.get("contact_name").getTextValue();
			//			String shipStreet = shippingAddress.get("street").getTextValue();
			//			String shipCity = shippingAddress.get("city").getTextValue();
			//			String shipState = shippingAddress.get("state").getTextValue();
			//			String shipZipCode = shippingAddress.get("zip_code").getTextValue();
			//			String shipTelephone = shippingAddress.get("telephone").getTextValue();
			//			//Billing Address
			//			JsonNode billingAddress = json.get("billing_address");
			//			String billCountry = billingAddress.get("country").getTextValue();
			//			//FALTA     String billContactName = billingAddress.get("contact_name").getTextValue();
			//			String billStreet = billingAddress.get("street").getTextValue();
			//			String billCity = billingAddress.get("city").getTextValue();
			//			String billState = billingAddress.get("state").getTextValue();
			//			String billZipCode = billingAddress.get("zip_code").getTextValue();
			//			String billTelephone = billingAddress.get("telephone").getTextValue();
			//			//			//Credit Card
			//			JsonNode creditCard = json.get("credit_card");
			//			String creditCardNumber = creditCard.get("number").getTextValue();
			//			String creditCardHoldersName = creditCard.get("holders_name").getTextValue();
			//			String creditCardExpDate = creditCard.get("exp_date").getTextValue();		


			//create user with his/her cart
			return created();//201
		}

	}
	//DONE
	public static Result getUserAccountInfo(int userId){
		try{
			Class.forName(DBManager.driver);
			Connection connection = DriverManager.getConnection(DBManager.db,DBManager.user,DBManager.pass);
			Statement statement = connection.createStatement();

			//Get user info
			ResultSet rset = statement.executeQuery("select username, pass,fname,lname,email,count(distinct shipAddr_Id) as shipCount, count(distinct crCard_Id) as crCardCount " + 
					"from users natural join shipping_address natural join credit_card " +
					"where uid = " + userId + " " + 
					"group by username, pass, fname, lname, email;");

			if(rset.next()){
				String username = rset.getString("username");
				String pass = rset.getString("pass");
				String fname = rset.getString("fname");
				String lname = rset.getString("lname");
				String email = rset.getString("email");
				Address[] shippingAddresses = new Address[rset.getInt("shipCount")];
				CreditCard[] creditCards = new CreditCard[rset.getInt("crCardCount")];

				//Get shipping addresses:
				rset = statement.executeQuery("select shipaddr_id,country, contact_name, street, city, state, zip_code, telephone " +
						"from shipping_address " +
						"where uid = " + userId + ";"); 
				int i=0;
				while(rset.next()){
					shippingAddresses[i++] = new Address(rset.getInt(1), rset.getString(2), rset.getString(3), 
							rset.getString(4), rset.getString(5), rset.getString(6), rset.getString(7), rset.getString(8));
				}
				//Get CreditCard-BillingAddress pair

				rset = statement.executeQuery("select billaddr_id,country,contact_name,street,city,state,zip_code,telephone, " +
						"sec_number,holders_name,exp_date " +
						"from billing_address natural join credit_card " +
						"where uid = " + userId + ";");
				Address tempBillingAddress = null;
				i=0;
				while(rset.next()){
					tempBillingAddress =  new Address(rset.getInt(1), rset.getString(2), rset.getString(3), 
							rset.getString(4), rset.getString(5), rset.getString(6), rset.getString(7), rset.getString(8));

					creditCards[i++] = new CreditCard(rset.getString(9), rset.getString(9), rset.getString(9), tempBillingAddress);
				}
				User user = new User(userId,fname, lname, username, pass, email, shippingAddresses, creditCards);

				return ok(Json.toJson(user));//200 respond with user data
			}
			else{
				return notFound("User not found");//404
			}
		}
		catch (Exception e) {
			Logger.info("EXCEPTION ON SIGNIN");
			e.printStackTrace();
			return notFound();
		}
	}

	//DONE pero de debe chequiar
	public static Result getRatingList(int productId){
		try{
			Class.forName(DBManager.driver);
			Connection connection = DriverManager.getConnection(DBManager.db,DBManager.user,DBManager.pass);
			Statement statement = connection.createStatement();
			ResultSet rset = statement.executeQuery("select username, stars " +
					"from item_for_sale natural join ranks as rnk(b_uid,uid,stars),users " +
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
					"from item_for_auction natural join ranks as rnk(b_uid,uid,stars),users " +
					"where iid = " + productId + " and users.uid = b_uid");
			while(rset.next()){
				buyersAndStars = Json.newObject();
				rating = new Rating(rset.getString("username"), rset.getInt("stars"));
				array.add(Json.toJson(rating));
			}
			respJson.put("ratingslist",array);
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
		else if(userId!=16){
			return notFound("User not found");//404
		}
		else{





			Logger.info(json.toString());
			return ok("User account has been updated");//200
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
