package controllers;

import java.io.File;
import java.io.FileNotFoundException;

import play.Logger;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import testmodels.Test;

public class ImageController  extends Controller{
	public static Result getImage(String imageName){
		try{	
		return ok(new File(Test.imagesDir + imageName));//200
		}
		catch(Exception e){
			Logger.info(imageName + " not found");
			return notFound("No image found with the requested name");//404
		}
	}

	public static Result getScaledImage(String imageName){
		try{
			return ok(new File(Test.imagesDir + "scaled/" + imageName));//200
		}
		catch(Exception e){
			Logger.info(imageName + " not found");
			return notFound("No image found with the requested name");//404
		}
	}

	public static Result uploadImage(){
		//File file = request().body().asRaw().asFile();
		  MultipartFormData body = request().body().asMultipartFormData();
		  FilePart picture = body.getFile("picture");
		  if (picture != null) {
		    String fileName = picture.getFilename();
		    String contentType = picture.getContentType(); 
		    File file = picture.getFile();
	        file.renameTo(new File(Test.imagesDir, "uploadedImageTest.jpg"));

		    return ok("File uploaded");
		  } else {

		    return redirect(routes.Application.index());    
		  }
	}
}
