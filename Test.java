
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//create URL object
		try{
			String pp="513756614";
			String msid="628787654321";
			long ts= new Date().getTime();
		URL url = new URL( "http://localhost:2095/spm/provisioning.ws" );  
		HttpURLConnection rc = (HttpURLConnection)url.openConnection();  
		//you need to check if server expects POST or GET
		//but to be honest I never saw web-service expecting GET reqiest

		 rc.setRequestMethod("POST");  
		 rc.setDoOutput( true );  
		 rc.setDoInput( true );   
		 //it is very important to specify the content type. Web-service will reject the request if it is 
		 //not XML
		 rc.setRequestProperty( "Content-Type", "text/xml; charset=utf-8" );  
		//and here comes some dummy SOAP message
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
			         "<prov:trxId>"+msid+ts+"</prov:trxId>"+
			         "<prov:subscriberId>"+msid+"</prov:subscriberId>"+
			         "<prov:groupId>"+pp+"</prov:groupId>"+
			         "<prov:operatorSpecificInfo/>"+
			      "</prov:modifySubscriberRequest>"+
			   "</soapenv:Body>"+
			"</soapenv:Envelope>";

		//several more definitions to request
		//String inputContent = "soauser:spm";
		//String base64String = BaseEncoding.base64().encode(inputContent.getBytes("UTF-8"));		
		//byte[] encodedBytes = Base64.encodeBase64("soauser:spm".getBytes());
		//System.out.println("encodedBytes " + new String(encodedBytes));
		int len = reqStr.length();  
		rc.setRequestProperty( "SOAPAction", "" );  
		rc.setRequestProperty( "Accept-Encoding","gzip,deflate" );
		rc.setRequestProperty( "Connnection","Keep-Alive" );
		//rc.setRequestProperty( "Host","localhost:2095" );
		rc.setRequestProperty( "Authorization","Basic c29hdXNlcjpzcG0=" );
		rc.connect();  
		//write XML to the server
		OutputStreamWriter outStr = new OutputStreamWriter( rc.getOutputStream() );   
		outStr.write( reqStr, 0, len );  
		outStr.flush();

		/* Here is the important part, if something goes wrong and excetion will
		* be thrown and you will have some meaningless exception saying blah.. blahh HTTP 500
		* which actually doesn't tell you a lot about what happen.
		* However most web-services provide as a response some error page that displays what 
		* was wrong. It whould be nice to see this page instead of stupid HTTP 500.
		* It is not difficult . All you need is actually read not the response stream , but the error stream
		*/

		InputStreamReader read;

		  read = new InputStreamReader( rc.getInputStream() );  
 
		  //if something wrong instead of the output, read the error
		  //read = new InputStreamReader( rc.getErrorStream() );  
	

		//read server response
		StringBuilder sb = new StringBuilder();     
		int ch;

			ch = read.read();
			while( ch != -1 ){  
				  sb.append((char)ch);  
				  ch = read.read();  
				}  
				String responseTr = sb.toString();
				System.out.println(responseTr);
				Pattern p = Pattern.compile("<responseCode>(.*?)</responseCode>");
				Matcher m = p.matcher(responseTr);
				if (m.find()) {
					if(m.group(1).equals("5000")){
						System.out.println("OK");
					}else{
						System.err.println("Error");
					}
				}
		}catch(Exception e){
			e.printStackTrace();
		}


	}

}