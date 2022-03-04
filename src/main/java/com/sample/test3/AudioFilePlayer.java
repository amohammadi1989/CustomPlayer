package com.sample.test3;
import javazoom.jl.decoder.Streams;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.SourceDataLine;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static javax.sound.sampled.AudioSystem.getAudioInputStream;


public class AudioFilePlayer {
  List<ByteArrayOutputStream> outlist=new ArrayList<>();
  Streams inputStream=null;
  public static void main(String[] args) {
    final AudioFilePlayer player = new AudioFilePlayer ();
    
    
    
    try {
      //player.readfiles();
      player.play(player.readfiles());
  /*    javazoom.jl.player.Player player1=
      new Player(new ByteArrayInputStream(player.readfiles().toByteArray()));
      player1.play();*/
      //player.play();
    } catch (Exception e) {
    
    } finally {
    
    }
    
    
    // player.play("something.ogg");
  }
  
  public void play(ByteArrayOutputStream out) {
 /*   int k=0;
    //while (outlist.get( k )!=null) {
    ByteArrayOutputStream out1=null;
    try {
      byte[] b=new byte[3000];
      inputStream.read(b,0,3000);
      out1=new ByteArrayOutputStream();
      out1.write(b);
    }catch (Exception ex){}
*/
    
    
    InputStream s=new ByteArrayInputStream( out.toByteArray() );
    try (final AudioInputStream in =
         getAudioInputStream( s )) {
      
      final AudioFormat outFormat = getOutFormat( in.getFormat() );
      final Info info = new Info( SourceDataLine.class, outFormat );
      
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
  
  
  public ByteArrayOutputStream   readfiles() throws Exception{
    int read=0;
    int totals=0;
    byte[] buffer=new byte[1024];
    inputStream=new Streams();
    //BufferedInputStream reader=new BufferedInputStream( new FileInputStream( "D:\\k.mp3" ) );
    BufferedInputStream reader=new BufferedInputStream( new FileInputStream(new File("C:\\Users\\mohamadi_a\\Desktop\\p\\testSound1.mp3")) );
    ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
    System.out.println("start read:"+new Date() );
    while ((read = reader.read(buffer)) >= 0) {
      // ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
      // inputStream.addBuffers( buffer );
      totals += read;
      outputStream.write( buffer, 0, read );
         /*outlist.add(outputStream);
        //this.baos = new ByteArrayOutputStream();*/
      //break;
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
  
  private void stream(AudioInputStream in, SourceDataLine line,AudioFormat inFormat)
  throws Exception {
    int bufferSize = (int) inFormat.getSampleRate() * inFormat.getFrameSize();
    
    final byte[] buffer = new byte[512];
    long start=System.currentTimeMillis();
    //System.out.println("start:"+start);
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
/*  public void play() {
    try {
      File fileIn = new File("J:\\TestProject\\sample\\Server\\a-32.mp3");
      
      AudioInputStream audioInputStream=AudioSystem.getAudioInputStream(fileIn);
      AudioFormat formatIn=audioInputStream.getFormat();
      AudioFormat format=new AudioFormat(formatIn.getSampleRate(),
                                         formatIn.getSampleSizeInBits(), formatIn.getChannels(),
                                         true, formatIn.isBigEndian());
      System.out.println(formatIn.toString());
      System.out.println(format.toString());
      byte[] data=new byte[1024];
      DataLine.Info dinfo=new DataLine.Info(SourceDataLine.class, format);
      SourceDataLine line=(SourceDataLine)AudioSystem.getLine(dinfo);
      if(line!=null) {
        line.open(format);
        line.start();
        long start=System.currentTimeMillis();
        System.out.println("start:"+start);
        while(true) {
          int k=audioInputStream.read(data, 0, data.length);
          if(k<0) break;
          line.write(data, 0, k);
        
        }
        long end=System.currentTimeMillis();
        System.out.println("end:"+end);
        System.out.println("total:"+(end-start));
        line.stop();
        line.close();
      }
    }
    catch(Exception ex) { ex.printStackTrace(); }
  }*/
}
