import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;



public class CheckSubsInv {
	   public static boolean isIntegerParseInt(String str) {
	        try {
	            Integer.parseInt(str);
	            return true;
	        } catch (NumberFormatException nfe) {}
	        return false;
	    }
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{
			BufferedReader br0 = new BufferedReader(new FileReader(args[0]));
			String line="";
			HashMap<String, String> payumap = new HashMap<String, String>();
			HashMap<String, String> qosmap = new HashMap<String, String>();
			while ((line = br0.readLine()) != null) {
				String[] fields=line.split(",");
				if(fields.length>1){
					payumap.put(fields[0],fields[1]);
				}
				if(fields.length>2){
					qosmap.put(fields[0],fields[2]);
				}
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
				String ao="";
				String next="";
				for (int i = 0; i < grp.length/2; i++) {
					String[] arr=grp[2*i+1].split(":");	
					if(!ao.equals("")){
						if(next.equals("")){
							Object obj =payumap.get(ao);
							if(obj!=null&&!obj.toString().equals(arr[0])){
								System.out.println(obj.toString()+"-invalid-"+arr[0]);
							}	
							next="qos";
						}else{
							Object obj =qosmap.get(ao);
							if(obj!=null&&!obj.toString().equals(arr[0])){
								System.out.println(obj.toString()+"-invalid-"+arr[0]);
							}	
							next="";
							ao="";
						}
					}
					if(arr.length>1&&!arr[0].equals("ROAMING")&&!arr[0].contains("_EXT")&&isIntegerParseInt(arr[0])){
						System.out.println(msid+";"+arr[0]);
						ao=arr[0];
					}else{
						
					}
				}
			}
			br.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
