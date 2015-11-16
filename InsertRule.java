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


public class InsertRule {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{
			MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
			DB db = mongoClient.getDB( "sapc" );
			String host = args[1];
			DBCollection coll = db.getCollection(host+"_rule");
			coll.drop();
			coll.createIndex(new BasicDBObject("id", 1));
			DBCollection collqos = db.getCollection(host+"_qos");			
			BufferedReader br = new BufferedReader(new FileReader(args[0]));
			String line="";
			while ((line = br.readLine()) != null) {
				if(line.contains("QosProfile")){
					String idstring=line.substring(line.indexOf("authRuleId"),line.indexOf("provisionedConditionFormula"));
					String id=idstring.split("\"")[1];	
					System.out.println(idstring+" : "+id);
					String formulastring=line.substring(line.indexOf("provisionedConditionFormula"),line.indexOf("provisionedPermitOutputAttrValues"));
					String formula=formulastring.substring(formulastring.indexOf("(\"")+2,formulastring.lastIndexOf("\")"));
					System.out.println(formulastring+" : "+formula);
					char accessType='A';
					if(formula.contains("AccessData.bearer.accessType")){
						String atstring=formula.substring(formula.indexOf("AccessData.bearer.accessType"));
						atstring=atstring.substring(atstring.indexOf("=")+2);
						//check after =
						if(atstring.charAt(0)!=' '){
							if(atstring.charAt(0)!='\\'){
								accessType=atstring.charAt(0);
							}else{
								accessType=atstring.charAt(2);
							}
						}else{
							if(atstring.charAt(1)!='\\'){
								accessType=atstring.charAt(1);
							}else{
								accessType=atstring.charAt(3);
							}						
						}
					}
					System.out.println("at : "+accessType);
					String qosstring=line.substring(line.indexOf("provisionedPermitOutputAttrValues"),line.indexOf("provisionedDenyOutputAttrValues"));
					String qoss[]=qosstring.split("QosProfile");
					JSONArray listqos = new JSONArray();
					for (int i = 1; i < qoss.length; i++) {
						String qos=qoss[i].split("\"")[1];
						System.out.println(qos.substring(0,qos.length()-1));
						//get qos id
						BasicDBObject fields = new BasicDBObject("_id",false);
						BasicDBObject whereQuery = new BasicDBObject();
						whereQuery.put("id", qos.substring(0,qos.length()-1));
						DBCursor cursor = collqos.find(whereQuery,fields);
						while(cursor.hasNext()) {
							String jsonstr=cursor.next().toString();
							System.out.println(jsonstr);
							DBObject dbObject = (DBObject) JSON.parse(jsonstr);
							listqos.add(dbObject);
						}
					}
					try{
						DBObject listItem = new BasicDBObject("id", id).append("formula",formula).append("accessType", accessType).append("qos", listqos);
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
