package controllers;

import java.io.File;
import java.io.FileNotFoundException;

import man.Manager;

import play.Logger;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;

public class ImageController  extends Controller{
	public static Result getImage(String imageName){
		try{	
		return ok(new File(Manager.imagesDir + imageName));//200
		}
		catch(Exception e){
			Logger.info(imageName + " not found");
			return notFound("No image found with the requested name");//404
		}
	}

	public static Result getScaledImage(String imageName){
		try{
			return ok(new File(Manager.imagesScaledDir + imageName));//200
		}
		catch(Exception e){
			Logger.info(imageName + " not found");
			return notFound("No image found with the requested name");//404
		}
	}

	public static Result uploadImage(int userId){
		//File file = request().body().asRaw().asFile();
		  MultipartFormData body = request().body().asMultipartFormData();
		  FilePart picture = body.getFile("picture");
		  if (picture != null) {
		    File file = picture.getFile();
	        file.renameTo(new File(Manager.imagesDir, "tmp_u" + userId +".jpg"));
		    return ok("File uploaded");
		  } else {

		    return redirect(routes.Application.index());    
		  }
	}
}
