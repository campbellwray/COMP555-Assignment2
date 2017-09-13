import java.io.*;
import java.util.*;

public class RecordParser {
  public static void main(String[] args) throws Exception {
	
    //ArrayList<Record> recs = new ArrayList<Record>();
	//args[0] is ALL.merged
	//args[1] is filtered
    //boolean process = false;
    //Scanner recordScanner = new Scanner(new File("/home/tcs/public_html/COMP555/VCFdata/ALL.merged.phase1_release_v3.20101123.snps_indels_svs.vcf"));
		Scanner recordScanner = new Scanner(new File("../filteredResults2.txt"));
		Scanner resultScanner = new Scanner(new File("../filteredResults.txt"));
	
	//ArrayList<Integer[]> present = new ArrayList<Integer[]>();
	
	/*while(sc2.hasNextLine()) {
		String line2 = sc2.nextLine();
		if(line2.contains("#")) {
      	continue;
      }
		String[] splits2 = line2.split("\\s+");
		
		Integer[] temp = new Integer[2];
		temp[0] = Integer.parseInt(splits2[1]);
		temp[1] = Integer.parseInt(splits2[2]);
		
		present.add(temp);
	}	*/
		String line = recordScanner.nextLine();
		
		while(line.contains("#")) {
      	line = recordScanner.nextLine();
     }
	

		int lineC = 0;
		boolean seen2 = false;
		boolean seen3 = false;
		
		
		String result = resultScanner.nextLine();
		result = resultScanner.nextLine();
		
		String[] splits2 = result.split("\\s+");
		
		int resultChr = Integer.parseInt(splits2[1]);
		int resultPos = Integer.parseInt(splits2[2]);
    while(recordScanner.hasNextLine()) {
    	
 
    	
		  	String[] splits = line.split("\\s+");
		    
		    int chr = Integer.parseInt(splits[0]);
				int pos = Integer.parseInt(splits[1]);
				
				
				if((chr == 2 && !seen2 )){
					seen2 = true;
					resultScanner = new Scanner(new File("../filteredResults.txt"));
					result = resultScanner.nextLine();
					result = resultScanner.nextLine();
					splits2 = result.split("\\s+");
						
						resultChr = Integer.parseInt(splits2[1]);
						resultPos = Integer.parseInt(splits2[2]);
				}
				else if (chr == 3 && !seen3){
					seen3 = true;
					resultScanner = new Scanner(new File("../filteredResults.txt"));
					result = resultScanner.nextLine();
					result = resultScanner.nextLine();
					splits2 = result.split("\\s+");
						
						resultChr = Integer.parseInt(splits2[1]);
						resultPos = Integer.parseInt(splits2[2]);
				}
		  	
		  	
		  	
		  	
		  	while (resultChr < chr || (resultChr <= chr && resultPos < pos)){
					if (resultScanner.hasNextLine()){
						result = resultScanner.nextLine();
						splits2 = result.split("\\s+");
						
						resultChr = Integer.parseInt(splits2[1]);
						resultPos = Integer.parseInt(splits2[2]);
						System.out.println("ALL.mereged.Record: chr=" + chr + " pos=" + pos + " <=====>" + " filteredResults.tx: chr=" + resultChr + " pos=" + resultPos);
			  		
					}
					else{
						return;
					}
				}
				System.out.println(lineC+ " ALL.mereged.Record: chr=" + chr + " pos=" + pos + " <=====>" + " filteredResults.tx: chr=" + resultChr + " pos=" + resultPos);
	    	
    		if (resultChr == chr && resultPos == pos){
			  	System.out.println(line);
			  }
    	
  			if((lineC++ % 100000) == 1){
	    		System.out.println(lineC+ " ALL.mereged.Record: chr=" + chr + " pos=" + pos + " <=====>" + " filteredResults.tx: chr=" + resultChr + " pos=" + resultPos);
	    	}
	    	
	    	
	    	line = recordScanner.nextLine();
	    	
  	  }
       
	    	   
	  /*for(Integer[] in : present) {
	  	if(in[0].equals(c1) && in[1].equals(pos1)) {
	  		System.out.println(line);
	  	}
	  }
	  
      
      }
      
      
      /*if(process) {
        String[] acan = splits[7].split(";");
        int ac = 0;
        int an = 0;
        for(int i = 0; i < 2; i++) {
          if(acan[0].contains("AC")) {
            ac = Integer.parseInt(acan[0].split("=")[1]);
            an = Integer.parseInt(acan[1].split("=")[1]);
          } else {
            ac = Integer.parseInt(acan[1].split("=")[1]);
            an = Integer.parseInt(acan[0].split("=")[1]);
          }
        }
        recs.add(new Record(Integer.parseInt(splits[0]), Integer.parseInt(splits[1]),
          splits[2], splits[3], splits[4], splits[5], splits[6], ac, an));
      }
      if(splits[0].contains("#CHROM")) {
        process = true;
      }
    }
    for(int i = 0; i < recs.size(); i++)
      System.out.println(recs.get(i).prettyString());*/
  }
}
