package controllers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import man.Manager;
import models.Product;
import models.ProductForSale;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;


import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

public class CartController extends Controller {

	//DONE
	public static Result getCartItems(int userId){	
		try{
			Class.forName(Manager.driver);
			Connection connection = DriverManager.getConnection(Manager.db,Manager.user,Manager.pass);
			Statement statement = connection.createStatement();
			ProductController.updateItemsAvailability();
			ObjectNode respJson = Json.newObject();
			ArrayNode array = respJson.arrayNode();
			ObjectNode itemJson = null;
			ProductForSale item = null;
			ResultSet rset = statement.executeQuery("select iid,ititle,instant_price,ishipping_price,username,avg(stars) " +
													"from item natural join item_for_sale natural join users natural left outer join ranks as rnk(b_uid,uid,stars) " +
													"where iid in (select iid from user_cart_item where uid = " + userId + " and user_cart_item.available = true) and item.available = true " + 
													"group by iid,ititle,instant_price,ishipping_price,username;"); 
			while(rset.next()){
				itemJson = Json.newObject();
				item = new ProductForSale(rset.getInt("iid"), rset.getString("ititle"), null, rset.getDouble("ishipping_price"), 
						Manager.andrScaledImgDir + "img" + rset.getInt("iid") +".jpg", rset.getString("username"), rset.getDouble("avg"), -1, rset.getDouble("instant_price"));
				itemJson.putPOJO("item", Json.toJson(item));
				array.add(itemJson);
			}
			
			respJson.put("cart", array);
			connection.close();
			return ok(respJson);//200
		}
		catch (Exception e) {
			Logger.info("EXCEPTION ON CART ITEMS");
			e.printStackTrace();
			return notFound();
		}
	}
	
	
	public static Result addItemToCart(int userId, int productId){
		try{
			Class.forName(Manager.driver);
			Connection connection = DriverManager.getConnection(Manager.db,Manager.user,Manager.pass);
			Statement statement = connection.createStatement();
			ProductController.updateItemsAvailability();
			
			ResultSet rset = statement.executeQuery("select iid from item natural join item_for_sale where iid = "+productId+" and item.available = true;");
			if(rset.next()){//if the item is avaiable
				rset = statement.executeQuery("select available from user_cart_item where uid = "+userId+" and iid = "+productId+";");
				if(rset.next()){//if has been or is currently on this cart
					if(rset.getBoolean("available")){
						connection.close();
						return badRequest("Item already on cart");//400
					}
					else{//add to cart (update current row)
						statement.executeUpdate("update user_cart_item set available = true where uid = "+userId+" and iid = "+productId+";");
						connection.close();
						return ok();//200
					}
				}
				else{//add to cart (create new row)
					statement.executeUpdate("insert into user_cart_item(uid,iid,available) values ("+userId+","+productId+",true);");
					connection.close();
					return ok();//200
				}
			}
			else{//Time ended or item sold (item not avaiable)
				connection.close();
				return notFound("Ended Sale");//404
			}
		}
		catch (Exception e) {
			Logger.info("EXCEPTION ON MY PLACE BID");
			e.printStackTrace();
			return internalServerError();//500
		}
	}
	public static Result removeItemFromCart(int userId, int productId){
		Logger.info("user ID = " + userId + " product Id to remove = " + productId);
		try{
			Class.forName(Manager.driver);
			Connection connection = DriverManager.getConnection(Manager.db,Manager.user,Manager.pass);
			Statement statement = connection.createStatement();
			statement.executeUpdate("update user_cart_item set available = false where uid = "+userId+" and iid = "+productId+";");
			connection.close();
			return noContent();//204 (product removed from cart successfully)
		}
		catch (Exception e) {
			Logger.info("EXCEPTION ON REMOVE ITEM FROM CART");
			e.printStackTrace();
			return internalServerError();
		}
	}
	
}
