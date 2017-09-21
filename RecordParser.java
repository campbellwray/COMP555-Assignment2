public class RecordParser {
    public static void main(String[] args) throws Exception {
    	String KNOWN_VARIANTS_FILE = "/home/tcs/public_html/COMP555/VCFdata/ALL.merged.phase1_release_v3.20101123.snps_indels_svs.vcf";
    	String QUERY_VARIANTS_FILE = "filteredResults3.vcf";
    
       	VCFScanner knownVariantsVCFScanner = new VCFScanner(KNOWN_VARIANTS_FILE, false);
        VCFScanner queryVariantVCFScanner = new VCFScanner(QUERY_VARIANTS_FILE, false);

        //Print new header
        String[] currentHeaderTokens = knownVariantsVCFScanner.header;
        System.out.print("#DISEASE\tPROPORTION\tALTMATCH\t");
        for (String token : currentHeaderTokens) {
            System.out.print(token + "\t");
        }
        System.out.println();

        String knownVariantLine;

        int lineCount = 0;

        //Chromosome and position
        int queryChrIndex = queryVariantVCFScanner.getHeaderIndex("CHROM");
        int queryPosIndex = queryVariantVCFScanner.getHeaderIndex("POS");
        int queryDiseaseIndex = queryVariantVCFScanner.getHeaderIndex("DISEASE");
        int queryAltIndex = queryVariantVCFScanner.getHeaderIndex("ALT");

        String[] querySplits = queryVariantVCFScanner.scanner.nextLine().split("\\s+");
        int queryChr = Integer.parseInt(querySplits[queryChrIndex]);
        int queryPos = Integer.parseInt(querySplits[queryPosIndex]);

        int knownChrIndex = knownVariantsVCFScanner.getHeaderIndex("CHROM");
        int knownPosIndex = knownVariantsVCFScanner.getHeaderIndex("POS");
        int knownAltIndex = knownVariantsVCFScanner.getHeaderIndex("ALT");
        int knownInfoIndex = knownVariantsVCFScanner.getHeaderIndex("INFO");

        while (knownVariantsVCFScanner.scanner.hasNextLine()) {
            knownVariantLine = knownVariantsVCFScanner.scanner.nextLine();

            String[] knownSplits = knownVariantLine.split("\\s+");

            //TODO: X chromosome?
            int knownChr = Integer.parseInt(knownSplits[knownChrIndex]);
            int knownPos = Integer.parseInt(knownSplits[knownPosIndex]);

            while (queryChr < knownChr || (queryChr <= knownChr && queryPos < knownPos)) {
                if (queryVariantVCFScanner.scanner.hasNextLine()) {
                    querySplits = queryVariantVCFScanner.scanner.nextLine().split("\\s+");
                    queryChr = Integer.parseInt(querySplits[queryChrIndex]);
                    queryPos = Integer.parseInt(querySplits[queryPosIndex]);
                    //System.out.println("ALL.merged.Record: chr=" + chr + " pos=" + pos + " <=====>" + " filteredResults.txt: chr=" + resultChrPos[0] + " pos=" + resultChrPos[1]);
                }
                else {
                    return;
                }
            }
            //System.out.println(lineCount + " ALL.merged.Record: chr=" + chr + " pos=" + pos + " <=====>" + " filteredResults.txt: chr=" + resultChrPos[0] + " pos=" + resultChrPos[1]);

            //Print out query which possibly matches known
            if (queryChr == knownChr && queryPos == knownPos) {
                String disease = querySplits[queryDiseaseIndex];
                String queryAlt = querySplits[queryAltIndex];
                String knownAlt = knownSplits[knownAltIndex];
                String knownInfo = knownSplits[knownInfoIndex];
                String[] acAn = knownInfo.split(";");
                int totalAlleleCount = 0;
                int alternateAlleleCount = 0;
                for (String token : acAn) {
                    if (token.contains("AC")) {
                        alternateAlleleCount = Integer.parseInt(token.split("=")[1]);
                    }
                    if (token.contains("AN")) {
                        totalAlleleCount = Integer.parseInt(token.split("=")[1]);
                    }
                }

                double proportion = (double) alternateAlleleCount / totalAlleleCount;

                //System.out.println(disease + "\t" +  (queryAlt.equals(knownAlt)? "TRUE" : "FALSE") + "\t" + knownVariantLine);
                System.out.format("%s\t%.8f\t%s\t%s", disease, proportion, (queryAlt.equals(knownAlt)? "TRUE" : "FALSE"), knownVariantLine);

            }

            if ((lineCount++ % 100000) == 100000-1) {
                System.err.println(lineCount + " ALL.merged.Record: chr=" + knownChr + " pos=" + knownPos + " <=====>" + " filteredResults.txt: chr=" + queryChr + " pos=" + queryPos);
            }
        }

    }


}
