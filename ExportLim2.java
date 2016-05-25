import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;


public class ExportLim2 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		MongoClient mongoClient;
		try {
			
			mongoClient = new MongoClient( "localhost" , 27017 );
			DB db = mongoClient.getDB( "sapc" );
			String host = args[1];
			PrintWriter writermsid = new PrintWriter(host+"_lim_msid.txt", "UTF-8");
			PrintWriter writer = new PrintWriter(host+"_lim_dum.ldif", "UTF-8");
			PrintWriter writerq = new PrintWriter(host+"_qual.ldif", "UTF-8");
			DBCollection coll = db.getCollection("mig_"+host+"_lim");
			DBCollection collq = db.getCollection("mig_"+host+"_qual");
			BufferedReader br = new BufferedReader(new FileReader(args[0]));
			String line="";
			JSONParser jsonParser = new JSONParser();
			while ((line = br.readLine()) != null) {
				try{
					boolean found=false;

				BasicDBObject whereQuery = new BasicDBObject();

				String msid = line.substring(line.indexOf("EPC_SubscriberPot"),line.indexOf("groups")-2);
				msid = msid.substring(msid.indexOf("\"")+1);				
				
				String[] idxs={"",""};
				
				String idx  = line.substring(line.indexOf("pccSubscriberPotRef:M1[:G[mdp:U(")+26,line.indexOf("]notificationData")-1);
				if(idx.startsWith("dVrsn")){
					idx  = line.substring(line.indexOf("pccSubscriberPotRef:M2[:G[mdp:U(")+26,line.indexOf("]notificationData")-1);
					if(!idx.startsWith("dVrsn")){
						//System.out.println("idx "+idx);
						idxs=idx.split(":G");
						for (int i = 0; i < idxs.length; i++) {
							idxs[i]=idxs[i].substring(i, idxs[i].length()+i-1);
							//System.out.println("idxs "+idxs[i]);
						}
						//idx=idx.split(":G")[0];
						//System.out.println("real idx "+idx);
						//idx=idx.substring(0, idx.length()-1);
						//System.out.println("final idx "+idx);
					}
				}else{
					idxs[0]=idx;
				}	

				for (int i = 0; i < idxs.length; i++) {
				if(idxs[i].startsWith("mdp")){
					//System.out.println(idx);
					whereQuery.put("idx", idxs[i]);
					DBCursor cursor = coll.find(whereQuery);							
					while(cursor.hasNext()) {
						String jsonstr=cursor.next().toString();
						JSONObject accObject = (JSONObject) jsonParser.parse(jsonstr);
						JSONObject struct = (JSONObject) accObject.get("lim");
					
						//System.out.println(msid+":"+struct);
						writer.println("dn: EPC-LimitName=EPC-LimitUsage,EPC-SubscriberId="+msid+",EPC-SubscribersName=EPC-Subscribers,applicationName=EPC-EpcNode,nodeName=jambala");
//						writer.println("objectClass: EPC-UsageControlLimit");
//						writer.println("ownerId: 0");
//						writer.println("groupId: 4003");
//						writer.println("shareTree: nodeName=jambala");
//						writer.println("permissions: 1");
						writer.println("EPC-LimitName: EPC-LimitUsage");
						writer.println("EPC-Data : {}");
						writer.println();
						System.out.println("dn: EPC-LimitName=EPC-LimitUsage,EPC-SubscriberId="+msid+",EPC-SubscribersName=EPC-Subscribers,applicationName=EPC-EpcNode,nodeName=jambala");
						System.out.println("changetype:modify");
						System.out.println("replace:EPC-Data");
						System.out.println("EPC-Data: "+struct);
						System.out.println();
						found=true;
					}	
					DBCursor cursorq = collq.find(whereQuery);
					if(cursorq.hasNext()){
						writerq.println("dn: EPC-Name=EPC-SubscriberQualification,EPC-SubscriberId="+msid+",EPC-SubscribersName=EPC-Subscribers,applicationName=EPC-EpcNode,nodeName=jambala");
						writerq.println("EPC-Name: EPC-SubscriberQualification");
						writerq.println("EPC-SubscriberQualificationData: SubscriberChargingSystemName:OCS_XL");
						writerq.println();	
						//found=true;
					}
				}
				}
				if(!idx.startsWith("dVrsn")&&!found){
					writermsid.println(msid);
				}
				}catch(Exception e){
					System.err.println("error line "+line);
					e.printStackTrace();
				}
			}
			br.close();
			writer.close();
			writermsid.close();
			writerq.close();
			coll.drop();
			collq.drop();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}

}
