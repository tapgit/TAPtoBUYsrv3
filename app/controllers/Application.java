package controllers;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import models.Address;
import models.Bid;
import models.MyHistoryProduct;
import models.MyHistoryProductForAuction;
import models.MyHistoryProductForSale;
import models.Product;
import models.ProductForAuction;
import models.ProductForAuctionInfo;
import models.ProductForSale;
import models.ProductForSaleInfo;
import models.User;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import dbman.DBManager;

import play.*;
import play.mvc.*;
import play.libs.Json;
import testmodels.Test;
import views.html.*;

public class Application extends Controller {

	public static String andrScaledImgDir = "http://10.0.2.2:9000/images/scaled/";

	public static Result testdb(){
		DBManager.getAll();
		return ok();
	}
	public static Result index() {
		return ok(index.render("Your new application is ready."));
	}

	public static Result getUserActivityHistory(int userId){

		try{
			Class.forName(DBManager.driver);
			Connection connection = DriverManager.getConnection(DBManager.db,DBManager.user,DBManager.pass);
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
						rset.getDouble("item_finalprice"), rset.getDouble("shipping_price"), andrScaledImgDir + "img" + rset.getInt("iid") +".jpg", 
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
						rset.getDouble("item_finalprice"), rset.getDouble("shipping_price"), andrScaledImgDir + "img" + rset.getInt("iid") +".jpg", 
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
						rset.getDouble("items_price"), rset.getDouble("shipping_price"), andrScaledImgDir + "img" + rset.getInt("iid") +".jpg",rset.getString("username"),-1, -1);
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
						rset.getDouble("items_price"), rset.getDouble("shipping_price"), andrScaledImgDir + "img" + rset.getInt("iid") +".jpg",rset.getString("username"),-1, -1);
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
	public static Result getOrderInfo(int orderId){
		
		
		
		
		
		return TODO;
		
	}
	
}
