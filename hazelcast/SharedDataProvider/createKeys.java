import java.util.*;
import java.io.*;


public class createKeys{

  public static void main(String[] args){
    final int nmbFiles = Integer.parseInt(args[0]);
    int i = nmbFiles;
    int k=1;

    try{

      PrintWriter writer = new PrintWriter("keys.txt");
      while(i!=0){
        for(int j=0;j<k;j++){
          char key=(char)((nmbFiles-i)%26 +97);
          writer.print(key);

        }
	writer.print(",img20ko.jpeg");
        writer.flush();
        writer.println();
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
