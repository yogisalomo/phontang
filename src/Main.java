import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Locale;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWebAuthNoRedirect;
import com.dropbox.core.DbxWriteMode;


public class Main {
	//checks for connection to the internet through dummy request
    private static ArrayList<String> tempfiles;
	
	
    public static boolean isInternetReachable()
    {
        try {
            //make a URL to a known source
            URL url = new URL("http://www.google.com");

            //open a connection to that source
            HttpURLConnection urlConnect = (HttpURLConnection)url.openConnection();

            //trying to retrieve data from the source. If there
            //is no connection, this line will fail
            Object objData = urlConnect.getContent();

        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
        return true;
    }
	
	public static void main(String[] args) {
		// Variable Define
		final String APP_KEY = "xwi6bo4698k2g2z";
        final String APP_SECRET = "3excm8tr1krnkvl";
        String connStatus;
        boolean active = true;
        tempfiles = new ArrayList<String>();
        DbxAppInfo appInfo = new DbxAppInfo(APP_KEY, APP_SECRET);
        DbxRequestConfig config = new DbxRequestConfig("JavaTutorial/1.0",
            Locale.getDefault().toString());
        DbxWebAuthNoRedirect webAuth = new DbxWebAuthNoRedirect(config, appInfo);
        
        //Checking Internet Connection
        if(isInternetReachable()){
        	connStatus ="Online";
        }
        else{
        	connStatus ="Offline";
        }
        //Fill The List Of locally saved file
        BufferedReader br = null;
		try {
			String tempNum;
			String sCurrentLine;
 
			br = new BufferedReader(new FileReader("localSavedList.txt"));
 
			tempNum = br.readLine();
			for(int i=0; i< Integer.parseInt(tempNum);i++){
				sCurrentLine = br.readLine();
				tempfiles.add(sCurrentLine);
			}
 
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
        
        // Have the user sign in and authorize your app.
    	String authorizeUrl = webAuth.start(); 
        System.out.println("1. Go to: " + authorizeUrl);
        System.out.println("2. Click \"Allow\" (you might have to log in first)");
        System.out.println("3. Copy the authorization code.");
		
        try{
        	String code = new BufferedReader(new InputStreamReader(System.in)).readLine().trim();
        	// This will fail if the user enters an invalid authorization code.
            DbxAuthFinish authFinish = webAuth.finish(code);
            String accessToken = authFinish.accessToken;

            DbxClient client = new DbxClient(config, accessToken);
        	
            
            while(active){
            	//The Menu That appear to the user
            	System.out.println("Welcome, " + client.getAccountInfo().displayName);
            	System.out.println("Your Internet Status : "+ connStatus); //This shows wether you are connected to Internet or not
                System.out.println("What do you want to do today?");
                System.out.println("1. Upload a picture");
                System.out.println("2. Browse My Photos from This App in Dropbox");
                System.out.println("3. Browse Local Saved Photos");
                System.out.println("4. Upload all the Local Saved Photos to Dropbox");
                System.out.println("5. Exit");
                System.out.print("Command>");
                String option = new BufferedReader(new InputStreamReader(System.in)).readLine().trim();
                if(option.equals("1")){
                	//This option is used to upload your photo to your Dropbox
                	System.out.println("Please enter your file's absolute path:");
                	String filePath = new BufferedReader(new InputStreamReader(System.in)).readLine().trim();
                	String fileName = filePath.split("/")[filePath.split("/").length-1];
                	
                	if(connStatus.equals("Online")){
                		//If you have internet connection, your file will be directly uploaded to your Dropbox Account
                		File inputFile = new File(filePath);
                		FileInputStream inputStream = new FileInputStream(inputFile);
                    	try {
                            DbxEntry.File uploadedFile = client.uploadFile("/phontang/"+fileName,DbxWriteMode.add(), inputFile.length(), inputStream);
                            System.out.println("Uploaded: " + uploadedFile.toString());
                        } finally {
                            inputStream.close();
                        }
                	}
                	else{
                		//If you are not connected to Internet, The program will Save the photo locally
                		try {
                			Path FROM = Paths.get(filePath);
                		    Path TO = Paths.get("temp/"+fileName);
                		    //overwrite existing file, if exists
                		    CopyOption[] options = new CopyOption[]{
                		      StandardCopyOption.REPLACE_EXISTING,
                		      StandardCopyOption.COPY_ATTRIBUTES
                		    }; 
                		    Files.copy(FROM, TO, options);
                		    
                		    tempfiles.add(fileName);
                 
                		} catch (IOException e) {
                			e.printStackTrace();
                		}
                	}
                	
                    
                }
                else if(option.equals("2")){
                	//This option is used to see The Photos you already uploaded to Dropbox trough this App
                	if(connStatus.equals("Offline")){
                	//The App is unable to complete this action if you are not connected to Internet
                		System.out.println("Unable to Perform This Command. Please Check Your Internet Connection");
                	}
                	else if(connStatus.equals("Online")){
                	//The App will give you the list of photos you already uploaded to Dropbox	
                		DbxEntry.WithChildren listing = client.getMetadataWithChildren("/phontang");
                		System.out.println("Photos your App:");
                		for (DbxEntry child : listing.children) {
                		    System.out.println("-" + child.name);
                		}
                		System.out.println("Do You Want to Download any photo?(Y/N)");
                		//This menu will also serve if you want to download any photo from the dropbox
                		String dl = new BufferedReader(new InputStreamReader(System.in)).readLine().trim();
                		if(dl.equals("Y")){
                			System.out.println("Write the full name of The Photo: ");
                			String dlName = new BufferedReader(new InputStreamReader(System.in)).readLine().trim();
                			
                			FileOutputStream outputStream = new FileOutputStream(dlName);
                	        try {
                	            DbxEntry.File downloadedFile = client.getFile("/phontang/"+dlName, null, outputStream);
                	            System.out.println("Metadata: " + downloadedFile.toString());
                	        } finally {
                	            outputStream.close();
                	        }
                		}
                	}
                }
                else if(option.equals("3")){
                	//This option is used to browse File from temp/ folder, which are photos that is waiting to be uploaded to dropbox
                	System.out.println("Files That are currently saved locally :");
                	for(int i=0; i< tempfiles.size();i++){
                		System.out.println("- "+ tempfiles.get(i));
                	}
                }
                else if(option.equals("4")){
                	//This option is used to upload all the files from temporary folder to dropbox once you have internet connection
                	if(connStatus.equals("Offline")){
                		System.out.println("Unable to Perform This Command. Please Check Your Internet Connection");
                	}
                	else if(connStatus.equals("Online")){
                		//Upload the list of local saved file
                		for(int i=0; i< tempfiles.size();i++){
                			File inputFile = new File("temp/"+tempfiles.get(tempfiles.size()-1));
                    		FileInputStream inputStream = new FileInputStream(inputFile);
                        	try {
                                DbxEntry.File uploadedFile = client.uploadFile("/phontang/"+tempfiles.get(tempfiles.size()-1),DbxWriteMode.add(), inputFile.length(), inputStream);
                                System.out.println("Uploaded: " + uploadedFile.toString());
                            } finally {
                                inputStream.close();
                            }
                		}
                		//Clear the temp files since all of the photos already uploaded to dropbox
                		tempfiles.clear();
                	}
                }
                else if(option.equals("5")){
                	active = false;
                	try {
            			File file = new File("localSavedList.txt");
             
            			// if file doesnt exists, then create it
            			if (!file.exists()) {
            				file.createNewFile();
            			}
             
            			FileWriter fw = new FileWriter(file.getAbsoluteFile());
            			BufferedWriter bw = new BufferedWriter(fw);
            			bw.write(tempfiles.size()+"");
            			bw.newLine();
            			for(int i=0; i< tempfiles.size();i++){
            				bw.write(tempfiles.get(i));
            				if(i< tempfiles.size()-1){
            					bw.newLine();
            				}
            			}
            			bw.close();
             
            		} catch (IOException e) {
            			e.printStackTrace();
            		}
                }
            }
        	
            
            
        }
        catch(Exception e){
        	System.out.println(e.getMessage());
        }
     }
}

