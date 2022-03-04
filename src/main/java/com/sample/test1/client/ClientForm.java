package com.sample.test1.client;


import com.sample.test1.main.MainServices2;
import com.sample.test1.server.ServerForm1;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientForm extends JFrame implements ActionListener,Runnable {
  
  // Components of the Form
  
  public static AtomicBoolean isStop=new AtomicBoolean(false);
  static AtomicInteger countPlayInt=new AtomicInteger(0);
 // private final JLabel countPlay;
  //private final JTextField countPlayValue;
  
  private Container c;
  private JLabel title;
  private JLabel name;
  private JLabel sizeReadLabel;
  private JLabel sizeTransferLabel;
  private JLabel readSize;
  private JLabel readSizeValue;
  
  private JLabel totalRead;
  private JLabel totalReadValue;
  
  private JLabel totalTransfer;
  private JLabel totalTransferValue;
  
  
  private JLabel transferSize;
  
  private JTextField tname;
  private JTextField tRead;
  private JTextField tTransfer;
  
  private JButton sub;
  private JButton ply;
  private JButton reset;
  private JTextField tnameServer;
  
  private JLabel res;
  JLabel totalRecSize;
  private byte[] endPlay="end".getBytes(StandardCharsets.UTF_8);
  
  
  ByteArrayOutputStream finalOutputStream;
  
  ArrayList<ByteArrayOutputStream> outputStreamList;
  private Player rePlayer;
  private Player player;
  
  public ClientForm(ArrayList<ByteArrayOutputStream> out,JTextField tnameClient,
                    JTextField tnameServer,JLabel totalRecSize)
  {
    
    setTitle("Client Form");
   // setBounds(300, 90, 500, 400);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setResizable(true);
    setSize( 600,400 );
    setLocation( 90,500 );
    c = getContentPane();
    c.setLayout(null);
    
    
    name = new JLabel("Path:");
    name.setFont(new Font("Arial", Font.PLAIN, 20));
    name.setSize(200, 20);
    name.setLocation(100, 50);
    c.add(name);
    
    tname = tnameClient;
    tname.setFont(new Font("Arial", Font.PLAIN, 15));
    tname.setSize(190, 20);
    tname.setLocation(200, 50);
    tname.setText( "G:\\m\\aa.mpeg" );
    c.add(tname);
    sizeReadLabel = new JLabel("Total Size:");
    sizeReadLabel.setFont(new Font("Arial", Font.PLAIN, 20));
    sizeReadLabel.setSize(200, 20);
    sizeReadLabel.setLocation(100, 70);
    c.add(sizeReadLabel);
    
    tRead = new JTextField();
    tRead.setFont(new Font("Arial", Font.PLAIN, 15));
    tRead.setSize(190, 20);
    tRead.setLocation(200, 70);
    tRead.setText( "1024*1" );
    tRead.setEditable( false );
    tRead.setVisible( true );
    c.add(tRead);
    
    this.tnameServer=tnameServer;
    sizeTransferLabel = new JLabel("Delay(ms):");
    sizeTransferLabel.setFont(new Font("Arial", Font.PLAIN, 20));
    sizeTransferLabel.setSize(100, 20);
    sizeTransferLabel.setLocation(100, 90);
    c.add(sizeTransferLabel);
   
    this.totalRecSize=totalRecSize;
    
    tTransfer = new JTextField();
    tTransfer.setFont(new Font("Arial", Font.PLAIN, 15));
    tTransfer.setSize(190, 20);
    tTransfer.setLocation(200, 90);
    tTransfer.setText( "250" );
    c.add(tTransfer);
    /*
    countPlay = new JLabel("Count play:");
    countPlay.setFont(new Font("Arial", Font.PLAIN, 20));
    countPlay.setSize(100, 20);
    countPlay.setLocation(100, 110);
    c.add(countPlay);
   
    countPlayValue = new JTextField();
    countPlayValue.setFont(new Font("Arial", Font.PLAIN, 15));
    countPlayValue.setSize(190, 20);
    countPlayValue.setLocation(200, 110);
    countPlayValue.setText( "20" );
    c.add(countPlayValue);
    */
    readSize = new JLabel("Total :");
    readSize.setFont(new Font("Arial", Font.PLAIN, 14));
    readSize.setSize(100, 20);
    readSize.setLocation(100, 150);
    c.add(readSize);
    
    readSizeValue = new JLabel("0");
    readSizeValue.setFont(new Font("Arial", Font.PLAIN, 14));
    readSizeValue.setSize(100, 20);
    readSizeValue.setLocation(180, 150);
    c.add(readSizeValue);
    
    totalRead = new JLabel("Total send:");
    totalRead.setFont(new Font("Arial", Font.PLAIN, 14));
    totalRead.setSize(170, 20);
    totalRead.setLocation(100, 170);
    c.add(totalRead);
    
    totalReadValue = new JLabel("0");
    totalReadValue.setFont(new Font("Arial", Font.PLAIN, 14));
    totalReadValue.setSize(100, 20);
    totalReadValue.setLocation(220, 170);
    c.add(totalReadValue);

        /*totalTransfer = new JLabel("Total Transfer:");
        totalTransfer.setFont(new Font("Arial", Font.PLAIN, 14));
        totalTransfer.setSize(100, 20);
        totalTransfer.setLocation(100, 190);
        c.add(totalTransfer);

        totalTransferValue= new JLabel("0");
        totalTransferValue.setFont(new Font("Arial", Font.PLAIN, 14));
        totalTransferValue.setSize(100, 20);
        totalTransferValue.setLocation(200, 190);
        c.add(totalTransferValue);
*/
  
  
  
    tnameServer.setText( tnameClient.getText() );
    sub = new JButton("Send to server");
    sub.setFont(new Font("Arial", Font.PLAIN, 15));
    sub.setSize(150, 20);
    sub.setLocation(70, 300);
    sub.addActionListener(this);
    c.add(sub);
    

    reset = new JButton("Rest");
    reset.setFont(new Font("Arial", Font.PLAIN, 15));
    reset.setSize(100, 20);
    reset.setLocation(270, 300);
    reset.addActionListener(this);
    reset.setVisible( false );
    c.add(reset);
    
    
    res = new JLabel("");
    res.setFont(new Font("Arial", Font.PLAIN, 20));
    res.setSize(500, 25);
    res.setLocation(100, 500);
    c.add(res);
    
    outputStreamList=out;
    setVisible(true);
  }
  
  public void actionPerformed(ActionEvent e)
  {
    
    try {
      if (e.getSource() == sub) {
        tnameServer.setText( tname.getText() );
        finalOutputStream=new ByteArrayOutputStream();
        BufferedInputStream reader = new BufferedInputStream( new FileInputStream(tname.getText()));
        BufferedInputStream reader2 =
        new BufferedInputStream( new FileInputStream(tname.getText()));
        ServerForm1.TOTALS_PLAY= ServerForm1.getTotalsPlay();
        sub.setEnabled(false);
        isStop.set(false);
        ServerForm1.Manage();
        read(reader,reader2);
        
        res.setText( "Action Successfully.." );
        
      }else {
        MainServices2.out=new ArrayList<>();
        this.totalRecSize.setText( "0" );
        readSizeValue.setText("0");
       
  
        this.sub.setEnabled(true);
        isStop.set(true);
        ServerForm1.TOTALS_PLAY=0;
    

        Thread.sleep( 2000 );
        ServerForm1.totalRecivesValue.setText( "0" );
        totalReadValue.setText("0");
        res.setText("Please accept the"
                    + " terms & conditions..");
        
      }
    } catch (Exception et) {
    
    } finally {
    
    }
    
    
  }
  
  private void read(BufferedInputStream reader,BufferedInputStream reader2) throws JavaLayerException {
    
    ClientReadServices services=new ClientReadServices( reader, outputStreamList, totalReadValue,
                                             totalTransferValue, Integer.valueOf(tTransfer.getText()));
    
    readSizeValue.setText(services.getSizeOfFile(reader2));
    
    services.start();
  }
  
  private void play() throws JavaLayerException {
    Thread thread=new Thread(this);
    thread.start();
  }
  
  
  private void rePlay() throws JavaLayerException {
    Runnable run = new Runnable() {
      @Override
      public void run() {
        try {
          
          if(finalOutputStream!=null && finalOutputStream.size()!=0) {
            InputStream stream = new ByteArrayInputStream(finalOutputStream.toByteArray());
            rePlayer = new Player(stream);
            rePlayer.play();
            rePlayer.close();
          }
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    };
    Thread thread = new Thread(run);
    thread.start();
    
  }
  
  @Override
  public void run() {
    System.out.println("start play:"+new Date() );
    try {
      
      int k=0;
      while (true) {
        if (isStop.get())
          break;
        if(outputStreamList.size()!=k) {
          if((outputStreamList.get( k )!=null)) {
            finalOutputStream.write(outputStreamList.get(k).toByteArray());
            ByteArrayInputStream inputStream =
            new ByteArrayInputStream(outputStreamList.get(k++).toByteArray());
            
            player = new Player(inputStream);
            
            player.play();
            player.close();
          }
        }
        if(k!=0)
          if(outputStreamList.size()!=0 && outputStreamList.get( k-1 )!=null){
            if(Arrays.equals(outputStreamList.get(k-1).toByteArray(),endPlay)){
              break;
            }
          }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }finally {
      ply.setEnabled(true);
      System.out.println("End play:"+new Date() );
    }
    
  }
}
