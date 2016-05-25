import java.io.BufferedReader;
import java.io.FileReader;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;


public class MigInsertQual {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{
			MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
			DB db = mongoClient.getDB( "sapc" );
			String host = args[1];
			DBCollection coll = db.getCollection("mig_"+host+"_qual");
			coll.createIndex(new BasicDBObject("idx", 1));
			BufferedReader br = new BufferedReader(new FileReader(args[0]));
			String line="";
			String idx="";
			while ((line = br.readLine()) != null) {
				idx  = line.substring(line.indexOf("mdp:U("),line.indexOf("]logicalDbNo"));
				//System.out.println(idx);
				DBObject listItem = new BasicDBObject("idx", idx);
				coll.insert(listItem);
			}
			br.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
