import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.Context;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;


public class SyMigPP6Soap {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{
			BufferedReader br0 = new BufferedReader(new FileReader(args[0]));
			String line="";
			ArrayList<String> obj = new ArrayList<String>();
			while ((line = br0.readLine()) != null) {
				obj.add(line.trim());				
			}
			br0.close();

			URL url = new URL( "http://localhost:2095/spm/provisioning.ws" );  
			
			Properties properties = new Properties();
			properties.put( Context.INITIAL_CONTEXT_FACTORY, 
			  "com.sun.jndi.ldap.LdapCtxFactory" );
			properties.put( Context.PROVIDER_URL, "ldap://"+args[2]+":7323" );
			properties.put( Context.REFERRAL, "ignore" );
			// set properties for authentication
			properties.put( Context.SECURITY_PRINCIPAL, "administratorName=jambala,nodeName=jambala" );
			properties.put( Context.SECURITY_CREDENTIALS, "Pokemon1" );		
			InitialDirContext context = new InitialDirContext( properties );
			String searchFilter = "(&(objectClass=*))";
	        SearchControls searchControls = new SearchControls();
	        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
	        
			BufferedReader br = new BufferedReader(new FileReader(args[1]));			
			while ((line = br.readLine()) != null) {
				//System.out.print(line);
				String[] fields=line.split(",");
				String ldapSearchBase = "EPC-SubscriberId="+fields[0]+",EPC-SubscribersName=EPC-Subscribers,applicationName=EPC-EpcNode,nodeName=jambala";
				try{
			        NamingEnumeration<SearchResult> results = context.search(ldapSearchBase, searchFilter, searchControls);
			        while(results.hasMore()) {	
			        	 SearchResult searchResult = (SearchResult) results.nextElement();	        	 
			        	 Attributes res = searchResult.getAttributes();
			        	 Object grup = res.get("epc-groupids");	
			        	 if(grup!=null){	
			        		 String grp[] = grup.toString().split(" ");
			        		 //boolean hasAO=false;
			        		 String pp="";
			        		 for (int i = 1; i < grp.length; i++) {			        			 
			        			 String[] grpid = grp[i].split(":");
			        			 if(grpid.length==1){
			        				 //System.out.print(" PP "+grp[i]);			        				 
			        				 pp=grp[i];	
			        			 }
			        		 }
			        		 if(!obj.contains(pp)){	
						            long ts= new Date().getTime();
						            String reqStr = "<soapenv:Envelope xmlns:prov=\"http://ericsson.com/EID/SPM/ProvisioningService/\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">"+
						     			   "<soapenv:Header>"+
						     			   "<wsse:Security soapenv:mustUnderstand=\"1\" xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\" xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\"><wsse:UsernameToken wsu:Id=\"UsernameToken-CF75B69D64EA265F1414619173310243\">"+
						     			   "<wsse:Username>soauser</wsse:Username><wsse:Password Type=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText\">spm</wsse:Password>"+
						     			   //"<wsse:Nonce EncodingType=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-soap-message-security-1.0#Base64Binary\">e560l6gvCqFth+VI8tmcyw==</wsse:Nonce>"+
						     			   //"<wsu:Created>2016-04-29T08:12:51.024Z</wsu:Created>
						     			   "</wsse:UsernameToken></wsse:Security>"+
						     			     "<wsse:Security xmlns:wsse=\"http://schemas.xmlsoap.org/ws/2003/06/secext\">"+
						     			       "<wsse:UsernameToken>"+
						     			         "<wsse:Username>soauser</wsse:Username>"+
						     			         "<wsse:Password Type=\"wsse:PasswordText\">spm</wsse:Password>"+
						     			       "</wsse:UsernameToken>"+
						     			     "</wsse:Security>"+
						     			   "</soapenv:Header>"+ 
						     			   "<soapenv:Body>"+
						     			      "<prov:modifySubscriberRequest>"+
						     			         "<prov:trxId>"+fields[0]+";"+ts+"</prov:trxId>"+
						     			         "<prov:subscriberId>"+fields[0]+"</prov:subscriberId>"+
						     			         "<prov:groupId>"+fields[2]+"</prov:groupId>"+
						     			         "<prov:operatorSpecificInfo/>"+
						     			      "</prov:modifySubscriberRequest>"+
						     			   "</soapenv:Body>"+
						     			"</soapenv:Envelope>";
						    		 
						    		int len = reqStr.length();  
						    		//write XML to the server
						    		HttpURLConnection rc = (HttpURLConnection)url.openConnection();  
									//you need to check if server expects POST or GET
									//but to be honest I never saw web-service expecting GET reqiest

									 rc.setRequestMethod("POST");  
									 rc.setDoOutput( true );  
									 rc.setDoInput( true );   
									 rc.setRequestProperty( "Content-Type", "text/xml; charset=utf-8" );  	
									 rc.setRequestProperty( "SOAPAction", "" );  
									rc.setRequestProperty( "Accept-Encoding","gzip,deflate" );
									rc.setRequestProperty( "Connnection","Keep-Alive" );
									//rc.setRequestProperty( "Host","localhost:2095" );
									rc.setRequestProperty( "Authorization","Basic c29hdXNlcjpzcG0=" );
									rc.connect(); 
									OutputStreamWriter outStr = new OutputStreamWriter( rc.getOutputStream() ); 
									InputStreamReader read;
						    		outStr.write( reqStr, 0, len );  
						    		outStr.flush();
	
						    		//System.out.println("code "+rc.getResponseCode());
						    		  read = new InputStreamReader( rc.getInputStream() );  	
						    		 
						    		//read server response
						    		StringBuilder sb = new StringBuilder();     
						    		int ch;

						    			ch = read.read();
						    			while( ch != -1 ){  
						    				  sb.append((char)ch);  
						    				  ch = read.read();  
						    				}  
						    				String responseTr = sb.toString();
						    				//System.out.println(responseTr);
						    				Pattern p = Pattern.compile("<responseCode>(.*?)</responseCode>");
						    				Matcher m = p.matcher(responseTr);
						    				if (m.find()) {
						    					if(m.group(1).equals("5000")){
						    						System.out.println(fields[0]+","+fields[1]+","+fields[2]+","+pp);
						    					}else{
						    						System.err.println(line);
						    					}
						    				}
						    				//System.out.println(responseTr);		
						    				//System.out.println(fields[0]+","+fields[1]+","+fields[2]+","+pp);
			        		 }			        		 
			        	 }
			        }
				}catch(NameNotFoundException e){
					//System.out.println(" msid not exist");
					e.printStackTrace();
				}
				//System.out.println();
			}
			br.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
