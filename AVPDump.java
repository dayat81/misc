import java.io.BufferedReader;
import java.io.FileReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;




public class AVPDump {

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

			String line="";
			BufferedReader br = new BufferedReader(new FileReader(args[0]+".qos"));
			while ((line = br.readLine()) != null) {
				int index=0;
				if(line.contains("(\"RS\")")){
					index=1;
				}
				
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
					String key=convertHexToString(avphex);
					System.out.println("ori:"+avphex);
					System.out.println("dec:"+key);
				}
			}
			br.close();

		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
