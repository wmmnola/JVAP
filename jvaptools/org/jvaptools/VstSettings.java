/*
 * VstSettings.java
 *
 * Created on 27. Januar 2006, 19:57
 *
 * JVAPTools - Tools for rapid Java-VST Audio Plugin creation Copyright (C) 2006 Daniel Reinert
 * This library is free software; you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library;
 * if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package org.jvaptools;

/**
 *
 * @author Daniel
 */
public class VstSettings implements java.io.Serializable{
      
  int parametercount;

  //public static final long serialVersionUID = 00001L;

  private String name;
  
  public static final String DEFAULT_NAME = "Settings";
  
  //String values for automation
  public java.util.Vector<java.util.Vector<String>> stringValues;
  public java.util.Vector<String> parameterLabel;
  public java.util.Vector<String> parameterName;
  
  private java.util.Map<String,Object> parameterValues;
  
  public VstSettings()
  {
      name  = DEFAULT_NAME;
      stringValues = new java.util.Vector<java.util.Vector<String>>();
      parameterValues = new java.util.TreeMap<String,Object>();
      parameterLabel = new java.util.Vector<String>();
      parameterName = new java.util.Vector<String>();      
      parametercount=0;
  }
  
  public VstSettings(VstSettings proto)
  {
      this();
      this.name=proto.name;
      this.stringValues.addAll(proto.stringValues);
      this.parameterValues.putAll(proto.parameterValues);
      this.parameterLabel.addAll(proto.parameterLabel);
      this.parameterName.addAll(proto.parameterName);
      this.parametercount=proto.parametercount;
  }
  
  public int getParametercount()
  {
      return parametercount;
  }
  
  public void newVstParameter(String name,String label,java.util.Vector<String> values)
  {
      parameterName.add(name);
      parameterLabel.add(label);
      stringValues.add(values);
      this.parametercount++;
  }
  
  /*public void newVstParameter(String name,String label,String[] values)
  {
      java.util.Vector<String> vals=new java.util.Vector<String>();
      for(int i=0;i<values.length;i++)
          vals.add(values[i]);
      this.newVstParameter(name,label,vals);
  }*/

  public String getName() { return this.name; }
  public void setName(String name) { this.name = name; }  
  
  public float StringToParameter(int index,String value)
  {
      float ret=0;
      if((index>=0)&&(index<parametercount)&&(stringValues.get(index)!=null))
      {
         int p=stringValues.get(index).indexOf(value);
         if(p!=-1){             
             int s=stringValues.get(index).size();          
             if(s>1) {
               ret=(float)p/(float)(s-1);               
             }             
         } else  try {
             if (value != null) ret=Float.parseFloat(value);      
          } catch(Exception e) {ret=0;}       
      } else try {
          if (value != null) ret=Float.parseFloat(value);      
      } catch(Exception e) {ret=0;}
      return ret;
  }
  
  public String ParameterToString(int index,float value)
  {
      String ret=""+value;
      if((index>=0)&&(index<parametercount)&&(stringValues.get(index)!=null))
      {
          int s=stringValues.get(index).size();
          if((s>=1)&&(value<=1f))
          {
             s=s-1;
             s=java.lang.Math.round(((float)s)*value);
             ret=stringValues.get(index).get(s);
          }          
      }
      return ret;
  }
  
  public String getParameterLabel(int index) {
    String label =null;
    if(index<this.parametercount) {
        label = this.parameterLabel.get(index);
    }
    if(label==null) label="";
    return label;
  }
  
  public synchronized String getParameterDisplay(int index) {
      String name=this.getParameterName(index);
      String erg="";
      if(parameterValues.containsKey(name)) {
          erg=erg+this.parameterValues.get(name);
      }
      return erg;
  }
  
  public String getParameterName(int index) {
   String label =null;
    if(index<this.parametercount) {
        label = this.parameterName.get(index);
    }
    if(label==null) label="";
    return label;
  }

public synchronized void setParameter(int index, float value) {  
    this.setParameter(index,this.ParameterToString(index, value));    
}

public synchronized void setParameter(int index,String value)
{
    if((index>=0)&&(index<parametercount)){
      String name=this.getParameterName(index);
      this.parameterValues.put(name, value);
    }
}

public synchronized Object getParameter(String key)
{
    return this.parameterValues.get(key);
}

public synchronized void setParameter(String key,Object value)
{
    this.parameterValues.put(key, value);
}

public boolean isVstParameter(String key) {
    return this.parameterName.contains(key);
}

public int VstParameterNumber(String key) {
    return this.parameterName.indexOf(key);
}

public synchronized float getParameter(int index) {
    if((index>=0)&&(index<this.parametercount))  {
       String name=this.getParameterName(index);
       String value=""+this.parameterValues.get(name);
       return this.StringToParameter(index,value);
    }        
    else return 0;
 }

}
