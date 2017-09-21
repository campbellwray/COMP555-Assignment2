import java.util.Scanner;
import java.io.*;
import java.util.ArrayList;

class Range {
    int start;
    int end;

    Range (int start, int end){
        this.start = start;
        this.end = end;
    }
}

class ExonRegion {
    String chromosome;
    String direction;
    ArrayList<Range> ranges = new ArrayList<Range>();

    ExonRegion(String chromosome, String direction){
        this.chromosome = chromosome;
        this.direction = direction;
    }

    void addRange(int start, int end){
        ranges.add(new Range(start,end));
    }
}

public class ExonParser {
	public static void main(String[] args) {
		ArrayList<ExonRegion> exons = getExonRegions(args[0]);
		
        for(ExonRegion e : exons){
            System.out.print(e.chromosome + " " + e.direction + " ");
            for (Range r : e.ranges){
                System.out.print(r.start+"-"+r.end+", ");
            }
            System.out.println("\n");
        }
	}

    static ArrayList<ExonRegion> getExonRegions(String filename) {
        try{
            Scanner sc = new Scanner(new File(filename));
            String previousChrom = "";
            ArrayList<ExonRegion> exons = new ArrayList<ExonRegion>();
            ExonRegion a = null;
            while (sc.hasNextLine()){
                String line = sc.nextLine();
                String [] arr = line.split("\\s+");
                String [] starts = arr[9].split(",");
                String [] ends = arr[10].split(",");
                for(ExonRegion e : exons){
                    if (e.chromosome.equals(arr[2]) && e.direction.equals(arr[3])){
                        a = e;
                    }
                }
                if (a == null){
                    a = new ExonRegion(arr[2], arr[3]);
                    exons.add(a);
                }
                
                for(int i = 0; i < starts.length; i ++){
                    a.addRange(Integer.parseInt(starts[i]), Integer.parseInt(ends[i]));
                }
                a = null;
            }
            return exons;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        //TODO: Something better?
        return null;
    }
}


