import java.io.BufferedReader;
import java.io.FileReader;
import java.net.InetAddress;


public class genIP {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		InetAddress ip;
		try {
			BufferedReader br = new BufferedReader(new FileReader("c:/Users/ehidhid/ip.txt"));
			String line="";
			int i=1;
			System.out.println("\"string\";\"string\";");	
			while ((line = br.readLine()) != null) {
				ip = InetAddress.getByName(line);
				byte[] bytes = ip.getAddress();
				 StringBuilder sb = new StringBuilder();
			    for (byte b : bytes) {
			        sb.append(String.format("%02X", b));
			    }
			    System.out.println("\""+i+"\";\"0x"+sb.toString()+"\";");
			    i++;
			}	
			br.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
