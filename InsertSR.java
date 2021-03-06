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


public class InsertSR {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{
			MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
			DB db = mongoClient.getDB( "sapc" );
			String host = args[1];
			DBCollection coll = db.getCollection(host+"_sr");
			coll.drop();
			coll.createIndex(new BasicDBObject("idx", 1));
			DBCollection collpol = db.getCollection(host+"_pol");
			BufferedReader br = new BufferedReader(new FileReader(args[0]));
			String line="";
			while ((line = br.readLine()) != null) {
				String idx  = line.substring(line.indexOf("mdp:U("),line.indexOf("]logicalDbNo"));
				String sbjgrp=line.substring(line.indexOf("subjectGroupId"),line.indexOf("resourceId")).split("\"")[1];
				String resourcestring=line.substring(line.indexOf("resourceId"),line.indexOf("contextId")).split("\"")[1];
				String contextstring=line.substring(line.indexOf("contextId"),line.indexOf("actionIds")).split("\"")[1];
				String actionstring=line.substring(line.indexOf("actionIds"),line.indexOf("priority"));
				String priostring=line.substring(line.indexOf("priority"),line.indexOf("policies"));
				String prio=priostring.substring(priostring.indexOf("(")+1, priostring.indexOf(")"));
//				System.out.println(line);
//				System.out.println(prio);
				String polstring=line.substring(line.indexOf("policies"),line.indexOf("]]"));
				//System.out.println(idx+" "+resourcestring+" "+contextstring);
				String[] action=actionstring.split("\"");
				JSONArray listaction = new JSONArray();
				for (int i = 0; i < action.length; i++) {
					if(i%2==1){
						listaction.add(action[i]);
					}
				}
				String[] pol=polstring.split("\"");
				JSONArray listpol = new JSONArray();
				for (int i = 0; i < pol.length; i++) {
					if(i%2==1){
						//get policy here
						BasicDBObject fields = new BasicDBObject("_id",false);
						BasicDBObject whereQuery = new BasicDBObject();
						whereQuery.put("id", pol[i]);
						DBCursor cursor = collpol.find(whereQuery,fields);
						while(cursor.hasNext()) {
							String jsonstr=cursor.next().toString();
							//System.out.println(jsonstr);
							DBObject dbObject = (DBObject) JSON.parse(jsonstr);
							listpol.add(dbObject);
						}						
					}					
				}
				try{
					DBObject listItem = new BasicDBObject("idx", idx).append("sbjgrp",sbjgrp).append("resource",resourcestring).append("context", contextstring).append("action",listaction).append("policy", listpol).append("prio", prio);
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
