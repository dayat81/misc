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


public class CheckSubsInvalid {
	   public static boolean isIntegerParseInt(String str) {
	        try {
	            Integer.parseInt(str);
	            return true;
	        } catch (NumberFormatException nfe) {}
	        return false;
	    }
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{
			MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
			DB db = mongoClient.getDB( "sapc" );
			DBCollection collsr = db.getCollection(args[0]+"_subj");
			BasicDBObject fields = new BasicDBObject("_id",false).append("subjectresource", true);
			BufferedReader br = new BufferedReader(new FileReader(args[0]+".sub"));
			String line="";
			while ((line = br.readLine()) != null) {
				String msid = line.substring(line.indexOf("EPC_SubscriberPot"),line.indexOf("groups")-2);
				msid = msid.substring(msid.indexOf("\"")+1);
				//System.out.println(msid);
				String grps = line.substring(line.indexOf("groups"),line.indexOf("services"));
				//System.out.println(grps);
				String[] grp =grps.split("\"");
				int epcprio=-1;
				outerloop:
				for (int i = 0; i < grp.length/2; i++) {
					String[] arr=grp[2*i+1].split(":");
					String grpid=arr[0];
					if(grpid.contains("_EXT")){
						grpid=grpid.split("_")[0];
						//System.out.println("grpid"+grpid);							
					}					
					if(arr.length>1&&!arr[0].equals("ROAMING")&&isIntegerParseInt(grpid)){
						//System.out.print(msid+";"+arr[0]+";"+arr[1]);
						//get prio epc
						BasicDBObject whereQuery = new BasicDBObject();
						whereQuery.put("id", grpid);
						DBCursor cursor1 = collsr.find(whereQuery,fields);
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
									System.out.print(msid+";"+arr[0]+";"+arr[1]);
									System.out.println(";"+object.get("prio"));
									int currprio=Integer.parseInt(object.get("prio").toString());
									if(currprio>=epcprio){
										epcprio=currprio;
									}else{
										System.out.println("invalid");
										break outerloop;
									}
								}
							}
						}
					}					
				}
			}
			br.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
