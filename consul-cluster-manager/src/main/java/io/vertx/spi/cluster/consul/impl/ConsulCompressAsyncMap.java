package io.vertx.spi.cluster.consul.impl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.shareddata.AsyncMap;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.Lock;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.ext.consul.KeyValueOptions;

import java.util.*;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import static io.vertx.core.Future.failedFuture;
import static io.vertx.core.Future.succeededFuture;
import static io.vertx.spi.cluster.consul.impl.ConversationUtils.asFutureString;
import static io.vertx.spi.cluster.consul.impl.ConversationUtils.asTtlConsulEntry;

public class ConsulCompressAsyncMap extends ConsulAsyncMap {

   public ConsulCompressAsyncMap(String name, ClusterManagerInternalContext appContext, ClusterManager clusterManager) {
     super(name, appContext, clusterManager);
   }

   @Override
   private Future<Boolean> putValue(K k, V v, KeyValueOptions keyValueOptions, Optional<Long> ttl) {

      Long ttlValue = ttl.map(aLong -> ttl.get()).orElse(null);
      return asFutureString(k, v, appContext.getNodeId(), ttlValue)
        .compose(value -> putPlainValue(keyPath(k), value, keyValueOptions))
        .compose(result ->
          ttlMonitor.apply(keyPath(k), ttl)
            .compose(aVoid -> succeededFuture(result)));
    }

    private String compressStr (String value) {
      try {
         byte[] input = value.getBytes("UTF-8");

         // Compress the bytes
         byte[] output = new byte[input.length];
         Deflater compresser = new Deflater();
         compresser.setInput(input);
         compresser.finish();
         int compressedDataLength = compresser.deflate(output);
         compresser.end();
      }catch(java.io.UnsupportedEncodingException ex) {
         System.exit(0);
      } catch (java.util.zip.DataFormatException ex) {
         System.exit(0);
      }
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
