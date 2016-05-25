import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;


public class QoSStat {
	private static Map<String, Integer> sortByComparator(Map<String, Integer> unsortMap) {

		// Convert Map to List
		List<Map.Entry<String, Integer>> list = 
			new LinkedList<Map.Entry<String, Integer>>(unsortMap.entrySet());

		// Sort list with comparator, to compare the Map values
		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> o1,
                                           Map.Entry<String, Integer> o2) {
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});

		// Convert sorted map back to a Map
		Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
		for (Iterator<Map.Entry<String, Integer>> it = list.iterator(); it.hasNext();) {
			Map.Entry<String, Integer> entry = it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}

	public static void printMap(Map<String, Integer> map) {
		for (Map.Entry<String, Integer> entry : map.entrySet()) {
			System.out.println(entry.getKey() 
                                      + " : " + entry.getValue());
		}
	}
	  private static String convertHexToString(String hex){

		  StringBuilder sb = new StringBuilder();
		  StringBuilder temp = new StringBuilder();
		  
		  //49204c6f7665204a617661 split into two characters 49, 20, 4c...
		  for( int i=0; i<hex.length()-1; i+=2 ){
			  
		      //grab the hex in pairs
		      String output = hex.substring(i, (i + 2));
		      //convert hex to decimal
		      int decimal = Integer.parseInt(output, 16);
		      //convert the decimal to character
		      sb.append((char)decimal);
			  
		      temp.append(decimal);
		  }
		  //System.out.println("Decimal : " + temp.toString());
		  
		  return sb.toString();
	  }

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{
			MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
			DB db = mongoClient.getDB( "sapc" );
			DBCollection coll = db.getCollection(args[0]+"_sess");
			BasicDBObject fields = new BasicDBObject("_id",false).append("msid", true);
			Map<String, Integer> ulmap = new HashMap<String, Integer>();
			Map<String, Integer> dlmap = new HashMap<String, Integer>();
			Map<String, Integer> pgwmap = new HashMap<String, Integer>();
			//ArrayList<String> pgws = new ArrayList<String>();
			String line="";
			BufferedReader br = new BufferedReader(new FileReader(args[0]+".qos"));
			while ((line = br.readLine()) != null) {
				int index=0;
				if(line.contains("(\"RS\")")){
					index=1;
				}
				String ggsn="";
				if(line.contains("pdpSessionId")&&line.contains("bearerUsage")){
					String pdpid=line.substring(line.indexOf("pdpSessionId"),line.indexOf("bearerUsage"));
					String[] peer = pdpid.split(";")[0].split("\\.");
					if(peer.length>4){
						ggsn=peer[peer.length-4]+"."+peer[peer.length-3]+"."+peer[peer.length-2]+"."+peer[peer.length-1];
						//System.out.println(ggsn);
					}else if(peer.length==3){
						String[] pgw=peer[peer.length-3].split("-");	
						ggsn=pgw[pgw.length-1]+"."+peer[peer.length-2]+"."+peer[peer.length-1];
						//System.out.println(ggsn);
					}					
					pdpid=pdpid.split("\"")[1];
					if(!pdpid.equals("#Unassigned#")){
						System.out.println(pdpid);
						BasicDBObject whereQuery = new BasicDBObject();
						whereQuery.put("sessions.peer", pdpid);						
						DBCursor cursor = coll.find(whereQuery,fields);
						if(cursor.hasNext()){
							System.out.println(cursor.next().get("msid"));
						}
					}					
				}
				String keydl="";
				String keyul="";
				if(line.contains("ix"+index+":A")&&line.contains("ix"+(index+1)+":A")){
					String ul=line.substring(line.indexOf("ix"+index+":A"),line.indexOf("ix"+(index+1)+":A"));					
					System.out.print("ul:");
					String ulhex="";
					Pattern p = Pattern.compile("\\((.*?)\\)");
					Matcher m = p.matcher(ul);
					while(m.find())
					{
					   //System.out.println(m.group(1));
						//if(!m.group(1).equals("0")){
							String hex = Integer.toHexString(Integer.parseInt(m.group(1)));
							//System.out.print(hex.substring(1));
							ulhex+=hex;
						//}
					}
					//String 
					keyul=convertHexToString(ulhex);
					System.out.println(keyul);
					Object val =ulmap.get(keyul); 
					if(val==null){
						//init
						ulmap.put(keyul, 1);
					}else{
						//inc
						ulmap.put(keyul, Integer.parseInt(val.toString())+1);
					}

				}
				if(line.contains("ix"+(index+1)+":A")&&line.contains("ix"+(index+2)+":A")){
					System.out.print("dl:");
					String dlhex="";
					String dl=line.substring(line.indexOf("ix"+(index+1)+":A"),line.indexOf("ix"+(index+2)+":A"));
					Pattern p = Pattern.compile("\\((.*?)\\)");					
					Matcher m = p.matcher(dl);
					while(m.find())
					{
					   //System.out.println(m.group(1));
						//if(!m.group(1).equals("0")){
							String hex = Integer.toHexString(Integer.parseInt(m.group(1)));
							//System.out.print(hex.substring(1));
							dlhex+=hex;
						//}
					}	
					//String 
					keydl=convertHexToString(dlhex);
					System.out.println(keydl);
					Object val =dlmap.get(keydl); 
					if(val==null){
						//init
						dlmap.put(keydl, 1);
					}else{
						//inc
						dlmap.put(keydl, Integer.parseInt(val.toString())+1);
					}
				}
				if(!ggsn.equals("")){
					/*if(!pgws.contains(ggsn)){
						pgws.add(ggsn);	
					}*/
					Object val =pgwmap.get(ggsn+","+keyul+","+keydl); 
					if(val==null){
						//init
						pgwmap.put(ggsn+","+keyul+","+keydl, 1);
					}else{
						//inc
						pgwmap.put(ggsn+","+keyul+","+keydl, Integer.parseInt(val.toString())+1);
					}	
				}			
				/*
				if(line.contains("ix"+(index+24)+":A")&&line.contains("specificDataIndexes")){
					String avps=line.substring(line.indexOf("ix"+(index+24)+":A"),line.indexOf("specificDataIndexes"));
					Pattern p = Pattern.compile("\\((.*?)\\)");		
					String avphex="";
					Matcher m = p.matcher(avps);
					while(m.find())
					{
					   //System.out.println(m.group(1));
						//if(!m.group(1).equals("0")){
							String hex = Integer.toHexString(Integer.parseInt(m.group(1)));
							//System.out.print(hex.substring(1));
							avphex+=hex;
						//}
					}	
					//String key=convertHexToString(avphex);
					System.out.println(avphex);
				}*/
			}
			br.close();
			System.out.println("ul:");
			Map<String, Integer> sortedMap = sortByComparator(ulmap);
			printMap(sortedMap);
		      System.out.println("dl:");
		  	Map<String, Integer> sortedMapdl = sortByComparator(dlmap);
			printMap(sortedMapdl);
			System.out.println("pgw:");
			Map<String, Integer> sortedMappgw = sortByComparator(pgwmap);
			printMap(sortedMappgw);			
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
