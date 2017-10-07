import java.io.FileNotFoundException;

/**
 * Get bayesian statistics from our candidate variants filtered on known variants
 */
public class Bayes {
    private static final double SICKLE_CELL_ANEMIA_PRIOR = 0.155;
    private static final double SPASTIC_PARAPLEGIA_PRIOR = 1.8/10000.0;
    private static final double RETENITIS_PIGMENTOSA_PRIOR = 1.0/5000.0;
    private static final double SKELETAL_DYSPLASIA_PRIOR = 1.0/5000.0;

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java Bayes <candidate Variants with population probabilities VCF file>");
            return;
        }
        try{
            VCFScanner variantScanner = new VCFScanner(args[0], false);

            String recordLine;
            String[] recordSplit;

            //Append probability to header
            String[] currentHeaderTokens = variantScanner.header;
            System.out.print("#BAYESPROB\t");
            for (String token : currentHeaderTokens) {
                    System.out.print(token + "\t");
            }
            System.out.println();

            int proportionIndex = variantScanner.getHeaderIndex("PROPORTION");
            int diseaseIndex = variantScanner.getHeaderIndex("DISEASE");

            //Calculate probability for each record
            while(variantScanner.scanner.hasNext()) {
                recordLine = variantScanner.scanner.nextLine();
                recordSplit = recordLine.split("\\s+");

                double prior = 0.0;
                String disease = recordSplit[diseaseIndex];
                switch(disease) {
                    case "SickleCellAnemiaAR":
                        prior = SICKLE_CELL_ANEMIA_PRIOR;
                        break;
                    case "SpasticParaplegiaPatternAR":
                        prior = SPASTIC_PARAPLEGIA_PRIOR;
                        break;
                    case "SpasticParaplegiaPatternAD":
                        prior = SPASTIC_PARAPLEGIA_PRIOR;
                        break;
                    case "RetinitisPigmentosaPatternAR":
                        prior = RETENITIS_PIGMENTOSA_PRIOR;
                        break;
                    case "RetinitisPigmentosaPatternAD":
                        prior = RETENITIS_PIGMENTOSA_PRIOR;
                        break;
                    case "SkeletalDysplasia":
                        prior = SKELETAL_DYSPLASIA_PRIOR;
                        break;
                    default:
                        prior = Double.NEGATIVE_INFINITY;
                        break;
                }

                double proportion = Double.parseDouble(recordSplit[proportionIndex]);
                double model = 1 - proportion;
                double numerator = model * prior;
                double denominator = numerator + (proportion * (1-prior));
                double prob = numerator / denominator;
                if(proportion == 0.0) {
                    prob = 0.0;
                }

                System.out.println(prob + "\t" + recordLine);
            }
        } catch(FileNotFoundException fnfx) {
            System.err.println("Could not read file: " + fnfx.getMessage());
        }
    }
}
