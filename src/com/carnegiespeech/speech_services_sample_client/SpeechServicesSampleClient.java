package com.carnegiespeech.speech_services_sample_client;

import java.io.*;
import com.carnegiespeech.SendFile.SendFile;
import java.io.InputStreamReader;
import java.io.File;
import java.io.FileInputStream;

public class SpeechServicesSampleClient extends Thread {
	static String clientSecretKey=null;     
    public SpeechServicesSampleClient() {
    }        
    public static String read_xmlfile(String xmlfile){
    	String aareqstr = "";
    	try{
    		BufferedReader br = new BufferedReader(new InputStreamReader( new FileInputStream(xmlfile),"UTF-8"));
    		String line = br.readLine();
    		while(line != null){
    			aareqstr += "    "+line+"\n";
    			line = br.readLine();
    		}
    		br.close();
    		System.out.println(aareqstr);
    	}    	
    	catch (IOException e) {
    		e.printStackTrace();
		} 
    	catch (Exception e) {
    		e.printStackTrace();
		}   		
		return aareqstr;
    }
    
    /*Main function*/
    public static void main(String[] args) throws NumberFormatException,
	Exception {
    	boolean dev = false;
    	String srvAddress;
    	int srvPort;
    	String requester, requesterID, userID, requesterInfo;
    	int requesterSeqNum;
    	String xmlfile;
    	String audfile;
    	String resultFolder;
    	String TransactionID= null;
    	
    	if(dev){
    		srvAddress="";
    		srvPort = 0000;
    		xmlfile ="./b2u4l1p10_7-15-14-53-246.xml";
    		audfile ="./b2u4l1p10_7-15-14-53-246.flv"; 
    		resultFolder = "./";
    		requester = "";
    		requesterID = "";
    		userID = "";
    		requesterInfo = "";
    		requesterSeqNum = (int)(Math.random()*10000000);    		
    		TransactionID = "";
    		clientSecretKey = "";
    	}
    	else{
    		if(args.length!= 12){
    			System.out.println("Not enough inputs!\nUsage: java -jar java -jar SpeechServicesSampleClient.jar serverAddress portNumber aarequest.xml audfile.wav resultFolder requester requesterID userID requesterInfo requesterSeqNum transactionID clientSecretKey ");
    			System.exit(1);
    		}
    		srvAddress = args[0];
        	srvPort = Integer.parseInt(args[1]);
        	xmlfile = args[2];
        	audfile = args[3];
        	resultFolder = args[4];
        	requester = args[5];
        	requesterID = args[6];
        	userID = args[7];
        	requesterInfo = args[8];
        	requesterSeqNum = Integer.parseInt(args[9]);
        	TransactionID = args[10];
        	clientSecretKey = args[11];
    	}
    	String AAreq_str;    

    	
    	//System.out.println("server is: "+srvAddress);		
        SendFile send;    
        AAreq_str = read_xmlfile(xmlfile);  
        for (int k =1; k<=1; k++){ 
        send = new SendFile(srvAddress, srvPort);
        String ret = send.single_AA_transaction(requester, requesterID, userID, requesterInfo, requesterSeqNum, AAreq_str, audfile, clientSecretKey, TransactionID);
        System.out.println("AA Result is:\n"+ret);
        String resultFile = resultFolder+File.separator+TransactionID+"-AAResult.xml";
		FileOutputStream fos = new FileOutputStream(resultFile);
		PrintWriter pw = new PrintWriter(fos);
		pw.println(ret);			
		pw.close();
        }
        
    }
    
}
    
 
