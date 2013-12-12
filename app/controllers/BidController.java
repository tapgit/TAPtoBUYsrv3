package controllers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import man.Manager;
import models.Bid;
import models.MyBiddingsProduct;
import models.Product;
import models.ProductForAuction;
import models.ProductForSale;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;


import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

public class BidController extends Controller {
	//DONE
	public static Result placeBid(int userId, int productId, double amount){
			try{
				Class.forName(Manager.driver);
				Connection connection = DriverManager.getConnection(Manager.db,Manager.user,Manager.pass);
				Statement statement = connection.createStatement();
				ProductController.updateItemsAvailability();
				//Get the highest bid amount on this item:
				ResultSet rset = statement.executeQuery("select bidid, bid_amount " +
						"from bid natural join item " +
						"where iid = "+productId+" and winningbid = true and available = true;");
				if(rset.next()){//if the item is avaiable
					int currentBidPrice = rset.getInt("bid_amount");
					int bidId = rset.getInt("bidid");
					if(amount<=currentBidPrice){
						connection.close();
						return badRequest("Invalid Amount");//400
					}
					else{
						//Place Bid => 1) Update row with this bidid(el q estaba ganando) to winningbid=false
						//			   2) Add this new bid 
						//			   3) Update current_bid_price and total_bids from this item_for_auction
						statement.executeUpdate("update bid set winningbid = false where bidid = " + bidId + ";" + 
												"insert into bid(bidid,bid_amount,bid_date,winningBid,iId,uid) " +
												"values (DEFAULT,"+amount+",current_timestamp,true,"+productId+","+userId+");");
						rset = statement.executeQuery("select count(*) as total_bids from bid where iid = "+productId+";");
						int totalBids = 1;
						if(rset.next()){
							totalBids = rset.getInt("total_bids");
						}
						statement.executeUpdate("update item_for_auction set (current_bid_price,total_bids) = ("+amount+","+totalBids+") where iid = " + productId + ";");
						connection.close();
						return ok();//200
					}
				}
				else{//Auction ended (item not avaiable)
					connection.close();
					return notFound("Auction Ended");//404
				}
			}
			catch (Exception e) {
				Logger.info("EXCEPTION ON MY PLACE BID");
				e.printStackTrace();
				return internalServerError();//500
			}
	}
	//DONE
	public static Result getMyBiddings(int userId){
		try{
			Class.forName(Manager.driver);
			Connection connection = DriverManager.getConnection(Manager.db,Manager.user,Manager.pass);
			Statement statement = connection.createStatement();
			ProductController.updateItemsAvailability();
			ResultSet rset = statement.executeQuery("select iid,ititle,ishipping_price,total_bids,bid_amount,winningbid, " +
													"to_char(istart_sale_date + itime_duration - current_timestamp,'DD') as days,to_char(istart_sale_date + itime_duration - current_timestamp,'HH24') as hours, " +
													"to_char(istart_sale_date + itime_duration - current_timestamp,'MI') as minutes, to_char(istart_sale_date + itime_duration - current_timestamp,'SS') as seconds, " +
													"username,avg(stars) " +
													"from item natural join item_for_auction natural join users natural join ranks as rnk(b_uid,uid,stars) join bid using(iid) " +
													"where bid.uid = "+userId+" and (iid,bid_amount) in (select iid, max(bid_amount) from bid where bid.uid = "+userId+" group by iid) " +
													"group by iid,ititle,ishipping_price,total_bids,bid_amount,winningbid,username");
			
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
						Manager.andrScaledImgDir + "img" + rset.getInt("iid") +".jpg", rset.getString("username"), rset.getDouble("avg"), 
						-1, rset.getDouble("bid_amount"), rset.getInt("total_bids"),rset.getBoolean("winningbid"));
				
				itemJson.putPOJO("item", Json.toJson(item));
				array.add(itemJson);
			}
			respJson.put("myBiddingsItems", array);
			connection.close();
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
			Class.forName(Manager.driver);
			Connection connection = DriverManager.getConnection(Manager.db,Manager.user,Manager.pass);
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
		connection.close();
		return ok(respJson);
		}
		catch (Exception e) {
			Logger.info("EXCEPTION ON BID LIST");
			e.printStackTrace();
			return notFound();
		}
	}

}
