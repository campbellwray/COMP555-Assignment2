class LocusFinder {
    public static void main(String[] args) {
    	try {
            VCFScanner theVCFScanner = new VCFScanner(args[0], false);
            VCFScanner cytoBandScanner = new VCFScanner(args[1], false);
            
            
            String[] currentHeaderTokens = theVCFScanner.header;
            System.out.print("#LOCUS\t");
            for (String token : currentHeaderTokens) {
                System.out.print(token + "\t");
            }
            System.out.println();
            
            String recordLine;
            String[] recordSplit;
            String refRecordLine;
            String[] refRecordSplit;
            
            int chrIndex = theVCFScanner.getHeaderIndex("CHROM");
            int posIndex = theVCFScanner.getHeaderIndex("POS");
            int refChrIndex = cytoBandScanner.getHeaderIndex("CHROM");
            int refPosStartIndex = cytoBandScanner.getHeaderIndex("POSSTART");
            int refPosEndIndex = cytoBandScanner.getHeaderIndex("POSEND");
            int refLocusIndex = cytoBandScanner.getHeaderIndex("LOCUS");
            
            String chromosome;
            int position;
            String refLocus = "";
            String lastLocus = "";
            
            while (theVCFScanner.scanner.hasNext()) {
                recordLine = theVCFScanner.scanner.nextLine();
                recordSplit = recordLine.split("\\s+");

                chromosome = "chr" + recordSplit[chrIndex];
                position = Integer.parseInt(recordSplit[posIndex]);
                
                //System.out.println(recordLine);
                //int count = 0;
                
                cytoBandScanner = new VCFScanner(args[1], false);
                //boolean found = false;
                while (cytoBandScanner.scanner.hasNext()){
                	refRecordLine = cytoBandScanner.scanner.nextLine();
                	//count ++;
                	
                	
                	refRecordSplit = refRecordLine.split("\\s+");
                	
                	String refChromosome = refRecordSplit[refChrIndex];
                	int refPosStartPosition = Integer.parseInt(refRecordSplit[refPosStartIndex]);
                	int refPosEndPosition = Integer.parseInt(refRecordSplit[refPosEndIndex]);
                	refLocus = refRecordSplit[refLocusIndex];
                
                	if (refChromosome.equals(chromosome) && position > refPosStartPosition && position < refPosEndPosition){
                		lastLocus = refLocus;
                		//found = true;
                	}
                
                }
                System.out.println(lastLocus + "\t" + recordLine);
                /*if(!found){
                	System.out.println(lastLocus + "\t" + recordLine);
                }*/
                //System.out.println(count + "\t" + recordLine);
                
            }
            
        }
        catch(Exception e){
        
        }
    
    }
    
}
