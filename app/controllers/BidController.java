package controllers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import models.Bid;
import models.MyBiddingsProduct;
import models.Product;
import models.ProductForAuction;
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

public class BidController extends Controller {
	public static String andrScaledImgDir = "http://10.0.2.2:9000/images/scaled/";
	
	public static Result placeBid(){
//		ArrayList<Product> allItems = Test.getProductList();
//		JsonNode json = request().body().asJson();
//		if(json == null) {
//			return badRequest("Expecting Json data");//400
//		} 
//		else{
//			Bid newBid = new Bid(json.get("user_id").getIntValue(), json.get("product_id").getIntValue(), 
//					json.get("amount").getDoubleValue());
//
//			if(newBid.getUser_id()!=0){
//				return notFound("User not found");//404
//			}
//			else if(!(newBid.getProduct_id() >=0 && newBid.getProduct_id() < 6)){
//				return notFound("Product not found");//404
//			}
//			else if(!(allItems.get(newBid.getProduct_id()) instanceof ProductForAuction)){
//				return badRequest("Cannot place bid on this item");//400
//			}
//			else {//item is an auction product
//				ProductForAuction product = (ProductForAuction) allItems.get(newBid.getProduct_id());
//				if(newBid.getAmount() <= product.getCurrentBidPrice()){
//					return badRequest("Cannot place that amount on the item");
//				}
//				else{
//					//place bid
//					return ok("Bid has been placed");//200
//				}	
//			}
//		}
		return ok();
	}
	//DONE
	public static Result getMyBiddings(int userId){
		try{
			Class.forName(DBManager.driver);
			Connection connection = DriverManager.getConnection(DBManager.db,DBManager.user,DBManager.pass);
			Statement statement = connection.createStatement();
			ResultSet rset = statement.executeQuery("select iid,ititle,ishipping_price,total_bids,current_bid_price,winningbid, " +
													"to_char(istart_sale_date + itime_duration - current_timestamp,'DD') as days,to_char(istart_sale_date + itime_duration - current_timestamp,'HH24') as hours, " + 
													"to_char(istart_sale_date + itime_duration - current_timestamp,'MI') as minutes, to_char(istart_sale_date + itime_duration - current_timestamp,'SS') as seconds, " +
													"username,avg(stars) " +
													"from item natural join item_for_auction natural join users natural join ranks as rnk(b_uid,uid,stars) join bid using(iid) " +
													"where bid.uid = " + userId + " " +
													"group by iid,ititle,ishipping_price,total_bids,current_bid_price,winningbid,username;");
			
			ObjectNode respJson = Json.newObject();
			ArrayNode array = respJson.arrayNode();
			ObjectNode itemJson = null;
			MyBiddingsProduct item = null;
			
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
				item = new MyBiddingsProduct(rset.getInt("iid"), rset.getString("ititle"), timeRemaining, rset.getDouble("ishipping_price"), 
						andrScaledImgDir + "img" + rset.getInt("iid") +".jpg", rset.getString("username"), rset.getDouble("avg"), 
						-1, rset.getDouble("current_bid_price"), rset.getInt("total_bids"),rset.getBoolean("winningbid"));
				
				itemJson.putPOJO("item", Json.toJson(item));
				array.add(itemJson);
			}
			respJson.put("myBiddingsItems", array);
			return ok(respJson);//200
		}
		catch (Exception e) {
			Logger.info("EXCEPTION ON MY SELLING PRODUCTS");
			e.printStackTrace();
			return notFound();
		}
	}
	
	//DONE
	public static Result getBidList(int productId){
		try{
			Class.forName(DBManager.driver);
			Connection connection = DriverManager.getConnection(DBManager.db,DBManager.user,DBManager.pass);
			Statement statement = connection.createStatement();
			ResultSet rset = statement.executeQuery("select username, bid_amount " +
													"from bid natural join users " +
													"where iid = " + productId + " " +
													"order by bid_amount;");
			ObjectNode bidderAndAmount = null;
			ObjectNode respJson = Json.newObject();
			ArrayNode array = respJson.arrayNode();
			while(rset.next()){
				bidderAndAmount = Json.newObject();
				bidderAndAmount.put("username", rset.getString("username"));
				bidderAndAmount.put("amount", rset.getDouble("bid_amount"));
				array.add(bidderAndAmount);
			}
		respJson.put("bidlist",array);
		Logger.info("Product ID Bidlist:" + productId);
		return ok(respJson);
		}
		catch (Exception e) {
			Logger.info("EXCEPTION ON BID LIST");
			e.printStackTrace();
			return notFound();
		}
	}

}
