package controllers;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import man.Manager;
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

import com.google.common.io.Files;





import play.Logger;

import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

public class ProductController extends Controller {

	//DONE
	public static Result getProductInfo(int productId){
		try{
			Class.forName(Manager.driver);
			Connection connection = DriverManager.getConnection(Manager.db,Manager.user,Manager.pass);
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
						"from item natural join item_for_sale natural join item_info natural join users natural left outer join ranks as rnk(b_uid,uid,stars) " +
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
						Manager.andrImgDir + "img" + rset.getInt("iid") +".jpg", rset.getString("username"), rset.getDouble("avg"),rset.getInt("remaining_quantity"), rset.getDouble("instant_price"),
						rset.getString("product"), rset.getString("model"), rset.getString("brand"), rset.getString("dimensions"), rset.getString("description"));
				itemInfoJson.putPOJO("productInfo", Json.toJson(itemInfo));
				connection.close();
				return ok(itemInfoJson);//200
			}
			else{//for auction
				itemInfoJson.put("forBid", true);
				rset = statement.executeQuery("select iid,ititle,ishipping_price, username,avg(stars),total_bids,current_bid_price,product,model,brand,dimensions,description, " +
						"to_char(istart_sale_date + itime_duration - current_timestamp,'DD') as days,to_char(istart_sale_date + itime_duration - current_timestamp,'HH24') as hours, " +
						"to_char(istart_sale_date + itime_duration - current_timestamp,'MI') as minutes, to_char(istart_sale_date + itime_duration - current_timestamp,'SS') as seconds " +
						"from item natural join item_for_auction natural join item_info natural join users natural left outer join ranks as rnk(b_uid,uid,stars) " +
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
							Manager.andrImgDir + "img" + rset.getInt("iid") +".jpg", rset.getString("username"), rset.getDouble("avg"), -1, rset.getDouble("current_bid_price"), 
							rset.getInt("total_bids"), rset.getString("product"), rset.getString("model"), rset.getString("brand"), rset.getString("dimensions"), rset.getString("description"));
					itemInfoJson.putPOJO("productInfo", Json.toJson(itemInfo));		
					connection.close();
					return ok(itemInfoJson);//200 
				}
				else{
					connection.close();
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
			Class.forName(Manager.driver);
			Connection connection = DriverManager.getConnection(Manager.db,Manager.user,Manager.pass);
			Statement statement = connection.createStatement();
			ProductController.updateItemsAvailability();
			ResultSet rset = statement.executeQuery("select * " +
					"from (select iid,ititle,ishipping_price,remaining_quantity as num1,instant_price as price,uid,false as forBid,to_char(istart_sale_date + itime_duration - current_timestamp,'DD') as days,to_char(istart_sale_date + itime_duration - current_timestamp,'HH24') as hours, " +
					"to_char(istart_sale_date + itime_duration - current_timestamp,'MI') as minutes, to_char(istart_sale_date + itime_duration - current_timestamp,'SS') as seconds " +
					"from item natural join item_for_sale " + 
					"where item.available = true " +
					"group by iid,ititle,ishipping_price,remaining_quantity,instant_price,uid " +
					"union " + 
					"select iid,ititle,ishipping_price,total_bids as num1,current_bid_price as price,uid,true as forBid,to_char(istart_sale_date + itime_duration - current_timestamp,'DD') as days,to_char(istart_sale_date + itime_duration - current_timestamp,'HH24') as hours, " +
					"to_char(istart_sale_date + itime_duration - current_timestamp,'MI') as minutes, to_char(istart_sale_date + itime_duration - current_timestamp,'SS') as seconds " +
					"from item natural join item_for_auction " +
					"where item.available = true " + 
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
							Manager.andrScaledImgDir + "img" + rset.getInt("iid") +".jpg", "", -1, rset.getInt("num1"), rset.getDouble("price"));
					itemJson.put("forBid", false);
				}
				else{//for auction
					item = new ProductForAuction(rset.getInt("iid"), rset.getString("ititle"), timeRemaining, rset.getDouble("ishipping_price"), 
							Manager.andrScaledImgDir + "img" + rset.getInt("iid") +".jpg", "", -1, -1, rset.getDouble("price"), rset.getInt("num1"));
					itemJson.put("forBid", true);
				}
				itemJson.putPOJO("item", Json.toJson(item));
				array.add(itemJson);
			}
			respJson.put("mySellingItems", array);
			connection.close();
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
			try{
				Class.forName(Manager.driver);
				Connection connection = DriverManager.getConnection(Manager.db,Manager.user,Manager.pass);
				Statement statement = connection.createStatement();
				//Get item Info
				String product = json.get("product").getTextValue().toString();
				String model = json.get("model").getTextValue().toString();
				String brand = json.get("brand").getTextValue().toString();
				String dimensions = json.get("dimensions").getTextValue().toString();
				String description = json.get("description").getTextValue().toString();

				ResultSet rset = statement.executeQuery("insert into item_info(iInfo_id,product,model,brand,dimensions,description) " +
						"values (DEFAULT,'" + product + "','" + model + "','" + brand + "','" + dimensions + "','"+ description + "') " +
						"returning iInfo_id;");
				rset.next();
				int last_iInfo_id = rset.getInt("iInfo_id");
				int last_iId;
				if(json.get("id").getIntValue() == -1){//Auction item
					rset = statement.executeQuery("insert into item(iId,iTitle,starting_quantity,remaining_quantity,iStart_sale_date,iTime_duration,iShipping_price,iInfo_id,cat_id) " +
							"values (DEFAULT,'" + json.get("title").getTextValue().toString() + "',1,1,current_timestamp, interval '" +json.get("timeRemaining").getTextValue().toString() +" day'," + json.get("shippingPrice").getDoubleValue() + ","+ last_iInfo_id + ",0) " +
							"returning iId;");
					rset.next();
					last_iId = rset.getInt("iId");
					statement.executeUpdate("insert into item_for_auction(iId,starting_bid_price,current_bid_price,total_bids,bid_rate,uId) " +
							"values ("+ last_iId + "," + json.get("startinBidPrice").getDoubleValue() + ","+json.get("currentBidPrice").getDoubleValue()+",0,0.2,"+userId+");");
				}
				else{//item for sale
					rset = statement.executeQuery("insert into item(iId,iTitle,starting_quantity,remaining_quantity,iStart_sale_date,iTime_duration,iShipping_price,iInfo_id,cat_id) " +
							"values (DEFAULT,'" + json.get("title").getTextValue().toString() + "',"+json.get("remainingQuantity").getIntValue()+","+json.get("remainingQuantity").getIntValue()+",current_timestamp, interval '" +json.get("timeRemaining").getTextValue().toString() +" day'," + json.get("shippingPrice").getDoubleValue() + ","+ last_iInfo_id + ",0) " +
							"returning iId;");
					rset.next();
					last_iId = rset.getInt("iId");
					statement.executeUpdate("insert into item_for_sale(iId,instant_price,uId) " +
							"values ("+last_iId+ ","+ json.get("instantPrice").getDoubleValue() +"," + userId + ");");
				}
				//Rename uploaded image so that in can be linked to the new item
				try{
					File image = new File(Manager.imagesDir, "tmp_u" + userId +".jpg");
					Files.copy(image, new File(Manager.imagesScaledDir, "img" + last_iId +".jpg"));
					image.renameTo(new File(Manager.imagesDir, "img" + last_iId +".jpg"));
				}catch(IOException e){
					Logger.info("EXCEPTION ON SELL PRODUCT (images)");
					e.printStackTrace();
					return notFound();
				}
				connection.close();
				return ok();//200 (product is now on sale)
			}
			catch (Exception e) {
				Logger.info("EXCEPTION ON SELL PRODUCT");
				e.printStackTrace();
				return notFound();
			}
		}
	}
	
	public static Result updateASellingProduct(int userId, int productId){
		JsonNode json = request().body().asJson();
		if(json == null) {
			return badRequest("Expecting Json data");//400
		} 
		else {
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
		try{
			Class.forName(Manager.driver);
			Connection connection = DriverManager.getConnection(Manager.db,Manager.user,Manager.pass);
			Statement statement = connection.createStatement();
			statement.executeUpdate("update item set available = false where iid = " + productId + ";");
			connection.close();
			return noContent();//204 (product removed from sale successfully)
		}
		catch (Exception e) {
			Logger.info("EXCEPTION ON QUIT FROM SELLING");
			e.printStackTrace();
			return notFound();
		}
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
				Class.forName(Manager.driver);
				Connection connection = DriverManager.getConnection(Manager.db,Manager.user,Manager.pass);
				Statement statement = connection.createStatement();
				ResultSet rset = statement.executeQuery("select * " + 
						"from (select iid,ititle,ishipping_price,remaining_quantity as num1,instant_price as price,false as forBid, " +
						"to_char(istart_sale_date + itime_duration - current_timestamp,'DD') as days,to_char(istart_sale_date + itime_duration - current_timestamp,'HH24') as hours, " +
						"to_char(istart_sale_date + itime_duration - current_timestamp,'MI') as minutes, to_char(istart_sale_date + itime_duration - current_timestamp,'SS') as seconds, " +
						"username,avg(stars) " +
						"from item natural join item_for_sale natural join users natural left outer join ranks as rnk(b_uid,uid,stars) " +
						"where item_for_sale.iid in " + buyNowIdsList + " " +
						"group by iid,ititle,ishipping_price,remaining_quantity,instant_price,username " +
						"union " +
						"select iid,ititle,ishipping_price,total_bids as num1,current_bid_price as price,true as forBid, " +
						"to_char(istart_sale_date + itime_duration - current_timestamp,'DD') as days,to_char(istart_sale_date + itime_duration - current_timestamp,'HH24') as hours, " +
						"to_char(istart_sale_date + itime_duration - current_timestamp,'MI') as minutes, to_char(istart_sale_date + itime_duration - current_timestamp,'SS') as seconds, " +
						"username,avg(stars) " +
						"from item natural join item_for_auction natural join users natural left outer join ranks as rnk(b_uid,uid,stars) " +
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
								Manager.andrScaledImgDir + "img" + rset.getInt("iid") +".jpg",  rset.getString("username"), rset.getDouble("avg"), rset.getInt("num1"), rset.getDouble("price"));
						itemJson.put("forBid", false);
					}
					else{//for auction
						item = new ProductForAuction(rset.getInt("iid"), rset.getString("ititle"), timeRemaining, rset.getDouble("ishipping_price"), 
								Manager.andrScaledImgDir + "img" + rset.getInt("iid") +".jpg",  rset.getString("username"), rset.getDouble("avg"), -1, rset.getDouble("price"), rset.getInt("num1"));
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
				connection.close();
				return ok(respJson);
			}
			catch (Exception e) {
				Logger.info("EXCEPTION ON MY SELLING PRODUCTS");
				e.printStackTrace();
				return notFound();
			}
		}
	}
	public static Result placeBuyNowOrder(int userId){
		JsonNode json = request().body().asJson();
		if(json == null) {
			return badRequest("Expecting Json data");//400
		} 
		else {
			try{
				Logger.info(json.toString());
				JSONObject jsonObj = new JSONObject(json.toString());
				int shipAddrId = jsonObj.getInt("shippingAddressId");
				String creditCardNum = jsonObj.getString("creditCardNum");
				JSONArray array = (jsonObj).getJSONArray("productIdsToBuy");
				if(array.length()==0){
					return badRequest("Expecting productlist");
				}
				else{
					Class.forName(Manager.driver);
					Connection connection = DriverManager.getConnection(Manager.db,Manager.user,Manager.pass);
					Statement statement = connection.createStatement();
					ProductController.updateItemsAvailability();
					//Get creditcard id
					ResultSet rset = statement.executeQuery("select crCard_Id from credit_card where sec_number = '"+creditCardNum+"' and uid = "+userId+";");
					rset.next();
					int crCardId = rset.getInt("crCard_Id");
					//Create BuyNow Order and get its id
					rset = statement.executeQuery("insert into buyNow_order(buyNow_order_id,buyNow_order_date,buyer_uId,shipAddr_Id,crCard_Id) " +
							"values(DEFAULT,current_timestamp,"+userId+","+shipAddrId+","+crCardId+") " +
							"returning buyNow_order_id;");
					rset.next();
					int last_buyNow_order_id = rset.getInt("buyNow_order_id");
					//Add to buynow_order_items all the items within the order
					for(int i=0;i<array.length();i++){
						jsonObj = array.getJSONObject(i);
						int productId = jsonObj.getInt("productId");
						int quantity = jsonObj.getInt("quantity");
						double totalPrice = jsonObj.getDouble("price") * quantity;
						double shippingPrice = jsonObj.getDouble("shippingPrice");//////////////////////////
						rset = statement.executeQuery("select uid,payid,available,remaining_quantity " +
								"from item natural join item_for_sale,paypal " +
								"where item_for_sale.uid = paypal.seller_uId and iid = "+productId+";");
						rset.next();
						int sellerId = rset.getInt("uid");
						int payid = rset.getInt("payid");
						int remainingQuantity = rset.getInt("remaining_quantity");
						if(rset.getBoolean("available")){//Buy item
							statement.executeUpdate("insert into seller_buynow_order(seller_id,buynow_order_id,payId) " +
									"values("+sellerId+","+last_buyNow_order_id+","+payid+");" +
											"insert into buyNow_order_items(buyNow_order_id,iId,items_price,shipping_price,quantity) " +
											"values("+last_buyNow_order_id+","+productId+","+totalPrice+","+shippingPrice+","+quantity+");" +
													"update item set remaining_quantity = " + (remainingQuantity - quantity) + " where iid = "+productId+";");
						}
						else{
							connection.close();
							return notFound("Product " + productId + " is no longer on sale");//404
						}
					}
					return ok("Order processed successfully");
				}
			}
			catch (Exception e) {
				Logger.info("EXCEPTION ON BUY NOW ORDER");
				e.printStackTrace();
				return internalServerError();
			}
		}
	}
	
	public static Result placeAuctionOrder(int userId){
		return TODO;
	}
	
	public static void updateItemsAvailability(){
		try{
			Class.forName(Manager.driver);
			Connection connection = DriverManager.getConnection(Manager.db,Manager.user,Manager.pass);
			Statement statement = connection.createStatement();
//			statement.executeUpdate("update item set available = false " +
//					"where cast(to_char(istart_sale_date + itime_duration - current_timestamp,'DD') as int) <= 0 and cast(to_char(istart_sale_date + itime_duration - current_timestamp,'HH24') as int) <= 0 and " +
//					"cast(to_char(istart_sale_date + itime_duration - current_timestamp,'MI') as int) <= 0 and cast(to_char(istart_sale_date + itime_duration - current_timestamp,'SS') as int) <= 0;");
			
			statement.executeUpdate("update item set available = false " +
					"where cast(to_char(istart_sale_date + itime_duration - current_timestamp,'DD') as int) <= 0 and cast(to_char(istart_sale_date + itime_duration - current_timestamp,'HH24') as int) <= 0 and " +
					"cast(to_char(istart_sale_date + itime_duration - current_timestamp,'MI') as int) <= 0 and cast(to_char(istart_sale_date + itime_duration - current_timestamp,'SS') as int) <= 0 " +
					"or item.remaining_quantity <= 0");
			connection.close();
		}
		catch (Exception e) {
			Logger.info("EXCEPTION WHILE UPDATING ITEMS AVAILABILITY");
			e.printStackTrace();
		}
		
	}

}
