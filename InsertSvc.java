import java.io.BufferedReader;
import java.io.FileReader;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;


public class InsertSvc {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{
			MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
			DB db = mongoClient.getDB( "sapc" );
			String host = args[1];
			DBCollection coll = db.getCollection(host+"_svc");
			coll.drop();
			coll.createIndex(new BasicDBObject("id", 1));
			DBCollection collpcc = db.getCollection(host+"_pcc");
			BufferedReader br = new BufferedReader(new FileReader(args[0]));
			String line="";
			while ((line = br.readLine()) != null) {				
				String id  = line.substring(line.indexOf("serviceId"),line.indexOf("serviceDescription")).split("\"")[1];
				//System.out.println(id);
//				String desc  = line.substring(line.indexOf("serviceDescription"),line.indexOf("pccRulePotRef")).split("\"")[1];
//				System.out.println(desc);
				String pcc  = line.substring(line.indexOf("pccRulePotRef"),line.indexOf("]]"));
				String[] refs=pcc.split(":G");
				String jsonstr="";
				for (int i = 1; i < refs.length; i++) {
					String ref=refs[i];
					if(refs[i].charAt(0)=='['){
						ref=ref.replaceAll("\\[", "");
					}
					if(refs[i].charAt(refs[i].length()-1)==']'){
						ref=ref.replaceAll("\\]", "");
					}					
					//System.out.println(ref);
					//lookup to pcc coll
					BasicDBObject fields = new BasicDBObject("_id",false).append("idx", false);
					BasicDBObject whereQuery = new BasicDBObject();
					whereQuery.put("idx", ref);
					DBCursor cursor = collpcc.find(whereQuery,fields);
					while(cursor.hasNext()) {
						jsonstr=cursor.next().toString();
					}
				}
				try{
					DBObject listItem = new BasicDBObject("id", id).append("pcc",(DBObject) JSON.parse(jsonstr));
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
