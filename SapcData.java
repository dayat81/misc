import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SapcData extends DefaultHandler {

	static FileWriter fw;
	static FileWriter fw1;
	static FileWriter tpsfw;
	static FileWriter rarfw;
	static FileWriter raafw;
//	static String mtcond;
//	static String sourcecond;
	String content="[";
	String tpscontent="[";
	String rarcontent="[";
	String raacontent="[";
	int total=0;
	int tpstotal=0;
	int rartotal=0;
	int raatotal=0;
	String template="[";
	String tpstemplate="[";
	String rartemplate="[";
	String raatemplate="[";
       private String temp;
       private String source="";
       private String mts="";
       private String mt="";
       static String info="";
       static int acc=0;
       //static Date lastupdate; 

       static String ip;


       int r=0;

       //long currtime = System.currentTimeMillis()+7*60*60*1000;
       static LinkedHashMap<String, Integer> map = new LinkedHashMap<String, Integer>();
       static LinkedHashMap<String, Integer> tps = new LinkedHashMap<String, Integer>();
       static LinkedHashMap<String, Integer> rar = new LinkedHashMap<String, Integer>();
       static LinkedHashMap<String, Integer> raa = new LinkedHashMap<String, Integer>();
       static String fn="chartgx.json";
       static String fn1 ="charttps.json";
       static String fn2 ="chartrar.json";
       static String fn3 ="chartraa.json";
       static File f = new File(fn);
       static File tpsf = new File(fn1);
       static File rarf = new File(fn2);
       static File raaf = new File(fn3);
       /** The main method sets things up for parsing */
       public static void main(String[] args) throws IOException, SAXException,
                     ParserConfigurationException {
    	   File newFile = new File(args[0]);
   		  if(raaf.exists()){
  			  System.out.println(fn3+" existed");
  				FileInputStream input1 = new FileInputStream(fn3);

  				byte[] fileData1 = new byte[input1.available()];

  				input1.read(fileData1);
  				input1.close();

  				String temp= new String(fileData1, "UTF-8");
  				String[] res=temp.split("'");
  				for (int i = 0; i < res.length/2; i++) {

  				raa.put(res[(2*i)+1],0);
  			}
   		  }	    	   
  		  if(rarf.exists()){
 			  System.out.println(fn2+" existed");
 				FileInputStream input1 = new FileInputStream(fn2);

 				byte[] fileData1 = new byte[input1.available()];

 				input1.read(fileData1);
 				input1.close();

 				String temp= new String(fileData1, "UTF-8");
 				String[] res=temp.split("'");
 				for (int i = 0; i < res.length/2; i++) {

 				rar.put(res[(2*i)+1],0);
 			}
  		  }	   		  
 		  if(tpsf.exists()){
			  System.out.println(fn1+" existed");
				FileInputStream input1 = new FileInputStream(fn1);

				byte[] fileData1 = new byte[input1.available()];

				input1.read(fileData1);
				input1.close();

				String temp= new String(fileData1, "UTF-8");
				String[] res=temp.split("'");
				for (int i = 0; i < res.length/2; i++) {

				tps.put(res[(2*i)+1],0);
			}
 		  }				
    		  if(f.exists()){
    			  System.out.println(fn+" existed");
  				FileInputStream input1 = new FileInputStream(fn);

  				byte[] fileData1 = new byte[input1.available()];

  				input1.read(fileData1);
  				input1.close();

  				String temp= new String(fileData1, "UTF-8");
  				String[] res=temp.split("'");
  				for (int i = 0; i < res.length/2; i++) {
					map.put(res[(2*i)+1],0);
				}
    		  }   

   			try {

              
			      System.out.println("parsing ... ");			  	   		
            //Create a "parser factory" for creating SAX parsers
            SAXParserFactory spfac = SAXParserFactory.newInstance();

            //Now use the parser factory to create a SAXParser object
            SAXParser sp = spfac.newSAXParser();

            //Create an instance of this class; it defines all the handler methods
            SapcData handler = new SapcData();
            

            //Finally, tell the parser to parse the input and notify the handler
            InputStream inp = new FileInputStream(newFile);	

            sp.parse(inp, handler);
          info="";	              
            inp.close();
            //newFile.delete();   
//        	int content;
//			while ((content = inp.read()) != -1) {
//				// convert to char and display it
//				System.out.print((char) content);
//			}           
              
              System.out.println("done");
   			} catch (Exception e) {
   				// TODO Auto-generated catch block
   				e.printStackTrace();
   			}

       }


       /*
        * When the parser encounters plain text (not XML elements),
        * it calls(this method, which accumulates them in a string buffer
        */
       public void characters(char[] buffer, int start, int length) {
              temp += new String(buffer, start, length);
       }
      

       /*
        * Every time the parser encounters the beginning of a new element,
        * it calls this method, which resets the string buffer
        */ 
       public void startElement(String uri, String localName,
               String qName, Attributes attributes) throws SAXException {
        temp = "";
        if(qName.equals("mt")&&!mt.equals("")){
      	  //System.out.println(mts+" "+mt+" "+acc); 
      	  mt="";
      	  //acc=0;
        }else if(qName.equals("mdc")){
    		   System.out.println("start xml");
				try {
    			      String csv= "datagx.json";
    			      fw = new FileWriter(csv,false); //the true will append the new data
    			      tpsfw= new FileWriter("datatps.json",false); //the true will append the new data
    			      rarfw= new FileWriter("datarar.json",false); //the true will append the new data
    			      raafw= new FileWriter("dataraa.json",false); //the true will append the new data
    			      //fw.write("host,tgl,mt,meas,mts,val\n");

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}     		   
       }
 }

       /*
        * When the parser encounters the end of an element, it calls this method
        */
       public void endElement(String uri, String localName, String qName)
               throws SAXException {

	   if(qName.equals("moid")){
  	   source = temp;
  	   r=0;
	   }else if(qName.equals("md")){
			
 }else if(qName.equals("mdc")){
		   System.out.println(mts+" end of one xml");
		   
			try {
			    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			    
			    Date d = sdf.parse(mts.substring(0,4)+"-"+mts.substring(0,6).substring(4)+"-"+mts.substring(0,8).substring(6)+" "+mts.substring(0,10).substring(8)+":"+mts.substring(0,12).substring(10));

			    Calendar c = Calendar.getInstance();
			    c.setTime(d);
			    long currtime = c.getTimeInMillis()+7*60*60*1000;							
				Iterator<String> iter = map.keySet().iterator();
				
				while(iter.hasNext()) {
				    String key = iter.next();
				    Integer val = (Integer)map.get(key);
				    content+="["+currtime+","+val+"],";
				    template+="{name: '"+key+"',data: []},";
				}	
				Iterator<String> tpsiter = tps.keySet().iterator();
				while(tpsiter.hasNext()) {
				    String key = tpsiter.next();

				    Integer valtps = (Integer)tps.get(key);

				    tpscontent+="["+currtime+","+valtps/300+"],";
				    tpstemplate+="{name: '"+key+"',data: []},";
				}	
				Iterator<String> rariter = rar.keySet().iterator();
				while(rariter.hasNext()) {
				    String key = rariter.next();

				    Integer valrar = (Integer)rar.get(key);

				    rarcontent+="["+currtime+","+valrar+"],";
				    rartemplate+="{name: '"+key+"',data: []},";
				}		
				Iterator<String> raaiter = raa.keySet().iterator();
				while(raaiter.hasNext()) {
				    String key = raaiter.next();

				    Integer valraa = (Integer)raa.get(key);

				    raacontent+="["+currtime+","+valraa+"],";
				    raatemplate+="{name: '"+key+"',data: []},";
				}				
				template=template.substring(0,template.length()-1)+"]";
				tpstemplate=tpstemplate.substring(0,tpstemplate.length()-1)+"]";
				rartemplate=rartemplate.substring(0,rartemplate.length()-1)+"]";
				raatemplate=raatemplate.substring(0,raatemplate.length()-1)+"]";
				content=content.substring(0,content.length()-1)+"]";
				tpscontent=tpscontent.substring(0,tpscontent.length()-1)+"]";
				rarcontent=rarcontent.substring(0,rarcontent.length()-1)+"]";
				raacontent=raacontent.substring(0,raacontent.length()-1)+"]";
				
//				System.out.println(template);
//				System.out.println(content);
				if(!f.exists()){
					System.out.println("write gx template");	
				fw1 = new FileWriter(fn,false); //the true will append the new data
				fw1.write(template);
				fw1.close();
				}
				if(!tpsf.exists()){
					System.out.println("write tps template");	
				fw1 = new FileWriter(fn1,false); //the true will append the new data
				fw1.write(tpstemplate);
				fw1.close();
				}	
				if(!rarf.exists()){
					System.out.println("write rar template");	
				fw1 = new FileWriter(fn2,false); //the true will append the new data
				fw1.write(rartemplate);
				fw1.close();
				}			
				if(!raaf.exists()){
					System.out.println("write raa template");	
				fw1 = new FileWriter(fn3,false); //the true will append the new data
				fw1.write(raatemplate);
				fw1.close();
				}				
				fw.write(content);
				fw.close();
				tpsfw.write(tpscontent);
				tpsfw.close();
				rarfw.write(rarcontent);
				rarfw.close();
				raafw.write(raacontent);
				raafw.close();				
				
				info="";
//				toServer.close();
//				socket.close();					
				//fw.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}     		   
 }else if(qName.equals("mi")){
		   //System.out.println(mts+" "+mt+" "+acc);
             		   
		   //demo.createDataset(mts.substring(10,12)+","+mts.substring(8,10)+","+mts.substring(6,8)+","+mts.substring(4,6)+","+mts.substring(0,4)+","+mt+","+acc);
		   try {

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   
 }else if(qName.equals("mts")){
		   //System.out.println("time "+temp); 
		   mts=temp;
 }else if(qName.equals("mt")){
	   //System.out.println("mt "+temp); 
	   mt=temp;
}else if(qName.equals("r")){  


if(source.contains("_SYSTEM")){
	
 //System.out.println(mt+" -"+source+"- -"+temp+"-");
 int x = source.lastIndexOf("-")+1;
 int y =source.indexOf(",");
 char[] c = source.toCharArray();
 String peer="";//source.substring(x, y);
 for (int i = x; i < y; i++) {
	 //System.out.print(c[i]);
	 peer+=c[i];
}
 //System.out.println(peer);
 if(mt.equals("ActiveGxSessions")){
	 int t=0;
	 if( (map.get(peer))!=null){
		 t=map.get(peer);
		 t+=Integer.parseInt(temp);
		 map.put(peer, t);	 
	 }else{
	 map.put(peer, Integer.parseInt(temp));
	 }
	 if( (map.get("total"))!=null){
		 total=map.get("total");
		 total+=Integer.parseInt(temp);
		 map.put("total", total);	 
	 }else{
	 map.put("total", Integer.parseInt(temp));
	 } 
}else if(mt.equals("GxPdpModifies")||mt.equals("GxPdpActivates")||mt.equals("GxPdpTerminates")||mt.equals("GxPdpReauthRequests")){
	//System.out.println(mt+" -"+peer+"- -"+temp+"-");
	 int t=0;
	 if( (tps.get(peer))!=null){
		 t=tps.get(peer);
		 t+=Integer.parseInt(temp);
		 tps.put(peer, t);	
		 //System.out.println(peer+" "+t);
	 }else{
	 tps.put(peer, Integer.parseInt(temp));
	 }
	 if( (tps.get("total"))!=null){
		 tpstotal=tps.get("total");
		 tpstotal+=Integer.parseInt(temp);
		 tps.put("total", tpstotal);	
		 //System.out.println("tpstotal "+tpstotal);
	 }else{
	 tps.put("total", Integer.parseInt(temp));
	 } 	
}
 if(mt.equals("GxPdpReauthRequests")){
	//System.out.println(mt+" -"+peer+"- -"+temp+"-");
	 int t=0;
	 if( (rar.get(peer))!=null){
		 t=rar.get(peer);
		 t+=Integer.parseInt(temp);
		 rar.put(peer, t);	
		 //System.out.println(peer+" "+t);
	 }else{
	 rar.put(peer, Integer.parseInt(temp));
	 }
	 if( (rar.get("total"))!=null){
		 rartotal=rar.get("total");
		 rartotal+=Integer.parseInt(temp);
		 rar.put("total", rartotal);	
		 //System.out.println("tpstotal "+tpstotal);
	 }else{
	 rar.put("total", Integer.parseInt(temp));
	 } 	
}else  if(mt.equals("GxPdpReauthResponses")){
	//System.out.println(mt+" -"+peer+"- -"+temp+"-");
	 int t=0;
	 if( (raa.get(peer))!=null){
		 t=raa.get(peer);
		 t+=Integer.parseInt(temp);
		 raa.put(peer, t);	
		 //System.out.println(peer+" "+t);
	 }else{
	 raa.put(peer, Integer.parseInt(temp));
	 }
	 if( (raa.get("total"))!=null){
		 raatotal=raa.get("total");
		 raatotal+=Integer.parseInt(temp);
		 raa.put("total", raatotal);	
		 //System.out.println("tpstotal "+tpstotal);
	 }else{
	 raa.put("total", Integer.parseInt(temp));
	 } 	
}
}


  	   source="";

	   }
 }
  	 
}

