 package com.kodcu.sdr.verticle;
import java.io.Serializable;

public class CompressObject implements Serializable {

   byte[] compressData;

   public CompressObject (byte[] compressData) {
      this.compressData = compressData;
   }

   public byte[] getByte () {
      return compressData;
   }
   public long length(){
      return compressData.length;
   }
}
