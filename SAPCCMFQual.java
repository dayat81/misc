import java.io.BufferedReader;
import java.io.FileReader;


public class SAPCCMFQual {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{
			BufferedReader br = new BufferedReader(new FileReader(args[0]));
			String line="";
			while ((line = br.readLine()) != null) {
				if(line.contains("EPC-SubscriberQualification")){
					String fields[]=line.split(";");
					if(fields.length>9){
						if(fields[9].contains("i_operationResult=0")){
							String msid=line.substring(line.indexOf("EPC-SubscriberId="),line.indexOf(",EPC-SubscribersName"));
							msid=msid.substring(17);
							//System.out.print(msid+"-");
							if(fields[2].contains("Create")){
								//System.out.println(msid.substring(17)+" create "+line);
								System.out.println("dn: EPC-Name=EPC-SubscriberQualification,EPC-SubscriberId="+msid+",EPC-SubscribersName=EPC-Subscribers,applicationName=EPC-EpcNode,nodeName=jambala");
								System.out.println("changetype:add");
								System.out.println("EPC-SubscriberQualificationData: SubscriberChargingSystemName:OCS_XL");
								System.out.println();
							}else if(fields[2].contains("Remove")){
								//System.out.println(msid.substring(17)+" remove "+line);
								System.out.println("dn: EPC-Name=EPC-SubscriberQualification,EPC-SubscriberId="+msid+",EPC-SubscribersName=EPC-Subscribers,applicationName=EPC-EpcNode,nodeName=jambala");
								System.out.println("changetype:delete");
								System.out.println();
							}
						}
					}
				}
			}
			br.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
