package controllers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import models.Address;
import models.Bid;
import models.Product;
import models.ProductForAuction;
import models.ProductForAuctionInfo;
import models.ProductForSale;
import models.ProductForSaleInfo;
import models.User;

import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.json.JSONArray;
import org.json.JSONObject;

import dbman.DBManager;




import play.Logger;

import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import testmodels.Test;

public class ProductController extends Controller {
	public static String andrImgDir = "http://10.0.2.2:9000/images/";
	public static String andrScaledImgDir = "http://10.0.2.2:9000/images/scaled/";
	
	//DONE
	public static Result getProductInfo(int productId){
		try{
			Class.forName(DBManager.driver);
			Connection connection = DriverManager.getConnection(DBManager.db,DBManager.user,DBManager.pass);
			Statement statement = connection.createStatement();

			ObjectNode itemInfoJson = Json.newObject();
			String findDate[] = {"days","hours","minutes","seconds"};
			int tCount = 0;
			String timeRemaining = "";
			ResultSet rset = statement.executeQuery("select * from item_for_sale where iid = " + productId + ";"); 
			
			if(rset.next()){//if for sale
				itemInfoJson.put("forBid", false);
				rset = statement.executeQuery("select iid,ititle,ishipping_price, username,avg(stars),remaining_quantity,instant_price,product,model,brand,dimensions,description, " +
											  "to_char(istart_sale_date + itime_duration - current_timestamp,'DD') as days,to_char(istart_sale_date + itime_duration - current_timestamp,'HH24') as hours, " +
											  "to_char(istart_sale_date + itime_duration - current_timestamp,'MI') as minutes, to_char(istart_sale_date + itime_duration - current_timestamp,'SS') as seconds " +
											  "from item natural join item_for_sale natural join item_info natural join users natural join ranks as rnk(b_uid,uid,stars) " +
											  "where iid = " + productId + " " +
											  "group by iid,ititle,ishipping_price,username,remaining_quantity,instant_price,product,model,brand,dimensions,description;");
				rset.next();
				for(int i=0;i<4;i++){
					if(!rset.getString(findDate[i]).equals("00") && tCount < 2){
						timeRemaining+=rset.getString(findDate[i]) + findDate[i].charAt(0) + " ";
						tCount++;
					}
				}
				Product itemInfo = new ProductForSaleInfo(rset.getInt("iid"), rset.getString("ititle"), timeRemaining, rset.getDouble("ishipping_price"), 
						andrImgDir + "img" + rset.getInt("iid") +".jpg", rset.getString("username"), rset.getDouble("avg"),rset.getInt("remaining_quantity"), rset.getDouble("instant_price"),
						rset.getString("product"), rset.getString("model"), rset.getString("brand"), rset.getString("dimensions"), rset.getString("description"));
				itemInfoJson.putPOJO("productInfo", Json.toJson(itemInfo));
				return ok(itemInfoJson);//200
			}
			else{//for auction
				itemInfoJson.put("forBid", true);
				rset = statement.executeQuery("select iid,ititle,ishipping_price, username,avg(stars),total_bids,current_bid_price,product,model,brand,dimensions,description, " +
											  "to_char(istart_sale_date + itime_duration - current_timestamp,'DD') as days,to_char(istart_sale_date + itime_duration - current_timestamp,'HH24') as hours, " +
											  "to_char(istart_sale_date + itime_duration - current_timestamp,'MI') as minutes, to_char(istart_sale_date + itime_duration - current_timestamp,'SS') as seconds " +
											  "from item natural join item_for_auction natural join item_info natural join users natural join ranks as rnk(b_uid,uid,stars) " +
											  "where iid = " + productId + " " +
											  "group by iid,ititle,ishipping_price,username,total_bids,current_bid_price,product,model,brand,dimensions,description;");
				if(rset.next()){
					for(int i=0;i<4;i++){
						if(!rset.getString(findDate[i]).equals("00") && tCount < 2){
							timeRemaining+=rset.getString(findDate[i]) + findDate[i].charAt(0) + " ";
							tCount++;
						}
					}
					Product itemInfo = new ProductForAuctionInfo(rset.getInt("iid"), rset.getString("ititle"), timeRemaining, rset.getDouble("ishipping_price"), 
							andrImgDir + "img" + rset.getInt("iid") +".jpg", rset.getString("username"), rset.getDouble("avg"), -1, rset.getDouble("current_bid_price"), 
							rset.getInt("total_bids"), rset.getString("product"), rset.getString("model"), rset.getString("brand"), rset.getString("dimensions"), rset.getString("description"));
							itemInfoJson.putPOJO("productInfo", Json.toJson(itemInfo));		
					return ok(itemInfoJson);//200 
				}
				else{
					return notFound("No product found with the requested id");//404
				}
			}
		}
		catch (Exception e) {
			Logger.info("EXCEPTION ON PRODUCT INFO");
			e.printStackTrace();
			return notFound();
		}
	}

	//DONE
	public static Result getAllSellingProducts(int userId){
		try{
			Class.forName(DBManager.driver);
			Connection connection = DriverManager.getConnection(DBManager.db,DBManager.user,DBManager.pass);
			Statement statement = connection.createStatement();
			ResultSet rset = statement.executeQuery("select * " +
													"from (select iid,ititle,ishipping_price,remaining_quantity as num1,instant_price as price,uid,false as forBid,to_char(istart_sale_date + itime_duration - current_timestamp,'DD') as days,to_char(istart_sale_date + itime_duration - current_timestamp,'HH24') as hours, " +
													"to_char(istart_sale_date + itime_duration - current_timestamp,'MI') as minutes, to_char(istart_sale_date + itime_duration - current_timestamp,'SS') as seconds " +
													"from item natural join item_for_sale " + 
													"group by iid,ititle,ishipping_price,remaining_quantity,instant_price,uid " +
													"union " + 
													"select iid,ititle,ishipping_price,total_bids as num1,current_bid_price as price,uid,true as forBid,to_char(istart_sale_date + itime_duration - current_timestamp,'DD') as days,to_char(istart_sale_date + itime_duration - current_timestamp,'HH24') as hours, " +
													"to_char(istart_sale_date + itime_duration - current_timestamp,'MI') as minutes, to_char(istart_sale_date + itime_duration - current_timestamp,'SS') as seconds " +
													"from item natural join item_for_auction " +
													"group by iid,ititle,ishipping_price,total_bids,current_bid_price,uid) as results " +
													"where results.uid = " + userId + ";");
			
			ObjectNode respJson = Json.newObject();
			ArrayNode array = respJson.arrayNode();
			ObjectNode itemJson = null;
			Product item = null;
			
			String findDate[] = {"days","hours","minutes","seconds"};
			
			while(rset.next()){
				itemJson = Json.newObject();
				int tCount = 0;
				String timeRemaining = "";
				for(int i=0;i<4;i++){
					if(!rset.getString(findDate[i]).equals("00") && tCount < 2){
						timeRemaining+=rset.getString(findDate[i]) + findDate[i].charAt(0) + " ";
						tCount++;
					}
				}
				if(!rset.getBoolean("forBid")){//for sale
					item = new ProductForSale(rset.getInt("iid"), rset.getString("ititle"), timeRemaining, rset.getDouble("ishipping_price"), 
							andrScaledImgDir + "img" + rset.getInt("iid") +".jpg", "", -1, rset.getInt("num1"), rset.getDouble("price"));
					itemJson.put("forBid", false);
				}
				else{//for auction
					item = new ProductForAuction(rset.getInt("iid"), rset.getString("ititle"), timeRemaining, rset.getDouble("ishipping_price"), 
							andrScaledImgDir + "img" + rset.getInt("iid") +".jpg", "", -1, -1, rset.getDouble("price"), rset.getInt("num1"));
					itemJson.put("forBid", true);
				}
				itemJson.putPOJO("item", Json.toJson(item));
				array.add(itemJson);
			}
		respJson.put("mySellingItems", array);
		return ok(respJson);//200
		}
		catch (Exception e) {
			Logger.info("EXCEPTION ON MY SELLING PRODUCTS");
			e.printStackTrace();
			return notFound();
		}
	}
	public static Result sellAProduct(int userId){
		JsonNode json = request().body().asJson();
		if(json == null) {
			return badRequest("Expecting Json data");//400
		} 
		else {
//			if(userId !=16){
//				return notFound("User not found");//404
//			}
//			else{
				Product theItem = null;
				JsonNode productInfoJson = json.get("productInfo");
				if(json.get("forBid").getBooleanValue()){
					theItem = new ProductForAuctionInfo(-1, productInfoJson.get("title").getTextValue(),
							productInfoJson.get("timeRemaining").getTextValue(), productInfoJson.get("shippingPrice").getDoubleValue(),
							productInfoJson.get("imgLink").getTextValue(), null, -1,  productInfoJson.get("startinBidPrice").getDoubleValue(),  
							productInfoJson.get("currentBidPrice").getDoubleValue(),  productInfoJson.get("totalBids").getIntValue(),
							productInfoJson.get("product").getTextValue(),productInfoJson.get("model").getTextValue(),
							productInfoJson.get("brand").getTextValue(),productInfoJson.get("dimensions").getTextValue(),
							productInfoJson.get("description").getTextValue());
				}
				else{
					theItem = new ProductForSaleInfo(-1, productInfoJson.get("title").getTextValue(), 
							productInfoJson.get("timeRemaining").getTextValue(), productInfoJson.get("shippingPrice").getDoubleValue(),
							productInfoJson.get("imgLink").getTextValue(), null, -1, productInfoJson.get("remainingQuantity").getIntValue(), 
							productInfoJson.get("instantPrice").getDoubleValue(),
							productInfoJson.get("product").getTextValue(),productInfoJson.get("model").getTextValue(),
							productInfoJson.get("brand").getTextValue(),productInfoJson.get("dimensions").getTextValue(),
							productInfoJson.get("description").getTextValue());
				}
				//calculate an id for the item (current id is -1 (not assigned)) 
				//and then add it to the db
				theItem.setId(0);

				return ok();//200 (product is now on sale)
			}

//		}
	}
	public static Result updateASellingProduct(int userId, int productId){
		JsonNode json = request().body().asJson();
		if(json == null) {
			return badRequest("Expecting Json data");//400
		} 
		else {
//			if(userId != 16){
//				return notFound("User not found");//404
//			}
//			else if(!(productId >=0 && productId < 6)){
//				return notFound("Product not found");//404
//			}
//			else{
				Product theItem = null;
				JsonNode productInfoJson = json.get("productInfo");
				if(json.get("forBid").getBooleanValue()){
					theItem = new ProductForAuctionInfo(productId, productInfoJson.get("title").getTextValue(),
							productInfoJson.get("timeRemaining").getTextValue(), productInfoJson.get("shippingPrice").getDoubleValue(),
							productInfoJson.get("imgLink").getTextValue(),  null, -1,  productInfoJson.get("startinBidPrice").getDoubleValue(),  
							productInfoJson.get("currentBidPrice").getDoubleValue(),  productInfoJson.get("totalBids").getIntValue(),
							productInfoJson.get("product").getTextValue(),productInfoJson.get("model").getTextValue(),
							productInfoJson.get("brand").getTextValue(),productInfoJson.get("dimensions").getTextValue(),
							productInfoJson.get("description").getTextValue());
				}
				else{
					theItem = new ProductForSaleInfo(productId, productInfoJson.get("title").getTextValue(), 
							productInfoJson.get("timeRemaining").getTextValue(), productInfoJson.get("shippingPrice").getDoubleValue(),
							productInfoJson.get("imgLink").getTextValue(), null, -1, 
							productInfoJson.get("remainingQuantity").getIntValue(),productInfoJson.get("instantPrice").getDoubleValue(),
							productInfoJson.get("product").getTextValue(),productInfoJson.get("model").getTextValue(),
							productInfoJson.get("brand").getTextValue(),productInfoJson.get("dimensions").getTextValue(),
							productInfoJson.get("description").getTextValue());
				}
				//update item information in the db

				return ok();//200 (product is now on sale)
			}

		//}
	}
	public static Result quitFromSelling(int userId, int productId){ //Includes items for sale and items in auctions
		Logger.info("user ID = " + userId + " product Id to remove = " + productId);
//		if(userId != 16){
//			return notFound("No cart found related to that user id");//404
//		}
//		else if(!(productId >=0 && productId < 6)){
//			return notFound("Product not found");//404
//		}
//		else{
			//Quit from sale
			return noContent();//204 (product removed from sale successfully)
		//}
	}
	
	//DONE
	public static Result buyNow(int userId){
		JsonNode json = request().body().asJson();
		if(json == null) {
			return badRequest("Expecting Json data");//400
		} 
		else {
				try{
					Logger.info(json.toString());
					JSONArray array = (new JSONObject(json.toString())).getJSONArray("productIdsToBuy");
					String buyNowIdsList = null;
					if(array.length()==0){
						return badRequest("Expecting productlist");
					}
					else{
						buyNowIdsList = "("+ array.getInt(0);
						for(int i=1;i<array.length();i++){
							buyNowIdsList += "," + array.getInt(i);
						}
						buyNowIdsList += ")";
					}
					Logger.info("Received buynow ids list: " + buyNowIdsList);
					Class.forName(DBManager.driver);
					Connection connection = DriverManager.getConnection(DBManager.db,DBManager.user,DBManager.pass);
					Statement statement = connection.createStatement();
					ResultSet rset = statement.executeQuery("select * " + 
															"from (select iid,ititle,ishipping_price,remaining_quantity as num1,instant_price as price,false as forBid, " +
															"to_char(istart_sale_date + itime_duration - current_timestamp,'DD') as days,to_char(istart_sale_date + itime_duration - current_timestamp,'HH24') as hours, " +
															"to_char(istart_sale_date + itime_duration - current_timestamp,'MI') as minutes, to_char(istart_sale_date + itime_duration - current_timestamp,'SS') as seconds, " +
															"username,avg(stars) " +
															"from item natural join item_for_sale natural join users natural join ranks as rnk(b_uid,uid,stars) " +
															"where item_for_sale.iid in " + buyNowIdsList + " " +
															"group by iid,ititle,ishipping_price,remaining_quantity,instant_price,username " +
															"union " +
															"select iid,ititle,ishipping_price,total_bids as num1,current_bid_price as price,true as forBid, " +
															"to_char(istart_sale_date + itime_duration - current_timestamp,'DD') as days,to_char(istart_sale_date + itime_duration - current_timestamp,'HH24') as hours, " +
															"to_char(istart_sale_date + itime_duration - current_timestamp,'MI') as minutes, to_char(istart_sale_date + itime_duration - current_timestamp,'SS') as seconds, " +
															"username,avg(stars) " +
															"from item natural join item_for_auction natural join users natural join ranks as rnk(b_uid,uid,stars) " +
															"where item_for_auction.iid in " + buyNowIdsList + " " +
															"group by iid,ititle,ishipping_price,total_bids,current_bid_price,username) as results;");
					
					ObjectNode respJson = Json.newObject();
					ArrayNode respArray = respJson.arrayNode();
	
					ObjectNode itemJson = null;
					double pricesTotal = 0;
					double shippingsTotal = 0;
					Product item = null;
					
					String findDate[] = {"days","hours","minutes","seconds"};
					
					while(rset.next()){
						itemJson = Json.newObject();
						int tCount = 0;
						String timeRemaining = "";
						for(int i=0;i<4;i++){
							if(!rset.getString(findDate[i]).equals("00") && tCount < 2){
								timeRemaining+=rset.getString(findDate[i]) + findDate[i].charAt(0) + " ";
								tCount++;
							}
						}
						if(!rset.getBoolean("forBid")){//for sale
							item = new ProductForSale(rset.getInt("iid"), rset.getString("ititle"), timeRemaining, rset.getDouble("ishipping_price"), 
									andrScaledImgDir + "img" + rset.getInt("iid") +".jpg",  rset.getString("username"), rset.getDouble("avg"), rset.getInt("num1"), rset.getDouble("price"));
							itemJson.put("forBid", false);
						}
						else{//for auction
							item = new ProductForAuction(rset.getInt("iid"), rset.getString("ititle"), timeRemaining, rset.getDouble("ishipping_price"), 
									andrScaledImgDir + "img" + rset.getInt("iid") +".jpg",  rset.getString("username"), rset.getDouble("avg"), -1, rset.getDouble("price"), rset.getInt("num1"));
							itemJson.put("forBid", true);
						}
						pricesTotal+=rset.getDouble("price");
						shippingsTotal+=rset.getDouble("ishipping_price");
						itemJson.putPOJO("item", Json.toJson(item));
						respArray.add(itemJson);
					}
					respJson.put("productsToBuy", respArray);
					respJson.put("total", "$"+ new DecimalFormat("##.##").format(pricesTotal) + " (Shipping: $" + new DecimalFormat("##.##").format(shippingsTotal) +")");
					Logger.info(respJson.toString());
					return ok(respJson);
				}
				catch (Exception e) {
					Logger.info("EXCEPTION ON MY SELLING PRODUCTS");
					e.printStackTrace();
					return notFound();
				}
		}
	}
	public static Result placeOrder(int userId){
		return TODO;
	}

}
