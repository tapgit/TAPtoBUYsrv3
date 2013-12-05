package controllers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import models.Product;
import models.ProductForAuction;
import models.ProductForSale;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import dbman.DBManager;

import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import testmodels.Test;

public class SearchController extends Controller {
	public static String imagesDir = "/home/cok0/git/TAPtoBUYsrv2/images/";
	//public static String imagesDir = "C:\\Users\\Kidany\\Documents\\GitHub\\git\\TAPtoBUYsrv\\images\\";
	public static String andrScaledImgDir = "http://10.0.2.2:9000/images/scaled/";

	//DONE
	public static Result getSearchResultsAll(int catId, int orderById){
		return getSearchResults(catId, orderById, "");
	}
	//DONE
	public static Result getSearchResults(int catId, int orderById, String searchString){
		searchString = searchString.replaceAll("%20"," ");//replace white spaces by %20
		//Specify in which way items will be ordered
		String orderByStr = "";
		if(orderById == 0) //by name (title)
			orderByStr = "ititle";
		else if(orderById == 1)//by price
			orderByStr = "price";
		else//by brand
			orderByStr = "brand";

		try{
			Class.forName(DBManager.driver);
			Connection connection = DriverManager.getConnection(DBManager.db,DBManager.user,DBManager.pass);
			Statement statement = connection.createStatement();
			String whereClause = "where results.ititle ilike '%" + searchString + "%' ";
			if(catId != -1)//search is by categories
				whereClause = "where results.cat_id = " + catId + " ";
			ResultSet rset = statement.executeQuery("select * " +
					"from (select iid,ititle,ishipping_price,-1 as total_bids,instant_price as price,brand,cat_id,to_char(istart_sale_date + itime_duration - current_timestamp,'DD') as days,to_char(istart_sale_date + itime_duration - current_timestamp,'HH24') as hours, " +
					"to_char(istart_sale_date + itime_duration - current_timestamp,'MI') as minutes, to_char(istart_sale_date + itime_duration - current_timestamp,'SS') as seconds, " + 
					"username,avg(stars) " +
					"from item natural join item_for_sale natural join item_info natural join users natural join ranks as rnk(b_uid,uid,stars) " +
					"group by iid,ititle,ishipping_price,instant_price,brand,cat_id,username " + 
					"union " + 
					"select iid,ititle,ishipping_price,total_bids,current_bid_price as price,brand,cat_id,to_char(istart_sale_date + itime_duration - current_timestamp,'DD') as days,to_char(istart_sale_date + itime_duration - current_timestamp,'HH24') as hours, " +
					"to_char(istart_sale_date + itime_duration - current_timestamp,'MI') as minutes, to_char(istart_sale_date + itime_duration - current_timestamp,'SS') as seconds, " +
					"username,avg(stars) " +
					"from item natural join item_for_auction natural join item_info natural join users natural join ranks as rnk(b_uid,uid,stars) " +
					"group by iid,ititle,ishipping_price,total_bids,current_bid_price,brand,cat_id,username) as results " +
					whereClause +  
					"order by results." + orderByStr + ";");
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
				if(rset.getInt("total_bids") == -1){//for sale
					item = new ProductForSale(rset.getInt("iid"), rset.getString("ititle"), timeRemaining, rset.getDouble("ishipping_price"), 
							andrScaledImgDir + "img" + rset.getInt("iid") +".jpg", rset.getString("username"), rset.getDouble("avg"), -1, rset.getDouble("price"));
					itemJson.put("forBid", false);
				}
				else{//for auction
					item = new ProductForAuction(rset.getInt("iid"), rset.getString("ititle"), timeRemaining, rset.getDouble("ishipping_price"), 
							andrScaledImgDir + "img" + rset.getInt("iid") +".jpg", rset.getString("username"), rset.getDouble("avg"), 
							-1, rset.getDouble("price"), rset.getInt("total_bids"));
					itemJson.put("forBid", true);
				}
				itemJson.putPOJO("item", Json.toJson(item));
				array.add(itemJson);
			}
			respJson.put("results", array);
			return ok(respJson);
		}
		catch (Exception e) {
			Logger.info("EXCEPTION ON SEARCH");
			e.printStackTrace();
			return notFound();
		}
	}
}
