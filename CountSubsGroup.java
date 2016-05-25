import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.json.simple.JSONArray;


public class CountSubsGroup {

	/**
	 * @param args
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{
			BufferedReader br0 = new BufferedReader(new FileReader(args[0]));
			String line="";
			ArrayList<String> obj = new ArrayList<String>();
			//HashMap<String, JSONArray> hmap = new HashMap<String, JSONArray>();
			HashMap<String, Integer> hmap = new HashMap<String, Integer>();
			while ((line = br0.readLine()) != null) {
				//JSONArray l3acc = new JSONArray();
				obj.add(line.trim());		
				hmap.put(line.trim(), 0);
			}
			br0.close();
			
			BufferedReader br = new BufferedReader(new FileReader(args[1]+".sub"));
			while ((line = br.readLine()) != null) {
				String msid = line.substring(line.indexOf("EPC_SubscriberPot"),line.indexOf("groups")-2);
				msid = msid.substring(msid.indexOf("\"")+1);
				//System.out.println(msid);
				String grps = line.substring(line.indexOf("groups"),line.indexOf("services"));
				//System.out.println(grps);
				String[] grp =grps.split("\"");
				for (int i = 0; i < grp.length/2; i++) {
					String[] arr=grp[2*i+1].split(":");
					String grpid=arr[0];
					//System.out.println(msid+":"+grpid);
					if(obj.contains(grpid)){
						//process
						//System.out.println(msid+":"+grpid);
						int temp = hmap.get(grpid);
						hmap.put(grpid, temp+1);
					}
				}
			}
			br.close();
		     Set<Entry<String, Integer>> set = hmap.entrySet();
		      Iterator<Entry<String, Integer>> iterator = set.iterator();
		      while(iterator.hasNext()) {
		         Map.Entry mentry = (Map.Entry)iterator.next();
		         System.out.print(mentry.getKey() + ",");
		         System.out.println(mentry.getValue());
		      }
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
