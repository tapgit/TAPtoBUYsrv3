package controllers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import dbman.DBManager;

import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

public class CategoryController extends Controller {

	//DONE
	public static Result getSubcategories(int parentCatId){
		try {
			Class.forName(DBManager.driver);
			Connection connection = DriverManager.getConnection(DBManager.db,DBManager.user,DBManager.pass);
			Statement statement = connection.createStatement();
			ObjectNode respJson = Json.newObject();
			ArrayNode array = respJson.arrayNode();
			ObjectNode catJson = null;
			ResultSet rset = statement.executeQuery("select cat_id, catname, count(CB.childcat_id) " + 
													"from category, has_subcat as CA natural left outer join has_subcat as CB(cid,childcat_id) " +
													"where cat_id = CA.childcat_id and CA.parentcat_id = " + parentCatId +
													"group by cat_id, catname " +
													"order by catname;");
			while(rset.next()){
				catJson = Json.newObject();
				catJson.put("catId", rset.getString(1));
				catJson.put("name", rset.getString(2));
				if(rset.getInt(3)==0){
					catJson.put("hasSubCategories", false );
				}
				else{
					catJson.put("hasSubCategories", true );
				}
				array.add(catJson);
			}
			catJson = Json.newObject();
			catJson.put("catId", parentCatId);
			catJson.put("name", "Other");
			catJson.put("hasSubCategories", false );
			array.add(catJson);
			respJson.put("subcategories", array);
			return ok(respJson);
		} catch (Exception e) {
			Logger.info("EXCEPTION ON CATEGORIES");
			e.printStackTrace();
			return notFound();
		}
	}
}
