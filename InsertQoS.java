import java.io.BufferedReader;
import java.io.FileReader;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;


public class InsertQoS {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
			DB db = mongoClient.getDB( "sapc" );
			String host = args[1];
			DBCollection coll = db.getCollection(host+"_qos");
			coll.drop();
			coll.createIndex(new BasicDBObject("id", 1));
			DBCollection colnp = db.getCollection("np");
			BufferedReader br = new BufferedReader(new FileReader(args[0]));
			String line="";
			while ((line = br.readLine()) != null) {
				String idstring=line.substring(line.indexOf("qosProfileName"),line.indexOf("trafficClass"));
				String id=idstring.split("\"")[1];
				//System.out.println(idstring+" : "+id);
				String mbrdlstring=line.substring(line.indexOf("mbrDownlink"),line.indexOf("mbrUplink"));
				String mbrdl=mbrdlstring.split("\"")[1];
				//System.out.println(mbrdlstring+" : "+mbrdl);
				String mbrulstring=line.substring(line.indexOf("mbrUplink"),line.indexOf("signalingIndicator"));
				String mbrul=mbrulstring.split("\"")[1];
				//System.out.println(mbrulstring+" : "+mbrul);
				String arpstring=line.substring(line.indexOf("arpPriorityLevel"),line.indexOf("arpPvi"));
				String arp=arpstring.split("\"")[1];
				//System.out.println(arpstring+" : "+arp);
				String cistring=line.substring(line.indexOf("classIdentifier"),line.indexOf("]]]"));
				String ci=cistring.split("\"")[1];
				//System.out.println(cistring+" : "+ci);
				BasicDBObject whereQuery = new BasicDBObject();
				whereQuery.put("arp", arp);
				whereQuery.put("ci", ci);
				DBCursor cursor = colnp.find(whereQuery);
				String np="";
				if(cursor.hasNext()){
					np=(String) cursor.next().get("np");
				}
				try{
					DBObject listItem = new BasicDBObject("id", id).append("mbrdl", mbrdl).append("mbrul",mbrul).append("arp", arp).append("ci", ci).append("np", np);
					coll.insert(listItem);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			br.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
