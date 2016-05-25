import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Iterator;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


public class DumpSub {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		BufferedReader br;
		try {
			
			br = new BufferedReader(new FileReader(args[0]));

		String msid="";
		Properties properties = new Properties();
		properties.put( Context.INITIAL_CONTEXT_FACTORY, 
		  "com.sun.jndi.ldap.LdapCtxFactory" );
		properties.put( Context.PROVIDER_URL, "ldap://"+args[1]+":"+args[2] );
		properties.put( Context.REFERRAL, "ignore" );

		// set properties for authentication
		properties.put( Context.SECURITY_PRINCIPAL, "administratorName=jambala,nodeName=jambala" );
		properties.put( Context.SECURITY_CREDENTIALS, "Pokemon1" );		
		InitialDirContext context = new InitialDirContext( properties );
		// Create the search controls
		SearchControls searchCtls = new SearchControls();

		// Specify the search scope
		searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		searchCtls.setCountLimit(10);
		// specify the LDAP search filter, just users
		String searchFilter = "(&(objectClass=*))";
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);			
		while ((msid = br.readLine()) != null) {
			 String ldapSearchBase = "EPC-SubscriberId="+msid+",EPC-SubscribersName=EPC-Subscribers,applicationName=EPC-EpcNode, nodeName=jambala";

				
			try {				
	        NamingEnumeration<SearchResult> results = context.search(ldapSearchBase, searchFilter, searchControls);
	        while(results.hasMore()) {	        	 	        	
	        	 SearchResult searchResult = (SearchResult) results.nextElement();
	        	 //System.out.println(searchResult.getAttributes());
	        	 Attributes res = searchResult.getAttributes();
	        	 Attribute subs = res.get("epc-subscriberid");
	        	 if(subs!=null){
	        		 System.out.println(subs.toString().split(" ")[1]+","+res.get("epc-groupids").toString().split(": ")[1]);
	        	 }  	 
	        }	    
        
			}catch(NamingException e){
				//e.printStackTrace();
			}
//			try {				
//    
//		        ldapSearchBase = "EPC-AccumulatedName="+msid+",EPC-SubscribersAccumulatedName=EPC-SubscribersAccumulated,applicationName=EPC-EpcNode, nodeName=jambala";
//		        NamingEnumeration<SearchResult> results = context.search(ldapSearchBase, searchFilter, searchControls);
//		        while(results.hasMore()) {	        	 	        	
//		        	 SearchResult searchResult = (SearchResult) results.nextElement();
//		        	 System.out.println(searchResult.getAttributes());
//		           	 Attributes res = searchResult.getAttributes();
//		           	 String acc = res.get("epc-accumulateddata").toString();   
//						 acc= acc.substring(21);
//						if(!acc.equals("null")){
//						//System.out.println(line.substring(21));
//						JSONParser jsonParser = new JSONParser();
//						JSONObject jsonObject = (JSONObject) jsonParser.parse(acc);
//
//						// get an array from the JSON object
//						JSONArray lang= (JSONArray) jsonObject.get("reportingGroups");
//
//						Iterator i = lang.iterator();
//
//						// take each value from the json array separately
//						while (i.hasNext()) {
//							JSONObject innerObj = (JSONObject) i.next();
//							System.out.print(innerObj.get("subscriberGroupName") + 
//									"," + innerObj.get("name")+",");
//							JSONObject structure = (JSONObject) innerObj.get("absoluteAccumulated");
//							if(structure!=null){
//							Long bidir = (Long) structure.get("bidirVolume");
//							if(bidir!=null){
//								System.out.print(bidir.toString()+",");
//							}
//							//System.out.println("absoluteAccumulated previousExpiryDate: " + structure.get("previousExpiryDate"));
//							JSONArray counters= (JSONArray) structure.get("counters");
//							//System.out.println("absoluteAccumulated counters: " + counters);
//							if(counters!=null){
//								Iterator j = counters.iterator();
//								while (j.hasNext()) {
//									JSONObject counter = (JSONObject) j.next();
//									System.out.print(counter.get("name").toString()+"="+counter.get("bidirVolume").toString()+",");
//								}
//							}
//							}
//						}	
//					}
//						System.out.println();		           	 
//		        }	
//        
//				}catch(NamingException e){
//					//e.printStackTrace();
//					System.out.println("no acc");	
//				}
//			try {				
//	
//		        ldapSearchBase = "EPC-PerSubsId="+msid+",EPC-SessionsName=EPC-Sessions,applicationName=EPC-EpcNode, nodeName=jambala";
//		        NamingEnumeration<SearchResult> results = context.search(ldapSearchBase, searchFilter, searchControls);
//		        while(results.hasMore()) {	        	 	        	
//		        	 SearchResult searchResult = (SearchResult) results.nextElement();
//		        	 //System.out.println(searchResult.getAttributes());
//		        	 Attributes res = searchResult.getAttributes();
//		        	 Attribute subsid= res.get("epc-persubsid");
//		        	 if(subsid!=null){
//		        	 System.out.println(subsid);
//		        	 }
//		        	 Attribute sess= res.get("epc-sessionid");	        	
//
//		        	 
//		        	 if(sess!=null){   
//			        	 Attribute lastactiv= res.get("epc-lastactivitytimestamp");
//			        	 Attribute ip = res.get("epc-ipaddress");
//			        	 Attribute trafid = res.get("epc-trafficsessionid");
//			        	 Attribute apn= res.get("epc-calledstationid");
//			        	 Attribute peer= res.get("epc-peerid");		        		 
//			       		 System.out.println(sess);
//			       		System.out.println(apn);	
//		        		 System.out.println(ip);
//		        		 System.out.println(peer);
//		        		 System.out.println(trafid);
//		        		 System.out.println(lastactiv);
//		        	 }
//		        }	        
//				}catch(NamingException e){
//					//e.printStackTrace();
//					System.out.println("no sess");
//				}	
//			System.out.println();
		}
		context.close();
		br.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
