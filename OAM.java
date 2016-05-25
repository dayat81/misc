

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.ProxySOCKS5;
import com.jcraft.jsch.Session;

public class OAM {
	private String expp;
	private String prompt;
	private String type;
	private String host;
	public String getType() {
		return type;
	}
	private String username;
	private String password;
	private int proxy;
	private int limiter;
	private Session session;
	private Channel channel;
	private OutputStream shellStream;
	private InputStream inputStream;
	public OAM(String type, String host, String username, String password,String prompt,
			int proxy) throws JSchException, IOException {
		super();
		this.prompt=prompt;
		this.type = type;
		this.host = host;
		this.username = username;
		this.password = password;
		this.proxy = proxy;
		 JSch jsch = new JSch();
 		session = jsch.getSession(username,host,22);
 		session.setPassword(password);
 		java.util.Properties config = new java.util.Properties();
 		config.put("StrictHostKeyChecking", "no");
 		session.setConfig(config);
 		if(proxy!=-1){
 		session.setProxy(new ProxySOCKS5("127.0.0.1",proxy));
 		}

	}
	public void Connect() throws JSchException, IOException{
 		session.connect();
 		if(type.equals("SSR")){
 			limiter = prompt.length()+username.length()+1;
	        channel=session.openChannel("shell");//only shell  
		      
		      shellStream=channel.getOutputStream();

	     inputStream=channel.getInputStream();
	        channel.connect(); 	 			
 		}	
 		System.out.println("connect done");
	}
	public void Quit() throws IOException{
		if(type.equals("SSR")){
        shellStream.close();	
inputStream.close();
channel.disconnect();
		}
    session.disconnect();		
	}
	public String LeaveConf() throws Exception {
		String output = "";
		if(type.equals("SSR")){
		    String pr="";
		    int num;
		    byte array[];
		    
	        shellStream.write("abort\n".getBytes());
	        shellStream.flush();
	       
	        int loop=0;
	        do {
	        	if(loop>5){
	        		//System.out.println("sleep "+loop);
	        	 Thread.sleep(100);
	        	}
	            if ((num = inputStream.available()) > 0) {
	                array = new byte[num];
	                num = inputStream.read(array);
	                String ret = new String(array, 0, num);
	                output += ret;
	           pr+=ret;
            
	            }else{
	            	
	            	if(pr.length()>limiter+1){
	            		String prompt1=pr.substring(pr.length()-(limiter+1), pr.length());
		            	//System.out.println("abort prompt -"+prompt1+"-");
	            		loop+=1;
	                if(prompt1.equals(expp)){
	                	break;
	                } 
	                if(loop>50){
	                	throw new Exception();	                	
	                }	                
	            	}
	            }
	        } while (true);

	        shellStream.write("end\n".getBytes());
	        shellStream.flush();
	       pr="";
	       //System.out.println("command end ..");
	       loop=0;
	        do {
	        	if(loop>5){
	        		//System.out.println("sleep "+loop);
	        	 Thread.sleep(100);
	        	}
	            if ((num = inputStream.available()) > 0) {
	                array = new byte[num];
	                num = inputStream.read(array);
	                String ret = new String(array, 0, num);
	                output += ret;
	             pr+=ret;

	                                    
	            }else{
	            	//System.out.println(pr);
	            	if(pr.length()>limiter+1){
	            		String prompt1=pr.substring(pr.length()-(limiter+1), pr.length());
	            		loop+=1;
		            	//System.out.println("end prompt -"+prompt1+"-");
	                if(prompt1.equals(expp)){
	                	break;
	                }  	
	                if(loop>50){
	                	throw new Exception();	                	
	                }	                
	            	}
	            }
	        } while (true);
	       
	        shellStream.write("exit\n".getBytes());
	        shellStream.flush();
	  pr="";
	  loop=0;
	  expp="[local]"+prompt+"#";
	        do {
	        	if(loop>5){
	        		//System.out.println("sleep "+loop);
	        	 Thread.sleep(100);
	        	}
	            if ((num = inputStream.available()) > 0) {
	                array = new byte[num];
	                num = inputStream.read(array);
	                String ret = new String(array, 0, num);
	                output += ret;
	              pr+=ret;
	                                   
	            }else{
	            	
	            	if(pr.length()>limiter-username.length()+7){
	            		String prompt1=pr.substring(pr.length()-(limiter-username.length()+7), pr.length());
		            	//System.out.println("exit prompt -"+prompt1+"-");
	            		
	            		loop+=1;
	                if(prompt1.equals(expp)){
	                	break;
	                }		
	                if(loop>50){
	                	throw new Exception();	                	
	                }	                
	            	}
	            }
	        } while (true);
			
		}
		return output;
	}
	public String exe(String cmd) throws Exception{
		String output="";
		if(type.equals("SSR")){
		    String pr="";
		    int num;
		    byte array[];		
		String s = cmd+"\n";
        shellStream.write(s.getBytes());
        shellStream.flush();
        int loop=0;
        do {
        	if(loop>5){        		
        	 Thread.sleep(100);
        	}
        	// Thread.sleep(500);
            if ((num = inputStream.available()) > 0) {
                array = new byte[num];
                num = inputStream.read(array);
                String ret = new String(array, 0, num);
                output += ret;
              pr+=ret;
                
                                    
            }else{
            	loop+=1;
            	if(pr.length()>limiter){
            		String prompt=pr.substring(pr.length()-(limiter+1), pr.length());
//	            	System.out.println("TRACE sess prompt -"+prompt+"-");
//	            	System.out.println("TRACE expected prompt -"+expp+"-");
                if(prompt.equals(expp)){
                	break;
                }
                if(loop>50){
                	throw new Exception();	                	
                }                
            	}
            }
        } while (true);
		}
		
		return output;
	}
	public String GetAllApn() throws Exception{
		String output="";
		if(type.equals("SSR")){
		    String pr="";
		    int num;
		    byte array[];		
		String s = "ManagedElement=1,Epg=1,Pgw=1\n";
        shellStream.write(s.getBytes());
        shellStream.flush();
        int loop=0;
        do {
        	if(loop>5){        		
        	 Thread.sleep(100);
        	}
        	// Thread.sleep(500);
            if ((num = inputStream.available()) > 0) {
                array = new byte[num];
                num = inputStream.read(array);
                String ret = new String(array, 0, num);
                output += ret;
              pr+=ret;
                
                                    
            }else{
            	loop+=1;
            	if(pr.length()>limiter){
            		String prompt=pr.substring(pr.length()-(limiter+1), pr.length());
//	            	System.out.println("TRACE sess prompt -"+prompt+"-");
//	            	System.out.println("TRACE expected prompt -"+expp+"-");
                if(prompt.equals(expp)){
                	break;
                }
                if(loop>50){
                	throw new Exception();	                	
                }                
            	}
            }
        } while (true);
        s = "show\n";
        shellStream.write(s.getBytes());
        shellStream.flush();
        loop=0;
        do {
        	if(loop>5){
        	 Thread.sleep(100);
        	}
        	// Thread.sleep(500);
            if ((num = inputStream.available()) > 0) {
                array = new byte[num];
                num = inputStream.read(array);
                String ret = new String(array, 0, num);
                output += ret;
              pr+=ret;
                
                                    
            }else{
            	loop+=1;
            	if(pr.length()>limiter){
            		String prompt=pr.substring(pr.length()-(limiter+1), pr.length());
//	            	System.out.println("TRACE sess prompt -"+prompt+"-");
//	            	System.out.println("TRACE expected prompt -"+expp+"-");	            	
                if(prompt.equals(expp)){
                	break;
                }
                if(loop>50){
                	throw new Exception();	                	
                }                
            	}
            }
        } while (true);        
		}
		return output;		
	}
	public String GetSub(String msid) throws Exception{
		String output="";
		if(type.equals("SSR")){
		    String pr="";
		    int num;
		    byte array[];		
		String s = "ManagedElement=1,Epg=1,Pgw=1,userInfo msisdn "+msid+"\n";
        shellStream.write(s.getBytes());
        shellStream.flush();

        do {
        	// Thread.sleep(500);
            if ((num = inputStream.available()) > 0) {
                array = new byte[num];
                num = inputStream.read(array);
                String ret = new String(array, 0, num);
                output += ret;
              pr+=ret;
                
                                    
            }else{
            	
            	if(pr.length()>limiter){
            		String prompt=pr.substring(pr.length()-(limiter+1), pr.length());
	            	//System.out.println("sess prompt -"+prompt+"-");
                if(prompt.equals(expp)){
                	break;
                }
            	}
            }
        } while (true);
		
		String imsi=output.substring(output.indexOf("imsi:")+5,output.indexOf("msisdn:")).trim();
		//System.out.println("==========imsi -"+imsi+"-");
		s = "ManagedElement=1,Epg=1,Pgw=1,userInfo imsi "+imsi+"\n";
        shellStream.write(s.getBytes());
        shellStream.flush();
       
       pr="";
        do {
        	// Thread.sleep(500);
            if ((num = inputStream.available()) > 0) {
                array = new byte[num];
                num = inputStream.read(array);
                String ret = new String(array, 0, num);
                output += ret;
              pr+=ret;
                
                                    
            }else{
            	
            	if(pr.length()>limiter){
            		String prompt=pr.substring(pr.length()-(limiter+1), pr.length());
	            	//System.out.println("sess prompt -"+prompt+"-");
                if(prompt.equals(expp)){
                	break;
                }
            	}
            }
        } while (true);
        
		s = "ManagedElement=1,Epg=1,Pgw=1,userInfoSacc msisdn "+msid+"\n";
        shellStream.write(s.getBytes());
        shellStream.flush();
       
       pr="";
        do {
        	// Thread.sleep(500);
            if ((num = inputStream.available()) > 0) {
                array = new byte[num];
                num = inputStream.read(array);
                String ret = new String(array, 0, num);
                output += ret;
              pr+=ret;
                
                                    
            }else{
            	
            	if(pr.length()>limiter){
            		String prompt=pr.substring(pr.length()-(limiter+1), pr.length());
	            	//System.out.println("sess prompt -"+prompt+"-");
                if(prompt.equals(expp)){
                	break;
                }
            	}
            }
        } while (true);
        
		}else{
    		Channel channel1=session.openChannel("exec");
    		
    		((ChannelExec)channel1).setCommand("show services epg pgw statistics msisdn "+msid);
    		channel1.setInputStream(null);
    		//
    		((ChannelExec)channel1).setErrStream(System.err);

    		InputStream in=channel1.getInputStream();

    		channel1.connect();

    		//System.out.println("fetching filename ...");
    		String resp = "";
    		String strtmp="";
    		byte[] tmp=new byte[512];
    		
    		while(true){
    			  //System.out.println("in "+in.available());
    		  while(in.available()>0){
    		    int k=in.read(tmp, 0, 512);
    		    if(k<0)break;
    		    //System.out.print(new String(tmp, 0, i));
    		    strtmp=new String(tmp, 0, k);
    		    resp+=strtmp;
    		  }
    		  if(channel1.isClosed()){
    		    System.out.println("exit-status: "+channel1.getExitStatus());
    		    break;
    		  }
    		  
    		}		   
    		channel1.disconnect();
    		output=resp;
    		
    		String imsi=output.substring(output.indexOf("International mobile subscriber identity:")+41,output.indexOf("Mobile station ISDN number:")).trim();
    		Channel channel2=session.openChannel("exec");
    		
    		((ChannelExec)channel2).setCommand("show services epg pgw statistics imsi "+imsi);
    		channel2.setInputStream(null);
    		//
    		((ChannelExec)channel2).setErrStream(System.err);

    		InputStream in1=channel2.getInputStream();

    		channel2.connect();

    		//System.out.println("fetching filename ...");
    		String resp1 = "";
    		String strtmp1="";
    		byte[] tmp1=new byte[512];
    		
    		while(true){
    			  //System.out.println("in "+in.available());
    		  while(in1.available()>0){
    		    int k=in1.read(tmp1, 0, 512);
    		    if(k<0)break;
    		    //System.out.print(new String(tmp, 0, i));
    		    strtmp1=new String(tmp1, 0, k);
    		    resp1+=strtmp1;
    		  }
    		  if(channel2.isClosed()){
    		    System.out.println("exit-status: "+channel2.getExitStatus());
    		    break;
    		  }
    		  
    		}		   
    		channel2.disconnect();
    		output+=resp1;
    		
    		Channel channel3=session.openChannel("exec");
    		
    		((ChannelExec)channel3).setCommand("show services epg pgw statistics msisdn "+msid+" sacc");
    		channel3.setInputStream(null);
    		//
    		((ChannelExec)channel3).setErrStream(System.err);

    		InputStream in2=channel3.getInputStream();

    		channel3.connect();

    		//System.out.println("fetching filename ...");
    		String resp2 = "";
    		String strtmp2="";
    		byte[] tmp2=new byte[512];
    		
    		while(true){
    			  //System.out.println("in "+in.available());
    		  while(in2.available()>0){
    		    int k=in2.read(tmp2, 0, 512);
    		    if(k<0)break;
    		    //System.out.print(new String(tmp, 0, i));
    		    strtmp2=new String(tmp2, 0, k);
    		    resp2+=strtmp2;
    		  }
    		  if(channel3.isClosed()){
    		    System.out.println("exit-status: "+channel3.getExitStatus());
    		    break;
    		  }
    		  
    		}		   
    		channel3.disconnect();
    		
    		output+=resp2;
		}
		return output;
	}
	public String EnterConf() throws Exception{
		String output = "";
		expp="[local]"+prompt+"#";
		if(type.equals("SSR")){

	        byte array[];
	        int num;
	       String pr="";
	       int loop=0;
	        do {
	        	if(loop>5){
	        		//System.out.println("TRACE sleep "+loop);
	        	 Thread.sleep(100);
	        	}
	            if ((num = inputStream.available()) > 0) {
	                array = new byte[num];
	                num = inputStream.read(array);
	                String ret = new String(array, 0, num);
	                output += ret; 
	                pr+=ret;

	            }else{
	            	//System.out.println("pr "+pr);
	            	if(pr.length()>limiter-username.length()+7){
	            	String prompt1=pr.substring(pr.length()-(limiter-username.length()+7), pr.length());
	            	loop+=1;
//	            	System.out.println("TRACE first prompt -"+prompt1+"-");	            		            	
//	            	System.out.println("TRACE expected prompt -"+expp+"-");
	                if(prompt1.equals(expp)){
	                	break;
	                }
	                if(loop>50){
	                	throw new Exception();	                	
	                }
	            	}
	            }
	        } while (true);
	        shellStream.write("start oam-cli\n".getBytes());
	        shellStream.flush();
pr="";
 loop=0;
	        do {
	        	if(loop>5){
	        		//System.out.println("sleep "+loop);
	        	 Thread.sleep(100);
	        	}
	            if ((num = inputStream.available()) > 0) {
	                array = new byte[num];
	                num = inputStream.read(array);
	                String ret = new String(array, 0, num);
	                output += ret;
	              pr+=ret;

	                                     
	            }else{
	            	//System.out.println("pr "+pr);
	            	if(pr.length()>limiter){
	            		String prompt1=pr.substring(pr.length()-(limiter+1), pr.length());
		            	//System.out.println("TRACE start prompt -"+prompt1+"-");
		            	expp=username+"@"+prompt+">";
		            	//System.out.println("TRACE expected prompt -"+expp+"-");
	            		loop+=1;
	                if(prompt1.equals(expp)){
	                	break;
	                }
	                if(loop>50){
	                	throw new Exception();	                	
	                }	                
	            	}
	            }
	        } while (true);
	   
	        shellStream.write("configure\n".getBytes());
	        shellStream.flush();
pr="";
loop=0;
	        do {
	        	if(loop>5){
	        		//System.out.println("sleep "+loop);
	        	 Thread.sleep(100);
	        	}
	            if ((num = inputStream.available()) > 0) {
	                array = new byte[num];
	                num = inputStream.read(array);
	                String ret = new String(array, 0, num);
	                output += ret;
	               pr+=ret;
	              
	            }else{
	            	
	            	if(pr.length()>limiter){
	            		String prompt1=pr.substring(pr.length()-(limiter+1), pr.length());
		            	//System.out.println("TRACE conf prompt -"+prompt1+"-");
		            	//System.out.println("TRACE expected prompt -"+expp+"-");
	            		loop+=1;
	                if(prompt1.equals(expp)){
	                	break;
	                } 
	                if(loop>50){
	                	throw new Exception();	                	
	                }	                
	            	}
	            }
	        } while (true);	        
	}
		
		return output;
	}
	public String GetSubHtml(String msid) throws Exception{
		String output="";
		if(type.equals("SSR")){
		    String pr="";
		    int num;
		    byte array[];		
		String s = "ManagedElement=1,Epg=1,Pgw=1,userInfo msisdn "+msid+"\n";
	    shellStream.write(s.getBytes());
	    shellStream.flush();
	    int loop=0;
	    do {
        	if(loop>5){
        		//System.out.println("sleep "+loop);
        	 Thread.sleep(100);
        	}
	        if ((num = inputStream.available()) > 0) {
	            array = new byte[num];
	            num = inputStream.read(array);
	            String ret = new String(array, 0, num);
	            output += ret;
	          pr+=ret;
	            
	                                
	        }else{
	        	
	        	if(pr.length()>limiter){
	        		String prompt=pr.substring(pr.length()-(limiter+1), pr.length());
	            	//System.out.println("sess prompt -"+prompt+"-");
	        		loop+=1;
	            if(prompt.equals(expp)){
	            	break;
	            }
                if(loop>50){
                	throw new Exception();	                	
                }	            
	        	}
	        }
	    } while (true);
		String res = output.substring(output.indexOf("mobile-user:"), output.indexOf("sn-information:"));
		String imsi=output.substring(output.indexOf("imsi:")+5,output.indexOf("msisdn:")).trim();
		output="";
		//System.out.println("==========imsi -"+imsi+"-");
		s = "ManagedElement=1,Epg=1,Pgw=1,userInfo imsi "+imsi+"\n";
	    shellStream.write(s.getBytes());
	    shellStream.flush();
	
	   pr="";
	   loop=0;
	    do {
        	if(loop>5){
        		//System.out.println("sleep "+loop);
        	 Thread.sleep(100);
        	}
	        if ((num = inputStream.available()) > 0) {
	            array = new byte[num];
	            num = inputStream.read(array);
	            String ret = new String(array, 0, num);
	            output += ret;
	          pr+=ret;
	            
	                                
	        }else{
	        	
	        	if(pr.length()>limiter){
	        		String prompt=pr.substring(pr.length()-(limiter+1), pr.length());
	            	//System.out.println("sess1 prompt -"+prompt+"-");
	        		loop+=1;
	            if(prompt.equals(expp)){
	            	break;
	            }
                if(loop>50){
                	throw new Exception();	                	
                }	            
	        	}
	        }
	    } while (true);
//	    String param = imsi +"x"+ this.prompt;
	    //System.out.println("imsiii ---"+imsi+"---");	 
	    imsi+=this.prompt.substring(this.prompt.length()-1, this.prompt.length());
	    String res1 = output.substring(output.indexOf("session-activation-timestamp:"), output.indexOf("sn-information:"));
	    output="";
		s = "ManagedElement=1,Epg=1,Pgw=1,userInfoSacc msisdn "+msid+"\n";
	    shellStream.write(s.getBytes());
	    shellStream.flush();
	
	   pr="";
	   loop=0;
	    do {
        	if(loop>5){
        		//System.out.println("sleep "+loop);
        	 Thread.sleep(100);
        	}
	        if ((num = inputStream.available()) > 0) {
	            array = new byte[num];
	            num = inputStream.read(array);
	            String ret = new String(array, 0, num);
	            output += ret;
	          pr+=ret;
	            
	                                
	        }else{
	        	
	        	if(pr.length()>limiter){
	        		String prompt=pr.substring(pr.length()-(limiter+1), pr.length());
	            	//System.out.println("sess2 prompt -"+prompt+"-");
	        		loop+=1;
	            if(prompt.equals(expp)){
	            	break;
	            }
                if(loop>50){
                	throw new Exception();	                	
                }	            
	        	}
	        }
	    } while (true);
	    String res2 = output.substring(output.indexOf("access-control-"), output.indexOf("(config)"));
	    output=res+res1+res2;
		}else{
			Channel channel1=session.openChannel("exec");
			
			((ChannelExec)channel1).setCommand("show services epg pgw statistics msisdn "+msid);
			channel1.setInputStream(null);
			//
			((ChannelExec)channel1).setErrStream(System.err);
	
			InputStream in=channel1.getInputStream();
	
			channel1.connect();
	
			//System.out.println("fetching filename ...");
			String resp = "";
			String strtmp="";
			byte[] tmp=new byte[512];
			
			while(true){
				  //System.out.println("in "+in.available());
			  while(in.available()>0){
			    int k=in.read(tmp, 0, 512);
			    if(k<0)break;
			    //System.out.print(new String(tmp, 0, i));
			    strtmp=new String(tmp, 0, k);
			    resp+=strtmp;
			  }
			  if(channel1.isClosed()){
			    System.out.println("exit-status: "+channel1.getExitStatus());
			    break;
			  }
			  
			}		   
			channel1.disconnect();
			output=resp;
			
			String imsi=output.substring(output.indexOf("International mobile subscriber identity:")+41,output.indexOf("Mobile station ISDN number:")).trim();
			Channel channel2=session.openChannel("exec");
			
			((ChannelExec)channel2).setCommand("show services epg pgw statistics imsi "+imsi);
			channel2.setInputStream(null);
			//
			((ChannelExec)channel2).setErrStream(System.err);
	
			InputStream in1=channel2.getInputStream();
	
			channel2.connect();
	
			//System.out.println("fetching filename ...");
			String resp1 = "";
			String strtmp1="";
			byte[] tmp1=new byte[512];
			
			while(true){
				  //System.out.println("in "+in.available());
			  while(in1.available()>0){
			    int k=in1.read(tmp1, 0, 512);
			    if(k<0)break;
			    //System.out.print(new String(tmp, 0, i));
			    strtmp1=new String(tmp1, 0, k);
			    resp1+=strtmp1;
			  }
			  if(channel2.isClosed()){
			    System.out.println("exit-status: "+channel2.getExitStatus());
			    break;
			  }
			  
			}		   
			channel2.disconnect();
			output+=resp1;
			
			Channel channel3=session.openChannel("exec");
			
			((ChannelExec)channel3).setCommand("show services epg pgw statistics msisdn "+msid+" sacc");
			channel3.setInputStream(null);
			//
			((ChannelExec)channel3).setErrStream(System.err);
	
			InputStream in2=channel3.getInputStream();
	
			channel3.connect();
	
			//System.out.println("fetching filename ...");
			String resp2 = "";
			String strtmp2="";
			byte[] tmp2=new byte[512];
			
			while(true){
				  //System.out.println("in "+in.available());
			  while(in2.available()>0){
			    int k=in2.read(tmp2, 0, 512);
			    if(k<0)break;
			    //System.out.print(new String(tmp, 0, i));
			    strtmp2=new String(tmp2, 0, k);
			    resp2+=strtmp2;
			  }
			  if(channel3.isClosed()){
			    System.out.println("exit-status: "+channel3.getExitStatus());
			    break;
			  }
			  
			}		   
			channel3.disconnect();
			
			output+=resp2;			
		}
		output=output.replaceAll("\n", "<br>");		
		return output;
	}
	public String GetPayload(String imsi) throws IOException, JSchException{
			String output="";
			if(type.equals("SSR")){
			    String pr="";
			    int num;
			    byte array[];		

			//output="";
			//System.out.println("==========imsi -"+imsi+"-");
			String s = "ManagedElement=1,Epg=1,Pgw=1,userInfo imsi "+imsi+"\n";
		    shellStream.write(s.getBytes());
		    shellStream.flush();
		
		   pr="";
		    do {
		    	// Thread.sleep(500);
		        if ((num = inputStream.available()) > 0) {
		            array = new byte[num];
		            num = inputStream.read(array);
		            String ret = new String(array, 0, num);
		            output += ret;
		          pr+=ret;
		            
		                                
		        }else{
		        	
		        	if(pr.length()>limiter){
		        		String prompt=pr.substring(pr.length()-(limiter+1), pr.length());
		            	//System.out.println("sess prompt -"+prompt+"-");
		            if(prompt.equals(expp)){
		            	break;
		            }
		        	}
		        }
		    } while (true);
	//	    String param = imsi +"x"+ this.prompt;
		    System.out.println("imsiii ---"+imsi+"---");
		    System.out.println(output);
		    output=output.substring(output.indexOf("session-activation-timestamp:"), output.indexOf("sn-information:"));
		    output=output.replaceAll("\n", "<br>");
			}else{
				Channel channel2=session.openChannel("exec");
				
				((ChannelExec)channel2).setCommand("show services epg pgw statistics imsi "+imsi);
				channel2.setInputStream(null);
				//
				((ChannelExec)channel2).setErrStream(System.err);
		
				InputStream in1=channel2.getInputStream();
		
				channel2.connect();
		
				//System.out.println("fetching filename ...");
				String resp1 = "";
				String strtmp1="";
				byte[] tmp1=new byte[512];
				
				while(true){
					  //System.out.println("in "+in.available());
				  while(in1.available()>0){
				    int k=in1.read(tmp1, 0, 512);
				    if(k<0)break;
				    //System.out.print(new String(tmp, 0, i));
				    strtmp1=new String(tmp1, 0, k);
				    resp1+=strtmp1;
				  }
				  if(channel2.isClosed()){
				    System.out.println("exit-status: "+channel2.getExitStatus());
				    break;
				  }
				  
				}		   
				channel2.disconnect();
				output+=resp1;
				
			}
			return output+new Date().toString();
		}
}
