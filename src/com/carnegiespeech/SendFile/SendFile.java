/**Title: SendFile.java 
*Description: Class for sending file based transaction to SpeechServices server and receive the AA result 
*	This is the working thread for MTFE server. Every connection from client side will
*Copyright: Carnegie Speech (c) 2012-2014 
*Company: Carnegie Speech Company
* 
* @author Chi Zhang 
* @version 1.4.1
* History: 	created Oct. 8 2012;
*  			ver. 1.4.1 Apr. 2014;
*  				-Make the validation attributions are following
*  				the new format which is described in "Carnegie Speech SimpleDB Table Descriptions" @
*  				https://docs.google.com/a/carnegiespeech.com/spreadsheet/ccc?key=0ArF7_3qEOwc9dGZwcnRkMWttVkMyUWpHbFFRVWlTMWc&usp=drive_web#gid=0
*  				-Cleared several depreciated methods 
*/
package com.carnegiespeech.SendFile;

import java.io.*;
//import java.net.InetAddress;
import java.net.Socket;
import java.security.MessageDigest;
import java.util.Date;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.text.SimpleDateFormat;


public class SendFile
{
	static String verID = "SendFileClass_JAVA_v1.4.1";
    Socket clientSocket;
    byte byteArray[];
    int in;
    int HdrLen = 0, BdyLen = 0;
    BufferedOutputStream bos;
    DataOutputStream dos;
    DataInputStream  dis;
    //int timeout = 60000;
    
    PrintWriter outm;
    /*construction method for class SendFile, the socket is established in this method*/
    public SendFile(String srvAddress, int portnum)
    {  
    	try{
    		//System.out.println("srvAddress is: "+srvAddress);
    		clientSocket = new Socket(srvAddress, portnum);
    		//clientSocket.setSoTimeout(3000);
    		//clientSocket = new Socket (InetAddress.getLocalHost(), portnum);
    		//System.out.println("Send Buf Size is:"+clientSocket.getSendBufferSize());
    		bos = new BufferedOutputStream(clientSocket.getOutputStream());
    		dos = new DataOutputStream(bos);
    		outm = new PrintWriter(bos, true);
    		InputStream ins = clientSocket.getInputStream();
    		dis = new DataInputStream(ins);
    	}
    	catch (IOException e){    	
    		System.out.println(srvAddress + ":" + portnum + "(close)");
    	}
    	catch(Exception e){    		
    	}
    }
    public void close(){  
    	
    	try{
    		if(null != clientSocket){
    			clientSocket.close();
    			System.out.println("close socket");
    		}
    		clientSocket.close();
    	}
    	catch (IOException e){  
    		e.printStackTrace();
    	}
    	catch(Exception e){    	
    		e.printStackTrace();
    	}
    }

    /*Sending AA Request Message, the validate_str and AAreq_str is prepared before calling this method*/
    public void send_AAReq(String TransactionID, String validate_str, String AAreq_str, String file_type){
        String audio_str;
        String audio_type = file_type;
        String message_str;        
        String header_str;
        //String entityString = "<!ENTITY aacute	\"&#x00E1;\"><!ENTITY Aacute	\"&#x00C1;\"><!ENTITY acirc	\"&#x00E2;\"><!ENTITY Acirc	\"&#x00C2;\"><!ENTITY agrave	\"&#x00E0;\"><!ENTITY Agrave	\"&#x00C0;\"><!ENTITY aring	\"&#x00E5;\"><!ENTITY Aring	\"&#x00C5;\"><!ENTITY atilde	\"&#x00E3;\"><!ENTITY Atilde	\"&#x00C3;\"><!ENTITY auml	\"&#x00E4;\"><!ENTITY Auml	\"&#x00C4;\"><!ENTITY aelig	\"&#x00E6;\"><!ENTITY AElig	\"&#x00C6;\"><!ENTITY ccedil	\"&#x00E7;\"><!ENTITY Ccedil	\"&#x00C7;\"><!ENTITY eth	\"&#x00F0;\"><!ENTITY ETH	\"&#x00D0;\"><!ENTITY eacute	\"&#x00E9;\"><!ENTITY Eacute	\"&#x00C9;\"><!ENTITY ecirc	\"&#x00EA;\"><!ENTITY Ecirc	\"&#x00CA;\"><!ENTITY egrave	\"&#x00E8;\"><!ENTITY Egrave	\"&#x00C8;\"><!ENTITY euml	\"&#x00EB;\"><!ENTITY Euml	\"&#x00CB;\"><!ENTITY iacute	\"&#x00ED;\"><!ENTITY Iacute	\"&#x00CD;\"><!ENTITY icirc	\"&#x00EE;\"><!ENTITY Icirc	\"&#x00CE;\"><!ENTITY igrave	\"&#x00EC;\"><!ENTITY Igrave	\"&#x00CC;\"><!ENTITY iuml	\"&#x00EF;\"><!ENTITY Iuml	\"&#x00CF;\"><!ENTITY ntilde	\"&#x00F1;\"><!ENTITY Ntilde	\"&#x00D1;\"><!ENTITY oacute	\"&#x00F3;\"><!ENTITY Oacute	\"&#x00D3;\"><!ENTITY ocirc	\"&#x00F4;\"><!ENTITY Ocirc	\"&#x00D4;\"><!ENTITY ograve	\"&#x00F2;\"><!ENTITY Ograve	\"&#x00D2;\"><!ENTITY oslash	\"&#x00F8;\"><!ENTITY Oslash	\"&#x00D8;\"><!ENTITY otilde	\"&#x00F5;\"><!ENTITY Otilde	\"&#x00D5;\"><!ENTITY ouml	\"&#x00F6;\"><!ENTITY Ouml	\"&#x00D6;\"><!ENTITY szlig	\"&#x00DF;\"><!ENTITY thorn	\"&#x00FE;\"><!ENTITY THORN	\"&#x00DE;\"><!ENTITY uacute	\"&#x00FA;\"><!ENTITY Uacute	\"&#x00DA;\"><!ENTITY ucirc	\"&#x00FB;\"><!ENTITY Ucirc	\"&#x00DB;\"><!ENTITY ugrave	\"&#x00F9;\"><!ENTITY Ugrave	\"&#x00D9;\"><!ENTITY uuml	\"&#x00FC;\"><!ENTITY Uuml	\"&#x00DC;\"><!ENTITY yacute	\"&#x00FD;\"><!ENTITY Yacute	\"&#x00DD;\"><!ENTITY yuml	\"&#x00FF;\">";
        message_str ="";
        //message_str = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE message[\n\t" + entityString + "\n]>\n";
        
        /*Preparing the aa request message string*/
        header_str = "<message serviceType='AARequest' transactionID='"+TransactionID+"'>";
        audio_str = "<audio type='"+audio_type+"' save='True'/>";
        message_str += header_str+"\n  "+validate_str+"\n  "+audio_str+"\n"+AAreq_str+"\n</message>";
        System.out.println("AARequest Message Header is:"+message_str);

        /*Preparing pre-amble values*/
        HdrLen = message_str.getBytes().length; // Calculate how many bytes the whole message is. 
        BdyLen = 0;
        try{
        	/*start to send message, dos is the DataOutpoutStream through socket*/           
        	dos.writeInt(HdrLen); // send Header Length, a 32-bit integer
        	dos.flush();
        	dos.writeInt(BdyLen); // send Message Body Length, a 32-bit integer
        	dos.flush();
            //System.out.println("AArequest Message: HdrLen:"+HdrLen+" BdyLen:"+ BdyLen);         
            dos.write(message_str.getBytes(), 0, HdrLen); //Send message string 
            dos.flush();
            //System.out.println("AArequest Message has been sent:\n"+message_str);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    
    /*Sending AA Request Message, the validate_str and AAreq_str is prepared before calling this method*/
    public void send_AAReq_PCM(String TransactionID, String validate_str, String AAreq_str, int freq_s){
        String audio_str;
        String message_str;        
        String header_str;
        //String entityString = "<!ENTITY aacute	\"&#x00E1;\"><!ENTITY Aacute	\"&#x00C1;\"><!ENTITY acirc	\"&#x00E2;\"><!ENTITY Acirc	\"&#x00C2;\"><!ENTITY agrave	\"&#x00E0;\"><!ENTITY Agrave	\"&#x00C0;\"><!ENTITY aring	\"&#x00E5;\"><!ENTITY Aring	\"&#x00C5;\"><!ENTITY atilde	\"&#x00E3;\"><!ENTITY Atilde	\"&#x00C3;\"><!ENTITY auml	\"&#x00E4;\"><!ENTITY Auml	\"&#x00C4;\"><!ENTITY aelig	\"&#x00E6;\"><!ENTITY AElig	\"&#x00C6;\"><!ENTITY ccedil	\"&#x00E7;\"><!ENTITY Ccedil	\"&#x00C7;\"><!ENTITY eth	\"&#x00F0;\"><!ENTITY ETH	\"&#x00D0;\"><!ENTITY eacute	\"&#x00E9;\"><!ENTITY Eacute	\"&#x00C9;\"><!ENTITY ecirc	\"&#x00EA;\"><!ENTITY Ecirc	\"&#x00CA;\"><!ENTITY egrave	\"&#x00E8;\"><!ENTITY Egrave	\"&#x00C8;\"><!ENTITY euml	\"&#x00EB;\"><!ENTITY Euml	\"&#x00CB;\"><!ENTITY iacute	\"&#x00ED;\"><!ENTITY Iacute	\"&#x00CD;\"><!ENTITY icirc	\"&#x00EE;\"><!ENTITY Icirc	\"&#x00CE;\"><!ENTITY igrave	\"&#x00EC;\"><!ENTITY Igrave	\"&#x00CC;\"><!ENTITY iuml	\"&#x00EF;\"><!ENTITY Iuml	\"&#x00CF;\"><!ENTITY ntilde	\"&#x00F1;\"><!ENTITY Ntilde	\"&#x00D1;\"><!ENTITY oacute	\"&#x00F3;\"><!ENTITY Oacute	\"&#x00D3;\"><!ENTITY ocirc	\"&#x00F4;\"><!ENTITY Ocirc	\"&#x00D4;\"><!ENTITY ograve	\"&#x00F2;\"><!ENTITY Ograve	\"&#x00D2;\"><!ENTITY oslash	\"&#x00F8;\"><!ENTITY Oslash	\"&#x00D8;\"><!ENTITY otilde	\"&#x00F5;\"><!ENTITY Otilde	\"&#x00D5;\"><!ENTITY ouml	\"&#x00F6;\"><!ENTITY Ouml	\"&#x00D6;\"><!ENTITY szlig	\"&#x00DF;\"><!ENTITY thorn	\"&#x00FE;\"><!ENTITY THORN	\"&#x00DE;\"><!ENTITY uacute	\"&#x00FA;\"><!ENTITY Uacute	\"&#x00DA;\"><!ENTITY ucirc	\"&#x00FB;\"><!ENTITY Ucirc	\"&#x00DB;\"><!ENTITY ugrave	\"&#x00F9;\"><!ENTITY Ugrave	\"&#x00D9;\"><!ENTITY uuml	\"&#x00FC;\"><!ENTITY Uuml	\"&#x00DC;\"><!ENTITY yacute	\"&#x00FD;\"><!ENTITY Yacute	\"&#x00DD;\"><!ENTITY yuml	\"&#x00FF;\">";
        
        System.out.println("AA Request TransactionID:"+TransactionID);
        message_str = "";
        //message_str = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE message[\n\t" + entityString + "\n]>\n";
        /*Preparing the message string*/        
        header_str ="<message serviceType='AARequest' TransactionID='"+TransactionID+"'>";
        audio_str = "<audio type='PCM' sampling ='"+String.valueOf(freq_s)+"' save='True'/>" ;
        message_str += header_str+"\n  "+validate_str+"\n  "+audio_str+"\n"+AAreq_str+"\n</message>";
        System.out.println(message_str);
        /*Preparing pre-amble values*/
        HdrLen = message_str.getBytes().length; // Calculate how many bytes the whole message is. 
        System.out.println(HdrLen);
        //MsgLen = HdrLen;
        BdyLen = 0;
        try{   	
            /*start to send message, dos is the DataOutpoutStream through socket*/           
        	dos.writeInt(HdrLen); // send Header Length, a 32-bit integer
        	dos.flush();
        	dos.writeInt(BdyLen); // send Message Body Length, a 32-bit integer
        	dos.flush();
            //System.out.println("AArequest Message: HdrLen:"+HdrLen+" BdyLen:"+ BdyLen);         
            dos.write(message_str.getBytes(), 0, HdrLen); //Send message string 
            dos.flush();
            //System.out.println("AArequest Message has been sent:\n"+message_str);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    /*Method used to get the root node of the received XML style AA Result Message*/
    private static Element getRootNode(String XML) {
    	Element root = null;
    	try {
    		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        	DocumentBuilder builder = factory.newDocumentBuilder();
        	InputStream is= new ByteArrayInputStream(XML.getBytes());
        	Document doc=  builder.parse(is);        	    		
			root = doc.getDocumentElement();			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}catch (Exception e){
			e.printStackTrace();
		}		
		return root;
	}
    
    /**
	 * Used for sending Audio Block Message.
	 * Send the whole audio file using just one Audio Block Message
	 * with specified data transfer rate
	 * 
	 * @param TransactionID
	 * string of Transaction ID
	 * @param filename
	 * string of filename of audio file
	 * @param drate
	 * string of data transfer rate in (Bytes/second)
	 * 	Example: 1024
	 */
    public void send_Audiofile(String TransactionID, String filename, String drate)throws FileNotFoundException{
    	if ((null == drate)||(""==drate)||(drate.compareTo("0")==0)){
    		send_Audiofile(TransactionID,filename);
    	}
    	else{
	    	String header_str;
	    	String audblk_str;
	    	String message_str;
	    	int rate = Integer.valueOf(drate);
	    	int fileByte;
	        /*Preparing the message string*/
	    	fileByte = findFileSize(filename); //Get how many Bytes of the audio file
	    	//System.out.println("Audio length is: "+(int)fileByte*7.37/44.1+" ms, audio size is: "+ fileByte+" Bytes");
	    	/*Calculate how long these audio file should be sent under the drate*/
	    	long total_time = (long)(((float) fileByte/rate) *1000);
	    	/*byte buffer for whole audio file*/
	    	byte [] aud_buffer = new byte[fileByte];
	    	/*prepare header*/
	    	header_str = (new StringBuilder("<message serviceType=\'AudioBlock\' TransactionID=\'")).append(TransactionID).append("\' final = \'true\'>").toString();
	    	audblk_str = (new StringBuilder("\n  <AudioBlock  AAReqID=\'").append(TransactionID).append("\'>\n  </AudioBlock>\n</message>")).toString();
	    	message_str = header_str+audblk_str;
	        /*Preparing pre-amble values*/
	    	HdrLen = message_str.getBytes().length;// Calculate how many bytes the header string is
	    	BdyLen = fileByte;
	    	//System.out.println("Audio Message: HdrLen:"+HdrLen+" BdyLen:"+ BdyLen);    
	    	//System.out.println("Aduio message header is:"+message_str);
	    	try{   
	    		/*Open file handler to read audio file*/ 		
	    		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(filename));
	    		bis.read(aud_buffer, 0, fileByte);
	    		/*start to send message, dos is the DataOutpoutStream through socket*/
	        	dos.writeInt(HdrLen);  //send Header Length, a 32-bit integer
	        	dos.flush();
	        	dos.writeInt(BdyLen);  //send Body Length, a 32-bit integer
	        	dos.flush();
	            dos.write(message_str.getBytes(), 0, HdrLen); //send header string 
	            dos.flush();
	            /*Sending audio samples*/
	            long st = System.currentTimeMillis();
	            dos.write(aud_buffer, 0, fileByte);
	            dos.flush();
	            long ed = System.currentTimeMillis();
	            long elapsed_time = ed-st;
	            if ((total_time-elapsed_time)>0){
	            	Thread.sleep(total_time-elapsed_time);
	            }
	            //System.out.println("Done sent "+fileByte+" audio bytes in "+total_time+ " miliseconds, actually in "+elapsed_time+" miliseconds");            
	        }
	        catch(IOException e){
	            e.printStackTrace();
	        }
	        catch(Exception e){
	            e.printStackTrace();
	        }
    	}
    }

    /**@overload
	 * Used for sending Audio Block Message.
	 * Send the whole audio file using just one Audio Block Message
	 * 
	 * @param TransactionID
	 * string of Transaction ID
	 * @param filename
	 * string of filename of audio file
	 */
    public void send_Audiofile(String TransactionID, String filename)throws FileNotFoundException
    {
    	String header_str;
    	String audblk_str;
    	String message_str;
    	int fileByte;
        /*Preparing the message string*/
    	fileByte = findFileSize(filename); //Get how many Bytes of the audio file
    	//System.out.println("Audio length is: "+(int) (fileByte*7.37/44.1)+" ms, audio size is: "+ fileByte+" Bytes");
    	/*byte buffer for whole audio file*/
    	byte [] aud_buffer = new byte[fileByte];
    	/*prepare header*/
    	header_str = (new StringBuilder("<message serviceType=\'AudioBlock\' transactionID=\'")).append(TransactionID).append("\' final = \'true\'>").toString();
    	audblk_str = (new StringBuilder("\n  <AudioBlock  AAReqID=\'").append(TransactionID).append("\'>\n  </AudioBlock>\n</message>")).toString();
    	message_str = header_str+audblk_str;
        /*Preparing pre-amble values*/
    	HdrLen = message_str.getBytes().length;// Calculate how many bytes the header string is 
    	BdyLen = fileByte;
    	System.out.println("Audio Message: HdrLen:"+HdrLen+" BdyLen:"+ BdyLen);    
    	System.out.println("Aduio message header is:"+message_str);
    	try{   
    		/*Open file handler to read audio file*/ 		
    		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(filename));
    		bis.read(aud_buffer, 0, fileByte);
    		/*start to send message, dos is the DataOutpoutStream through socket*/
        	dos.writeInt(HdrLen);  //send Header Length, a 32-bit integer
        	dos.flush();
        	dos.writeInt(BdyLen);  //send Body Length, a 32-bit integer
        	dos.flush();        	
            dos.write(message_str.getBytes(), 0, HdrLen); //send header string 
            dos.flush();
            System.out.println("test");
            /*Sending audio samples*/
            dos.write(aud_buffer, 0, BdyLen);
            dos.flush();
            //System.out.println("Done sent "+fileByte+" audio bytes");            
        }
        catch(IOException e){
            e.printStackTrace();
        }
        catch(Exception e){
            e.printStackTrace();
        }        
    }
    
    /**@overload
	 * Used for sending Audio Block Message.
	 * Split audio file into several fixed length segments , send each segment within a Audio Block Message
	 * 
	 * @param TransactionID
	 * string of Transaction ID
	 * @param filename
	 * string of filename of audio file
	 * @param blklen
	 * integer describing the audio block length
	 */
    public void send_Audiofile(String TransactionID, String filename, int blklen)throws FileNotFoundException
    {
    	/*Each Audio Block Message contains "blklen" Bytes of audio data, the final Audio Block Message contains data less or equal to "blklen" Bytes*/
    	String header_str;	    	
    	String audblk_str;
    	String message_str;
        int msg_index;
    	int fileByte;
    	int message_num;
    	int leftByteNum;
    	try{	    	
	    	fileByte = findFileSize(filename);
	    	message_num = fileByte/blklen; //Calculate how many Audio Block Messages needed for sending this audio file.
	    	leftByteNum = fileByte%blklen;//Calculate how many Bytes of data for the last Audio Block Message
	    	if(((leftByteNum == 0)&&(message_num ==1))||(leftByteNum == fileByte)){
	    		//If the audio file size is less than the "blklen", use only one Audio Block Message to send
	    		send_Audiofile(TransactionID, filename); 
	    	}
	    	/*Open file handler to read audio file*/ 
	    	BufferedInputStream bis = new BufferedInputStream(new FileInputStream(filename));
	        /*Reading the audio file*/
	        byte [] aud_buffer =  new byte[fileByte];
	        bis.read(aud_buffer, 0, fileByte);
	        byte [] msg_buffer = new byte[blklen];
	        /*Preparing the message string*/    	
	    	header_str = (new StringBuilder("<message serviceType=\'AudioBlock\' TransactionID=\'")).append(TransactionID).append("\' final = \'false\'>").toString();
	    	audblk_str = (new StringBuilder("<AudioBlock  AAReqID=\'").append(TransactionID).append("\'></AudioBlock></message>")).toString();
	    	message_str = header_str+audblk_str;
	    	
	        /*Preparing pre-amble values*/
	    	HdrLen = message_str.getBytes().length; //Calculate how many Bytes of the header    	
	    	for(msg_index = 0; msg_index<message_num; msg_index++){
	    		System.arraycopy(aud_buffer, msg_index*blklen, msg_buffer, 0, blklen);
	    		BdyLen = blklen; //Calculate how many bytes of the body of Audio Block Message
				System.out.println("Sending "+ (msg_index+1)+" message out of "+ (message_num+1)+" messages");
				/*Sending Pre-amble, header message*/
				dos.writeInt(HdrLen);dos.flush(); //Send Header Length, a 32-bit integer
				dos.writeInt(BdyLen);dos.flush(); //Send Body Length, a 32-bit integer
				System.out.println("Audioblk Message: HdrLen:"+HdrLen+" BdyLen:"+ BdyLen);
				System.out.println("Aduioblk message header is:"+message_str);
				/*sending header string*/
				dos.write(message_str.getBytes(), 0, HdrLen);dos.flush(); //Send header string
				/*Sending audio samples*/
				dos.write(msg_buffer, 0, blklen); dos.flush();	            
	    		System.out.println("Done sent "+blklen+" audio bytes");    		
    		}	    	
	    	/*Sending the last Audio Block Message*/	    	
	    	header_str = (new StringBuilder("<message serviceType=\'AudioBlock\' TransactionID=\'")).append(TransactionID).append("\' final = \'true\'>").toString();
	    	message_str = header_str+audblk_str;
	    	HdrLen = message_str.getBytes().length; //Calculate how many bytes the header has
	    	BdyLen = leftByteNum; //Calculate how many bytes of the body of audio message is
	    	System.out.println("Sending "+ (msg_index+1)+" message out of "+ (message_num+1)+" messages");
	    	System.out.println("Audioblk Message: HdrLen:"+HdrLen+" BdyLen"+ BdyLen);    
	    	System.out.println("Aduioblk message header is:"+message_str);
			
			/*Sending Pre-amble, header message*/			
			dos.writeInt(HdrLen);dos.flush();//Send Header Length, a 32-bit integer
			dos.writeInt(BdyLen);dos.flush();//Send Body Length, a 32-bit integer
			/*sending header string*/
			dos.write(message_str.getBytes(), 0, HdrLen);dos.flush(); //Send header string
			/*Sending audio samples*/
			msg_buffer = new byte[leftByteNum];
			System.arraycopy(aud_buffer, message_num*blklen, msg_buffer, 0, leftByteNum);
			dos.write(msg_buffer, 0, leftByteNum);dos.flush();	            
    		System.out.println("Done sent "+leftByteNum+" audio bytes");	    	
    	}
        catch(IOException e){
            e.printStackTrace();
        }
        catch(Exception e){
            e.printStackTrace();
        }        
    }

    /**@overload
	 * Used for sending Audio Block Message.
	 * Split audio file into several fixed length segments , send each segment within a Audio Block Message
	 * with specified data transfer rate
	 * 
	 * @param TransactionID
	 * string of Transaction ID
	 * @param filename
	 * string of filename of audio file
	 * @param blklen
	 * integer describing the audio block length
	 * @param drate
	 * string of data transfer rate in (Bytes/second)
	 * 	Example: 1024
	 */
    public void send_Audiofile(String TransactionID, String filename, int blklen, String drate)throws FileNotFoundException
    {
    	if(drate == null){
    		send_Audiofile(TransactionID,filename, blklen);    		
    	}
    	else{
	    	/*Each Audio Block Message contains "blklen" Bytes of audio data, except the final Audio Block Message*/
	    	String header_str;	    	
	    	String audblk_str;
	    	String message_str;
	    	int rate  = Integer.valueOf(drate);
	        int msg_index;
	    	int fileByte;
	    	int message_num;
	    	int leftByteNum;
	    	long total_time;
	    	long st,ed;
	    	try{	    	
		    	fileByte = findFileSize(filename);
		    	message_num = fileByte/blklen; //Calculate how many Audio Block Messages needed for sending this audio file.
		    	leftByteNum = fileByte%blklen;//Calculate how many Bytes of data for the last Audio Block Message
		    	if(((leftByteNum == 0)&&(message_num ==1))||(leftByteNum == fileByte)){
		    		//If the audio file size is less than the "blklen", use only one Audio Block Message to send
		    		send_Audiofile(TransactionID, filename); 
		    	}
		    	/*Open file handler to read audio file*/ 
		    	BufferedInputStream bis = new BufferedInputStream(new FileInputStream(filename));
		        /*Reading the audio file*/
		        byte [] aud_buffer =  new byte[fileByte];
		        bis.read(aud_buffer, 0, fileByte);
		        byte [] msg_buffer = new byte[blklen];
		        /*Preparing the message string*/    	
		    	header_str = (new StringBuilder("<message serviceType=\'AudioBlock\' TransactionID=\'")).append(TransactionID).append("\' final = \'false\'>").toString();
		    	audblk_str = (new StringBuilder("<AudioBlock  AAReqID=\'").append(TransactionID).append("\'></AudioBlock></message>")).toString();
		    	message_str = header_str+audblk_str;
		        /*Preparing pre-amble values*/
		    	HdrLen = message_str.getBytes().length; //Calculate how many Bytes of the header    	
		    	for(msg_index = 0; msg_index<message_num; msg_index++){
		    		System.arraycopy(aud_buffer, msg_index*blklen, msg_buffer, 0, blklen);
		    		BdyLen = blklen; //Calculate how many bytes of the body of Audio Block Message
					System.out.println("Sending "+ (msg_index+1)+" message out of "+ (message_num+1)+" messages");
					/*Sending Pre-amble, header message*/					
					dos.writeInt(HdrLen);dos.flush(); //Send Header Length, a 32-bit integer
					dos.writeInt(BdyLen);dos.flush(); //Send Body Length, a 32-bit integer
					System.out.println("Audioblk Message: HdrLen:"+HdrLen+" BdyLen:"+ BdyLen);
					System.out.println("Aduioblk message header is:"+message_str); 
					/*sending header string*/
					dos.write(message_str.getBytes(), 0, HdrLen);dos.flush(); //Send header string
					total_time = (long)(((float) blklen/rate) *1000);
					/*Sending audio samples*/
					st = System.currentTimeMillis(); 
					dos.write(msg_buffer, 0, blklen); dos.flush();	
					ed = System.currentTimeMillis();
					if ((total_time-(ed-st))>0){
						Thread.sleep(total_time-(ed-st));
					}
		    		System.out.println("Done sent "+blklen+" audio bytes");    		
	    		}	    	
		    	/*Sending the last Audio Block Message*/	    	
		    	header_str = (new StringBuilder("<message serviceType=\'AudioBlock\' TransactionID=\'")).append(TransactionID).append("\' final = \'true\'>").toString();
		    	message_str = header_str+audblk_str;
		    	HdrLen = message_str.getBytes().length; //Calculate how many bytes the header has
		    	BdyLen = leftByteNum;
				System.out.println("Sending "+ (msg_index+1)+" message out of "+ (message_num+1)+" messages");
				System.out.println("Audioblk Message: HdrLen:"+HdrLen+" BdyLen:"+ BdyLen);
				System.out.println("Aduioblk message header is:"+message_str);
				/*Sending Pre-amble, header message*/
				dos.writeInt(HdrLen);dos.flush();//Send Header Length, a 32-bit integer
				dos.writeInt(BdyLen);dos.flush();//Send Body Length, a 32-bit integer
				/*sending header string*/
				dos.write(message_str.getBytes(), 0, HdrLen);dos.flush(); //Send header string
				/*Sending audio samples*/
				msg_buffer = new byte[leftByteNum];
				System.arraycopy(aud_buffer, message_num*blklen, msg_buffer, 0, leftByteNum);
				total_time = (long)(((float) leftByteNum/rate) *1000);
				st = System.currentTimeMillis();
				dos.write(msg_buffer, 0, leftByteNum);dos.flush();
				ed = System.currentTimeMillis();
				Thread.sleep(total_time-(ed-st));
	    		System.out.println("Done sent "+leftByteNum+" audio bytes");	    	
	    	}
	        catch(IOException e){
	            e.printStackTrace();
	        }
	        catch(Exception e){
	            e.printStackTrace();
	        }      
    	}
    }
  
    
    /**
	 * This method is to receive the AA Result Message from Speech Service Server
	 * 
	 * @return 
	 * The string of AA result 
	 */
    public String recv_AAresult() throws IOException{
    	int  HedrLen, BodyLen, str_len;
    	String recvStr = null;
    	InputStream local_is = clientSocket.getInputStream();
    	DataInputStream local_dis = new DataInputStream(local_is);
    	
    	/*Start to read AA Result Message, "local_dis" is the DataInputStream through Socket*/
    	HedrLen = local_dis.readInt();// Read a 32-bit Integer as header length
    	BodyLen = local_dis.readInt();// Read a 32-bit Integer as header length
    	str_len = HedrLen+BodyLen; //Calculate how many Bytes the message string is within AA Result Message
    	
    	byte [] recvBytes = new byte[str_len];
    	local_dis.readFully(recvBytes, 0, str_len);//Read the message string in bytes					
    	recvStr = new String(recvBytes);//convert from bytes to string
    	//System.out.println("Received resposne\n");
    	return recvStr;
    }
    
    /**
	 * This method is to write the received AA Result String into a local xml file
	 * 
	 * @param folder
	 * string of folder path
	 * @param TransactionID
	 * string of transactionID, to form file name 
	 * @param reqID
	 * request ID
	 * @param recvd_aaresult
	 * string of received AA result
	 */
    public void write_AAresult(String folder, String TransactionID, String reqID, String recvd_aaresult){
    	String filename = folder+reqID+"-"+TransactionID+"-AAResult.xml";
    	File file = new File(filename);
    	if(file.exists()==true){file.delete();}
    	System.out.println(filename);
    	try {
    		PrintWriter pw = new PrintWriter(new FileOutputStream(filename), true); 
    		pw.println(recvd_aaresult);
			pw.flush();
            pw.close();    		
    	}
    	catch(IOException e){
    		System.out.println("write AAresult file Catched IOException");
    	}
    }
    
    /**
	 * This method is to generate a string of validation node of AA request message header
	 * 
	 * @param auth_str
	 * string contains validation information in xml style format
	 * @param clientSecretKey
	 * string of client secret Key, customer should obtained its unique key from Carnegie Speech 
	 */
       
    public static String generate_valid_string(String auth_str, String clientSecretKey){
    	Element root = null;
    	String valid_str = "";
    	String requester = null;
    	String requesterID = null;
    	String requesterInfo = null;
    	String requesterSeqNum = null;
    	String dateTimeStamp = null;
    	String hashToken  = null;    	    	
    	
    	try {
			root =getRootNode(auth_str);
			System.out.println("requester is: "+root.getAttribute("requester").toString());
			requester = root.getAttribute("requester").toString();
			requesterID = root.getAttribute("requesterID").toString();			
			requesterInfo = root.getAttribute("requesterInfo").toString();
			requesterSeqNum = root.getAttribute("requesterSeqNum").toString();

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZ");//2014-01-27T15:53:30.167+00:00
			String DateTimeStr = sdf.format(new Date());
			//dateTimeStamp=DateTimeStr.substring(0, 26)+":"+DateTimeStr.substring(26,DateTimeStr.length());
		
			//dateTimeStamp ="2015-10-05 08:49:38";			
			/*Generate MD5 hash token string*/
			String hashTokenInstance = requester+"-"+requesterID+"-"+requesterInfo+"-"+dateTimeStamp+"-"+requesterSeqNum+"-"+clientSecretKey;
			MessageDigest md = MessageDigest.getInstance("MD5");		
			hashToken = byteArrayToHexString(md.digest(hashTokenInstance.getBytes()));		
			/*Generate validation node string*/
			valid_str = (new StringBuilder("<validate requester=\'")).append(requester).append("\'\n         requesterID=\'").append(requesterID).append("\'\n         requesterInfo=\'").append(requesterInfo).append("\'\n         requesterSeqNum=\'").append(requesterSeqNum).append("\'\n         dateTimeStamp=\'").append(dateTimeStamp).append("\'\n         hashToken=\'").append(hashToken).append("\'/>").toString();
    	}
    	catch (Exception e) {
			valid_str = null;
    		return valid_str;
		}   	
		return valid_str;
    }
    
    /**
	 * This method is to Making a single AA transaction with specified data transfer rate
	 * 
	 * @param auth_str
	 * string contains validation information in xml style format
	 * @param AAreq_str
	 * string of AArequest node 
	 * @param Audio_filename
	 * string of audio file name
	 * @param clientSecretKey
	 * string of client secret Key, customer should obtained its unique key from Carnegie Speech
	 * @param TransactionID
	 * string of transactionID
	 * @param f_type
	 * string of audio format
	 * 	Example: ".wav"
	 * @param drate
	 * string of data transfer rate in (Bytes/second)
	 * 	Example: 1024
	 * 
	 */
    public String single_AA_transaction(String auth_str, String AAreq_str, String Audio_filename, String secretKey, String TransactionID, String f_type, String drate){
    	if (null == drate){
    		String ret = single_AA_transaction( auth_str, AAreq_str, Audio_filename, secretKey, TransactionID);
    		return ret;
    	}
    	String error_msg = null;
    	String validation_str = null;
    	String aarequest_st = null;
    	String aaresult = null;
    	String audio_type = null;
    	System.out.println("auth string is is:"+ auth_str);
    	validation_str = generate_valid_string(auth_str, secretKey);    	
    	aarequest_st = AAreq_str;
    	
    	int dot = f_type.lastIndexOf(".");
    	f_type = f_type.substring(dot+1);
    	if(f_type.toLowerCase().compareTo("pcm")==0){
    		error_msg += "PCM format audio must specify the sampling rate\n";
    		return error_msg;
    	}
    	
    	if(!IsFile(Audio_filename)){    		
   			error_msg += "Audio file is not existing "+Audio_filename+" \n";
    	}   	
    	if(null != error_msg){
    		return error_msg;
    	}
    	audio_type = getExtensionName(Audio_filename).toLowerCase();
    	if(f_type.toLowerCase().compareTo(audio_type)!=0){
    		error_msg += "File type"+f_type+"is mismatching with the "+Audio_filename+" \n";
    		return error_msg;
    	}
    	this.send_AAReq(TransactionID, validation_str, aarequest_st, audio_type);
    	try{
    		this.send_Audiofile(TransactionID, Audio_filename, drate);
    	}
    	catch (FileNotFoundException e){
    		error_msg += "file not found exception\n";    
    		return error_msg;
    	}
    	try{
    		aaresult = this.recv_AAresult();   
    		close();
    		return aaresult;
    	}
    	catch(IOException exc){
    		exc.printStackTrace();
    		error_msg += "There is IOException\n";
    		close();
    		return error_msg;
    	}
    }
    /**@overload
	 * Used for making a single AA transaction.
	 * 
	 * @param auth_str
	 * XML-style authorization string
	 * 	e.g.: <validate requester="CSDev" requesterID="21" requesterInfo='user1' requesterSeqNum='936864273622677291'/>
	 * @param AAreq_str
	 * XML-style AA request string
	 * 	e.g.: <AARequest AAReqID="201"><analysis analysisID="201" analysisType="ReadingFluency"  ><text>Will you help me with the VAST chip security phrases?</text></analysis></AARequest>
	 * @param Audio_filename
	 * string of filename for audio file, full path
	 * @param secretKey
	 * string of client secret Key, customer should obtained its unique key from Carnegie Speech
	 * @param TransactionID
	 * transaction ID for this single transaction
	 * @return AA result string sent from speech services server
	 */
    public String single_AA_transaction(String auth_str, String AAreq_str, String Audio_filename, String secretKey, String TransactionID){
    	String error_msg = null;
    	String validation_str = null;
    	String aarequest_st = null;
    	String aaresult = null;
    	String audio_type =  null;
    	System.out.println("auth string is is:"+ auth_str);
    	validation_str = generate_valid_string(auth_str, secretKey);
    	aarequest_st = AAreq_str;    	   	   	
    	
    	audio_type  = getExtensionName(Audio_filename).toLowerCase();
    	
    	if(audio_type.compareTo("pcm")==0){
    		error_msg += "PCM format audio must specify the sampling rate\nPlease use the method single_AA_transaction_PCM ";
    		return error_msg;
    	}
    	if(!IsFile(Audio_filename)){
    		error_msg += "Audio file is not existing "+Audio_filename+" \n";
    	}   	
    	if(null != error_msg){
    		return error_msg;
    	}
    	  	
    	this.send_AAReq(TransactionID, validation_str, aarequest_st, audio_type);
    	try{
    		this.send_Audiofile(TransactionID, Audio_filename);
    	}
    	catch (FileNotFoundException e){
    		error_msg += "file not found exception\n";    
    		return error_msg;
    	}
    	try{
    		long start_ms = System.currentTimeMillis();
    		aaresult = this.recv_AAresult();   
    		long end_ms = System.currentTimeMillis();
    		System.out.println("receiveResultTimeReport:"+(end_ms - start_ms)+ " ms");
    		close();
    		return aaresult;
    	}
    	catch(IOException exc){
    		exc.printStackTrace();
    		error_msg += "There is IOException\n";
    		close();
    		return error_msg;
    	}
    }
    
    
    
    /** @overload
     * Used for making a single AA transaction reading in audio data from an audio file with validation info as parameters.
     * 
     * @param requester
     * requester name string
     * 	e.g. "Cambridge-English"
     * @param requesterID
     * requesterID string
     * 	e.g. "001"
     * @param userID
     * userID/assessmentID string
     * 	e.g. "Assessment_1234"
     * @param requesterInfo
     * requesterInfo string
     * 	e.g. "Question_12"
     * @param requesterSeqNum
     * requesterSeqNum integer
     * 	e.g. "12467"
     * @param aareqString
     * aareqString XML-style string
     * 	e.g. "<AARequest AAReqID="201"><analysis analysisID="201" analysisType="ReadingFluency"  ><text>Will you help me with the VAST chip security phrases?</text></analysis></AARequest>"
     * @param audioFileName
     * audio file name string, full path
     * 	e.g. "audiofile.flv"
     * @param clientSecretKey
     * string of client secret Key, customer should obtained its unique key from Carnegie Speech
     * @param transactionID
     * transactionID string for this single transaction
     * 	e.g. "000012354783"
     * @return AA result string sent from speech services server
     */
    public String single_AA_transaction( String requester, String requesterID, String userID, String requesterInfo, int requesterSeqNum, String aareqString, String audioFileName, String clientSecretKey, String transactionID)
    {
    	String authString = null;
    	String errorMsg = null;
    	String validationString = null;
    	String aarequestString = null;
    	String audioType = null;
    	String aaresult = null;

        authString = "<validate requester='" + requester + "' requesterID='" + requesterID + "' userID='" + userID + "' requesterInfo='" + requesterInfo + "' requesterSeqNum='" + requesterSeqNum + "'/>";
        /*Start to Check Inputs*/
        validationString = generateValidationString(requester, requesterID, userID, requesterInfo, requesterSeqNum, clientSecretKey);
        if (null == validationString)
        {
            errorMsg += "Failed in generatin validation string, please check your auth string\n";
        }
        if (null == aareqString)
        {
            errorMsg += "aarequest string should not be empty\n";
        }
        if (null == transactionID)
        {
            errorMsg += "Transaction ID should not be null\n";
        }
        /*End of Inputs Checking*/
        aarequestString = aareqString;

        if (!IsFile(audioFileName))
        {
            errorMsg += "Audio file is not existing " + audioFileName + " \n";
        }
        if (null != errorMsg)
        {
            return errorMsg;
        }
        audioType = getExtensionName(audioFileName);
        //System.out.println("Audio type is: " + audioType);
        this.send_AAReq(transactionID, validationString, aarequestString, audioType);
        try{        	
            this.send_Audiofile(transactionID, audioFileName);
            long start_ms = System.currentTimeMillis();
            aaresult = this.recv_AAresult();
            long end_ms = System.currentTimeMillis();
            System.out.println("receiveResultTimeReport:"+(end_ms-start_ms)+" ms");
            
        }
        catch (FileNotFoundException e)
        {
            errorMsg += "file not found exception\n";
            e.printStackTrace();
            close();
            return errorMsg;
        }
        catch (Exception ee)
        {
        	errorMsg += "exception in receive aa result\n";
        	ee.printStackTrace();
        	close();
        	return errorMsg;
        }  
        close();
        return aaresult;
    }

    /**
	 * Used for making a single AA transaction for PCM audio.
	 * 
	 * @param XML-style authorization string
	 * 		Example: <validate requester="CSDev" requesterID="21" requesterInfo='user1' requesterSeqNum='936864273622677291'/>
	 * @param XML-style AA request string
	 * 		Example: <AARequest AAReqID="201"><analysis analysisID="201" analysisType="ReadingFluency"  ><text>Will you help me with the VAST chip security phrases?</text></analysis></AARequest>
	 * @param string of filename for audio file
	 * @param secrectKey for client
	 * @param transaction ID for this single transaction
	 * @param fs for specifying the sampling frequency of PCM audio
	 * @return AA result string sent from speechservices server
	 */
    public String single_AA_transaction_PCM(String auth_str, String AAreq_str, String Audio_filename, String secretKey, String TransactionID, int fs){
    	String error_msg = null;
    	String validation_str = null;
    	String aarequest_st = null;
    	String aaresult = null;
    	String audio_type =  null;
    	System.out.println("auth string is is:"+ auth_str);
    	validation_str = generate_valid_string(auth_str, secretKey);
    	if(null == validation_str){
    		error_msg += "can not generate validation node, please check the input validation information.";
    		return error_msg;
    	}
    	System.out.println("validation string is:"+ validation_str);
    	aarequest_st = AAreq_str;      	   	
    	audio_type  = getExtensionName(Audio_filename).toLowerCase();
    	
    	if(audio_type.compareTo("pcm")!=0){
    		error_msg += "This method is only used for PCM audio\nPlease use the method single_AA_transaction\n";
    		return error_msg;
    	}
    	if(!IsFile(Audio_filename)){
    		error_msg += "Audio file is not existing "+Audio_filename+" \n";
    	}   	
    	if(null != error_msg){
    		return error_msg;
    	}
    	  	
    	this.send_AAReq_PCM(TransactionID, validation_str, aarequest_st, fs);
    	try{
    		this.send_Audiofile(TransactionID, Audio_filename);
    	}
    	catch (FileNotFoundException e){
    		error_msg += "file not found exception\n";    
    		return error_msg;
    	}
    	try{
    		System.out.println("Socket connected? :"+clientSocket.isConnected());
    		aaresult = this.recv_AAresult();   
    		close();
    		return aaresult;
    	}
    	catch(IOException exc){
    		exc.printStackTrace();
    		error_msg += "There is IOException\n";
    		close();
    		return error_msg;
    	}
    }
   
    
    public static String generateValidationString(String requester, String requesterID, String userID, String requesterInfo,int requesterSeqNum, String clientSecretKey){
    	if ((null == clientSecretKey)){
        	String validationString = null;
        	return validationString;
    	}

    	try{
    		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZ");//2014-01-27T15:53:30.167+00:00
			String DateTimeStr = sdf.format(new Date());
			String dateTimeStamp=DateTimeStr.substring(0, 26)+":"+DateTimeStr.substring(26,DateTimeStr.length());
    		//SimpleDBUtils.encodeDate(new Date());
    		//System.out.println(dateTimeStamp);
    		String hashTokenInstance = requester + "-" + requesterID + "-" +requesterInfo+"-"+dateTimeStamp + "-" + requesterSeqNum+"-"+clientSecretKey;
    		System.out.println(hashTokenInstance);
    		MessageDigest md = MessageDigest.getInstance("MD5");		
    		String hashString = byteArrayToHexString(md.digest(hashTokenInstance.getBytes()));		
    		String validationString = "   <validate requester='" + requester + "'\n" +
    										"       requesterID='" + requesterID + "'\n" +
    										"       userID='"+ userID+ "'\n"+
    										"       apiVersion='"+verID+"'\n"+
    										"       requesterInfo='" + requesterInfo + "'\n" +
    										"       requesterSeqNum='" + requesterSeqNum + "'\n" +
    										"       dateTimeStamp='" + dateTimeStamp + "'\n" +
    										"       hashToken='" + hashString + "'/>\n";
    		return validationString;
    	}
    	catch (Exception e){
    		e.printStackTrace();
    		String validationString = null;
    		return validationString;
    	}
    }
    public static String byteArrayToHexString(byte[] b) { 
        StringBuffer resultSb = new StringBuffer(); 
        for (int i = 0; i < b.length; i++) { 
          resultSb.append(byteToHexString(b[i])); 
        } 
        return resultSb.toString(); 
      }

    private static String byteToHexString(byte b) { 
        int n = b; 
        if (n < 0) 
          n = 256 + n; 
        int d1 = n / 16; 
        int d2 = n % 16; 
        return hexDigits[d1] + hexDigits[d2]; 
      }
    
    private static final String[] hexDigits = {
    	"0", "1", "2", "3", "4", "5", "6", "7",
    	"8", "9", "a", "b", "c", "d", "e", "f"};

    private int findFileSize(String filename)
    {
        File file = new File(filename);
        if(!file.exists() || !file.isFile())
        {
            //this.parent.writeLog("Error: " + filename + " does not exist");
            return -1;
        } else
        {
            return (int)file.length();
        }
    }
    
    private boolean IsFile(String filename)
    {
    	File file = new File(filename);
        if(!file.exists() || !file.isFile())
        {       
            return false;
        } else
        {
            return true;
        }
    }
    /* 
     * Java get filename's extension 
     */  
    public static String getExtensionName(String fname) {   
    	String ext_str = null;
		String filename  =fname;
    	if ((filename != null) && (filename.length() > 0)) {   
        	int dot = filename.lastIndexOf('.');   
        	if ((dot >-1) && (dot < (filename.length() - 1))) {   
            	ext_str = filename.substring(dot + 1);
            	return ext_str;   
        	}   
    	}   
    	return ext_str;   
    }   
    /* 
     * Java get filename without extension 
     */  
    public static String getFileNameNoEx(String fname) {   
    	String filename  =fname;
        if ((filename != null) && (filename.length() > 0)) {   
        	int dot = filename.lastIndexOf('.');   
            if ((dot >-1) && (dot < (filename.length()))) {   
            	return filename.substring(0, dot);   
            }   
        }   
        return filename;   
	}
    
    
}
