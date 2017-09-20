import java.io.*;
import java.util.*;

public class RecordParser {
    public static void main(String[] args) throws Exception {
        //ArrayList<Record> recs = new ArrayList<Record>();
        //args[0] is ALL.merged
        //args[1] is filtered
        //boolean process = false;
        //Scanner recordScanner = getVCFScanner("/home/tcs/public_html/COMP555/VCFdata/ALL.merged.phase1_release_v3.20101123.snps_indels_svs.vcf");
        VCFScanner knownVariantsVCFScanner = new VCFScanner("../VCFdata/ALL.merged.phase1_release_v3.20101123.snps_indels_svs.vcf", false);
        VCFScanner queryVariantVCFScanner = new VCFScanner("filteredResults.vcf", false);
        //Scanner recordScanner = getVCFScanner("../filteredResults2.txt");
        //Scanner resultScanner = getVCFScanner("../filteredResults.txt");

        /*
        ArrayList<Integer[]> present = new ArrayList<Integer[]>();
        while (sc2.hasNextLine()) {
            String line2 = sc2.nextLine();
            if (line2.contains("#")) {
                continue;
            }
            String[] splits2 = line2.split("\\s+");

            Integer[] temp = new Integer[2];
            temp[0] = Integer.parseInt(splits2[1]);
            temp[1] = Integer.parseInt(splits2[2]);

            present.add(temp);
        }*/

        /*
        String line = recordScanner.nextLine();
        //Skip long header
        while (line.contains("#")) {
            line = recordScanner.nextLine();
        }*/

        String knownVariantLine;

        int lineCount = 0;
        boolean seen2 = false;
        boolean seen3 = false;

        //Chromosome and position
        int[] resultChrPos = new int[2];
        resultChrPos = getNextChrPos(queryVariantVCFScanner.scanner, resultChrPos);

        int chrIndex = knownVariantsVCFScanner.getHeaderIndex("CHROM");
        int posIndex = knownVariantsVCFScanner.getHeaderIndex("POS");

        while (knownVariantsVCFScanner.scanner.hasNextLine()) {
            knownVariantLine = knownVariantsVCFScanner.scanner.nextLine();

            String[] splits = knownVariantLine.split("\\s+");

            //TODO: X chromosome?
            int chr = Integer.parseInt(splits[chrIndex]);
            int pos = Integer.parseInt(splits[posIndex]);

            //Known variants file is not numerically ordered: Reset queryVariant scanner after 19->2, 22->3 transitions
            if ((chr == 2 && !seen2)) {
                seen2 = true;
                System.out.println("\nRESETTING FROM 2\n");
                queryVariantVCFScanner = new VCFScanner("../filteredResults.txt", false);
                resultChrPos = getNextChrPos(queryVariantVCFScanner.scanner, resultChrPos);
            } else if (chr == 3 && !seen3) {
                seen3 = true;
                System.out.println("\nRESETTING FROM 3\n");
                queryVariantVCFScanner = new VCFScanner("../filteredResults.txt", false);
                resultChrPos = getNextChrPos(queryVariantVCFScanner.scanner, resultChrPos);
            }

            while (resultChrPos[0] < chr || (resultChrPos[0] <= chr && resultChrPos[1] < pos)) {
                if (queryVariantVCFScanner.scanner.hasNextLine()) {
                    resultChrPos = getNextChrPos(queryVariantVCFScanner.scanner, resultChrPos);
                    //System.out.println("ALL.merged.Record: chr=" + chr + " pos=" + pos + " <=====>" + " filteredResults.txt: chr=" + resultChrPos[0] + " pos=" + resultChrPos[1]);
                } else {
                    return;
                }
            }

            //System.out.println(lineCount + " ALL.merged.Record: chr=" + chr + " pos=" + pos + " <=====>" + " filteredResults.txt: chr=" + resultChrPos[0] + " pos=" + resultChrPos[1]);

            if (resultChrPos[0] == chr && resultChrPos[1] == pos) {
                System.out.println("\n" + knownVariantLine + "\n");
            }

            if ((lineCount++ % 100000) == 1) {
                System.out.println(lineCount + " ALL.merged.Record: chr=" + chr + " pos=" + pos + " <=====>" + " filteredResults.txt: chr=" + resultChrPos[0] + " pos=" + resultChrPos[1]);
            }
        }

        /*
        for(Integer[] in : present) {
            if (in[0].equals(c1) && in[1].equals(pos1)) {
                System.out.println(line);
            }
        }*/
    }
      
      
      /*if(process) {
        String[] acan = splits[7].split(";");
        int ac = 0;
        int an = 0;
        for(int i = 0; i < 2; i++) {
          if(acan[0].contains("AC")) {
            ac = Integer.parseInt(acan[0].split("=")[1]);
            an = Integer.parseInt(acan[1].split("=")[1]);
          } else {
            ac = Integer.parseInt(acan[1].split("=")[1]);
            an = Integer.parseInt(acan[0].split("=")[1]);
          }
        }
        recs.add(new Record(Integer.parseInt(splits[0]), Integer.parseInt(splits[1]),
          splits[2], splits[3], splits[4], splits[5], splits[6], ac, an));
      }
      if(splits[0].contains("#CHROM")) {
        process = true;
      }
    }
    for(int i = 0; i < recs.size(); i++)
      System.out.println(recs.get(i).prettyString());*/

    private static int[] getNextChrPos(Scanner resultScanner, int[] resultChrPos) {
        String[] splits2 = resultScanner.nextLine().split("\\s+");
        resultChrPos[0] = Integer.parseInt(splits2[1]);
        resultChrPos[1] = Integer.parseInt(splits2[2]);
        return resultChrPos;
    }
}
