import com.unboundid.ldap.sdk.BindRequest;
import com.unboundid.ldap.sdk.BindResult;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPConnectionOptions;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.SimpleBindRequest;
import com.unboundid.ldif.LDIFReader;


public class LdifAdd {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
		    final LDAPConnectionOptions connectionOptions = 
		    	       new LDAPConnectionOptions();
    	    connectionOptions.setAbandonOnTimeout(true);
    	    int connectionTimeoutMillis = 1000;
    	    connectionOptions.setConnectTimeoutMillis(connectionTimeoutMillis);
    	    final BindRequest bindRequest = new SimpleBindRequest("administratorName=jambala,nodeName=jambala","Pokemon1"); 	    
    	    final String host = args[1];
    	    final int port = 7323;

    	    LDAPConnection ldapConnection = 
    		   new LDAPConnection(connectionOptions,host,port);
    	    final BindResult bindResult = ldapConnection.bind(bindRequest);
    	    final ResultCode resultCode = bindResult.getResultCode();
    	    if(resultCode.equals(ResultCode.SUCCESS))
    	    {
    	        System.out.println("user is authenticated");
    			LDIFReader ldifReader = new LDIFReader(args[0]);
    			Entry entry;
    			entry = ldifReader.readEntry();
    			while(entry!=null){
    				//System.out.println(entry.getDN());
    				//System.out.println(entry.getAttributes());
    				try{
	    				LDAPResult addResult = ldapConnection.add(entry);
	    				System.out.println(addResult);
    				}catch(Exception e){
    					e.printStackTrace();
    				}
    				entry = ldifReader.readEntry();    				
    			}    	        
    	    }			    	    
			
			ldapConnection.close();			
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
