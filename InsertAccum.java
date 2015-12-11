import java.io.BufferedReader;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;


public class InsertAccum {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
			String host = args[2];
			DB db = mongoClient.getDB( "sapc" );
			DBCollection coll = db.getCollection(host+"_acc");
			coll.drop();
			coll.createIndex(new BasicDBObject("msid", 1));
			DBCollection var = db.getCollection("variant");						
			List ids=var.distinct("bucketid");
			BufferedReader br = new BufferedReader(new FileReader(args[0]));
			Date last= new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(args[1]);
			String line="";
			while ((line = br.readLine()) != null) {
				String msid = line.substring(line.indexOf("subscriberId"),line.indexOf("usageControlAccum")-2);
				msid = msid.substring(msid.indexOf("\"")+1);
				//System.out.println(msid);
				String acc  = line.substring(line.indexOf("usageControlAccum")).replaceAll("\\\\", "");
				if(acc.contains("reportingGroups")){
				acc = "{\"reportingGroups\""+acc.substring(acc.indexOf(":["),acc.length()-6);
				if(!acc.substring(acc.length()-1).equals("}")){
					acc=acc+"}";
				}
				}
				//System.out.println(acc);
				boolean inserted=false;
				if(!acc.startsWith("usageControlAccum:S4(\"null\")")){
					//cek new framework
					for (Object str : ids) {
						if(acc.contains("\"name\":\""+str.toString()+"\"")){
							JSONParser jsonParser = new JSONParser();
							try{
							JSONObject accObject = (JSONObject) jsonParser.parse(acc);
							
							JSONArray lang= (JSONArray) accObject.get("reportingGroups");
							Iterator i1 = lang.iterator();	
														
							upperloop:
							while (i1.hasNext()) {
								JSONObject innerObj = (JSONObject) i1.next();
								JSONObject structure = (JSONObject) innerObj.get("absoluteAccumulated");
//								System.out.println(msid+","+innerObj.get("subscriberGroupName") + 
//										" " + innerObj.get("name"));	
								JSONArray counters= (JSONArray) structure.get("counters");
								//System.out.println("absoluteAccumulated counters: " + counters);
								if(counters!=null){
									Iterator j = counters.iterator();
									while (j.hasNext()) {
										JSONObject counter = (JSONObject) j.next();
										String mk = counter.get("name").toString();
										if(mk.equals(str)){
										//System.out.print(msid+";"+var1+";"+mk+","+counter.get("bidirVolume").toString()+";"+counter.get("expiryDate").toString());
										//usage=Long.valueOf(counter.get("bidirVolume").toString());
										//System.out.println(counter.get("expiryDate").toString());
											String exp =counter.get("expiryDate").toString();
											String[] dt = exp.split("\"");
											exp = dt[3];	
											exp=exp.replace("T", " ");
											//System.out.println(exp);
											Date lastp= new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(exp);
//											System.out.println(lastp);
//											System.out.println(last);
											if(!lastp.before(last)){
												try{
												DBObject dbObject = (DBObject) JSON.parse(acc);
												DBObject listItem = new BasicDBObject("msid", msid).append("acc", dbObject);
												coll.insert(listItem);
												inserted=true;
												}catch(Exception e){
													System.out.println(line);
													e.printStackTrace();
												}												
											}
										break upperloop;
										}
									}
								}									
							}	
						}catch(Exception e){
							e.printStackTrace();
						}
							break;
						}
					}

			}
			}
			br.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
