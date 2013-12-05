package dbman;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Calendar;

import play.Logger;

public class DBManager {
	public static String driver = "org.postgresql.Driver";
	public static String db = "jdbc:postgresql://localhost:5432/TAPtoBUYDB";
	public static String user = "postgres";
	public static String pass = "postgres";

	
	
	public static void getAll(){
		try {
			Class.forName(driver);
			Connection connection = DriverManager.getConnection(db,user,pass);
			Statement statement = connection.createStatement();
			
			ResultSet rset = statement.executeQuery("select istart_sale_date from item");
			Timestamp a = null;
			Calendar b = null;

			//if(!rset.next()){Logger.info("NO hay na");} 2013-11-08
			while(rset.next()){
				//Logger.info("date: " + a.get+ "\n");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Logger.info("EXCEPTION");
			e.printStackTrace();
		}
		
	}
}
