import java.io.*;
import java.util.*;

public class RecordParser {
    public static void main(String[] args) throws Exception {
    	
    	String KNOWN_VARIANTS_FILE = "/home/tcs/public_html/COMP555/VCFdata/ALL.merged.phase1_release_v3.20101123.snps_indels_svs.vcf";
    	String QUERY_VARIANTS_FILE = "filteredResults2.vcf";
    
       	VCFScanner knownVariantsVCFScanner = new VCFScanner(KNOWN_VARIANTS_FILE, false);
        VCFScanner queryVariantVCFScanner = new VCFScanner(QUERY_VARIANTS_FILE, false);

        String knownVariantLine;

        int lineCount = 0;

        //Chromosome and position
        String[] querySplits = queryVariantVCFScanner.scanner.nextLine().split("\\s+");
        int resultChr = Integer.parseInt(querySplits[1]);
        int resultPos = Integer.parseInt(querySplits[2]);

        int chrIndex = knownVariantsVCFScanner.getHeaderIndex("CHROM");
        int posIndex = knownVariantsVCFScanner.getHeaderIndex("POS");

        while (knownVariantsVCFScanner.scanner.hasNextLine()) {
            knownVariantLine = knownVariantsVCFScanner.scanner.nextLine();

            String[] splits = knownVariantLine.split("\\s+");

            //TODO: X chromosome?
            int chr = Integer.parseInt(splits[chrIndex]);
            int pos = Integer.parseInt(splits[posIndex]);

            while (resultChr < chr || (resultChr <= chr && resultPos < pos)) {
                if (queryVariantVCFScanner.scanner.hasNextLine()) {
                    querySplits = queryVariantVCFScanner.scanner.nextLine().split("\\s+");
                    resultChr = Integer.parseInt(querySplits[1]);
                    resultPos = Integer.parseInt(querySplits[2]);
                    //System.out.println("ALL.merged.Record: chr=" + chr + " pos=" + pos + " <=====>" + " filteredResults.txt: chr=" + resultChrPos[0] + " pos=" + resultChrPos[1]);
                }
                else {
                    return;
                }
                /*
                else if (seen2 && seen3) {
                    return;
                } else {
                    //Known variants file is not numerically ordered: Reset queryVariant scanner after 19->2, 22->3 transitions
                    if ((chr == 2 && !seen2)) {
                        seen2 = true;
                        System.err.println("\nRESETTING FROM 2\n");
                        queryVariantVCFScanner = new VCFScanner(QUERY_VARIANTS_FILE, false);
                        String[] splits2 = queryVariantVCFScanner.scanner.nextLine().split("\\s+");
                        resultChrPos[0] = Integer.parseInt(splits2[1]);
                        resultChrPos[1] = Integer.parseInt(splits2[2]);
                        resultChrPos = resultChrPos;
                    } else if (chr == 3 && !seen3) {
                        seen3 = true;
                        System.err.println("\nRESETTING FROM 3\n");
                        queryVariantVCFScanner = new VCFScanner(QUERY_VARIANTS_FILE, false);
                        String[] splits2 = queryVariantVCFScanner.scanner.nextLine().split("\\s+");
                        resultChrPos[0] = Integer.parseInt(splits2[1]);
                        resultChrPos[1] = Integer.parseInt(splits2[2]);
                        resultChrPos = resultChrPos;
                    }
                }*/
            }
            //System.out.println(lineCount + " ALL.merged.Record: chr=" + chr + " pos=" + pos + " <=====>" + " filteredResults.txt: chr=" + resultChrPos[0] + " pos=" + resultChrPos[1]);

            if (resultChr == chr && resultPos == pos) {
                System.out.println(knownVariantLine);
            }

            if ((lineCount++ % 100000) == 100000-1) {
                System.err.println(lineCount + " ALL.merged.Record: chr=" + chr + " pos=" + pos + " <=====>" + " filteredResults.txt: chr=" + resultChr + " pos=" + resultPos);
            }
        }

    }


}
