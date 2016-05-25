import java.io.BufferedReader;
import java.io.FileReader;


public class AggPeer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{
			BufferedReader br = new BufferedReader(new FileReader(args[0]));
			String line="";
			while ((line = br.readLine()) != null) {
				String[] fields= line.split(",");
				//System.out.println(fields[3]);
				String[] peer = fields[3].split("\\.");
				//System.out.println(peer.length);
				if(peer.length>4){
					System.out.println(fields[0]+","+fields[1]+","+fields[2]+","+peer[peer.length-4]+"."+peer[peer.length-3]+"."+peer[peer.length-2]+"."+peer[peer.length-1]+","+fields[4]+","+fields[5]);
				}else if(peer.length==3){
					String[] pgw=peer[peer.length-3].split("-");					
					System.out.println(fields[0]+","+fields[1]+","+fields[2]+","+pgw[pgw.length-1]+"."+peer[peer.length-2]+"."+peer[peer.length-1]+","+fields[4]+","+fields[5]);
				}else{
					System.out.println(line);
				}
			}
			br.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
