import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Iterator;

import org.bson.BSONObject;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;


public class XLLegacy {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{
			MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
			DB db = mongoClient.getDB( "sapc" );
			DBCollection collleg = db.getCollection("btr1_leg");
			collleg.drop();
			collleg.createIndex(new BasicDBObject("id", 1));	
			DBCollection coll = db.getCollection("btr1_subj");
			BufferedReader br = new BufferedReader(new FileReader(args[0]));
			String line="";
			while ((line = br.readLine()) != null) {
				System.out.println(line);
				BasicDBObject whereQuery = new BasicDBObject();
				whereQuery.put("id", line);
				DBCursor cursor = coll.find(whereQuery);
				while(cursor.hasNext()) {
					DBObject res = cursor.next();
					DBObject sbg = (DBObject) res.get("subscribergroup");
					if(sbg==null){
						break;
					}
					BasicDBList svc = (BasicDBList) sbg.get("services");
					JSONArray listsvcid = new JSONArray();
					for (Iterator<Object> iterator = svc.iterator(); iterator.hasNext();) {
						DBObject object = (DBObject) iterator.next();
						if(object!=null){
							String svcid=(String)object.get("id");
							listsvcid.add(svcid);
						}
					}
					DBObject reso = (DBObject) res.get("resource");
					String AccessType="2G,3G,4G";
					String TB="00-24";
					if(reso!=null){
						BasicDBList resopol = (BasicDBList) reso.get("policy");	
						String tempAT="";
						for (Iterator iterator = resopol.iterator(); iterator
								.hasNext();) {
							DBObject object = (DBObject) iterator.next();
							BasicDBList resorules = (BasicDBList) object.get("rules");	
							for (Iterator iterator2 = resorules.iterator(); iterator2
									.hasNext();) {
								DBObject object1 = (DBObject) iterator2.next();
								String AccType=(String) object1.get("accessType");
								String formula=(String) object1.get("formula");
								if(formula.contains("now.time")){
									String forms[] = formula.split("&&");
									for (int i = 0; i < forms.length; i++) {
										if(forms[i].contains(">")){
											String start = forms[i].split("\"")[1];
											TB=start.substring(0, start.length()-1);
										}else if(forms[i].contains("<")){
											String end=forms[i].split("\"")[1];
											TB=TB+"-"+end.substring(0,end.length()-1);
										}
									}
								}
								if(tempAT.equals("")||tempAT.equals("A")){
									tempAT=AccType;
								}
							}
						}					
						if(tempAT.equals("1")){
							AccessType="3G";	
						}else if(tempAT.equals("2")){
							AccessType="2G";	
						}else if(tempAT.equals("6")){
							AccessType="4G";	
						}
					}
					DBObject l3g =  new BasicDBObject("SID",listsvcid).append("AccessType", AccessType).append("TimeBand", TB);
					BasicDBList sbj = (BasicDBList) res.get("subjectresource");	
					if(sbj==null){
						break;
					}
					String prio="-1";
					String qosprio="-1";
					String np="-1",dl="",ul="",QoS_AccessType="";
					JSONArray l4qos=null;
					for (Iterator<Object> iterator = sbj.iterator(); iterator.hasNext();) {
						DBObject object = (DBObject) iterator.next();
						String resource=(String)object.get("resource");
						String context=(String)object.get("context");
						if(resource.equals("_ServiceDomain_")&&context.equals("Access")){
							prio=(String)object.get("prio");
						}else if(resource.equals("_Bearer_")&&context.equals("QoS")){
							qosprio=(String)object.get("prio");
							//get polici
							BasicDBList policyqos = (BasicDBList)object.get("policy");
							for (Iterator<Object> iterator2 = policyqos.iterator(); iterator2
									.hasNext();) {
								DBObject object2 = (DBObject) iterator2.next();
								BasicDBList ruleqos = (BasicDBList)object2.get("rules");
								l4qos = new JSONArray();
								for (Iterator<Object> iterator3 = ruleqos.iterator(); iterator3
										.hasNext();) {
									DBObject object3 = (DBObject) iterator3.next();
									BasicDBList qoss = (BasicDBList)object3.get("qos");
									for (Iterator<Object> iterator4 = qoss.iterator(); iterator4
											.hasNext();) {
										DBObject object4 = (DBObject) iterator4
												.next();
										np = (String)object4.get("np");
										dl = (String)object4.get("mbrdl");		
										ul = (String)object4.get("mbrul");		
										break;
									}
									String at = (String)object3.get("accessType");
									if(at.equals("A")){
										QoS_AccessType="2G,3G,4G";										
									}else if(at.equals("1")){
										QoS_AccessType="3G";	
									}else if(at.equals("2")){
										QoS_AccessType="2G";	
									}else if(at.equals("6")){
										QoS_AccessType="4G";	
									}
									System.out.println("at : "+QoS_AccessType);
									DBObject l4 = new BasicDBObject("QoS_DL",dl).append("QoS_UL", ul).append("QoS_AccessType", QoS_AccessType);
									l4qos.add(l4);
								}
								break;
							}
						}
					}
					DBObject l3n = new BasicDBObject("QoSPriority",qosprio).append("NetworkPriority", np);
					DBObject l2 = new BasicDBObject("Service Id SOAR",sbg.get("id")).append("Product Name", sbg.get("desc")).append("ProductPriority", prio);
					DBObject l1 = new BasicDBObject("Type","LegacyXL").append("Brand", "XL").append("Billing Plan", "Volume").append("IsVariantID", "NO").append("VariantID",sbg.get("id")).append("Variant Name", sbg.get("desc"));
					try{
						DBObject listItem = new BasicDBObject("id",line).append("L1", l1).append("L2", l2).append("L3-General", l3g).append("L3-Network", l3n).append("L4", l4qos);
						collleg.insert(listItem);
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
