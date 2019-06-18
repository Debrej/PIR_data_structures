import java.io.*;
import java.util.zip.*;

public class Compresser {
    public static void main(String[] args) throws IOException {
        String sourceFile = args[0];
		String outputFile = args[1];
        FileOutputStream fos = new FileOutputStream(outputFile);
        DeflaterOutputStream zipOut = new DeflaterOutputStream(fos);
        File fileToZip = new File(sourceFile);
        FileInputStream fis = new FileInputStream(fileToZip);

        byte[] bytes = new byte[1024];
        int length;
        while((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        zipOut.close();
        fis.close();
        fos.close();
    }
}
