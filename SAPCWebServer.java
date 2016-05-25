import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
public class SAPCWebServer {   
	private static final int fNumberOfThreads = 100; 
	private static final Executor fThreadPool = Executors.newFixedThreadPool(fNumberOfThreads);   
	public static void main(String[] args) throws IOException { 
		ServerSocket socket = new ServerSocket(Integer.parseInt(args[0])); 
		while (true) { 
			final Socket connection = socket.accept(); 
			Runnable task = new Runnable() { 
				public void run() { 
					HandleRequest(connection); 
				} 
			}; 
			fThreadPool.execute(task); 
		} 
	}   
	private static void HandleRequest(Socket s) { 
		BufferedReader in; 
		PrintWriter out; 
		String request;
		try { 
			String webServerAddress = s.getInetAddress().toString(); 
			System.out.println("New Connection:" + webServerAddress); 
			in = new BufferedReader(new InputStreamReader(s.getInputStream()));   
			request = in.readLine(); 
			System.out.println("--- Client request: " + request);
			String[] reqs=request.split(" ");
			String id=reqs[1].substring(1);
			System.out.println(id);  

	        String response="";
	        String temp="";
	        if(id.matches("\\d+")){
//	    		String[] ip={"172.29.26.61","172.29.26.189","172.29.27.61","172.29.27.189","172.29.1.61","172.29.31.61","172.29.31.141","172.29.31.221"};
//	    		String[] hosts={"cbt1","cbt2","cbt3","cbt4","btr1","cbt5","cbt6","cbt7"};
//	    		String[] suff={"3","5","8","1,[0-4]2","0","4,[5-9]2","6,[0-4]7","9,[5-9]7"};
	        	
//	    		String[] ip={"172.29.33.125","172.29.32.125","172.29.32.189","172.29.27.189","172.29.32.61","172.29.31.61","172.29.31.141","172.29.31.221"};
//	    		String[] hosts={"snb6","snb2","snb3","cbt4","snb1","cbt5","cbt6","cbt7"};
//	    		String[] suff={"3","5","8","1,[0-4]2","0","4,[5-9]2","6,[0-4]7","9,[5-9]7"};
	    		
//	    		String[] ip={"172.29.32.125","172.29.32.189","172.29.27.189","172.29.32.61","172.29.31.61","172.29.31.141","172.29.31.221","172.29.1.61","172.29.26.61","172.29.32.253","172.29.33.61"};
//	    		String[] hosts={"snb2","snb3","cbt4","snb1","cbt5","cbt6","cbt7","btr1","cbt1","snb4","snb5"};
//	    		String[] suff={"5","8","1,[0-4]2","0","4,[5-9]2","6,[0-4]7","[4-5]9,[5-9]7","[0-1]9","[2-3]9","[5-9]3,[8-9]9","[0-4]3,[6-7]9"};	   
	    		
//	    		String[] ip={"172.29.32.189","172.29.27.189","172.29.31.61","172.29.31.141","172.29.31.221","172.29.1.61","172.29.26.61","172.29.32.253","172.29.33.61","172.29.33.125","172.29.33.189","172.29.33.253","172.29.34.61"};
//	    		String[] hosts={"snb3","cbt4","cbt5","cbt6","cbt7","btr1","cbt1","snb4","snb5","snb6","snb7","snb8","snb9"};
//	    		String[] suff={"8","1,[0-4]2","[4-5]0,[5-9]2","6,[0-4]7","[4-5]9,[5-9]7","[0-1]9","[2-3]9","[5-9]3,[8-9]9","[0-4]3,[6-7]9","[5-9]4,[0-1]0","[0-4]4,[6-7]0","[5-9]5,[2-3]0","[0-4]5,[8-9]0"};	  

//	    		String[] ip={"172.29.26.189","172.29.27.61","172.29.32.61","172.29.32.125","172.29.27.189","172.29.31.61","172.29.31.141","172.29.31.221","172.29.1.61","172.29.26.61","172.29.32.253","172.29.33.61","172.29.33.125","172.29.33.189","172.29.33.253","172.29.34.61"};
//	    		String[] hosts={"cbt2","cbt3","snb1","snb2","cbt4","cbt5","cbt6","cbt7","btr1","cbt1","snb4","snb5","snb6","snb7","snb8","snb9"};
//	    		String[] suff={"[0-1]8","[2-3]8","[0-4]7,[4-5]8","[7-9]6,[6-9]8","1,[0-4]2","[4-5]0,[5-9]2","[0-6]6","[4-5]9,[5-9]7","[0-1]9","[2-3]9","[5-9]3,[8-9]9","[0-4]3,[6-7]9","[5-9]4,[0-1]0","[0-4]4,[6-7]0","[5-9]5,[2-3]0","[0-4]5,[8-9]0"};

	    		String[] ip={"172.29.32.189","172.29.26.189","172.29.27.61","172.29.32.61","172.29.32.125","172.29.27.189","172.29.31.61","172.29.31.141","172.29.31.221","172.29.26.61","172.29.32.253","172.29.33.61","172.29.33.125","172.29.33.189","172.29.33.253","172.29.34.61"};
	    		String[] hosts={"snb3","cbt2","cbt3","snb1","snb2","cbt4","cbt5","cbt6","cbt7","cbt1","snb4","snb5","snb6","snb7","snb8","snb9"};
	    		String[] suff={"[8-9]1,[0-4]2","[0-1]8","[2-3]8","[0-4]7,[4-5]8","[7-9]6,[6-9]8","[0-7]1","[4-5]0,[5-9]2,09","19,[0-6]6","[4-5]9,[5-9]7","[2-3]9","[5-9]3,[8-9]9","[0-4]3,[6-7]9","[5-9]4,[0-1]0","[0-4]4,[6-7]0","[5-9]5,[2-3]0","[0-4]5,[8-9]0"};
	    		
	    		
	    		
	    		outerloop:
	    			for (int i = 0; i < hosts.length; i++) {
	    				//split
	    				String[] suf = suff[i].split(",");
	    				for (int j = 0; j < suf.length; j++) {
	    					if(id.matches(".*"+suf[j])){	        	
//here	        		        	
	        	
				FileInputStream input = new FileInputStream("up.html");
				temp=hosts[i]+"<br>"+id+"<br>";
				byte[] fileData = new byte[input.available()];

				input.read(fileData);
				input.close();

				response= new String(fileData, "UTF-8");
				
				Properties properties = new Properties();
				properties.put( Context.INITIAL_CONTEXT_FACTORY, 
				  "com.sun.jndi.ldap.LdapCtxFactory" );
				properties.put( Context.PROVIDER_URL, "ldap://"+ip[i]+":7323");
				properties.put( Context.REFERRAL, "ignore" );
	
				// set properties for authentication
				properties.put( Context.SECURITY_PRINCIPAL, "administratorName=jambala,nodeName=jambala" );
				properties.put( Context.SECURITY_CREDENTIALS, "Pokemon1" );
				InitialDirContext context = new InitialDirContext( properties );	
				
				//String line="6287705897797";
				// specify the LDAP search filter, just users
				String searchFilter = "(&(objectClass=*))";
		        SearchControls searchControls = new SearchControls();
		        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);	
		        
					        
		        //response+=id+"<br>";
					String dn="EPC-SubscriberId="+id+",EPC-SubscribersName=EPC-Subscribers,applicationName=EPC-EpcNode,nodeName=jambala";
					try {					
				        NamingEnumeration<SearchResult> results = context.search(dn, searchFilter, searchControls);
				        while(results.hasMore()) {
				        	 SearchResult searchResult = (SearchResult) results.nextElement();
				        	 Attributes res = searchResult.getAttributes();	
				        	 //System.out.println(res);
				        	 Attribute sub=res.get("epc-groupids");
				        	 if(sub!=null){
				        		 System.out.println(sub);
				        		 //temp+=sub+"<br>";
				        		 String[] grps = sub.toString().substring(13).split(", ");
				        		 temp+="<table style='width:100%' border='1'><tr bgcolor='#cccccc'><td>groupid</td><td>priority</td><td>startdate</td><td>enddate</td></tr>";
				        		 for (int k = 0; k < grps.length; k++) {
									//System.out.println(grps[k]);
				        			 temp+="<tr>";
				        			 String[] detail = grps[k].replaceAll(",", ":").split(":");
				        			 for (int l = 0; l < detail.length; l++) {
										//System.out.print(detail[l]+" ");
										if(l==2||l==5){
											temp+="<td>"+detail[l]+":";
										}else if(l==3||l==6){
											temp+=detail[l]+":";
										}else if(l==4||l==7){
											temp+=detail[l]+"</td>";
										}else{										
											temp+="<td>"+detail[l]+"</td>";
										}
									}
				        			 temp+="</tr>";
				        			 //System.out.println();
								}
				        		 temp+="</table><br>";
				        	 }
				        	 Attribute opinfo=res.get("epc-operatorspecificinfo");
				        	 if(opinfo!=null){
				        		 temp+=opinfo+"<br>";
				        	 }				        	 
				        	 Attribute lim=res.get("epc-data");
				        	 if(lim!=null&&!lim.toString().contains("EPC-Data: {}")){
				        		// response+=lim+"<br>";
				 				FileInputStream input1 = new FileInputStream("middle.html");

								byte[] fileData1 = new byte[input1.available()];

								input1.read(fileData1);
								input1.close();

								response= response +"var json = '"+lim.toString().substring(10)+"';"+ new String(fileData1, "UTF-8");			
								//System.out.println("limitt "+lim);
				        	 }
				        	  //response+=sub;
				        	  //response=res.toString();
				        	 //System.out.println(res);
				        	 
				        }
					}catch(NamingException e){
						e.printStackTrace();
						response="subscriber not found.";
					}
					
					String dn1="EPC-AccumulatedName="+id+",EPC-SubscribersAccumulatedName=EPC-SubscribersAccumulated,applicationName=EPC-EpcNode,nodeName=jambala";
					try {					
				        NamingEnumeration<SearchResult> results = context.search(dn1, searchFilter, searchControls);
				        while(results.hasMore()) {
				        	 SearchResult searchResult = (SearchResult) results.nextElement();
				        	 Attributes res = searchResult.getAttributes();	
				        	 Attribute acc = res.get("epc-accumulateddata");
				        	 //response+=acc+"<br>";
				 				FileInputStream input2 = new FileInputStream("middle1.html");

								byte[] fileData2 = new byte[input2.available()];

								input2.read(fileData2);
								input2.close();				        	 
				        	 response= response +"var json1 = '"+acc.toString().substring(20)+"';"+ new String(fileData2, "UTF-8");	
				        	  //response+=sub;
				        	  //response=res.toString();
				        	 //System.out.println(res);
				        	 
				        }
					}catch(NamingException e){
		 				FileInputStream input2 = new FileInputStream("middle1.html");

						byte[] fileData2 = new byte[input2.available()];

						input2.read(fileData2);
						input2.close();				        	 
		        	 response= response +"var json1 = '{}';"+ new String(fileData2, "UTF-8");							
						e.printStackTrace();
						//response+="accumulator not found.";
					}
					
					String dn2="EPC-PerSubsId="+id+",EPC-SessionsName=EPC-Sessions,applicationName=EPC-EpcNode,nodeName=jambala";
					try {					
				        NamingEnumeration<SearchResult> results = context.search(dn2, searchFilter, searchControls);
				        while(results.hasMore()) {
				        	 SearchResult searchResult = (SearchResult) results.nextElement();
				        	 Attributes res = searchResult.getAttributes();
				        	 Attribute traf = res.get("epc-trafficsessionid");
				        	 Attribute ipaddr = res.get("epc-ipaddress");
				        	 Attribute lastactiv = res.get("epc-lastactivitytimestamp");
				        	 Attribute peerid = res.get("epc-peerid");
				        	 Attribute apn = res.get("epc-calledstationid");
				        	 Attribute sessid = res.get("epc-sessionid");
				        	 if(sessid!=null){
				          		 String ggsn = peerid.get(0).toString();
				          		 //System.out.println("ggsn "+ggsn);
				          		 try{
				          			 ggsn = ggsn.substring(ggsn.lastIndexOf("-")+1, ggsn.indexOf(".")); 				        		 
				        		 	 temp+="<br>"+sessid+"<br>"+ipaddr+"<br>"+lastactiv+"<br><a href='http://localhost:8082/"+ggsn+"/"+id+"'>"+peerid+"</a><br>"+apn+"<br>"+traf+"<br><br>";
				          		 }catch(Exception e){
				          			 e.printStackTrace();
				          			 temp+="<br>"+sessid+"<br>"+ipaddr+"<br>"+lastactiv+"<br>"+peerid+"<br>"+apn+"<br>"+traf+"<br><br>";
				          		 }
				        	 }
				        	  //response=res.toString();
				        	 //System.out.println(res);
				        	 
				        }
					}catch(NamingException e){
						e.printStackTrace();
						//response+="session not found.";
					}					
					
				context.close();
 				FileInputStream input3 = new FileInputStream("end.html");

				byte[] fileData3 = new byte[input3.available()];

				input3.read(fileData3);
				input3.close();	
				//System.out.println("temp di sini");
				response=response+temp+new String(fileData3, "UTF-8");
				break outerloop;
	    					}}}
				//here
	        }
	        
			//response+="</body></html>";
			
			//long currtime = System.currentTimeMillis();
			out = new PrintWriter(s.getOutputStream(), true); 
			out.println("HTTP/1.0 200"); 
			out.println("Content-type: text/html");
			out.println("Server-name: myserver"); 
			
			//String response = String.valueOf(randomInt)+","+String.valueOf(randomInt1)+","+String.valueOf(randomInt2)+","+String.valueOf(randomInt3)+","+String.valueOf(randomInt4)+","+String.valueOf(randomInt5)+","+String.valueOf(randomInt6)+","+String.valueOf(randomInt7)+","+String.valueOf(randomInt8)+","+String.valueOf(randomInt9); 
			out.println("Content-length: " + response.length()); 
			out.println(""); 
			out.println(response); 
			//System.out.println(response);
			out.flush(); 
			out.close(); 
			s.close(); 
			
		} catch (Exception e) { 
			System.out.println("Failed respond to client request: " + e.getMessage()); 
			e.printStackTrace();
		} 
		finally { 
			if (s != null) { 
				try { 
					s.close(); 
				} 
				catch (IOException e) { e.printStackTrace(); } } } return; 
	}

} 