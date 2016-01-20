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


public class XLSy {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{
			MongoClient mongoClient = new MongoClient( "localhost" , 27018 );
			DB db = mongoClient.getDB( "sapc" );
			DBCollection coll = db.getCollection("btr1_subj");
			BufferedReader br = new BufferedReader(new FileReader("C:/Users/ehidhid/sy.txt"));
			String line="";
			while ((line = br.readLine()) != null) {	
				System.out.println(line);
				BasicDBObject whereQuery = new BasicDBObject();
				whereQuery.put("id", line);
				DBCursor cursor = coll.find(whereQuery);
				while(cursor.hasNext()) {
					DBObject res = cursor.next();
					BasicDBList sbj = (BasicDBList) res.get("subjectresource");	
					if(sbj==null){
						break;
					}
					for (Iterator<Object> iterator = sbj.iterator(); iterator.hasNext();) {
						DBObject object = (DBObject) iterator.next();
						String resource=(String)object.get("resource");
						String context=(String)object.get("context");
//						
						BasicDBList policy = (BasicDBList)object.get("policy");
						for (Iterator<Object> iterator2 = policy.iterator(); iterator2
								.hasNext();) {
							DBObject object2 = (DBObject) iterator2.next();
							BasicDBList rule = (BasicDBList)object2.get("rules");
							for (Iterator<Object> iterator3 = rule.iterator(); iterator3
									.hasNext();) {
								DBObject object3 = (DBObject) iterator3.next();
								String formula = (String)object3.get("formula");
								if(formula.contains("SubsCharging.state")){
									System.out.println(formula);
									String exp="";
									char temp='i';
									int last=0;
									for (int i = 0; i < formula.length(); i++){
									    char c = formula.charAt(i);        
									    //Process char
									    if(temp=='i'){
									    	if(c=='&'){
									    		temp=c;
									    	}else if(c=='|'){
									    		temp=c;
									    	}
									    }else{
									    	if(c==temp){
									    		//cut
									    		exp=formula.substring(last, i-1).trim();
									    		//System.out.println(exp);
									    		if(exp.contains("AccessData.bearer.accessType")){
									    			String at=exp.split("\"")[1];
									    			System.out.println("at="+at.substring(0,at.length()-1));									    			
									    		}else if(exp.contains("SubsCharging.state")){
									    			String[] vals=exp.split("\"");
									    			System.out.println(vals[1].substring(0, vals[1].length()-1)+":"+vals[3].substring(0, vals[3].length()-1));
									    		}else if(exp.contains("now.time")){
									    			String[] tb=exp.split("\"");
									    			System.out.println("TB "+tb[1].substring(0, tb[1].length()-1));
									    		}
									    		last=i+1;
									    		temp='i';
									    	}
									    }
									}
									exp=formula.substring(last, formula.length()).trim();
									//System.out.println(exp);
						    		if(exp.contains("AccessData.bearer.accessType")){
						    			String at=exp.split("\"")[1];
						    			System.out.println("at="+at.substring(0,at.length()-1));									    			
						    		}else if(exp.contains("SubsCharging.state")){
						    			String[] vals=exp.split("\"");
						    			System.out.println(vals[1].substring(0, vals[1].length()-1)+":"+vals[3].substring(0, vals[3].length()-1));
						    		}else if(exp.contains("now.time")){
						    			String[] tb=exp.split("\"");
						    			System.out.println("TB "+tb[1].substring(0, tb[1].length()-1));
						    		}		
						    		System.out.println(resource+" "+context);
						    		if(resource.equals("_Bearer_")&&context.equals("QoS")){
						    			BasicDBList qos = (BasicDBList)object3.get("qos");
						    			for (Iterator<Object> iterator4 = qos
												.iterator(); iterator4
												.hasNext();) {
											DBObject object4 = (DBObject) iterator4
													.next();
											System.out.println(object4);
											break;
										}
						    		}
									System.out.println();
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
