package com.sample.test1.server;


import com.sample.test1.client.ClientForm;
import com.sample.test1.main.MainServices2;

import javazoom.jl.decoder.CPlayer;
import javazoom.jl.decoder.JavaLayerException;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import static javax.sound.sampled.AudioSystem.getAudioInputStream;

public class ServerForm1 extends JFrame implements Runnable {
  private final JButton sub;

  // Components of the Form
  
  private Container c;
  private JLabel title;
  private JLabel name;
  private JLabel sizeReadLabel;
  private static List<CPlayer> playerList=new ArrayList<>();

  private JLabel totalRecives;
  public static JLabel totalRecivesValue;

  private JTextField tname;
  private static JTextField tRead;
  
  
  private static int k=0;
  
  public static  int TOTALS_PLAY=0;
  
  private JLabel res;

  
  
  static ByteArrayOutputStream finalOutputStream;
  
  static ArrayList<ByteArrayOutputStream> outputStreamList=null;

  private static CPlayer player;
  

  public ServerForm1(ArrayList<ByteArrayOutputStream> out, JTextField tnameServer)
  {
   
    setTitle("Server Form");
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setResizable(true);
    setSize( 600,400 );
    setLocation( 700,500 );
    c = getContentPane();
    c.setLayout(null);
    player=null;
    
    
    name = new JLabel("Path:");
    name.setFont(new Font("Arial", Font.PLAIN, 20));
    name.setSize(100, 20);
    name.setLocation(100, 50);
    c.add(name);
    
    this.tname = tnameServer;
    this.tname.setFont(new Font("Arial", Font.PLAIN, 15));
    this.tname.setSize(190, 20);
    this.tname.setLocation(200, 50);
    this.tname.setText( "a.mp3" );
    this.tname.setEditable( false );
    c.add(this.tname);
    
    sizeReadLabel = new JLabel("Total play:");
    sizeReadLabel.setFont(new Font("Arial", Font.PLAIN, 20));
    sizeReadLabel.setSize(120, 20);
    sizeReadLabel.setLocation(100, 70);
    c.add(sizeReadLabel);
    
    tRead = new JTextField();
    tRead.setFont(new Font("Arial", Font.PLAIN, 15));
    tRead.setSize(190, 20);
    tRead.setLocation(200, 70);
    tRead.setText( "20" );
    c.add(tRead);
    
    
    
    totalRecives = new JLabel( "Total receive:");
    totalRecives.setFont( new Font( "Arial", Font.PLAIN, 14));
    totalRecives.setSize( 130, 20);
    totalRecives.setLocation( 100, 170);
    c.add( totalRecives );
    
    totalRecivesValue = new JLabel( "0");
    totalRecivesValue.setFont( new Font( "Arial", Font.PLAIN, 14));
    totalRecivesValue.setSize( 100, 20);
    totalRecivesValue.setLocation( 210, 170);
    c.add( totalRecivesValue );


    sub = new JButton("Change Action");
    sub.setFont(new Font("Arial", Font.PLAIN, 15));
    sub.setSize(150, 20);
    sub.setLocation(100, 300);
    sub.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
            // CPlayer.ChangeFlag();
      }
    });
    c.add(sub);

    
    
    res = new JLabel("");
    res.setFont(new Font("Arial", Font.PLAIN, 20));
    res.setSize(500, 25);
    res.setLocation(100, 500);
    c.add(res);
    
    outputStreamList=out;
    setVisible(true);
    
    
  }
  public static int getTotalsPlay(){
    return Integer.valueOf(tRead.getText());
  }
  public static void Manage() throws Exception {
    Thread thread=new Thread( MainServices2.serverForm );
    thread.start();
    Runnable rr=new Runnable() {
      @Override
      public void run() {
        play();
      }
    };
    Thread tt=new Thread(rr);
    tt.start();
    

  }

  public static   void play(){
    Runnable r=new Runnable() {
      @Override
      public void run() {
        int k=0;
        while (true) {
          try {
            if(playerList.get( k )!=null){
              playerList.get( k ).executePlayer();
              k++;
            }
          } catch (Exception e) {
          
          } finally {
          
          }
        }
      
      
      }
    };
  
    Thread t=new Thread(r);
    t.start();
  }
  
  @Override
  public void run() {
    try {

      int ki=1;

    
      outputStreamList= MainServices2.out;

      TOTALS_PLAY=Integer.valueOf(tRead.getText());
      
      finalOutputStream=new ByteArrayOutputStream();
      while (true) {
        if(ClientForm.isStop.get()){break;}
        if(outputStreamList.size()!=k) {
          if((outputStreamList.get( k )!=null)) {
            finalOutputStream.write(outputStreamList.get(k).toByteArray(),0,
                                    outputStreamList.get(k).toByteArray().length);
            totalRecivesValue.setText(String.valueOf((new Integer(totalRecivesValue.getText()))+outputStreamList.get( k ).toByteArray().length) );
  
            //System.out.println("k="+k+ "==output stream=" + finalOutputStream.toByteArray()
            // .length );
            if(player==null){
              player=new CPlayer();
              //playerList.add( player );
            }
            
            player.addBuffers(finalOutputStream.toByteArray());
            finalOutputStream = new ByteArrayOutputStream();
           // if(ki%1000==0){
             // playerList.add(  player );
             // player=null;
            //}
            //ki++;
            //TOTALS_PLAY--;
            //processFlags( TOTALS_PLAY==0,k );

         /*   if(TOTALS_PLAY==0){
              TOTALS_PLAY=20;
            }*/
            k++;
          }

        }

      }
      
    } catch (Exception e) {
      e.printStackTrace();
    }
    finally {
      System.out.println("");
    }
    
  }
  
  private void processFlags(boolean b,int k) throws JavaLayerException {
    if (b && finalOutputStream.toByteArray().length!=0) {
      System.out.println("k="+k+ "==output stream=" + finalOutputStream.toByteArray().length );
      ByteArrayInputStream in=new ByteArrayInputStream( finalOutputStream.toByteArray() );

      player.addBuffers(finalOutputStream.toByteArray());

      finalOutputStream = new ByteArrayOutputStream();

    }
  }

}
