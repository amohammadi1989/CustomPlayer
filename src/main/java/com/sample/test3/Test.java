package com.sample.test3;
import javazoom.jl.decoder.Streams;

import javax.sound.sampled.*;
import java.io.*;
import java.util.Date;

import static javax.sound.sampled.AudioSystem.getAudioInputStream;
/**
 * Created By: Ali Mohammadi
 * Date: 02 Feb, 2022
 */
public class Test {
  Streams inputStream=null;
  public static void main(String[] args) {
 
    Test player=new Test();
    try {
      player.play(player.readfiles());

    } catch (Exception e) {
    
    } finally {
    
    }
  }
  
  public ByteArrayOutputStream readfiles() throws Exception{
    int read=0;
    int totals=0;
    byte[] buffer=new byte[1024];
    inputStream=new Streams();
    BufferedInputStream reader=new BufferedInputStream( new FileInputStream( new File( "C:\\Users\\mohamadi_a\\Desktop\\p\\testSound1.mp3")) );
    ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
    System.out.println("start read:"+new Date() );
    while ((read = reader.read(buffer)) >= 0) {

      totals += read;
      outputStream.write( buffer, 0, read );

    }
    System.out.println("end read:"+new Date() );
    return outputStream;
  }
  
  private AudioFormat getOutFormat(AudioFormat inFormat) {
    final int ch = inFormat.getChannels();
    final float rate = inFormat.getSampleRate();
    float sampleRate = 8000;
    int sampleSizeInBits = 16;
    int channels = 2;
    boolean signed = true;
    boolean bigEndian = true;
    return new AudioFormat(inFormat.getSampleRate(), sampleSizeInBits,
                           channels, true,
                           true);
    
/*    return new AudioFormat(inFormat.getSampleRate()*2, inFormat.getSampleSizeInBits(),
    inFormat.getChannels(), true, inFormat.isBigEndian());*/
    
    // return new AudioFormat(PCM_SIGNED, rate, 16, ch, ch*2, 8000, true);
  }
  
  private void stream(AudioInputStream in, SourceDataLine line, AudioFormat inFormat)
  throws Exception {
    int bufferSize = (int) inFormat.getSampleRate() * inFormat.getFrameSize();
    
    final byte[] buffer = new byte[512];
    long start=System.currentTimeMillis();
    for (int n = 0; n != -1; n = in.read(buffer, 0, buffer.length)) {
      int k=0;
      
      try {
        line.write(buffer, 0, n);
        
      } catch (Exception e) {
        System.out.println(e.getMessage());
        e.printStackTrace();
      }
      
      
    }
    long end=System.currentTimeMillis();
    //System.out.println( "end="+end);
    System.out.println("totals="+(end-start));
  }
  
  public void play(ByteArrayOutputStream out) {
    
    InputStream s=new ByteArrayInputStream( out.toByteArray() );
    try (final AudioInputStream in =
         getAudioInputStream( s )) {
      
      final AudioFormat outFormat = getOutFormat( in.getFormat() );
      final DataLine.Info info = new DataLine.Info( SourceDataLine.class, outFormat );
      
      try (final SourceDataLine line =
           (SourceDataLine) AudioSystem.getLine( info )) {
        while (true)
          try {
            if (line != null) {
              line.open( outFormat );
              line.start();
              stream( getAudioInputStream( outFormat, in ), line, outFormat );
              line.drain();
              line.stop();
            }
          }catch (Exception ex){
            System.out.println("test");
          }
        
      }
      
    } catch (Exception e) {
      e.printStackTrace();
    }
    // }
  }
}
