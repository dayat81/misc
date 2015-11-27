import java.io.BufferedReader;
import java.io.FileReader;

import org.json.simple.JSONArray;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;


public class InsertSubg {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{
			MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
			DB db = mongoClient.getDB( "sapc" );
			String host = args[1];
			DBCollection coll = db.getCollection(host+"_sbg");
			coll.drop();
			coll.createIndex(new BasicDBObject("id", 1));	
			DBCollection collsvc = db.getCollection(host+"_svc");
			BufferedReader br = new BufferedReader(new FileReader(args[0]));
			String line="";
			while ((line = br.readLine()) != null) {
				String sbgstring  = line.substring(line.indexOf("subscriberGroupId"),line.indexOf("subscriberGroupDescription")).split("\"")[1];
				//System.out.println(sbgstring);
				String desc  = line.substring(line.indexOf("subscriberGroupDescription"),line.indexOf("subscribedServices")).split("\"")[1];
				//System.out.println(desc);
				String serv[]  = line.substring(line.indexOf("subscribedServices"),line.indexOf("blacklistServices")).split("\"");
				JSONArray listsr = new JSONArray();
				for (int i = 0; i < serv.length; i++) {
					if(i%2==1){
						//System.out.println(serv[i]);
						//lookup svc
						BasicDBObject fields = new BasicDBObject("_id",false);
						BasicDBObject whereQuery = new BasicDBObject();
						whereQuery.put("id", serv[i]);
						DBCursor cursor = collsvc.find(whereQuery,fields);
						String jsonstr="";
						while(cursor.hasNext()) {
							jsonstr=cursor.next().toString();
						}
						listsr.add((DBObject) JSON.parse(jsonstr));						
					}
				}
				try{
					DBObject listItem = new BasicDBObject("id", sbgstring).append("desc",desc).append("services", listsr);
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
