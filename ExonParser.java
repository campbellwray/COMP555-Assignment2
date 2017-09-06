import java.util.Scanner;
import java.io.*;
import java.util.ArrayList;

public class ExonParser {
    public static void main(String[] args) {
        try{
            Scanner sc = new Scanner(new File("VCFData/wgEncodeGencodeBasicV17.txt"));
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
            for(ExonRegion e : exons){
                System.out.print(e.chromosome + " " + e.direction + " ");
                for (Range r : e.ranges){
                    System.out.print(r.start+"-"+r.end+", ");
                }
                System.out.println("\n");
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}


