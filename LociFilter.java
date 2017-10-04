import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Filters variants by known disease-loci combinations
 */
public class LociFilter {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java LociFilter <known Loci VCF file> <query variants VCF file>");
            return;
        }
        try {
            VCFScanner knownLociVCFScanner = new VCFScanner(args[0], false);
            VCFScanner queryVariantVCFScanner = new VCFScanner(args[1], true);
            //Chromosome and position
            int queryLocusIndex = queryVariantVCFScanner.getHeaderIndex("LOCUS");
            int queryDiseaseIndex = queryVariantVCFScanner.getHeaderIndex("DISEASE");

            int knownLocusIndex = knownLociVCFScanner.getHeaderIndex("LOCUS");
            int knownDiseaseIndex = knownLociVCFScanner.getHeaderIndex("DISEASE");
            int knownInheritanceIndex = knownLociVCFScanner.getHeaderIndex("INHERITANCE");

            //Put known loci into memory for faster processing
            List<String> storedLines = new ArrayList<>();
            while (knownLociVCFScanner.scanner.hasNextLine()) {
                storedLines.add(knownLociVCFScanner.scanner.nextLine());
            }

            //Filter each record by known disease-loci combination
            while (queryVariantVCFScanner.scanner.hasNextLine()) {
                String queryLocusLine = queryVariantVCFScanner.scanner.nextLine();
                String[] querySplits = queryLocusLine.split("\\s+");
                String queryLocus = querySplits[queryLocusIndex];
                String queryDisease = querySplits[queryDiseaseIndex];

                for (String theLine : storedLines) {
                    String[] knownSplits = theLine.split("\\s+");
                    String knownLocus = knownSplits[knownLocusIndex];
                    String knownDisease = knownSplits[knownDiseaseIndex];

                    //Filter by locus and disease
                    if (queryLocus.startsWith(knownLocus) && queryDisease.contains(knownDisease)) {
                        //If known loci contained inheritance information, also filter by this
                        if (knownSplits.length > 2) {
                            String[] inheritances = knownSplits[knownInheritanceIndex].split(",");
                            for (String inheritance : inheritances) {
                                //System.err.println("INHERITANCE:" + inheritance + ", ");

                                if (queryDisease.contains(inheritance)) {
                                    System.out.println(queryLocusLine);
                                    break;
                                }
                            }
                        } else {
                            System.out.println(queryLocusLine);
                            break;
                        }
                    }
                }
            }
        } catch (FileNotFoundException fnfx) {
            System.err.println("Could not read file: " + fnfx.getMessage());
        }
    }
}