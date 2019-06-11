package io.vertx.spi.cluster.hazelcast.impl;

import java.io.Serializable;

public class CompressObject implements Serializable{

   byte[] compressData;

   public CompressObject (byte[] compressData) {
      this.compressData = compressData;
   }

   public byte[] getByte () {
      return compressData;
   }


}
