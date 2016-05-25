import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;




public class pgwtcat {

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
			
			OAM gg= new OAM("SSR","10.195.70.9","ericsson","ericsson123","PGWCBT06",-1);
			//OAM gg=new OAM("SSR","10.162.128.9","engagan","Linkin8859013","PGWTESTCBT",-1);
			gg.Connect();
			//Thread.sleep(1000);
			if(gg.getType().equals("SSR")){	
			System.out.println(gg.EnterConf());
			}
			int i=0;
			while(i<apns.size()){
				String apn = apns.get(i);
				int j=0;
				while(j<cmds.size()){
					String cmd = cmds.get(j);
					if(cmd.contains("{apn}")){
						cmd=cmd.replace("{apn}", apn);						
					}
					String res=gg.exe(cmd);
					System.out.println(res);
					if(cmd.contains("statistics")){
						//get info for specific category
						res=res.substring(res.indexOf("ccr-identifier: sapcbtr01Gx"), res.indexOf("ccr-identifier: sapccbt01Gx"));
						if(!res.contains("active-ipcan-sessions: 0")){
							j=j-2;
							Thread.sleep(10000);
						}
					}
					j++;
				}
				i++;
			}
//			//System.out.println(gg.exe("ManagedElement=1,Epg=1,Pgw=1"));						
//			for (Iterator<String> iterator = apns.iterator(); iterator.hasNext();) {
//				String string = (String) iterator.next();
//				for (Iterator<String> iterator2 = cmds.iterator(); iterator2.hasNext();) {
//					String string2 = (String) iterator2.next();
//					if(string2.contains("{apn}")){
//						string2=string2.replace("{apn}", string);						
//					}
//					System.out.println(gg.exe(string2));	
//				}
//				//System.out.println(gg.exe("validate"));
//				//System.out.println(gg.exe("commit"));
//			}
			
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
