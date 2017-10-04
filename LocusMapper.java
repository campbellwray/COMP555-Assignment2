import java.io.FileNotFoundException;

/**
 * Maps chromosome-position information to the locus, for filtering by locus
 */
class LocusMapper {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java LocusMapper <query VCF file> <position-locus-mapping VCF file>");
            return;
        }
        try {
            VCFScanner theVCFScanner = new VCFScanner(args[0], false);
            VCFScanner cytoBandScanner = new VCFScanner(args[1], false);

            //Append locus information to header
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

            //Get locus information for each record
            while (theVCFScanner.scanner.hasNext()) {
                recordLine = theVCFScanner.scanner.nextLine();
                recordSplit = recordLine.split("\\s+");

                chromosome = "chr" + recordSplit[chrIndex];
                position = Integer.parseInt(recordSplit[posIndex]);

                cytoBandScanner = new VCFScanner(args[1], false);
                while (cytoBandScanner.scanner.hasNext()) {
                    refRecordLine = cytoBandScanner.scanner.nextLine();
                    refRecordSplit = refRecordLine.split("\\s+");

                    String refChromosome = refRecordSplit[refChrIndex];
                    int refPosStartPosition = Integer.parseInt(refRecordSplit[refPosStartIndex]);
                    int refPosEndPosition = Integer.parseInt(refRecordSplit[refPosEndIndex]);
                    refLocus = refRecordSplit[refLocusIndex];

                    if (refChromosome.equals(chromosome) && position > refPosStartPosition && position < refPosEndPosition) {
                        lastLocus = refChromosome.substring(3) + refLocus;
                    }
                }
                //Append locus information to VCF record
                System.out.println(lastLocus + "\t" + recordLine);
            }
        } catch (FileNotFoundException fnfx) {
            System.err.println("Could not read file: " + fnfx.getMessage());
        } catch (NumberFormatException nfx) {
            System.err.println("Could parse number: " + nfx.getMessage());
        }
    }
}