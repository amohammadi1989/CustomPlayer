package com.sample.test3;
import java.util.function.Consumer;
/**
 * Created By: Ali Mohammadi
 * Date: 17 Feb, 2022
 */
public class ObjTest  {
  private String name;
  private String lastName;
  
  
  
  public void ObjTest1(String name, String lastName) {
    this.name = name;
    this.lastName = lastName;
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public String getLastName() {
    return lastName;
  }
  
  public void setLastName(String lastName) {
    this.lastName = lastName;
  }
  
  @Override
  public String toString() {
    return "ObjTest{" +
    "name='" + name + '\'' +
    ", lastName='" + lastName + '\'' +
    '}';
  }
}
