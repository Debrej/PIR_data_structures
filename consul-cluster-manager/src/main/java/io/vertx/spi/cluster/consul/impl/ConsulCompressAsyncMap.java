package io.vertx.spi.cluster.consul.impl;

import java.util.zip.Deflater;
import java.util.zip.Inflater;
import java.util.*;

public class ConsulCompressAsyncMap extends ConsulAsyncMap {

   public ConsulCompressAsyncMap(String name, ClusterManagerInternalContext appContext, ClusterManager clusterManager) {
     super(name, appContext, clusterManager);
   }

   @Override
   private Future<Boolean> putValue(K k, V v, KeyValueOptions keyValueOptions, Optional<Long> ttl) {
      byte[] input = v.getBytes("UTF-8");
      Long ttlValue = ttl.map(aLong -> ttl.get()).orElse(null);
      return asFutureString(k, v, appContext.getNodeId(), ttlValue)
        .compose(value -> putPlainValue(keyPath(k), value, keyValueOptions))
        .compose(result ->
          ttlMonitor.apply(keyPath(k), ttl)
            .compose(aVoid -> succeededFuture(result)));
    }
}

/*
try {
    // Encode a String into bytes
    String inputString = "blahblahblah";
    byte[] input = inputString.getBytes("UTF-8");

    // Compress the bytes
    byte[] output = new byte[100];
    Deflater compresser = new Deflater();
    compresser.setInput(input);
    compresser.finish();
    int compressedDataLength = compresser.deflate(output);
    compresser.end();

    // Decompress the bytes
    Inflater decompresser = new Inflater();
    decompresser.setInput(output, 0, compressedDataLength);
    byte[] result = new byte[100];
    int resultLength = decompresser.inflate(result);
    decompresser.end();

    // Decode the bytes into a String
    String outputString = new String(result, 0, resultLength, "UTF-8");
} catch(java.io.UnsupportedEncodingException ex) {
    // handle
} catch (java.util.zip.DataFormatException ex) {
    // handle
}
*/
