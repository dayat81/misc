import java.io.BufferedReader;
import java.io.FileReader;

import org.json.simple.JSONArray;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;


public class InsertSess {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
			DB db = mongoClient.getDB( "sapc" );
			String host = args[1];
			DBCollection coll = db.getCollection(host+"_sess");
			try{
			coll.drop();
			}catch(Exception e){
				e.printStackTrace();
			}
			coll.createIndex(new BasicDBObject("msid", 1));
			coll.createIndex(new BasicDBObject("sessions.peer", 1));
			BufferedReader br = new BufferedReader(new FileReader(args[0]));
			String line="";
			while ((line = br.readLine()) != null) {
				String ipsess  = line.substring(line.indexOf("EPC_IpSessionPot"),line.indexOf("pdpSessionRefs")).split("\"")[1];
				
				String msid  = line.substring(line.indexOf("subscriberId"),line.indexOf("subscriptionType")).split("\"")[1];
				//System.out.print(msid+" ");
				String apn  = line.substring(line.indexOf("apnId"),line.indexOf("specificData-shadow")).split("\"")[1];
				//System.out.print(apn+" ");
				String peer = line.substring(line.indexOf("generalSessionId"),line.indexOf("afSessionsPotRefs")).split("\"")[1];							
				//System.out.println(peer);
//				String lastactiv = line.substring(line.indexOf("lastActivityTT"),line.indexOf("correlatedIPv4")).split("\"")[1];
//				DateFormat format = new SimpleDateFormat("dd-MM-yyyy'T'HH:mm:ss", Locale.ENGLISH);
				try{
					DBObject elem = new BasicDBObject("apn", apn).append("peer", peer).append("ip", ipsess);
					BasicDBObject whereQuery = new BasicDBObject();
					whereQuery.put("msid", msid);
					DBCursor cursor = coll.find(whereQuery);					
					if(cursor.hasNext()){//update
						DBObject listItem = new BasicDBObject("sessions", elem);
						DBObject updateQuery = new BasicDBObject("$push", listItem);
						coll.update(whereQuery, updateQuery);						
					}else{//insert
						DBObject listItem = new BasicDBObject("msid", msid);	
						JSONArray list = new JSONArray();
						list.add(elem);
						listItem.put("sessions", list);
						coll.insert(listItem);						
					}
				}catch(Exception e){
					System.out.println(line);
					e.printStackTrace();
				}

			}
			br.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
