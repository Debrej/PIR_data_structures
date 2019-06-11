package io.vertx.spi.cluster.hazelcast.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream; 
import java.io.File;  
import java.io.FileInputStream;  
import java.io.FileOutputStream; 
import java.io.InputStream;  
import java.util.List;  
import java.util.Map;  
import java.util.zip.DataFormatException;  
import java.util.zip.Deflater;  
import java.util.zip.Inflater;  

import com.hazelcast.core.IMap;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.shareddata.AsyncMap;

public class HazelcastCompressAsyncMap<K, V> extends HazelcastAsyncMap<K, V> implements AsyncMap<K, V> {

    private CompressObject vCompressed;

    public HazelcastCompressAsyncMap(Vertx vertx, IMap<K, V> map) {
        super(vertx, map);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void put(K k, V v, Handler<AsyncResult<Void>> completionHandler) {
        try {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            ObjectOutputStream in = new ObjectOutputStream(b);
            in.writeObject(v);
            byte[] bytes =  b.toByteArray();
            CompressObject compressedV = new CompressObject(compress(bytes));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        super.put(k, v, completionHandler);
    }

   
    public static byte[] compress(byte[] data) throws IOException {  
        Deflater deflater = new Deflater();  
        deflater.setInput(data);  
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);   
        deflater.finish();  
        byte[] buffer = new byte[1024];   
        while (!deflater.finished()) {  
            int count = deflater.deflate(buffer); // returns the generated code... index  
            outputStream.write(buffer, 0, count);   
        }  
        outputStream.close();  
        byte[] output = outputStream.toByteArray();  
        return output;  
    }  
    public static byte[] decompress(byte[] data) throws IOException, DataFormatException {  
        Inflater inflater = new Inflater();   
        inflater.setInput(data);  
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);  
        byte[] buffer = new byte[1024];  
        while (!inflater.finished()) {      
            int count = inflater.inflate(buffer);  
            outputStream.write(buffer, 0, count);  
        }  
        outputStream.close();  
        byte[] output = outputStream.toByteArray();  
        return output;  

    }  
    
 
 
 }
 