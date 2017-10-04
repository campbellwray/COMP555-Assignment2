import java.util.Scanner;
import java.io.*;
import java.util.ArrayList;

/**
 * Range for an exon region
 */
class Range {
    int start;
    int end;

    Range (int start, int end){
        this.start = start;
        this.end = end;
    }
}

/**
 * Exon region which contains information about the chromosome, direction and range of positions
 */
class ExonRegion {
    final String chromosome;
    final String direction;
    ArrayList<Range> ranges = new ArrayList<Range>();

    ExonRegion(String chromosome, String direction){
        this.chromosome = chromosome;
        this.direction = direction;
    }

    void addRange(int start, int end){
        ranges.add(new Range(start,end));
    }
}

/**
 * Reads all exon regions from a file into memory
 */
public class ExonParser {
    public static void main(String[] args) {
        try {
            ArrayList<ExonRegion> exons = getExonRegions(args[0]);
            for (ExonRegion e : exons) {
                System.out.print(e.chromosome + " " + e.direction + " ");
                for (Range r : e.ranges) {
                    System.out.print(r.start + "-" + r.end + ", ");
                }
                System.out.println("\n");
            }
        } catch (FileNotFoundException fnfx) {
            System.err.println("Could not read file: " + fnfx.getMessage());
        }
    }

    static ArrayList<ExonRegion> getExonRegions(String filename) throws FileNotFoundException {
        Scanner sc = new Scanner(new File(filename));
        ArrayList<ExonRegion> exons = new ArrayList<ExonRegion>();
        ExonRegion a = null;

        //Build exon region object, then add to list
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] arr = line.split("\\s+");
            String[] starts = arr[9].split(",");
            String[] ends = arr[10].split(",");
            for (ExonRegion e : exons) {
                if (e.chromosome.equals(arr[2]) && e.direction.equals(arr[3])) {
                    a = e;
                }
            }
            if (a == null) {
                a = new ExonRegion(arr[2], arr[3]);
                exons.add(a);
            }

            for (int i = 0; i < starts.length; i++) {
                a.addRange(Integer.parseInt(starts[i]), Integer.parseInt(ends[i]));
            }
            a = null;
        }
        return exons;
    }
}


