package controllers;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import man.Manager;
import models.Address;
import models.Bid;
import models.MyHistoryProduct;
import models.MyHistoryProductForAuction;
import models.MyHistoryProductForSale;
import models.Order;
import models.Product;
import models.ProductForAuction;
import models.ProductForAuctionInfo;
import models.ProductForSale;
import models.ProductForSaleInfo;
import models.User;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;


import play.*;
import play.mvc.*;
import play.libs.Json;
import testmodels.Test;
import views.html.*;

public class Application extends Controller {

	public static Result index() {
		return ok(index.render("Your new application is ready."));
	}

	//DONE
	public static Result getUserActivityHistory(int userId){
		try{
			Class.forName(Manager.driver);
			Connection connection = DriverManager.getConnection(Manager.db,Manager.user,Manager.pass);
			Statement statement = connection.createStatement();
			ObjectNode respJson = Json.newObject();
			ArrayNode array = respJson.arrayNode();
			ObjectNode itemJson = null;
			MyHistoryProduct item = null;

			//Buscar los de subasta que he vendido
			ResultSet rset = statement.executeQuery("select iid,auc_order_id,ititle,item_finalprice,shipping_price,username " + 
					"from auction_order join item using(iid) natural join item_for_auction, users " +
					"where users.uid = buyer_uid and seller_uid = " + userId + ";");

			while(rset.next()){
				itemJson = Json.newObject();
				item = new MyHistoryProductForAuction(rset.getInt("iid"), rset.getInt("auc_order_id"), rset.getString("ititle"), 
						rset.getDouble("item_finalprice"), rset.getDouble("shipping_price"), Manager.andrScaledImgDir + "img" + rset.getInt("iid") +".jpg", 
						rset.getString("username"),-1,-1);
				itemJson.put("forBid", true);
				itemJson.put("sold", true);
				itemJson.putPOJO("item", Json.toJson(item));
				array.add(itemJson);
			}

			//Buscar los de subasta que he comprado
			rset = statement.executeQuery("select iid,auc_order_id,ititle,item_finalprice,shipping_price,username " + 
					"from auction_order join item using(iid) natural join item_for_auction natural join users " +
					"where buyer_uid = " + userId + ";");
			while(rset.next()){
				itemJson = Json.newObject();
				item = new MyHistoryProductForAuction(rset.getInt("iid"), rset.getInt("auc_order_id"), rset.getString("ititle"), 
						rset.getDouble("item_finalprice"), rset.getDouble("shipping_price"), Manager.andrScaledImgDir + "img" + rset.getInt("iid") +".jpg", 
						rset.getString("username"),-1,-1);
				itemJson.put("forBid", true);
				itemJson.put("sold", false);
				itemJson.putPOJO("item", Json.toJson(item));
				array.add(itemJson);
			}

			//Buscar los de buynow que he vendido
			rset = statement.executeQuery("select iid,buynow_order_id,ititle,items_price,shipping_price,username " + 
					"from seller_buynow_order natural join buynow_order natural join buynow_order_items join item using(iid) natural join item_for_sale, users " +
					"where users.uid = buyer_uid and seller_id = " + userId +";");
			while(rset.next()){
				itemJson = Json.newObject();
				item = new MyHistoryProductForSale(rset.getInt("iid"), rset.getInt("buynow_order_id"), rset.getString("ititle"), 
						rset.getDouble("items_price"), rset.getDouble("shipping_price"), Manager.andrScaledImgDir + "img" + rset.getInt("iid") +".jpg",rset.getString("username"),-1, -1);
				itemJson.put("forBid", false);
				itemJson.put("sold", true);
				itemJson.putPOJO("item", Json.toJson(item));
				array.add(itemJson);
			}

			//Buscar los de buynow que he comprado
			rset = statement.executeQuery("select iid,buynow_order_id,ititle,items_price,shipping_price,username " + 
					"from seller_buynow_order natural join buynow_order natural join buynow_order_items join item using(iid) natural join item_for_sale natural join users " +
					"where buyer_uid = " + userId +";");
			while(rset.next()){
				itemJson = Json.newObject();
				item = new MyHistoryProductForSale(rset.getInt("iid"), rset.getInt("buynow_order_id"), rset.getString("ititle"), 
						rset.getDouble("items_price"), rset.getDouble("shipping_price"), Manager.andrScaledImgDir + "img" + rset.getInt("iid") +".jpg",rset.getString("username"),-1, -1);
				itemJson.put("forBid", false);
				itemJson.put("sold", false);
				itemJson.putPOJO("item", Json.toJson(item));
				array.add(itemJson);
			}
			respJson.put("myHistory", array);
			return ok(respJson);//200
		}
		catch (Exception e) {
			Logger.info("EXCEPTION ON MY HISTORY");
			e.printStackTrace();
			return notFound();
		}
	}

	//IN Progress....
	public static Result getOrderInfo(int orderId, boolean forBid, boolean sold){
		try{
			Class.forName(Manager.driver);
			Connection connection = DriverManager.getConnection(Manager.db,Manager.user,Manager.pass);
			Statement statement = connection.createStatement();
			ObjectNode respJson = Json.newObject();
			ResultSet rset = null;
			Order theOrder = null;
			String shippingAddress = "";
			String creditCard = "";
			if(forBid){
				if(sold){
					rset = statement.executeQuery("select auc_order_id, pay_email,auc_order_date " + 
							"from auction_order natural join paypal " + 
							"where auc_order_id = " + orderId + ";");
					rset.next();
					theOrder = new Order(rset.getInt("auc_order_id"), rset.getString("auc_order_date"), null, rset.getString("pay_email"), -1, -1);
				}
				else{
					rset = statement.executeQuery("select auc_order_id, auc_order_date, contact_name, street, city,state, zip_code, country,telephone, sec_number " + 
							"from auction_order, shipping_address, credit_card " +
							"where auction_order.shipAddr_Id = shipping_address.shipAddr_id and " + 
							"auction_order.crCard_id = credit_card.crCard_id and auc_order_id =  " + orderId + ";");
					rset.next();
					shippingAddress = rset.getString("contact_name") + "\n" + rset.getString("street")+  "\n" + rset.getString("city") + 
							" " + rset.getString("state") + " " + rset.getString("zip_code") + "\n" + rset.getString("country") + "\n" + 
							rset.getString("telephone");
					creditCard =  "xxxx-xxxx-xxxx-" + rset.getString("sec_number").substring(12);
					theOrder = new Order(rset.getInt("auc_order_id"), rset.getString("auc_order_date"), shippingAddress, creditCard, -1, -1);
				}
			}
			else{
				if(sold){
					rset = statement.executeQuery("select buynow_order_id, pay_email, buynow_order_date " + 
							"from seller_buynow_order natural join buynow_order natural join paypal " + 
							"where buynow_order_id = " + orderId + ";");
					rset.next();
					theOrder = new Order(rset.getInt("buynow_order_id"), rset.getString("buynow_order_date"), null, rset.getString("pay_email"), -1, -1);
				}
				else{
					rset = statement.executeQuery("select buynow_order_id, buynow_order_date, contact_name, street, city,state, zip_code, country,telephone, sec_number " + 
												  "from buynow_order, shipping_address, credit_card " + 
												  "where buynow_order.shipAddr_Id = shipping_address.shipAddr_id and " + 
												  "buynow_order.crCard_id = credit_card.crCard_id and buynow_order_id =  " + orderId + ";");

					rset.next();
					shippingAddress = rset.getString("contact_name") + "\n" + rset.getString("street")+  "\n" + rset.getString("city") + 
							" " + rset.getString("state") + " " + rset.getString("zip_code") + "\n" + rset.getString("country") + "\n" + 
							rset.getString("telephone");
					creditCard =  "xxxx-xxxx-xxxx-" + rset.getString("sec_number").substring(12);
					theOrder = new Order(rset.getInt("buynow_order_id"), rset.getString("buynow_order_date"), shippingAddress, creditCard, -1, -1);
				}
			}
			respJson.putPOJO("order", Json.toJson(theOrder));
			return ok(respJson);
		}
		catch (Exception e) {
			Logger.info("EXCEPTION ON ORDER RECEIPT");
			e.printStackTrace();
			return notFound();
		}
	}

}
