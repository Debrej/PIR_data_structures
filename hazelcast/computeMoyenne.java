import java.nio.file.*;

import java.io.PrintWriter;

import java.lang.*;

public class computeMoyenne{

  public static void main(String[] args){
  double res=0;

  try{
    String tempsStr = new String(Files.readAllBytes(Paths.get(args[0])));
    String[] tempsS = tempsStr.split("\n");
    double[] temps = new double[tempsS.length];
    for (int i =0;i<tempsS.length;i++){
      temps[i]=  Double.parseDouble(tempsS[i]);
    }

    for (double t:temps){
      res+=t;
    }
    res=res/temps.length;

  }catch(Exception e){

  }

  try{
    PrintWriter writerMoy = new PrintWriter("Moyenne.txt");
    writerMoy.println(res);
    writerMoy.flush();

  }catch(Exception e){

  }
  }
}
