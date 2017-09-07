import java.io.*;
import java.util.*;

public class ReadInfo {
  public static void main(String[] args) {

    ArrayList<Record> recs = new ArrayList<Record>();

    boolean process = false;
    Scanner sc = new Scanner(System.in);

    while(sc.hasNextLine()) {
      String line = sc.nextLine();
      String[] splits = line.split("\\s+");
      if(process) {
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
      System.out.println(recs.get(i).prettyString());
  }
}


class Record {
  int chromosone;
  int pos;
  String id;
  String ref;
  String alt;
  String qual;
  String filter;
  int ac;
  int an;

  public Record(int chrom, int pos, String id,
    String ref, String alt, String qual, String filter, int ac, int an) {
      this.chromosone = chrom;
      this.pos = pos;
      this.id = id;
      this.ref = ref;
      this.alt = alt;
      this.qual = qual;
      this.filter = filter;
      this.ac = ac;
      this.an = an;
  }

  public String prettyString() {
    return chromosone + " " + pos + " " + id + " " + ref + " " + alt + " " + qual + " " +
            filter + " " + ac + " " + an;
  }
}
