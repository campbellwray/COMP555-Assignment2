import java.util.ArrayList;
import java.util.List;

public class LociFilter {
    public static void main(String[] args) throws Exception {
       	VCFScanner knownLociVCFScanner = new VCFScanner(args[0], false);
        VCFScanner queryVariantVCFScanner = new VCFScanner(args[1], true);
        //Chromosome and position
        int queryLocusIndex = queryVariantVCFScanner.getHeaderIndex("LOCUS");
        int queryDiseaseIndex = queryVariantVCFScanner.getHeaderIndex("DISEASE");

        int knownLocusIndex = knownLociVCFScanner.getHeaderIndex("LOCUS");
        int knownDiseaseIndex = knownLociVCFScanner.getHeaderIndex("DISEASE");
        int knownInheritanceIndex = knownLociVCFScanner.getHeaderIndex("INHERITANCE");

        //Put into memory
        List<String> storedLines = new ArrayList<>();
        while (knownLociVCFScanner.scanner.hasNextLine()) {
            storedLines.add(knownLociVCFScanner.scanner.nextLine());
        }

        while (queryVariantVCFScanner.scanner.hasNextLine()) {
            String queryLocusLine = queryVariantVCFScanner.scanner.nextLine();
            String[] querySplits = queryLocusLine.split("\\s+");
            String queryLocus = querySplits[queryLocusIndex];
            String queryDisease = querySplits[queryDiseaseIndex];

            for (String theLine : storedLines) {
                String[] knownSplits = theLine.split("\\s+");
                String knownLocus = knownSplits[knownLocusIndex];
                String knownDisease = knownSplits[knownDiseaseIndex];

                if (queryLocus.contains(knownLocus) && queryDisease.contains(knownDisease)) {
                    System.out.println(queryLocusLine);
                    break;
                }
            }
        }

    }
}
