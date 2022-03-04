package com.sample.test1.main;

import com.sample.test1.client.ClientForm;
import com.sample.test1.server.ServerForm1;

import javax.swing.*;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class MainServices2 {
  public static ServerForm1 serverForm;
  public static ArrayList<ByteArrayOutputStream> out;
  public static void main(String[] args) {
     out=new ArrayList<ByteArrayOutputStream>();
    JTextField tnameServer=new JTextField();
    JTextField tnameClient=new JTextField();
    JLabel totalRecSize=new JLabel();
    serverForm=new ServerForm1( out, tnameServer);
    ClientForm clientForm=new ClientForm(out,tnameClient,tnameServer,totalRecSize);
  }
}
