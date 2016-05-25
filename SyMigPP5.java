import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Properties;

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


public class SyMigPP5 {

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
			
			Properties properties = new Properties();
			properties.put( Context.INITIAL_CONTEXT_FACTORY, 
			  "com.sun.jndi.ldap.LdapCtxFactory" );
			properties.put( Context.PROVIDER_URL, "ldap://platform-vip:7323" );
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
			        		 boolean hasAO=false;
			        		 String pp="";
			        		 for (int i = 1; i < grp.length; i++) {			        			 
			        			 String[] grpid = grp[i].split(":");
			        			 if(grpid.length==1){
			        				 //System.out.print(" PP "+grp[i]);			        				 
			        				 pp=grp[i];	
			        			 }else if(grpid.length>1){
			        				 if(!grpid[0].equals("ROAMING")){
			        					 hasAO=true;
			        					 //break;
			        				 }
			        			 }
			        		 }
			        		 boolean proceed=false;
			        		 if(!obj.contains(pp)){
			        			 if(!hasAO){
			        				 proceed=true;
			        			 }
			        		 }else{
			        			 proceed=true;
			        		 }
			        		 if(proceed){
		 							Attribute attribute = new BasicAttribute("epc-groupids", pp);
		 							Attribute newattribute = new BasicAttribute("epc-groupids", fields[2]);
						            ModificationItem[] item = new ModificationItem[2];
						            item[0] = new ModificationItem(DirContext.REMOVE_ATTRIBUTE,attribute);
						            item[1] = new ModificationItem(DirContext.ADD_ATTRIBUTE,newattribute);
						            try{
						            	context.modifyAttributes(ldapSearchBase, item);
						            	System.out.println(fields[0]+","+fields[1]+","+fields[2]+","+pp);
						            	System.out.println(System.currentTimeMillis());
						            	Attributes entry = new BasicAttributes("EPC-SubscriberQualificationData","SubscriberChargingSystemName:OcsEnv5");
						            	context.createSubcontext("EPC-Name=EPC-SubscriberQualification,EPC-SubscriberId="+fields[0]+",EPC-SubscribersName=EPC-Subscribers,applicationName=EPC-EpcNode,nodeName=jambala", entry);
						            }catch(NamingException e){
						            	e.printStackTrace();
						            }				        			 
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
