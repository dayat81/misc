import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;


public class DumpSess {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{
			MongoClient mongoClient = new MongoClient( "localhost" , 27019 );
			DB db = mongoClient.getDB( "sapc" );
			//String host = args[0];
			String host = "snb7";
			HashMap< String,Integer> hmap = new HashMap<String,Integer>();
			DBCollection coll = db.getCollection(host+"_sess");
			BasicDBObject whereQuery = new BasicDBObject();
			whereQuery.put("$where", "this.sessions.length >1");
			BasicDBObject field = new BasicDBObject("_id",false);//.append("sessions", false);
			DBCursor cursor = coll.find(whereQuery,field);	
			int total=0;
			while(cursor.hasNext()){
				DBObject res = cursor.next();				
				//System.out.println(res.get("msid"));
				BasicDBList sess = (BasicDBList) res.get("sessions");	
				if(sess!=null){
					total=total+sess.size()-1;
					for (Iterator<Object> iterator = sess.iterator(); iterator.hasNext();) {
						DBObject object = (DBObject) iterator.next();
						String per=(String)object.get("peer");
						//System.out.println(per);
						String[] fields= per.split(";");
						//System.out.println(fields[3]);
						String[] peer = fields[0].split("\\.");
						//System.out.println(peer.length);
						String pr="";
						if(peer.length>4){
							pr=peer[peer.length-4]+"."+peer[peer.length-3]+"."+peer[peer.length-2]+"."+peer[peer.length-1];
						}else if(peer.length==3){
							String[] pgw=peer[peer.length-3].split("-");					
							pr=pgw[pgw.length-1]+"."+peer[peer.length-2]+"."+peer[peer.length-1];
						}	
						if(hmap.get(pr)!=null){
							int c=hmap.get(pr);
							c+=1;
							hmap.put(pr, c);
						}else{
							hmap.put(pr, 1);
						}
					}
				}
			}
	      Set<Entry<String, Integer>> set2 = hmap.entrySet();
	      Iterator<Entry<String, Integer>> iterator2 = set2.iterator();
	      while(iterator2.hasNext()) {
	          Map.Entry mentry2 = (Map.Entry)iterator2.next();
	          System.out.print("Key is: "+mentry2.getKey() + " & Value is: ");
	          System.out.println(mentry2.getValue());
	       }	
	      System.out.println(total);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
