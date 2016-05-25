import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;




public class pgw {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			System.out.println(new Date());
			List<String> apns= new ArrayList<String>();
			List<String> cmds= new ArrayList<String>();
			
			BufferedReader br = new BufferedReader(new FileReader(args[0]));
			String line="";
			while ((line = br.readLine()) != null) {				
				line=line.trim();
				apns.add(line.trim());	
			}
			br.close();	
			BufferedReader br1 = new BufferedReader(new FileReader(args[1]));
			String line1="";
			while ((line1 = br1.readLine()) != null) {
				line1=line1.trim();
				cmds.add(line1);
			}
			br1.close();
			
			//OAM gg= new OAM("SSR","10.195.70.9","ericsson","ericsson123","PGWCBT06",-1);
			//OAM gg=new OAM("SSR","10.162.128.9","engagan","Linkin8859013","PGWTESTCBT",-1);
			OAM gg= new OAM("SSR","10.195.24.6","engagan","Linkin8859013","PGWCBT04",-1);
			//OAM gg= new OAM("SSR","10.195.26.9","engagan","Linkin8859013","PGWCBT07",-1);
			gg.Connect();
			//Thread.sleep(1000);
			if(gg.getType().equals("SSR")){	
			System.out.println(gg.EnterConf());
			}
			//System.out.println(gg.exe("ManagedElement=1,Epg=1,Pgw=1"));						
			for (Iterator<String> iterator = apns.iterator(); iterator.hasNext();) {
				String string = (String) iterator.next();
				for (Iterator<String> iterator2 = cmds.iterator(); iterator2.hasNext();) {
					String string2 = (String) iterator2.next();
					if(string2.contains("{apn}")){
						string2=string2.replace("{apn}", string);						
					}
					String res=gg.exe(string2);
					if(string2.contains("show")&&!res.contains("msisdn")){
						System.out.println("NOMSID");
					}
					System.out.println(res);	
				}
				//System.out.println(gg.exe("validate"));
				//System.out.println(gg.exe("commit"));
			}
			
			if(gg.getType().equals("SSR")){	
			System.out.println(gg.LeaveConf());	
			}
			gg.Quit();
			System.out.println(new Date());
		}  catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
