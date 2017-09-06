import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;

class VCFRecord {
	String theRecord;
	String[] header;
	
	
}

class ReadVCF {
    public static void main(String[] args) {
    	if (args.length != 1) {
    		System.err.println("Example usage: java ReadVCF <filename>");
            return;
    	}
        try {
		    Scanner theScanner = new Scanner(new File(args[0]));
		    String theLine = theScanner.nextLine();
		    String[] splitLines;
		    //Ignore metadata
		    while (theLine.matches("^##")) {
		    	theLine = theScanner.nextLine();
		    }
		    //Remove #
		    System.out.println(theLine);
		    theLine = theLine.substring(1);
		    String[] header = theLine.split("\\s+");
		    
		    int chromIndex = getHeaderIndex("CHROM", header);
		    int posIndex = getHeaderIndex("POS", header);
		    
		    //The Records
		    while (theScanner.hasNext()) {
		        theLine = theScanner.nextLine();
		        splitLines = theLine.split("\\s+");
		        
		        String chromosome = "chr"+splitLines[chromIndex];
		        int position = Integer.parseInt(splitLines[posIndex]);
		        
		        //System.out.println(chromosome + " , " + position);
		        
		        for (ExonRegion theExonRegion : exonRegions) {
		        	if (theExonRegion.chromosome.equals(chromosome) {
				    	for (Range theRange : theExonRegion) {
				    		//TODO: Early stop?
				    		if (position > theRange.start && position < theRange.end) {
				    			System.out.println(theLine);
				    			break;
			    			}
				    	}
		        	}
		        }
		        
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
