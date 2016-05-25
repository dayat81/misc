import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


public class CMFProc {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{
			BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\ehidhid\\Documents\\trace\\applog.CMF.8"));
			String line="";
			HashMap<String, Integer> hmap = new HashMap<String, Integer>();
			while ((line = br.readLine()) != null) {
				if(line.contains("Modify")){
					String ip  = line.substring(line.indexOf("vc_ipAddress"),line.indexOf("vc_message"));
					//System.out.println(ip);
					Integer count=hmap.get(ip);
					if(count==null){
						hmap.put(ip, 1);
					}else{
						count=count+1;
						hmap.put(ip, count);
					}
				}
			}
			br.close();
		     Set<Entry<String, Integer>> set = hmap.entrySet();
		      Iterator<Entry<String, Integer>> iterator = set.iterator();
		      while(iterator.hasNext()) {
		         Map.Entry mentry = (Map.Entry)iterator.next();
		         System.out.print("key is: "+ mentry.getKey() + " & Value is: ");
		         System.out.println(mentry.getValue());
		      }			
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
