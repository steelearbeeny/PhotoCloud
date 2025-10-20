    "@mui/material": "^5.15.19",
g:
cd \newapp\source\photocloud
set PATH=%PATH%;g:\apache-maven-3.9.7\bin
mvn dependency:Resolve


mvn install
mvn validate


npx webpack build


npm outdated
npm update


Maven mvn is in
C:\Users\arbeeny\.m2\wrapper\dists\apache-maven-3.9.2-bin\3238cb54\apache-maven-3.9.2\bin
to install wrapper (mvnw) run mvn wrapper:wrapper



to create Common jar
From PhotoCloudCommon folder...

command prompt
mvnw compile (ran first time got error about -source 8, run again, error gone)
mvnw package
its in taret folder


COMMON Jar
is included into tomcat project under tomcat config pages.
Should point to classes folder in common project 
Build from within eclipse is still not correct tho...BufferedImage not found


TOMCAT SETUP
server.xml is in G:\NewApp\source\.metadata\.plugins\org.eclipse.wst.server.core\tmp1\conf
This points to classes in project folder

TOMCAT CONFIG IN ECLIPSE
In lower tabs, Servers. Double click on Tomcat v9.0 Server at Localhost
This will show the config page.
Click Open Launch Configuration -> Classpath - This is the runtime jars for tomcat
Jars for the common project need to go here so tomcat can find them.
Even though they are included in the common jar, somehow they need to be here too.
Look in mavendependencies node in project explorer for the missing jar, 
get the path and copy to G:\NewApp\libs
Then "add external JAR"  in classpath in tomcat config.



TESTING NOTES
For testing things that will eventually be moved the the common project, and run by the scheduler...
you need to put the needed jars into the web project so they can be found at runtime.
It appears that the common project is run from the compiled class exploded source folder, 
so jars it wont find.


SCHEDULER
1. Just run the SchedulerMain::main as an app
2. Dashboard will be at localhost:8000
3. If you get deserialization error...make sure all job parameters 
have no-arg constructors and are other wise serializable...no need to implement Serializable


TEST ACCOUNTS
Flickr
stancohen1962
nasa on the commons
steelearbeeny@msn.com / P$1234


OSMIUM
NOTE: This is not used. We are using Nominatim instaead
Its installed as a systemd service nominatim.service

This is the util that reads the map files from openstreetmap.com
Its in the Rocky VM need to access via anaconda as root
1. su to root
2. conda activate sgaconda
3. osmium is the command  - manual https://osmcode.org/osmium-tool/manual.html
4. create places extract
	osmium tags-filter -o njplace.osm ./new-jersey-latest.osm.pbf place=state place=city place=town place=village
5. 

POSTGIS
Need to install postgis for geometry data type which is export format from osmium
create extension postgis

CREATE TABLE osmdata (
geom      GEOMETRY, -- or GEOGRAPHY
tags      JSONB -- or JSON, or TEXT
);

select * from osmdata

copy osmdata from '/mnt/hgfs/jdbctest/mapdata/njplace.pgsql' 


[out:json]
[bbox:{{bbox}}];
( node["place"="state"]["name"="New Jersey"]; <; );
out geom;


RECOGNIZE ANYTHING IMAGE TAGGING
Installed from  https://github.com/xinyu1205/recognize-anything/tree/main

conda activate recognize-anything
conda deactivate

TEST FROM CURL
curl -X POST -F "file=@/cygdrive/d/Pictures/DSC01673.JPG" http://localhost:5000/inference



Packages Needed
 _libgcc_mutex      pkgs/main/linux-64::_libgcc_mutex-0.1-main
  _openmp_mutex      pkgs/main/linux-64::_openmp_mutex-5.1-1_gnu
  ca-certificates    pkgs/main/linux-64::ca-certificates-2025.9.9-h06a4308_0
  ld_impl_linux-64   pkgs/main/linux-64::ld_impl_linux-64-2.44-h153f514_2
  libffi             pkgs/main/linux-64::libffi-3.4.4-h6a678d5_1
  libgcc-ng          pkgs/main/linux-64::libgcc-ng-11.2.0-h1234567_1
  libgomp            pkgs/main/linux-64::libgomp-11.2.0-h1234567_1
  libstdcxx-ng       pkgs/main/linux-64::libstdcxx-ng-11.2.0-h1234567_1
  libxcb             pkgs/main/linux-64::libxcb-1.17.0-h9b100fa_0
  libzlib            pkgs/main/linux-64::libzlib-1.3.1-hb25bd0a_0
  ncurses            pkgs/main/linux-64::ncurses-6.5-h7934f7d_0
  openssl            pkgs/main/linux-64::openssl-3.0.18-hd6dcaed_0
  pip                pkgs/main/linux-64::pip-24.2-py38h06a4308_0
  pthread-stubs      pkgs/main/linux-64::pthread-stubs-0.3-h0ce48e5_1
  python             pkgs/main/linux-64::python-3.8.20-he870216_0
  readline           pkgs/main/linux-64::readline-8.3-hc2a1206_0
  setuptools         pkgs/main/linux-64::setuptools-75.1.0-py38h06a4308_0
  sqlite             pkgs/main/linux-64::sqlite-3.50.2-hb25bd0a_1
  tk                 pkgs/main/linux-64::tk-8.6.15-h54e0aa7_0
  wheel              pkgs/main/linux-64::wheel-0.44.0-py38h06a4308_0
  xorg-libx11        pkgs/main/linux-64::xorg-libx11-1.8.12-h9b100fa_1
  xorg-libxau        pkgs/main/linux-64::xorg-libxau-1.0.12-h9b100fa_0
  xorg-libxdmcp      pkgs/main/linux-64::xorg-libxdmcp-1.1.5-h9b100fa_0
  xorg-xorgproto     pkgs/main/linux-64::xorg-xorgproto-2024.1-h5eee18b_1
  xz                 pkgs/main/linux-64::xz-5.6.4-h5eee18b_1
  zlib               pkgs/main/linux-64::zlib-1.3.1-hb25bd0a_0






TODO
1. DONE - Check album url input security - DONE
2. ImageUtil & HttpUtil combine / refactor - DONE
3. ICloudShareReader & SharedPhotoManager combine / refactor - DONE
4. Better album and photo names and description on flickr upload.  
	Existing metadata - DONE
	Geolocation - DONE
	AI data
	UI to configure naming
5. Default source and target selections in UI - DONE
6. Photo name and description from reader not writer - DONE
7. Targets
	Local files downlaod
	OneDrive
	Google - DONE
8. Big transfers
	Local file uploads
	Selecting multiple albums
	Oauth token timeout/refresh
9. Dynamic react component creation from map of components
10. FlickrAuth TODO - token not found
11. View of job history and progress
12. Back should clear errors and after job submitted, no back

	
	
GOOGLE OAUTH2

Client Id: 833263805285-nk2koc7pfq0njk4qbbcb6rop6asktj26.apps.googleusercontent.com
Secret: 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

TO ADD SCOPES
1. Log into google with steele.arbeeny@gmail.com
2. search for cloud console
3. Go to My Console -> APIs and services
4. Credentials -> FotoFreedom App
5. Data Access
6. add and save here
7. Add to GoogleServlet newFlow()
8. You need to completely log out, delete the token in the DB and delete the connection to the 3rd party app
9. Google account -> security -> your connections -> delete



