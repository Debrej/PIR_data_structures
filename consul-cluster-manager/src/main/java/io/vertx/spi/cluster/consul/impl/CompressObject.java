 package io.vertx.spi.cluster.consul.impl;

public class CompressObject {

   byte[] compressData;

   public CompressObject (byte[] compressData) {
      this.compressData = compressData;
   }

   public byte[] getByte () {
      return compressData;
   }
}
