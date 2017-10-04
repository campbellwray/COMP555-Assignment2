/**
 * Filters candidate variants by the list of known variants, extracting the population proportions
 */
public class KnownVariantFilter {
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Usage: java KnownVariantFilter <Known variants VCF file> <Query variants VCF file>");
            return;
        }

        VCFScanner knownVariantsVCFScanner = new VCFScanner(args[0], false);
        VCFScanner queryVariantVCFScanner = new VCFScanner(args[1], false);

        //Append disease, proportion, match and locus information to header
        String[] currentHeaderTokens = knownVariantsVCFScanner.header;
        System.out.print("#DISEASE\tPROPORTION\tALTMATCH\tLOCUS\t");
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
        int queryLocusIndex = queryVariantVCFScanner.getHeaderIndex("LOCUS");

        String[] querySplits = queryVariantVCFScanner.scanner.nextLine().split("\\s+");
        int queryChr = Integer.parseInt(querySplits[queryChrIndex]);
        int queryPos = Integer.parseInt(querySplits[queryPosIndex]);

        int knownChrIndex = knownVariantsVCFScanner.getHeaderIndex("CHROM");
        int knownPosIndex = knownVariantsVCFScanner.getHeaderIndex("POS");
        int knownAltIndex = knownVariantsVCFScanner.getHeaderIndex("ALT");
        int knownInfoIndex = knownVariantsVCFScanner.getHeaderIndex("INFO");

        //Filter each candidate variant by all known variants
        while (knownVariantsVCFScanner.scanner.hasNextLine()) {
            knownVariantLine = knownVariantsVCFScanner.scanner.nextLine();
            String[] knownSplits = knownVariantLine.split("\\s+");

            //X chromosome does not show up in our candidate variants, so can use Integer for chromosome
            int knownChr = Integer.parseInt(knownSplits[knownChrIndex]);
            int knownPos = Integer.parseInt(knownSplits[knownPosIndex]);

            //Quickly skip through query file if we have not yet found a known variant
            while (queryChr < knownChr || (queryChr <= knownChr && queryPos < knownPos)) {
                if (queryVariantVCFScanner.scanner.hasNextLine()) {
                    querySplits = queryVariantVCFScanner.scanner.nextLine().split("\\s+");
                    queryChr = Integer.parseInt(querySplits[queryChrIndex]);
                    queryPos = Integer.parseInt(querySplits[queryPosIndex]);
                } else {
                    return;
                }
            }

            //Print out queries that match the given position, though may not match the given base
            if (queryChr == knownChr && queryPos == knownPos) {
                String disease = querySplits[queryDiseaseIndex];
                String queryAlt = querySplits[queryAltIndex];
                String queryLocus = querySplits[queryLocusIndex];
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

                //Add the population proportion, and whether the known variant base matched the candidate variant base
                System.out.format("%s\t%.8f\t%s\t%s\t%s\n", disease, proportion, (queryAlt.equals(knownAlt) ? "TRUE" : "FALSE"), queryLocus, knownVariantLine);
            }

            if ((lineCount++ % 100000) == 100000 - 1) {
                System.err.println(lineCount + " Known Variant chr=" + knownChr + " pos=" + knownPos + " <===>" + " Query Variant chr=" + queryChr + " pos=" + queryPos);
            }
        }
    }
}