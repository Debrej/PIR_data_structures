import java.nio.file.*;

import java.io.PrintWriter;

import java.lang.*;

public class computeMoyenne{

  public static void main(String[] args){
  double res=0;
  double res2=0;

  try{
    String tempsStr = new String(Files.readAllBytes(Paths.get("temps_lecture.txt")));
    String[] tempsS = tempsStr.split("\n");
    double[] temps = new double[tempsS.length];

    String tempsStr2 = new String(Files.readAllBytes(Paths.get("temps_put.txt")));
    String[] tempsS2 = tempsStr2.split("\n");
    double[] temps2 = new double[tempsS2.length];

    for (int i =0;i<tempsS.length;i++){
      temps[i]=  Double.parseDouble(tempsS[i]);
    }

    for (double t:temps){
      res+=t;
    }
    res=res/temps.length;

    for (int i =0;i<tempsS2.length;i++){
      temps2[i]=  Double.parseDouble(tempsS2[i]);
    }

    for (double t:temps2){
      res2+=t;
    }
    res2=res2/temps2.length;

  }catch(Exception e){

  }

  try{
    PrintWriter writerMoy = new PrintWriter("MoyenneRead.txt");
    writerMoy.println(res);
    writerMoy.flush();

    PrintWriter writerMoy2 = new PrintWriter("MoyennePut.txt");
    writerMoy2.println(res2);
    writerMoy2.flush();

  }catch(Exception e){

  }
  }
}
