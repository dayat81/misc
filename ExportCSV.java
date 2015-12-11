import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;


public class ExportCSV {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		MongoClient mongoClient;
		try {
			
			mongoClient = new MongoClient( "localhost" , 27017 );
			DB db = mongoClient.getDB( "sapc" );
			String host = args[1];
			//PrintWriter writer = new PrintWriter("/home/xl/ehidhid/BI/"+host+"archive.txt", "UTF-8");
			//Date last= new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(args[2]);
			DBCollection coll = db.getCollection(host+"_lim");
			DBCollection acc = db.getCollection(host+"_acc");
			DBCollection variant = db.getCollection("variant");
			
			List ids=variant.distinct("bucketid");
			List varids=variant.distinct("variantid");

//			DBCollection arc = db.getCollection("archive_"+args[3].split(":")[0]);

		    //String[] varid={"10","12"};
		    //String[] buckid={"10000","10900","10500","10501","12000","12900","12500","12501"};
		    HashMap<String, Integer> statusdef = new HashMap<String, Integer>();
		    HashMap<String, Long> accdef = new HashMap<String, Long>();
		    HashMap<String, Long> limdef = new HashMap<String, Long>();		    
//		    for (int i = 0; i < varid.length; i++) {
//		    	statusdef.put(varid[i], 0);
//		    }
		    for (Object str : varids) {
		    	statusdef.put(str.toString(), 0);
		    }
		    for (Object str : ids) {
		    	accdef.put(str.toString(), 0L);
		    	limdef.put(str.toString(), 0L);
		    }


		    
			BufferedReader br = new BufferedReader(new FileReader(args[0]));
			String line="";
			while ((line = br.readLine()) != null) {
				try{

				//HashMap<String, Integer> status = new HashMap<String, Integer>();
				//status = (HashMap<String, Integer>) statusdef.clone();
				HashMap<String, Long> accu = new HashMap<String, Long>();
				accu = (HashMap<String, Long>) accdef.clone();
				HashMap<String, Long> limit = new HashMap<String, Long>();
				limit = (HashMap<String, Long>) limdef.clone();
				
				HashMap<String, String> var = new HashMap<String, String>();
				
				HashMap<String, String> exps = new HashMap<String, String>();

				BasicDBObject whereQuery = new BasicDBObject();
				BasicDBObject accQuery = new BasicDBObject();
				//BasicDBObject varQuery = new BasicDBObject();
				String msid = line.substring(line.indexOf("EPC_SubscriberPot"),line.indexOf("groups")-2);
				msid = msid.substring(msid.indexOf("\"")+1);
				//System.out.println(msid);
				String grps = line.substring(line.indexOf("groups"),line.indexOf("services"));
				//System.out.println(grps);
				String[] grp =grps.split("\"");
				for (int i = 0; i < grp.length/2; i++) {
					String[] epcgrp= grp[2*i+1].split(":");
					String gp =epcgrp[0];
					//cek new framework
					for (Object str : varids) {
						if(str.toString().equals(gp)){							
							String st = "null";
							if(epcgrp.length>4){
								st=epcgrp[2]+":"+epcgrp[3]+":"+epcgrp[4];
							}
							var.put(gp, st);
							//int val = status.get(str.toString());
							//val++;
							//status.put(str.toString(), val);
							//statuschanged=true;
						}						
					}

				}

				accQuery.put("msid", msid);
				DBCursor cursoracc = acc.find(accQuery);
				JSONParser jsonParser = new JSONParser();
				//String accstr="";
				while(cursoracc.hasNext()) {
					String jsonstr=cursoracc.next().toString();
					//System.out.println(jsonstr);
					JSONObject accObject = (JSONObject) jsonParser.parse(jsonstr);
					JSONObject struct = (JSONObject) accObject.get("acc");
					JSONArray lang= (JSONArray) struct.get("reportingGroups");
					Iterator i1 = lang.iterator();	

					while (i1.hasNext()) {
						//boolean islegacy=false;
						JSONObject innerObj = (JSONObject) i1.next();
						JSONObject structure = (JSONObject) innerObj.get("absoluteAccumulated");
//						System.out.println(msid+","+innerObj.get("subscriberGroupName") + 
//								" " + innerObj.get("name"));	

						JSONArray counters= (JSONArray) structure.get("counters");
						//System.out.println("absoluteAccumulated counters: " + counters);
						if(counters!=null){
							Iterator j = counters.iterator();
							while (j.hasNext()) {
								JSONObject counter = (JSONObject) j.next();
								String mk = counter.get("name").toString();
								//System.out.println("iter mk "+mk);

								for (Object str : varids) {
									if(mk.startsWith(str.toString())){
										//accstr=struct.toString();
										//System.out.println("add mk "+mk);
										Long usage=Long.valueOf(counter.get("bidirVolume").toString());
										accu.put(mk, usage);
										String exp =counter.get("expiryDate").toString();
										String[] dt = exp.split("\"");
										exp = dt[3];
										exp=exp.replace("T", " ");
										exp=exp.substring(0,exp.length()-3);
										exps.put(mk, exp);
									}									
								}
								
							}
						}									
					}												
				}

				
				String idx  = line.substring(line.indexOf("pccSubscriberPotRef:M1[:G[mdp:U(")+26,line.indexOf("]notificationData")-1);

				if(idx.startsWith("mdp")){
					//System.out.println(idx);
					whereQuery.put("idx", idx);
					DBCursor cursor = coll.find(whereQuery);							
					while(cursor.hasNext()) {
						String jsonstr=cursor.next().toString();
						//System.out.println(jsonstr);
						JSONObject accObject = (JSONObject) jsonParser.parse(jsonstr);
						JSONObject struct = (JSONObject) accObject.get("lim");
						JSONArray lang= (JSONArray) struct.get("reportingGroups");
						Iterator i1 = lang.iterator();
						
						otherloop:
						while (i1.hasNext()) {
							JSONObject innerObj = (JSONObject) i1.next();
							JSONObject structure = (JSONObject) innerObj.get("absoluteLimits");
							//String expir = innerObj.get("subscriptionDate").toString();
//							System.out.println(msid+","+innerObj.get("subscriberGroupName") + 
//									" " + innerObj.get("name"));	
							JSONArray counters= (JSONArray) structure.get("conditionalLimits");
							//System.out.println("absoluteAccumulated counters: " + counters);
							if(counters!=null){
								Iterator j = counters.iterator();
								while (j.hasNext()) {
									JSONObject counter = (JSONObject) j.next();
									String mk = counter.get("name").toString();
									for (Object str : varids) {
										if(mk.startsWith(str.toString())){
											
											String limi = counter.get("bidirVolume").toString();
											limi=limi.substring(1,limi.length()-1);
											
											//String limallstr="";
											//limitall.put(mk, limallstr);
											
											String[] arrlim = limi.split(",");
											Long lim = Long.valueOf(arrlim[0]);
											limit.put(mk, lim);
											//status.put(mk, 1);
											
											//limitall.put(mk, limallstr);
											
											//statuschanged=true;
											String exp =counter.get("resetPeriod").toString();
											String[] dt = exp.split("\"");
											exp = dt[3];

										}
									}
								}
							}											
						}
					}					
				}

			    Iterator it = var.entrySet().iterator();
			    String subdt="";

			    while (it.hasNext()) {			    	
			        Map.Entry pairs = (Map.Entry)it.next();
	        
//			        System.out.println(pairs.getKey() + "=" + pairs.getValue()+" ");
			        for (Object str : ids) {
						if(str.toString().startsWith(pairs.getKey().toString())){

							//System.out.println(buckid[i]+" acc="+accu.get(buckid[i])+" lim="+limit.get(buckid[i])+" ");
							Long usage = accu.get(str.toString())/1048576;
							Long lim = limit.get(str.toString())/1024;
							Long rem = lim-usage;	
							if(rem<0){
								rem=0L;
							}	
					        subdt=pairs.getValue().toString();

							System.out.println(msid+";"+pairs.getKey()+";"+subdt+";"+str.toString()+";"+usage+";"+rem+";"+lim+";"+exps.get(str.toString()));

						}
			        }
			    
			    }

				}catch(Exception e){
					System.err.println("error line "+line);
					e.printStackTrace();
				}
			}
			br.close();
			coll.drop();
			acc.drop();
			//writer.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}

}
