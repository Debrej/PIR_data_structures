import java.util.*;
import java.io.*;


public class createKeys{

  public static void main(String[] args){
    final int nmbFiles = Integer.parseInt(args[0]);
    final String nameFile = args[1];
    int i = nmbFiles;
    int k=1;

    try{

      PrintWriter writer = new PrintWriter("filenamesProvider.txt");
      PrintWriter writer2 = new PrintWriter("Key_Access_History.txt");
      while(i!=0){
        for(int j=0;j<k;j++){
          char key=(char)((nmbFiles-i)%26 +97);
          writer.print(key);
          writer2.print(key);

        }
        writer.print(","+nameFile);
        writer.flush();
        writer.println();

        writer2.print(",");
        writer2.flush();
        writer2.println();

        i--;
        if((nmbFiles-i)%26==0){
          k++;
        }

      }

    }catch(Exception e){
      System.out.println("erreur");
    }


  }


}
