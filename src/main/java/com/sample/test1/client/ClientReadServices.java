package com.sample.test1.client;

import com.sample.test1.main.MainServices2;

import javax.swing.*;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

public class ClientReadServices extends Thread{
  BufferedInputStream reader;

  ArrayList<ByteArrayOutputStream> outputStreamList;
  JLabel totalReadValue;
  JLabel totalTransferValue;

  ByteArrayOutputStream baos;
  byte[] buffer;
  int delay;


  public ClientReadServices(BufferedInputStream reader, ArrayList<ByteArrayOutputStream> outputStreamList,
                            JLabel totalReadValue,JLabel totalTransferValue,int transfer){
    this.buffer=new byte[1024];
    this.reader=reader;
    this.outputStreamList=outputStreamList;
    this.totalReadValue=totalReadValue;
    this.totalTransferValue=totalTransferValue;
    this.delay=transfer;

  }
  int[] arr=new int[]{250,390,420,520,600,700,1000,1200};
  public void readFileOutTextArea() throws Exception{
    int read=0;
    int totals=0;
    outputStreamList= MainServices2.out;
    System.out.println("start read:"+new Date() );
    this.baos = new ByteArrayOutputStream();
    byte[] buf=new byte[1024];
    try {
      while ((read = reader.read(buffer)) >= 0) {
        baos.write(buffer, 0, read);
        delay();
        totalReadValue.setText( String.valueOf( Integer.valueOf( totalReadValue.getText() ) + read ) );
        outputStreamList.add( this.baos );
        totalReadValue.setText( String.valueOf( Integer.valueOf( totalReadValue.getText() ) + read ) );
        totals += read;
        this.baos = new ByteArrayOutputStream();
      }
    }catch (Exception ex){
      ex.printStackTrace();
    }
    System.out.println("end read:"+new Date() );
  }


  public String getSizeOfFile(BufferedInputStream reader2){
    this.baos = new ByteArrayOutputStream();
    try {
      int read=0;
      while ((read = reader2.read(buffer)) >= 0) {
        baos.write( buffer,0,read );
      }
    } catch (Exception e) {

    }

    return String.valueOf(baos.size());

  }






  @Override
  public void run() {

    try {
      this.readFileOutTextArea();
    } catch (Exception e) {

    }


  }

  private void delay(){

    Random r=new Random();
    try {
      //Thread.sleep( arr[r.nextInt( arr.length )] );
      Thread.sleep(this.delay);
    } catch (Exception e) {

    }

  }
}


