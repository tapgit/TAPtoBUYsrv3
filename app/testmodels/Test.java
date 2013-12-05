package testmodels;


import java.util.ArrayList;

import models.Address;
import models.CreditCard;
import models.MyBiddingsProduct;
import models.Product;
import models.ProductForAuction;
import models.ProductForAuctionInfo;
import models.ProductForSale;
import models.ProductForSaleInfo;
import models.User;


public class Test {

	public static String imagesDir = "/home/cok0/git/TAPtoBUYsrv2/images/";
	//public static String imagesDir = "C:\\Users\\Kidany\\Documents\\GitHub\\git\\TAPtoBUYsrv2\\images\\";
	
	//DONE
	public static ArrayList<Product> getProductList(){
		String scaledImgDir = "http://10.0.2.2:9000/images/scaled/";

		Product item1 = new ProductForSale(0,"iPhone 5s black new", "12d 5h", 9.99, scaledImgDir+"img1.jpg", "juanitoManito77", 4.5, 10, 599.00);
		Product item2 = new ProductForAuction(1,"Database System Concepts 6.ed", "10h 20m",0, scaledImgDir+ "img4.jpg", "loloLopez13", 3.0, 0.99, 24.99,11);
		Product item3 = new ProductForAuction(2,"Samsumg Galaxy 4s used(like new)", "20m 33s", 0, scaledImgDir+ "img2.jpg", "Kidobv", 5.0, 0.99, 500.99, 60);
		Product item4 = new ProductForSale(3,"Java concepts horstmann 6 ed hardcover", "1d 3h", 3.99, scaledImgDir+ "img5.jpg", "Apu Diaz", 2.5, 5, 79.99);
		Product item5 = new ProductForAuction(4,"Samsumg Galaxy 4s unlocked", "2d 5h", 0, scaledImgDir+ "img3.jpg", "bondLolo", 4.8, 9.99, 299.99, 29);
		Product item6 = new ProductForSale(5,"iPad 4 (new unopened)", "10d 3h", 10.99, scaledImgDir+ "img6.jpg", "YangXi", 5.80, 50, 499.99);

		ArrayList<Product> items = new ArrayList<Product>();
		items.add(item1);
		items.add(item2);
		items.add(item3);
		items.add(item4);
		items.add(item5);
		items.add(item6);
		return items;
	}
	//DONE
	public static ArrayList<Product> getProductInfoList(){
		String imgDir = "http://10.0.2.2:9000/images/";
		Product productInfo1 = new ProductForSaleInfo(0,"iPhone 5s black new", "12d 5h", 9.99, imgDir+"img1.jpg", "juanitoManito77", 4.5, 10, 599.00, "iPhone", "5s", "Apple", "10x5", "Brand new black iphone 5s");
		Product productInfo2 = new ProductForAuctionInfo(1,"Database System Concepts 6.ed", "10h 20m",0, imgDir+ "img4.jpg", "loloLopez13", 3.0, 0.99, 24.99,11, "DataBase System Concepts", "6th", "Wiley", "10x5", "Brand new international 6th edition..");
		Product productInfo3 = new ProductForAuctionInfo(2,"Samsumg Galaxy 4s used(like new)", "20m 33s", 0, imgDir+ "img2.jpg", "Kidobv", 5.0, 0.99, 500.99, 60, "Samsung Galaxy", "4s", "Samsung", "10x5", "Used(like new) samsung galaxy..");
		Product productInfo4 = new ProductForSaleInfo(3,"Java concepts horstmann 6 ed hardcover", "1d 3h", 3.99, imgDir+ "img5.jpg", "Apu Diaz", 2.5, 5, 79.99, "Java Concepts", "6th", "Wiley", "10x5", "Brand new hardcover book..");
		Product productInfo5 = new ProductForAuctionInfo(4,"Samsumg Galaxy 4s unlocked", "2d 5h", 0, imgDir+ "img3.jpg", "bondLolo", 4.8, 9.99, 299.99, 29,"Samsung Galaxy", "4s", "Samsung", "10x5", "Samsung galaxy 4s unlocked working perfectly..");
		Product productInfo6 = new ProductForSaleInfo(5,"iPad 4 (new unopened)", "10d 3h", 10.99, imgDir+ "img6.jpg", "YangXi", 5.80, 50, 499.99, "iPad", "4", "Apple", "10x5", "Brand new black iPad 4");
		ArrayList<Product> productInfos = new ArrayList<Product>();
		productInfos.add(productInfo1);
		productInfos.add(productInfo2);
		productInfos.add(productInfo3);
		productInfos.add(productInfo4);
		productInfos.add(productInfo5);
		productInfos.add(productInfo6);
		return productInfos;
	}
	//DONE
	public static ArrayList<ProductForSale> getCartItemsList(){
		ArrayList<Product> items = getProductList();
		ArrayList<ProductForSale> cartItems = new ArrayList<ProductForSale>();
		cartItems.add((ProductForSale)items.get(0));
		cartItems.add((ProductForSale)items.get(3));
		cartItems.add((ProductForSale)items.get(5));
		return cartItems;
	}
	//DONE
	public static ArrayList<Product> getSellingItemsList(){
		ArrayList<Product> items = getProductList();
		ArrayList<Product> mySellingItems = new ArrayList<Product>();
		mySellingItems.add(items.get(3));
		mySellingItems.add(items.get(4));
		return mySellingItems;
	}
	public static ArrayList<Product> getHistoryItemsList(){
		ArrayList<Product> items = getProductList();
		ArrayList<Product> myHistoryItems = new ArrayList<Product>();
		myHistoryItems.add(items.get(0));
		myHistoryItems.add(items.get(1));
		myHistoryItems.add(items.get(2));
		myHistoryItems.add(items.get(3));
		return myHistoryItems;
	}
	//DONE
	public static User getUser(){
		Address[] shippingAddresses = new Address[2];
		shippingAddresses[0] = new Address(0,"Puerto Rico", "Kevin Castillo", "Calle Plantio # 48 Urb. Paseos del Prado", "Carolina", "PR", 
				"00987", "787-757-5115");
		shippingAddresses[1] = new Address(1,"Puerto Rico", "Kevin Castillo", "Calle El Sole # 30 Urb. Terrace", "Mayaguez", "PR", 
				"00777", "787-777-7777");
		CreditCard[] creditCards = new CreditCard[2];
		Address creditCard0BillingAddress = shippingAddresses[0];
		Address creditCard1BillingAddress = shippingAddresses[1];
		creditCards[0] = new CreditCard("1234567890129090", "Lolo Lopez", "09/09/14",creditCard0BillingAddress);
		creditCards[1] = new CreditCard("7772372431431234", "Medalla Rodriguez", "06/07/16",creditCard1BillingAddress);
		User user = new User(16,"Kevin", "Castillo", "kebinbin", "1234", "kebinbin@hotmail.com", shippingAddresses, creditCards);
		return user;
	}
	//DONE
	public static ArrayList<MyBiddingsProduct> getMyBiddingsItemList(){
		String scaledImgDir = "http://10.0.2.2:9000/images/scaled/";
		boolean item2winningBid = false;
		boolean item5winningBid = true;
		MyBiddingsProduct item2 = new MyBiddingsProduct(1,"Database System Concepts 6.ed", "10h 20m",0, scaledImgDir+ "img4.jpg", "loloLopez13", 3.0, 0.99, 24.99,11, item2winningBid);
		MyBiddingsProduct item5 = new MyBiddingsProduct(4,"Samsumg Galaxy 4s unlocked", "2d 5h", 0, scaledImgDir+ "img3.jpg", "bondLolo", 4.8, 9.99, 299.99, 29,item5winningBid);
		ArrayList<MyBiddingsProduct> result = new ArrayList<MyBiddingsProduct>();
		result.add(item2);
		result.add(item5);
		return result;
	}
	
//	public static ArrayList<Product> getBuyNowItemList(){
//		ArrayList<Product> items = getProductList();
//		ArrayList<Product> buyNowItems = new ArrayList<Product>();
//		buyNowItems.add(items.get(0));
//		buyNowItems.add(items.get(3));
//		return buyNowItems;
//	}

	
}
