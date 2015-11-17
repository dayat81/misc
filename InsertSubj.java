import java.io.BufferedReader;
import java.io.FileReader;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;


public class InsertSubj {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{
			MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
			DB db = mongoClient.getDB( "sapc" );
			String host = args[1];
			DBCollection coll = db.getCollection(host+"_subj");
			coll.drop();
			coll.createIndex(new BasicDBObject("id", 1));
			DBCollection collsr = db.getCollection(host+"_sbj");
			BufferedReader br = new BufferedReader(new FileReader(args[0]));
			String line="";
			while ((line = br.readLine()) != null) {
				//System.out.println(line);
				String sbjstring  = line.substring(line.indexOf("subjectGroupId"),line.indexOf("]]]")).split("\"")[1];
				//System.out.println(sbjstring);
				BasicDBObject fields = new BasicDBObject("_id",false);
				BasicDBObject whereQuery = new BasicDBObject();
				whereQuery.put("id", sbjstring);
				DBCursor cursor = collsr.find(whereQuery,fields);
				boolean found=false;
				while(cursor.hasNext()) {
					found=true;
					String jsonstr=cursor.next().toString();
					//System.out.println(jsonstr);
					DBObject dbObject = (DBObject) JSON.parse(jsonstr);
					try{						
						coll.insert(dbObject);
					}catch(Exception e){
						e.printStackTrace();
					}					
				}				
				if(!found){
					try{			
						DBObject listItem = new BasicDBObject("id", sbjstring);
						coll.insert(listItem);
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}
			br.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
