import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;


public class InsertLimitGrp {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
			DB db = mongoClient.getDB( "sapc" );
			String host = args[1];
			DBCollection coll = db.getCollection(host+"_limgrp");
			coll.drop();
			coll.createIndex(new BasicDBObject("idx", 1));

//			for (Object str : ids) {
//			    //do something
//				System.out.println(str.toString());
//			}
			BufferedReader br = new BufferedReader(new FileReader(args[0]));
			String line="";
			String idx="";
			String lim="";
			while ((line = br.readLine()) != null) {
				try{
				idx  = line.substring(line.indexOf("mdp:U("),line.indexOf("]logicalDbNo"));
				lim  = line.substring(line.indexOf("usageControlLimits")).replaceAll("\\\\", "");
				//System.out.println("lim "+lim);
				if(lim.contains("reportingGroups")){
				lim = "{\"reportingGroups\""+lim.substring(lim.indexOf(":["),lim.length()-5);
//				System.out.println(idx);
//				System.out.println(lim);

						//System.out.println("mengandung "+lim);
						try{
						DBObject dbObject = (DBObject) JSON.parse(lim);
						DBObject listItem = new BasicDBObject("idx", idx).append("lim", dbObject);
						coll.insert(listItem);
						}catch(Exception e){
							System.out.println(idx);
							System.out.println(line);
							e.printStackTrace();
						}

				}
				}catch(Exception e){
					System.out.println(idx);
					System.out.println(line);	
					System.out.println(lim);
					e.printStackTrace();
				}
				
			}
			br.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
