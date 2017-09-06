import java.util.*;

public class ExonRegion {
    public String chromosome;
    public String direction;
    public ArrayList<Range> ranges = new ArrayList<Range>();
    
    public ExonRegion(String chromosome, String direction){
        this.chromosome = chromosome;
        this.direction = direction;
    }
    
    public void addRange(int start, int end){
        ranges.add(new Range(start,end));
    }
    
}
