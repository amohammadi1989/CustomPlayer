package com.sample.test3;

import java.io.File;
import java.util.function.Consumer;
import java.util.function.Function;
/**
 * Created By: Ali Mohammadi
 * Date: 12 Feb, 2022
 */
public class Sample {
  public static void main(String[] args) throws Exception{
    Sample sample=new Sample();
    String name="karim";
    String lastName="mohammadi";
    Function<Integer,String> a=new Function<Integer, String>() {
      @Override
      public String apply(Integer integer) {
        return null;
      }
    }  ;
    /*a.
    Consumer<String> consumer = s -> System.out.println(s);
    consumer.andThen(s -> System.out.println(s.toUpperCase()));
  */
  
    Consumer<ObjTest> stringConsumer= sample.getConsumer(name,lastName);
    
    stringConsumer.andThen(sample::Test).accept( new ObjTest() );
    
  }
  
  public Consumer<ObjTest> getConsumer(String name,String lastName){
    return (ob)->ob.ObjTest1( name,lastName );
  }
  public void Test(ObjTest test){
    System.out.println(test.getLastName()+test.getName());
  }
  
}
