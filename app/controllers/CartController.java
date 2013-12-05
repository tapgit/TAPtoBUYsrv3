package controllers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import models.Product;
import models.ProductForSale;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import dbman.DBManager;

import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import testmodels.Test;

public class CartController extends Controller {

	public static String andrScaledImgDir = "http://10.0.2.2:9000/images/scaled/";
	//DONE
	public static Result getCartItems(int userId){	
		try{
			Class.forName(DBManager.driver);
			Connection connection = DriverManager.getConnection(DBManager.db,DBManager.user,DBManager.pass);
			Statement statement = connection.createStatement();
			ObjectNode respJson = Json.newObject();
			ArrayNode array = respJson.arrayNode();
			ObjectNode itemJson = null;
			ProductForSale item = null;
			ResultSet rset = statement.executeQuery("select iid,ititle,instant_price,ishipping_price,username,avg(stars) " +
													"from item natural join item_for_sale natural join users natural join ranks as rnk(b_uid,uid,stars) " +
													"where iid in (select iid from user_cart_item where uid = " + userId + ") " + 
													"group by iid,ititle,instant_price,ishipping_price,username;"); 
			while(rset.next()){
				itemJson = Json.newObject();
				item = new ProductForSale(rset.getInt("iid"), rset.getString("ititle"), null, rset.getDouble("ishipping_price"), 
						andrScaledImgDir + "img" + rset.getInt("iid") +".jpg", rset.getString("username"), rset.getDouble("avg"), -1, rset.getDouble("instant_price"));
				itemJson.putPOJO("item", Json.toJson(item));
				array.add(itemJson);
			}
			
			respJson.put("cart", array);
			return ok(respJson);//200
		}
		catch (Exception e) {
			Logger.info("EXCEPTION ON CART ITEMS");
			e.printStackTrace();
			return notFound();
		}
	}
	
	
	public static Result addItemToCart(int userId, int productId){
		JsonNode json = request().body().asJson();
		if(json == null) {
			return badRequest("Expecting Json data");//400
		} 
		else {
			if(userId != 16){
				return notFound("No cart found related to that user id");//404
			}
			else if(!(productId >=0 && productId < 6)){
				return notFound("Product not found");//404
			}
			else{
				//Add item to cart
				
				return ok();//200 (item was added to cart successfully)
			}
		}
	}
	public static Result removeItemFromCart(int userId, int productId){
			if(userId != 16){
				return notFound("No cart found related to that user id");//404
			}
			else if(!(productId >=0 && productId < 6)){
				return notFound("Product not found");//404
			}
			else{
				//Delete item from cart
				
				return noContent();//204 (item removed from cart successfully)
			}
		}
	
	
}
