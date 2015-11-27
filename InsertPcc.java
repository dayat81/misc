import java.io.BufferedReader;
import java.io.FileReader;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;


public class InsertPcc {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{
			MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
			DB db = mongoClient.getDB( "sapc" );
			String host = args[1];
			DBCollection coll = db.getCollection(host+"_pcc");
			coll.drop();
			coll.createIndex(new BasicDBObject("idx", 1));
			BufferedReader br = new BufferedReader(new FileReader(args[0]));
			String line="";
			while ((line = br.readLine()) != null) {
				String idx  = line.substring(line.indexOf("mdp:U("),line.indexOf("]logicalDbNo"));
				//System.out.println(idx);
				String rid  = line.substring(line.indexOf("ruleId"),line.indexOf("ruleIdv6")).split("\"")[1];
				//System.out.println(rid);
				String rtype  = line.substring(line.indexOf("ruleType"),line.indexOf("bearerUsage"));
				//System.out.println(rtype.charAt(11));	
				try{
					DBObject listItem = new BasicDBObject("idx", idx).append("ruleid",rid).append("ruletype",rtype.charAt(11));
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
