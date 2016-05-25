import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.InitialDirContext;


public class LdapAdd {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{
			Properties properties = new Properties();
			properties.put( Context.INITIAL_CONTEXT_FACTORY, 
			  "com.sun.jndi.ldap.LdapCtxFactory" );
			properties.put( Context.PROVIDER_URL, "ldap://"+args[1]+":7323" );
			properties.put( Context.REFERRAL, "ignore" );
			// set properties for authentication
			properties.put( Context.SECURITY_PRINCIPAL, "administratorName=jambala,nodeName=jambala" );
			properties.put( Context.SECURITY_CREDENTIALS, "Pokemon1" );		
			InitialDirContext context = new InitialDirContext( properties );	
			
			BufferedReader br = new BufferedReader(new FileReader(args[0]));	
			String line="";
			String dn="";
			String data="";
			while ((line = br.readLine()) != null) {
				//System.out.println(line);
				if(line.contains("dn:")){
					if(!dn.equals("")){
						//execute
						//System.out.println("dn:"+dn);
						//System.out.println("EPC-AccumulatedData:"+data);
						try{
							Attributes attributes=new BasicAttributes();
							attributes.put("EPC-AccumulatedData",data);							
							context.createSubcontext(dn,attributes);
						}catch(Exception e){
							e.printStackTrace();
						}
					}
					dn=line.substring(4);
				}else if(line.contains("EPC-AccumulatedData:")){
					data=line.substring(21);
				}else if(line.contains("objectClass:")){
					//skip
				}else if(line.contains("EPC-AccumulatedName:")){
					//skip
				}else if(line.contains("Error:")){
					//skip
				}else{
					data+=line;
				}
			}
			br.close();
			//execute
			//System.out.println("dn:"+dn);
			//System.out.println("EPC-AccumulatedData:"+data);		
			Attributes attributes=new BasicAttributes();
			attributes.put("EPC-AccumulatedData",data);
			context.createSubcontext(dn,attributes);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
