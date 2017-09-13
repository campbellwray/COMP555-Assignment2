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
    	
    	ArrayList<ExonRegion> exonRegions = ExonParser.getExonRegions(args[1]);
    	
    	
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
		        
		        String chromosome = "chr"+splitLines[chromIndex];
		        int position = Integer.parseInt(splitLines[posIndex]);
		        
		        String[] familyMembers = new String[(son2Index-fatIndex)+1];
		        //System.out.println(son2Index-fatIndex);
		        //System.out.println(theLine);
		        for (int i=fatIndex; i<=son2Index; i++) {
		        	familyMembers[i-fatIndex] = splitLines[i];
		        }
		        
		        //System.out.println(chromosome + " , " + position);
		        /*for (String familyMember : familyMembers) {
		        	String genotype = familyMember.split(":")[0];
		        	//System.out.print(genotype + "\t");
		        	
		        }*/
		       
				for (ExonRegion theExonRegion : exonRegions) {
					if (theExonRegion.chromosome.equals(chromosome)) {
						for (Range theRange : theExonRegion.ranges) {
							//TODO: Early stop?
							if (position > theRange.start && position < theRange.end) {
								checkSickle(familyMembers, theLine);
								checkRetin(familyMembers, theLine);
								checkSkele(familyMembers, theLine);
								checkParap(familyMembers, theLine);
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
    
    static boolean genotypeHetero(String genotype){
		if (genotype.equals("0/1") || 
			genotype.equals("1/0") || 
			genotype.equals("1|0") || 
			genotype.equals("0|1")){
			return true;
		}
		else {
			return false;
		}
    }
    static boolean genotypeHomoRec(String genotype){
		if (genotype.equals("1/1")||genotype.equals("1|1")){
			return true;
		}
		else {
			return false;
		}
    }
    static boolean genotypeHomoDom(String genotype){
		if (genotype.equals("0/0")||genotype.equals("0|0")){
			return true;
		}
		else {
			return false;
		}
    }
    
    static void checkSickle (String [] familyMembers, String theLine){
    if (genotypeHetero(familyMembers[0].split(":")[0]) && //fATHER
    	genotypeHetero(familyMembers[1].split(":")[0]) &&	//Mother
    	genotypeHetero(familyMembers[2].split(":")[0]) &&	//D1
    	genotypeHomoDom(familyMembers[3].split(":")[0]) && //D2
    	genotypeHomoRec(familyMembers[4].split(":")[0]) && //D3
    	genotypeHetero(familyMembers[5].split(":")[0]) && //S1
    	genotypeHomoDom(familyMembers[6].split(":")[0]) //S2
    	){
			for (String familyMember : familyMembers) {
				String genotype = familyMember.split(":")[0];
				System.out.print(genotype + "\t");
			}
			System.out.println("(Sickle)--->\t" + theLine);
		}
    }
    static void checkRetin (String [] familyMembers, String theLine){
    if (genotypeHomoRec(familyMembers[0].split(":")[0]) && //fATHER
    	genotypeHetero(familyMembers[1].split(":")[0]) &&	//Mother
    	genotypeHetero(familyMembers[2].split(":")[0]) &&	//D1
    	genotypeHomoRec(familyMembers[3].split(":")[0]) && //D2
    	genotypeHetero(familyMembers[4].split(":")[0]) && //D3
    	genotypeHetero(familyMembers[5].split(":")[0]) && //S1
    	genotypeHomoRec(familyMembers[6].split(":")[0]) //S2
    	){
			for (String familyMember : familyMembers) {
				String genotype = familyMember.split(":")[0];
				System.out.print(genotype + "\t");
			}
			System.out.println("(Retin)--->\t" + theLine);
		}
    }
    
    static void checkSkele (String [] familyMembers, String theLine){
    if (genotypeHetero(familyMembers[0].split(":")[0]) && //fATHER
    	genotypeHetero(familyMembers[1].split(":")[0]) &&	//Mother
    	(genotypeHetero(familyMembers[2].split(":")[0]) || genotypeHomoDom(familyMembers[2].split(":")[0])) &&	//D1
    	genotypeHomoRec(familyMembers[3].split(":")[0]) && //D2
    	(genotypeHetero(familyMembers[4].split(":")[0]) || genotypeHomoDom(familyMembers[4].split(":")[0])) && //D3
    	genotypeHomoRec(familyMembers[5].split(":")[0]) && //S1
    	(genotypeHetero(familyMembers[6].split(":")[0]) || genotypeHomoDom(familyMembers[6].split(":")[0])) //S2
    	){
			for (String familyMember : familyMembers) {
				String genotype = familyMember.split(":")[0];
				System.out.print(genotype + "\t");
			}
			System.out.println("(Skele)--->\t" + theLine);
		}
    }
	static void checkParap (String [] familyMembers, String theLine){
    if (genotypeHomoRec(familyMembers[0].split(":")[0]) && //fATHER
    	genotypeHetero(familyMembers[1].split(":")[0]) &&	//Mother
    	genotypeHomoRec(familyMembers[2].split(":")[0]) &&	//D1
    	genotypeHetero(familyMembers[3].split(":")[0]) && //D2
    	genotypeHomoRec(familyMembers[4].split(":")[0]) && //D3
    	genotypeHetero(familyMembers[5].split(":")[0]) && //S1
    	genotypeHomoRec(familyMembers[6].split(":")[0]) //S2
    	){
			for (String familyMember : familyMembers) {
				String genotype = familyMember.split(":")[0];
				System.out.print(genotype + "\t");
			}
			System.out.println("(Parap)--->\t" + theLine);
		}
    }
}
