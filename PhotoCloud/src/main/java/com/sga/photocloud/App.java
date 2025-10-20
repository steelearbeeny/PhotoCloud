package com.sga.photocloud;

import java.time.LocalDateTime;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.flickr4java.flickr.people.User;
import com.sga.common.flickr.Albums;
import com.sga.common.flickr.FlickrConnection;
import com.sga.common.flickr.Users;
import com.sga.common.generic.GenericPhoto;
import com.sga.common.generic.JobConfiguration;
import com.sga.common.geocode.Geocoder;
import com.sga.common.google.GoogleAlbum;
import com.sga.common.google.GoogleMediaItemList;
import com.sga.common.http.HttpUtils;
import com.sga.common.log.Log;
import com.sga.common.oauth.OauthToken;
import com.sga.common.oauth.OauthUtil;
import com.sga.common.util.ReturnValue;
import com.sga.common.util.Utils;

public class App {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String mn="App::main";
		User u=null;
		String userId;
		GenericPhoto p;
		ReturnValue<String> rv;
		
		Path path = Paths.get("D:\\Pictures\\sgapic3.JPG");
		
		byte[] dum = Files.readAllBytes(path);
		p=new GenericPhoto();
		p.imageData=dum;
		p.inputStream=new ByteArrayInputStream(dum);
		p.fileSize=(long)dum.length;
		p.name="sgapic3.JPG";
		
		rv=HttpUtils.PostPhoto("http://localhost:5000/inference", p, "user","1","person","33");
		
		Log.Info(mn, rv.toString());
		
		//FlickrConnection f = new FlickrConnection();
		//Users.Initialize(f);
		//Albums.Initialize(f);
		
		//Geocoder.ReverseLookup(40.3255305555555, 74.00707222222222);
		
		//Geocoder.ReverseLookup("40 19' 31.91\"","");
		/*
		Geocoder.ReverseLookup("-10° 20' 30.123\"", "");
		
		
		Geocoder.ReverseLookup("40° 19' 31.91\"","-74° 0' 25.46\"");
		Geocoder.ReverseLookup("40° 29' 10.44\"","-74° 1' 18.25\"");
		Geocoder.ReverseLookup("40° 21' 50.67\"","-73° 58' 26.67\"");
		*/
		
		//OauthUtil.InsertTokenRequest(1,2,"3","4","5");
		//OauthUtil.InsertTokenRequest(1,2,"300",null,null);
		
		//OauthUtil.InsertTokenResponse(1, 2, "300", "RESP", "Refresh", null);
		
		int i=0;
		String listAlbumsURL = "https://photoslibrary.googleapis.com/v1/albums";
		
		//OauthToken token=OauthUtil.GetTokenForUserAndService(1,4);
		//ReturnValue<String> albumRv=HttpUtils.GetRequest(
		//		token, 
		//		listAlbumsURL, 
		//		"pageSize",50/*,
		//		"excludeNonAppCreatedData","true"*/);
		
		GoogleAlbum album = new GoogleAlbum();
		album.album.id="Thi sis hte id";
		
		String xx= Utils.GetGson().toJson(album);
		i=1;
		
		/*
		JobConfiguration c = new JobConfiguration(1,2,3,"Test");
		c.sourceAlbums.add("sourcealbum123");
		c.sourceToken=OauthUtil.GetTokenForUserAndService(1,4);
		c.targetToken=OauthUtil.GetTokenForUserAndService(1,0);
		
		String j = Utils.GetGson().toJson(c);
		
		System.out.println(j);
		
		JobConfiguration x;
		
		c=Utils.GetGson().fromJson(j,JobConfiguration.class);
		*/
		
		String json="""
		{
			  "mediaItems": [
			    {
			      "id": "AByIYSfX6LJIj5I0tGghHZc-o2u5s2aLEoKeraT3QWPM3QVhMMZVi_drEaL-SXvK8qteucJOvzms1Vqh9VoaWPaU4hFDX7ThGg",
			      "createTime": "2010-03-14T15:06:31Z",
			      "type": "PHOTO",
			      "mediaFile": {
			        "baseUrl": "https://lh3.googleusercontent.com/ppa/AOyj2Fl2NMgix8wwhBg66jiv9KQauFd026W7p-tdQLooiGAOL-6H4GnwHhgo5jjKApjUPm04Y4fx9lErqo6iI8bO2lHJlDpgbQG0",
			        "mimeType": "image/jpeg",
			        "mediaFileMetadata": {
			          "width": 4000,
			          "height": 3000,
			          "cameraMake": "SONY",
			          "cameraModel": "DSC-W220",
			          "photoMetadata": {
			            "focalLength": 5.35,
			            "apertureFNumber": 2.8,
			            "isoEquivalent": 320,
			            "exposureTime": "0.033333335s"
			          }
			        },
			        "filename": "DSC01664.JPG"
			      }
			    },
			    {
			      "id": "AByIYSe8khO46D0vefqFxOY4x6Twik_pSsaqZvg4ngv35WqwnjULRtsuJ80VU9YkcxYVkyLqqBB-7b5ZMLTrSwMxW79akxjyRg",
			      "createTime": "2010-03-13T17:46:17Z",
			      "type": "PHOTO",
			      "mediaFile": {
			        "baseUrl": "https://lh3.googleusercontent.com/ppa/AOyj2FnKh4xlK4DblLdHrIqUgJWZOgKYA2TmN_jFZWvYloVKndQJxBcc5XXaIYy2RIXEU0GYr1N5gmEQs_IgaKzLPOp9-qupLjfk",
			        "mimeType": "image/jpeg",
			        "mediaFileMetadata": {
			          "width": 4000,
			          "height": 3000,
			          "cameraMake": "SONY",
			          "cameraModel": "DSC-W220",
			          "photoMetadata": {
			            "focalLength": 5.35,
			            "apertureFNumber": 2.8,
			            "isoEquivalent": 400,
			            "exposureTime": "0.039999999s"
			          }
			        },
			        "filename": "DSC01658.JPG"
			      }
			    },
			    {
			      "id": "AByIYSd8QNKJV9zXHk8SYaDonTS-YavcPr9IJm-9DHvVYTa5dvQjSBeibpl0rVScOQiCyWzMeJwzgqQ_6MRF_0Ju2DTMMsqyAQ",
			      "createTime": "2010-03-14T15:45:06Z",
			      "type": "PHOTO",
			      "mediaFile": {
			        "baseUrl": "https://lh3.googleusercontent.com/ppa/AOyj2Fl4fBgTP-gInG0Dsasj2XtwDVSrYz6XfiEpOEv7cUbi1APCCuvjdsA-jXqdp00G_uaP_OTWGUvaPw8fdZk0ie7Q0KfNUjfS",
			        "mimeType": "image/jpeg",
			        "mediaFileMetadata": {
			          "width": 4000,
			          "height": 3000,
			          "cameraMake": "SONY",
			          "cameraModel": "DSC-W220",
			          "photoMetadata": {
			            "focalLength": 7.64,
			            "apertureFNumber": 3.2,
			            "isoEquivalent": 200,
			            "exposureTime": "0.016666667s"
			          }
			        },
			        "filename": "DSC01675.JPG"
			      }
			    }
			  ]
			}""";
		
		
		//Type listType = new TypeToken<List<MediaItem>>() {}.getType();
		
		GoogleMediaItemList myObjects = Utils.GetGson().fromJson(json, GoogleMediaItemList.class);
		
		i=1;
		
		
		
/*
		u = Users.FindUser("arbeeny@yahoo.com");
		
		if(u==null)
		{
			Log.Info(mn,"user not found");
			return;
		}
		
		Log.Info(mn,"The user id was %s",u.getId());

		userId=u.getId();
		
		Albums.ListAlbums(userId);
		*/
	
	}
	
	

}
