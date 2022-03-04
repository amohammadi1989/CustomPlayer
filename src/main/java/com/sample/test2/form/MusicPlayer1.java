package com.sample.test2.form;

import javazoom.jl.decoder.CMPlayer;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

//implementing ActionListener interface
public class MusicPlayer1 implements ActionListener {
    JFrame frame;
    JLabel songNameLabel=new JLabel();
    JButton selectButton=new JButton("Choice Mp3");
    JButton playButton=new JButton("Play");
    JButton pauseButton=new JButton("Pause");
    JButton resumeButton=new JButton("Resume");
    JButton nextButton =new JButton("Next");
    JButton preButton =new JButton("Previous");
    DefaultListModel list;

    JList<String> listSong=new JList<String>();
    JFileChooser fileChooser;
    FileInputStream fileInputStream;
    BufferedInputStream bufferedInputStream;
    File myFile=null;
    String filename;
    String filePath;
    long totalLength;
    boolean status=false;
    long pause;
    Player player;
    Thread playThread;
    Thread resumeThread;
    private JSlider jSlider;
    private JLabel pBytesLbl;
    private JLabel pBytes;
    private JLabel tBytesLbl;
    private JLabel tBytes;
    private JLabel pTimeLbl;
    private JLabel pTime;
    private JLabel tTime;

    MusicPlayer1(){
        prepareGUI();
        addActionEvents();
        playThread=new Thread(runnablePlay);
        resumeThread=new Thread(runnableResume);

    }
    public void prepareGUI(){
        frame=new JFrame();
        frame.setTitle("Custom Music Player");
        frame.getContentPane().setLayout(null);
        frame.getContentPane().setBackground(Color.CYAN);
        frame.setSize(570,300);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        list = new DefaultListModel();
        list.addElement("m\\a.mp3");
        list.addElement("m\\b.mp3");
        list.addElement("m\\c.mp3");
        list.addElement("m\\d.mp3");
        list.addElement("m\\e.mp3");
        list.addElement("m\\f.mp3");
        list.addElement("m\\g.mp3");


        CMPlayer.AddMusicToList( list.get( 0).toString());
        CMPlayer.AddMusicToList(list.get(1).toString());
        CMPlayer.AddMusicToList(list.get(2).toString());
        CMPlayer.AddMusicToList(list.get(3).toString());
        CMPlayer.AddMusicToList(list.get(4).toString());
        CMPlayer.AddMusicToList(list.get(5).toString());
        CMPlayer.AddMusicToList(list.get(6).toString());

        listSong=new JList<>(list);
        listSong.setEnabled(true);
        listSong.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //System.out.println(listSong.getSelectedIndex());
                CMPlayer.SelectedMusic(listSong.getSelectedIndex());
            }
        });/*;addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {


                CMPlayer.SelectedMusic(listSong.getSelectedIndex());
            }*/
        //  });

        listSong.setBounds(30,15,190,180);
        listSong.setSelectedIndex(0);
        frame.add(listSong);


        selectButton.setBounds(300,10,100,30);
        frame.add(selectButton);

        songNameLabel.setBounds(100,50,300,30);
        frame.add(songNameLabel);

        playButton.setBounds(30,200,100,30);
        frame.add(playButton);

        pauseButton.setBounds(120,200,100,30);
        frame.add(pauseButton);

        resumeButton.setBounds(210,200,100,30);
        frame.add(resumeButton);
        resumeButton.setVisible(false);

        jSlider=new JSlider(JSlider.HORIZONTAL);
        jSlider.setBounds(270,60,250,50);
        jSlider.setMajorTickSpacing(500);
        //jSlider.setPaintTicks(true);
        //jSlider.setPaintLabels(true);
        //jSlider.setPaintTrack(true);
        jSlider.setMaximum(1000);
        jSlider.setMinimum(0);
        jSlider.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                status=true;
            }

            @Override
            public void mousePressed(MouseEvent e) {
                status=true;
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                status=false;
            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        jSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {

                pBytes.setText(String.valueOf(((JSlider)(e.getSource())).getValue()));
                if(status)
                    CMPlayer.SkipCurrent(((JSlider)(e.getSource())).getValue());
            }
        });

        frame.add(jSlider);

        pBytesLbl =new JLabel("Read size:");
        pBytesLbl.setBounds(300,100,100,30);
        frame.add(pBytesLbl);

        pBytes =new JLabel("0");
        pBytes.setBounds(400,100,100,30);
        frame.add(pBytes);

        tBytesLbl =new JLabel("Total size:");
        tBytesLbl.setBounds(300,120,100,30);
        frame.add(tBytesLbl);

        tBytes =new JLabel("0");
        tBytes.setBounds(400,120,100,30);
        frame.add(tBytes);

        JLabel tTimeLbl =new JLabel("Read time:");
        tTimeLbl.setBounds(300,140,100,30);
        frame.add(tTimeLbl);

        tTime =new JLabel("0");
        tTime.setBounds(400,140,100,30);
        frame.add(tTime);

        pTimeLbl =new JLabel("Total time:");
        pTimeLbl.setBounds(300,160,100,30);
        frame.add(pTimeLbl);

        pTime =new JLabel("0");
        pTime.setBounds(400,160,100,30);
        frame.add(pTime);







        nextButton.setBounds(300,200,100,30);
        frame.add(nextButton);




        preButton.setBounds(400,200,100,30);
        frame.add(preButton);

        frame.invalidate();
        frame.validate();
        frame.repaint();

        Runnable run=new Runnable() {
            @Override
            public void run() {


                int current=0;
                while (true){
                    //if(current==listSong.getSelectedIndex()) {
                    listSong.setSelectedIndex(CMPlayer.GetCurrentPlay());
                    int len=Integer.valueOf(CMPlayer.GetTotalBytesOfCurrentFile().toString());
                    jSlider.setMinimum(0);
                    jSlider.setMajorTickSpacing(len/2);
                    jSlider.setPaintTicks(true);
                    jSlider.setMaximum(len);
                    tBytes.setText(String.valueOf(len));
                    pTime.setText(CMPlayer.ConvertSecondToMinute(CMPlayer.GetTotalTimeOfCurrentPlay()));
                    tTime.setText(CMPlayer.GetReadTimeOfCurrentPlay());

                    if(!status) {
                        int av = (int) CMPlayer.GetReadBytesOfCurrentFile();
                        jSlider.setValue(av);

                    }
                    try {
                        Thread.sleep(500);
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                }
            }
        };
        Thread thread=new Thread(run);
        thread.start();

    }
    public void addActionEvents(){
        //registering action listener to buttons
        selectButton.addActionListener(this);
        playButton.addActionListener(this);
        pauseButton.addActionListener(this);
        resumeButton.addActionListener(this);
        nextButton.addActionListener(this);
        preButton.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==selectButton){
            //code for selecting our mp3 file from dialog window
            fileChooser=new JFileChooser();
            fileChooser.setCurrentDirectory(new File("C:\\Users\\hamee\\Downloads"));
            fileChooser.setDialogTitle("Select Mp3");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setFileFilter(new FileNameExtensionFilter("Mp3 files","mp3"));
            if(fileChooser.showOpenDialog(selectButton)==JFileChooser.APPROVE_OPTION){
                myFile=fileChooser.getSelectedFile();
                filename=fileChooser.getSelectedFile().getName();
                filePath=fileChooser.getSelectedFile().getPath();
                list.addElement(myFile.getName());
                CMPlayer.AddMusicToList(myFile.getPath());
            }
        }
        if(e.getSource()==playButton){
            //starting play thread
            if(playButton.getText().equals("Play")){
                CMPlayer.PlayMusic();
                playButton.setText("Stop");
            }else if(playButton.getText().equals("Stop")){
                CMPlayer.StopMusic();
                playButton.setText("Play");
            }

            songNameLabel.setText("now playing : "+filename);
        }
        if(e.getSource()==pauseButton){
            if(pauseButton.getText().equals("Pause")) {
                pauseButton.setText("Resume");
                CMPlayer.PauseMusic();
            }else if(pauseButton.getText().equals("Resume")){
                pauseButton.setText("Pause");
                CMPlayer.ResumeMusic();
            }
        }

        if(e.getSource()==resumeButton){
            CMPlayer.ResumeMusic();
        }
        if(e.getSource()== nextButton){
            listSong.setSelectedIndex(CMPlayer.NextMusic());

        }
        if(e.getSource()==preButton){
            listSong.setSelectedIndex(CMPlayer.PreviousMusic());
        }

    }

    Runnable runnablePlay=new Runnable() {
        @Override
        public void run() {
            try {
                //code for play button
                fileInputStream=new FileInputStream(myFile);
                bufferedInputStream=new BufferedInputStream(fileInputStream);
                player=new Player(bufferedInputStream);
                totalLength=fileInputStream.available();
                player.play();//starting music
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (JavaLayerException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    Runnable runnableResume=new Runnable() {
        @Override
        public void run() {
            try {
                //code for resume button
                fileInputStream=new FileInputStream(myFile);
                bufferedInputStream=new BufferedInputStream(fileInputStream);
                player=new Player(bufferedInputStream);
                fileInputStream.skip(totalLength-pause);
                player.play();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (JavaLayerException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };
}
