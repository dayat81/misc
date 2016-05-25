import java.net.UnknownHostException;

import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONCompare;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.JSONCompareResult;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;


public class CheckConfig {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			MongoClient mongoClient;
			mongoClient = new MongoClient( "localhost" , 27017 );
			DB db = mongoClient.getDB( "sapc" );
			BasicDBObject fields = new BasicDBObject("_id",false);
			String host="before";
			DBCollection collsr = db.getCollection(host+"_subj");
			DBCursor cursor = collsr.find(null,fields);
			while(cursor.hasNext()) {
				DBObject jsonstr=(DBObject)cursor.next();				
				//String[] hosts = {"cbt1","cbt2","cbt3","cbt4","cbt5","btr1","cbt7","snb1","snb2","snb3","snb4","snb5","snb6","snb7","snb8","snb9"};
				String[] hosts = {"after"};
				for (int i = 0; i < hosts.length; i++) {
					DBCollection colls = db.getCollection(hosts[i]+"_subj");
					BasicDBObject whereQuery = new BasicDBObject();
					whereQuery.put("id", jsonstr.get("id"));
					DBCursor cursor1 = colls.find(whereQuery,fields);
					while(cursor1.hasNext()) {
						DBObject jsonstr1=(DBObject)cursor1.next();						
						try{
							 JSONCompareResult result = JSONCompare.compareJSON(jsonstr.toString(), jsonstr1.toString(), JSONCompareMode.LENIENT);
							//JSONAssert.assertEquals(jsonstr.toString(), jsonstr1.toString(), false);							 
							 if(result.failed()&&!result.toString().contains("prio")){
								 System.out.println(jsonstr.get("id")+" : "+host+" != "+hosts[i]);
								 System.out.println(result);
							 }
						}catch(JSONException e){
							e.printStackTrace();
						}
					}
				}
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
