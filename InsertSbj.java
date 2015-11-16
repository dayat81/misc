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


public class InsertSbj {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{
			MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
			DB db = mongoClient.getDB( "sapc" );
			String host = args[1];
			DBCollection coll = db.getCollection(host+"_sbj");
			coll.drop();
			coll.createIndex(new BasicDBObject("id", 1));
			DBCollection collsr = db.getCollection(host+"_sr");
			BufferedReader br = new BufferedReader(new FileReader(args[0]));
			String line="";
			while ((line = br.readLine()) != null) {
				String sbjstring  = line.substring(line.indexOf("subjectGroupId"),line.indexOf("refs")).split("\"")[1];
				String idxstring  = line.substring(line.indexOf("refs"),line.indexOf("]]"));
				//System.out.println(sbjstring+" "+idxstring);
				String[] idx=idxstring.split(":G");
				JSONArray listsr = new JSONArray();
				for (int i = 1; i < idx.length; i++) {
					String id=idx[i];
					if(idx[i].charAt(0)=='['){
						id=id.replaceAll("\\[", "");
					}
					if(idx[i].charAt(idx[i].length()-1)==']'){
						id=id.replaceAll("\\]", "");
					}					
					//System.out.println(id);
					//get subjectresource		
					BasicDBObject fields = new BasicDBObject("_id",false).append("idx", false);
					BasicDBObject whereQuery = new BasicDBObject();
					whereQuery.put("idx", id);
					DBCursor cursor = collsr.find(whereQuery,fields);
					while(cursor.hasNext()) {
						String jsonstr=cursor.next().toString();
						//System.out.println(jsonstr);
						DBObject dbObject = (DBObject) JSON.parse(jsonstr);
						listsr.add(dbObject);
					}					
				}
				try{
					DBObject listItem = new BasicDBObject("id", sbjstring).append("subjectresource",listsr);
					coll.insert(listItem);
				}catch(Exception e){
					e.printStackTrace();
				}				
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
