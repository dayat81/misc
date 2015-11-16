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


public class InsertPol {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{
			MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
			DB db = mongoClient.getDB( "sapc" );
			String host = args[1];
			DBCollection coll = db.getCollection(host+"_pol");
			coll.drop();
			coll.createIndex(new BasicDBObject("id", 1));
			DBCollection collrule = db.getCollection(host+"_rule");
			BufferedReader br = new BufferedReader(new FileReader(args[0]));
			String line="";
			while ((line = br.readLine()) != null) {
				String type="";
				String idstring=line.substring(line.indexOf("policyId"),line.indexOf("ruleCombiningAlgorithm"));
				String id=idstring.split("\"")[1];				
				System.out.println(idstring+" : "+id);
				String rulestring=line.substring(line.indexOf("authorizationRules"),line.indexOf("policyCtxName"));
				String rules[]=rulestring.split("\"");
				JSONArray listrule = new JSONArray();
				for (int i = 0; i < rules.length; i++) {
					if(i%2==1){
						System.out.println(rules[i]);
						//get rule
						BasicDBObject fields = new BasicDBObject("_id",false);
						BasicDBObject whereQuery = new BasicDBObject();
						whereQuery.put("id", rules[i]);
						DBCursor cursor = collrule.find(whereQuery,fields);
						while(cursor.hasNext()) {
							String jsonstr=cursor.next().toString();
							if(jsonstr.contains("qos")){
								type="qos";
							}
							System.out.println(jsonstr);
							DBObject dbObject = (DBObject) JSON.parse(jsonstr);
							listrule.add(dbObject);
						}
					}
				}
				try{
					DBObject listItem = new BasicDBObject("id", id).append("rules",listrule).append("type", type);
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
