package man;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Calendar;

import play.Logger;

public class Manager {
	//DB info
	public static String driver = "org.postgresql.Driver";
	public static String db = "jdbc:postgresql://localhost:5432/TAPtoBUYDB";
	public static String user = "postgres";
	public static String pass = "postgres";
	
	//Image Dir (local)
	//public static String imagesDir = "/home/cok0/git/TAPtoBUYsrv2/images/";
	public static String imagesDir = "C:\\Users\\Kidany\\Documents\\GitHub\\git\\TAPtoBUYsrv2\\images\\";
	
	//Image localhost url to send to the client (Android)
	//public static String andrImgDir = "http://10.0.2.2:9000/images/";
	//public static String andrScaledImgDir = "http://10.0.2.2:9000/images/scaled/";
	//Image localhost url to send to the client (Android)
	public static String andrImgDir = "taptobuy.no-ip.biz:9000/images/";
	public static String andrScaledImgDir = "taptobuy.no-ip.biz:9000/images/scaled/";
	
}
