import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Iterator;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;


public class GetEpcPrio {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{
			MongoClient mongoClient = new MongoClient( "localhost" , 27019 );
			DB db = mongoClient.getDB( "sapc" );
			DBCollection collsr = db.getCollection("btr1_subj");
			BasicDBObject fields = new BasicDBObject("_id",false).append("subjectresource", true);
			BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\ehidhid\\Documents\\ao.csv"));
			String line="";
			while ((line = br.readLine()) != null) {
				System.out.print(line);
				String[] field = line.split(",");
				//System.out.println(field[0]);
				BasicDBObject whereQuery = new BasicDBObject();
				whereQuery.put("id", field[0]);
				DBCursor cursor1 = collsr.find(whereQuery,fields);
				outerloop:
				while(cursor1.hasNext()) {
					DBObject res = cursor1.next();
					BasicDBList sbj = (BasicDBList) res.get("subjectresource");	
					if(sbj==null){
						break;
					}
					for (Iterator<Object> iterator = sbj.iterator(); iterator.hasNext();) {
						DBObject object = (DBObject) iterator.next();
						String resource=(String)object.get("resource");
						String context=(String)object.get("context");
						if(resource.equals("_Bearer_")&&context.equals("QoS")){
							int currprio=Integer.parseInt(object.get("prio").toString());
							System.out.print(","+currprio);
							break outerloop;
						}
					}
				}
				System.out.println();
			}
			br.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
