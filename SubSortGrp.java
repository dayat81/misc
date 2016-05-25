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


public class SubSortGrp {
	   public static boolean isIntegerParseInt(String str) {
	        try {
	            Integer.parseInt(str);
	            return true;
	        } catch (NumberFormatException nfe) {}
	        return false;
	    }

	public static Map<String, Integer> sortByComparator(Map<String, Integer> unsortMap) {

		// Convert Map to List
		List<Map.Entry<String, Integer>> list = 
			new LinkedList<Map.Entry<String, Integer>>(unsortMap.entrySet());

		// Sort list with comparator, to compare the Map values
		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> o1,
                                           Map.Entry<String, Integer> o2) {
				return (o1.getValue()).compareTo(o2.getValue());
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
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{
			BufferedReader br0 = new BufferedReader(new FileReader(args[0]));
			String line="";
			HashMap<String, Integer> hmap = new HashMap<String, Integer>();
			while ((line = br0.readLine()) != null) {		
				String[] fields = line.split(",");
				hmap.put(fields[0].trim(), Integer.parseInt(fields[1].trim()));
			}
			br0.close();	
			BufferedReader br = new BufferedReader(new FileReader(args[1]));
			while ((line = br.readLine()) != null) {
				String msid = line.substring(line.indexOf("EPC_SubscriberPot"),line.indexOf("groups")-2);
				msid = msid.substring(msid.indexOf("\"")+1);
				System.out.println(msid);
				HashMap<String, Integer> hgrp = new HashMap<String, Integer>();
				HashMap<String, Integer> hidx = new HashMap<String, Integer>();
				HashMap<String, String> hbuff = new HashMap<String, String>();
				String grps = line.substring(line.indexOf("groups"),line.indexOf("services"));
				//System.out.println(grps);
				String[] grp =grps.split("\"");
				int idx=0;
				//String prev="";
				String ao="";
				for (int i = 0; i < grp.length/2; i++) {
					String[] arr=grp[2*i+1].split(":");
					if(arr.length==1){
						//pp
						System.out.println("pp:"+arr[0]);
					}else {
						String grpid=arr[0];
						if(grpid.contains("_EXT")){
							grpid=grpid.split("_")[0];
							//System.out.println("grpid"+grpid);							
						}	
						if(isIntegerParseInt(grpid)){
							ao=grpid;
//							if(!prev.equals(ao)){
//								idx=0;
//							}							
							Object prio = hmap.get(grpid);
							if(prio==null){
								hmap.put(grpid, 9000);
							}
							hgrp.put(grpid,hmap.get(grpid));
						}
						//buffer all groupid
						if(!grpid.equals("ROAMING")&&!ao.equals("")){
							Object id = hidx.get(ao);
							if(id==null){
								idx=0;
							}else{
								idx= hidx.get(ao);
							}
							if(arr.length==2){
								System.out.println("ao:"+arr[0]+":"+arr[1]);
								hbuff.put(ao+"_"+idx, arr[0]+":<prio>");
							}else if(arr.length<=5){
								System.out.println("ao:"+arr[0]+":"+arr[1]+":"+arr[2]+":"+arr[3]+":"+arr[4]);
								hbuff.put(ao+"_"+idx, arr[0]+":<prio>:"+arr[2]+":"+arr[3]+":"+arr[4]);
							}else{
								System.out.println("ao:"+arr[0]+":"+arr[1]+":"+arr[2]+":"+arr[3]+":"+arr[4]+":"+arr[5]+":"+arr[6]);
								hbuff.put(ao+"_"+idx, arr[0]+":<prio>:"+arr[2]+":"+arr[3]+":"+arr[4]+":"+arr[5]+":"+arr[6]);
							}	
							idx++;
							hidx.put(ao, idx);
						}
					}
					//String grpid=arr[0];
					//System.out.println(msid+":"+grpid+":"+arr.length);
				}	
				Map<String, Integer> sortedGrp = sortByComparator(hgrp);
				int p=2;
				for (Map.Entry<String, Integer> entry : sortedGrp.entrySet()) {
//					System.out.println(entry.getKey() 
//		                                      + " : "+p+":" + entry.getValue());					
					
					boolean empty =false;
					int init=0;
					while(!empty){
						Object item = hbuff.get(entry.getKey()+"_"+init);
						if(item==null){
							empty=true;
						}else{
							System.out.println(item.toString().replaceAll("<prio>", String.valueOf(p)));
							p++;
						}
						init++;
					}
				}
//				for (Entry<String, String> entry : hbuff.entrySet()) {
//					System.out.println(entry.getKey() 
//		                                      + " : " + entry.getValue());
//				}				
			}
			br.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
