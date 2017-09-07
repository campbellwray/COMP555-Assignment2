import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.ArrayList;

class VCFRecord {
	String theRecord;
	String[] header;
	
	
}

class ReadVCF {
    public static void main(String[] args) {
    	if (args.length != 2) {
    		System.err.println("Example usage: java ReadVCF <filename> <exons>");
            return;
    	}
    	
    	//VCFData/wgEncodeGencodeBasicV17.txt"
    	
    	//ArrayList<ExonRegion> exonRegions = ExonParser.getExonRegions(args[1]);
    	
    	
        try {
		    Scanner theScanner = new Scanner(new File(args[0]));
		    String theLine = theScanner.nextLine();
		    String[] splitLines;
		    //Ignore metadata
		    while (theLine.matches("^##.*")) {
		    	theLine = theScanner.nextLine();
		    }
		    //Remove #
		    System.out.println(theLine);
		    theLine = theLine.substring(1);
		    String[] header = theLine.split("\\s+");
		    
		    int chromIndex = getHeaderIndex("CHROM", header);
		    int posIndex = getHeaderIndex("POS", header);
		    int fatIndex = getHeaderIndex("FATHER", header);
		    int son2Index = getHeaderIndex("SON2", header);
		    
		    //The Records
		    while (theScanner.hasNext()) {
		        theLine = theScanner.nextLine();
		        splitLines = theLine.split("\\s+");
		        
		        //String chromosome = "chr"+splitLines[chromIndex];
		        //int position = Integer.parseInt(splitLines[posIndex]);
		        
		        String[] familyMembers = new String[(son2Index-fatIndex)+1];
		        //System.out.println(son2Index-fatIndex);
		        
		        for (int i=fatIndex; i<=son2Index; i++) {
		        	familyMembers[i-fatIndex] = splitLines[i];
		        }
		        
		        //System.out.println(chromosome + " , " + position);
		        
		        for (String familyMember : familyMembers) {
		        	System.out.print(familyMember.split(":")[0] + "\t");
		        }
		        System.out.println();
		        
		        
		        
		        
		        /*
		        for (ExonRegion theExonRegion : exonRegions) {
		        	if (theExonRegion.chromosome.equals(chromosome)) {
				    	for (Range theRange : theExonRegion.ranges) {
				    		//TODO: Early stop?
				    		if (position > theRange.start && position < theRange.end) {
				    			System.out.println(theLine);
				    			break;
			    			}
				    	}
		        	}
		        }*/
		        
		    }
        }
        catch (IOException iox) {
        	System.err.println("Could not read from input: " + iox.getMessage());
        }
        
    }
    
    static int getHeaderIndex(String search, String[] header) {
    	for (int i=0; i<header.length; i++) {
    		if (header[i].equals(search)) {
    			return i;
    		}
    	}
    	return -1;
    }

}
