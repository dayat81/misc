

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeMap;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

public class DelSessHang {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	
			BufferedReader br;
			try {
				DateFormat format = new SimpleDateFormat("dd-MM-yyyy'T'HH:mm:ss", Locale.ENGLISH);
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
			// set properties for our connection and provider
			//String msid="62818325562";
			 String ldapSearchBase = "EPC-PerSubsId="+msid+",EPC-SessionsName=EPC-Sessions,applicationName=EPC-EpcNode, nodeName=jambala";

			
			try {
				

	        NamingEnumeration<SearchResult> results = context.search(ldapSearchBase, searchFilter, searchControls);

	        //SearchResult searchResult = (SearchResult) results.nextElement();
	        Integer i=0;
	        System.out.println("msid "+msid);
	        System.out.println("----------------");	
	        //Date star = new Date();
	        //HashMap<Attribute, Attribute> ips = new HashMap<Attribute, Attribute>();
	        Map<Date, String> hangsess = new TreeMap<Date,String>();
	        while(results.hasMore()) {	        	 	        	
	        	 SearchResult searchResult = (SearchResult) results.nextElement();
	        	 //System.out.println(searchResult.getAttributes());EPC-PerSubsId
	        	 Attributes res = searchResult.getAttributes();
	        	 Attribute ip = res.get("epc-ipaddress");
	        	 Attribute trafid = res.get("epc-trafficsessionid");
	        	 Attribute sess= res.get("epc-sessionid");	        	
	        	 Attribute lastactiv= res.get("epc-lastactivitytimestamp");

	        	 Attribute apn= res.get("epc-calledstationid");
	        	 Attribute peer= res.get("epc-peerid");
	        	 
	        	 if(sess!=null){     					       			
		       		 System.out.println(sess);
		       		System.out.println(apn);	
	        		 System.out.println(ip);
	        		 System.out.println(peer);
	        		 System.out.println(trafid);
	        		 System.out.println(lastactiv);

	        		 i++;
	        		 hangsess.put(format.parse(lastactiv.toString().split(" ")[1]),sess.toString().substring(15));	  	        			 


	        	 }	        	 
        	 
	        }
	        
	        //System.out.println("start "+star+" now "+new Date()+" total "+i);
	        System.out.println("----------------");	
//	        for(Integer j=1;j<i;j++){
//	        	String todelete ="EPC-SessionId="+hangsess.get(j)+",EPC-PerSubsId="+msid+",EPC-SessionsName=EPC-Sessions,applicationName=EPC-EpcNode,nodeName=jambala";
//	        	System.out.println("delete "+todelete);
//	        	try{
//	        	context.destroySubcontext(todelete);
//				} catch (NamingException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
//	        }
	        int count=0;
	        for(Entry<Date, String> entry : hangsess.entrySet()) {
	        	  count++;
	        	  if(hangsess.size()==count){
	        		  break;
	        	  }	        	
	        	  Date key = entry.getKey();
	        	  String value = entry.getValue();

	        	  System.out.println("todelete "+ key + " => " + value);
		        	String todelete ="EPC-SessionId="+value+",EPC-PerSubsId="+msid+",EPC-SessionsName=EPC-Sessions,applicationName=EPC-EpcNode,nodeName=jambala";
		        	System.out.println("delete "+todelete);
		        	try{
		        	context.destroySubcontext(todelete);
					} catch (NamingException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}	        	  
	        	}	        
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			}
			context.close();
				br.close();

			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (NamingException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			} 
		
	}

}
