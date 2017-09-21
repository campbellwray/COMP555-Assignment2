import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.ArrayList;

class VCFScanner {
    final Scanner scanner;

    final String[] header;

    VCFScanner(String pathname, boolean printHeader) throws FileNotFoundException {
        scanner = new Scanner(new File(pathname));
        String headerLine = scanner.nextLine();
        //Ignore metadata
        while (headerLine.matches("^##.*")) {
            headerLine = scanner.nextLine();
        }
        if (printHeader) {
            System.out.println(headerLine);
        }
        //Remove # from header
        headerLine = headerLine.substring(1);
        header = headerLine.split("\\s+");
    }

    int getHeaderIndex(String search) {
        for (int i = 0; i < header.length; i++) {
            if (header[i].equals(search)) {
                return i;
            }
        }
        return -1;
    }
}

class InheritenceExonFilter {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Example usage: java ReadVCF <filename> <exons>");
            return;
        }

        //VCFData/wgEncodeGencodeBasicV17.txt"

        ArrayList<ExonRegion> exonRegions = ExonParser.getExonRegions(args[1]);

        try {
            VCFScanner theVCFScanner = new VCFScanner(args[0], false);

            //Print new header
            String[] currentHeaderTokens = theVCFScanner.header;
            System.out.print("#DISEASE\t");
            for (String token : currentHeaderTokens) {
                System.out.print(token + "\t");
            }
            System.out.println();

            String recordLine;
            String[] recordSplit;

            int chrIndex = theVCFScanner.getHeaderIndex("CHROM");
            int posIndex = theVCFScanner.getHeaderIndex("POS");
            int fatherIndex = theVCFScanner.getHeaderIndex("FATHER");
            int son2Index = theVCFScanner.getHeaderIndex("SON2");

            //The Records
            while (theVCFScanner.scanner.hasNext()) {
                recordLine = theVCFScanner.scanner.nextLine();
                recordSplit = recordLine.split("\\s+");

                String chromosome = "chr" + recordSplit[chrIndex];
                int position = Integer.parseInt(recordSplit[posIndex]);

                String[] familyMembers = new String[(son2Index - fatherIndex) + 1];

                System.arraycopy(recordSplit, fatherIndex, familyMembers, 0, son2Index + 1 - fatherIndex);

                if (exonRegions != null) {
                    for (ExonRegion theExonRegion : exonRegions) {
                        if (theExonRegion.chromosome.equals(chromosome)) {
                            for (Range theRange : theExonRegion.ranges) {
                                //TODO: Early stop?
                                if (position > theRange.start && position < theRange.end) {
                                    checkSickleCellAnemia(familyMembers, recordLine);
                                    checkRetinitisPigmentosaA(familyMembers, recordLine);
                                    checkRetinitisPigmentosaB(familyMembers, recordLine);
                                    checkSkeletalDysplasia(familyMembers, recordLine);
                                    checkSpasticParaplegiaA(familyMembers, recordLine);
                                    checkSpasticParaplegiaB(familyMembers, recordLine);
                                    break;
                                }
                            }
                        }
                    }
                } else {
                    System.err.println("Error: Exonregions was empty");
                    return;
                }
            }
        } catch (IOException iox) {
            System.err.println("Could not read from input: " + iox.getMessage());
        }
    }

    private static boolean genotypeHetero(String familyMember) {
        String genotype = getGenotype(familyMember);
        return genotype.equals("0/1") || genotype.equals("1/0") || genotype.equals("1|0") || genotype.equals("0|1");
    }

    private static boolean genotypeHomoRec(String familyMember) {
        String genotype = getGenotype(familyMember);
        return genotype.equals("1/1") || genotype.equals("1|1");
    }

    private static boolean genotypeHomoDom(String familyMember) {
        String genotype = getGenotype(familyMember);
        return genotype.equals("0/0") || genotype.equals("0|0");
    }

    private static String getGenotype(String familyMemberData) {
        return familyMemberData.split(":")[0];
    }

    private static void checkSickleCellAnemia(String[] familyMembers, String theLine) {
        if (genotypeHetero(familyMembers[0]) &&             //Father
                genotypeHetero(familyMembers[1]) &&         //Mother
                genotypeHetero(familyMembers[2]) &&         //D1
                genotypeHomoDom(familyMembers[3]) &&        //D2
                genotypeHomoRec(familyMembers[4]) &&        //D3
                genotypeHetero(familyMembers[5]) &&         //S1
                genotypeHomoDom(familyMembers[6])           //S2
                ) {
            System.out.println("SickleCellAnemia\t" + theLine);
        }
    }

    private static void checkRetinitisPigmentosaA(String[] familyMembers, String theLine) {
        if (genotypeHomoRec(familyMembers[0])    &&         //Father
                genotypeHetero(familyMembers[1]) &&         //Mother
                genotypeHetero(familyMembers[2]) &&         //D1
                genotypeHomoRec(familyMembers[3])&&         //D2
                genotypeHetero(familyMembers[4]) &&         //D3
                genotypeHetero(familyMembers[5]) &&         //S1
                genotypeHomoRec(familyMembers[6])           //S2
                ) {
            System.out.println("RetinitisPigmentosaPatternA\t" + theLine);
        }
    }
	private static void checkRetinitisPigmentosaB(String[] familyMembers, String theLine) {
        if (genotypeHetero(familyMembers[0]) &&              //Father
                genotypeHomoDom(familyMembers[1]) &&         //Mother
                genotypeHomoDom(familyMembers[2]) &&         //D1
                genotypeHetero(familyMembers[3]) &&          //D2
                genotypeHomoDom(familyMembers[4]) &&         //D3
                genotypeHomoDom(familyMembers[5]) &&         //S1
                genotypeHetero(familyMembers[6])             //S2
                ) {
            System.out.println("RetinitisPigmentosaPatternB\t" + theLine);
        }
    }
    private static void checkSkeletalDysplasia(String[] familyMembers, String theLine) {
        if (genotypeHetero(familyMembers[0]) &&                                             //Father
                genotypeHetero(familyMembers[1]) &&                                         //Mother
                (genotypeHetero(familyMembers[2]) || genotypeHomoDom(familyMembers[2])) &&  //D1
                genotypeHomoRec(familyMembers[3]) &&                                        //D2
                (genotypeHetero(familyMembers[4]) || genotypeHomoDom(familyMembers[4])) &&  //D3
                genotypeHomoRec(familyMembers[5]) &&                                        //S1
                (genotypeHetero(familyMembers[6]) || genotypeHomoDom(familyMembers[6]))     //S2
                ) {
            System.out.println("SkeletalDysplasia\t" + theLine);
        }
    }

    private static void checkSpasticParaplegiaA(String[] familyMembers, String theLine) {
        if (genotypeHomoRec(familyMembers[0]) &&            //Father
                genotypeHetero(familyMembers[1]) &&         //Mother
                genotypeHomoRec(familyMembers[2]) &&        //D1
                genotypeHetero(familyMembers[3]) &&         //D2
                genotypeHomoRec(familyMembers[4]) &&        //D3
                genotypeHetero(familyMembers[5]) &&         //S1
                genotypeHomoRec(familyMembers[6])           //S2
                ) {
            System.out.println("SpasticParaplegiaPatternA\t" + theLine);
        }
    }
    private static void checkSpasticParaplegiaB(String[] familyMembers, String theLine) {
        if (genotypeHetero(familyMembers[0]) &&              //Father
                genotypeHomoDom(familyMembers[1]) &&         //Mother
                genotypeHetero(familyMembers[2]) &&          //D1
                genotypeHomoDom(familyMembers[3]) &&         //D2
                genotypeHetero(familyMembers[4]) &&          //D3
                genotypeHomoDom(familyMembers[5]) &&         //S1
                genotypeHetero(familyMembers[6])             //S2
                ) {
            System.out.println("SpasticParaplegiaPatternB\t" + theLine);
        }
    }
}
