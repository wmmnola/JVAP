/*
 * Configuration.java
 *
 * Created on 28. Januar 2006, 09:13
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

package org.jvaptools.vstparam2midi;

import org.jvaptools.*;
import javax.sound.midi.*;

/**
 *
 * @author Daniel
 */
public class Configuration extends MidiDeviceImpl {
    
    vstmain owner;
    
    int[][] ctr2param;
    int[] param2ctr;
    int[] param2chan;
    VstSettings s;
    String name;
    String inDev;
    String outDev;
    
    /** Creates a new instance of Configuration */
    public Configuration(vstmain owner) {
        this.owner=owner;
        msg=new ShortMessage();
        s=new VstSettings();        
        ctr2param=new int[16][128];
        for(int i=0;i<128;i++)
            for(int j=0;j<16;j++)
                ctr2param[j][i]=-1;
       param2ctr=new int[0];
       param2chan=new int[0];
    }
    
    public boolean CreateDefautSettings(java.io.OutputStream out) {
        java.util.Properties p=new java.util.Properties();
        p.setProperty("Name","DefaultConfig");          
        p.setProperty("MIDI-Out","Dummy");
        p.setProperty("MIDI-In","Dummy");
        p.setProperty("ParameterCount", "2");
        p.setProperty("Parameter1Name", "Test1");
        p.setProperty("Parameter1Label", " label");
        p.setProperty("Parameter1Controller", "11");
        p.setProperty("Parameter1Channel", "1");        
        p.setProperty("Parameter1ValueCount", "3");
        p.setProperty("Parameter1Value1", "Val1");
        p.setProperty("Parameter1Value2", "Val2");
        p.setProperty("Parameter1Value3", "Val3");
        p.setProperty("Parameter2Name", "Test2");
        p.setProperty("Parameter2Label", " label2");
        p.setProperty("Parameter2Controller", "12");
        p.setProperty("Parameter2Channel", "16");       
        p.setProperty("Parameter2ValueCount", "0");
        try{
        p.storeToXML(out,"Generated default configuration");
        } catch(Exception e)
        {
            return false;
        }
        return true;
    }
    
    public boolean LoadSettings(java.io.InputStream input)
    {
        java.util.Properties p=new java.util.Properties();
        try{
          p.loadFromXML(input);
          name=LoadProperty(p,"Name");          
          outDev=LoadProperty(p,"MIDI-Out");
          inDev=LoadProperty(p,"MIDI-In");
          String numparams=LoadProperty(p,"ParameterCount");
          int c=Integer.parseInt(numparams);
          s=new VstSettings();
          ctr2param=new int[16][128];
          for(int i=0;i<128;i++)
              for(int j=0;j<16;j++)
                  ctr2param[j][i]=-1;
          param2ctr=new int[c];
          param2chan=new int[c];
          for(int i=0;i<c;i++) {
              String parametername=LoadProperty(p,"Parameter"+(i+1)+"Name");
              String parameterlabel=LoadProperty(p,"Parameter"+(i+1)+"Label");
              String parametercontroller=LoadProperty(p,"Parameter"+(i+1)+"Controller");              
              int ctr=Integer.parseInt(parametercontroller);
              String parameterchannel=LoadProperty(p,"Parameter"+(i+1)+"Channel");    
              int chan=Integer.parseInt(parameterchannel)-1;
              param2ctr[i]=ctr;
              param2chan[i]=chan;
              ctr2param[chan][ctr]=i;
              String valuecount=LoadProperty(p,"Parameter"+(i+1)+"ValueCount");
              int vc=Integer.parseInt(valuecount);
              java.util.Vector<String> paramvals=null;
              if(vc>0) {
                  paramvals=new java.util.Vector<String>();
                  for(int j=0;j<vc;j++)
                  {
                      String paramval=LoadProperty(p, "Parameter"+(i+1)+"Value"+(j+1));
                      if(!"".equals(paramval))
                        paramvals.add(paramval);
                  }                  
              } 
              s.newVstParameter(parametername,parameterlabel, paramvals);
              try{
                 String paramvalue=LoadProperty(p, "Parameter"+(i+1)+"DefValue");
                 if((paramvals==null)||(paramvals.indexOf(paramvalue)==-1)) {
                 try{ float fval=Float.parseFloat(paramvalue);
                     if(fval>1f) fval=fval/127f;
                     if(fval<0) fval=0;
                     paramvalue=""+fval;
                 } catch(Exception e) {}
                 }
                 s.setParameter(i,paramvalue);
              } catch(Exception e){}
          }          
          return true;
        } catch(Exception e)
        {
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(null,"Error in config-file: "+e.getMessage());
            return false;
        }
    }
    
    private String LoadProperty(java.util.Properties p,String key) throws Exception
    {
        String erg=p.getProperty(key);
        if(erg==null) throw new java.util.prefs.InvalidPreferencesFormatException("Property "+key+" is missing!");
        return erg;
    }
    
    public VstSettings getProtoSettings()
    {
        return s;
    }
    
    public String getName()
    {
        return this.name;
    }
    
    public String getInDevName()
    {
        return this.inDev;
    }
    
    public String getOutDevName()
    {
        return this.outDev;
    }
    
    //When midi note is recieved
    public void send(MidiMessage m,long timestamp)
    {
        int s=m.getStatus();
        if(((int)(s & 0xF0))==ShortMessage.CONTROL_CHANGE) {
            int chan=(int)(s & 0x0F);
            byte[] data=m.getMessage();
            if(data.length>=3) {
                int contrnum=(int)(data[1]&0xFF);
                int contrval=(int)(data[2]&0xFF);
                this.controller2param(contrnum, chan,contrval);
            }
        } else if((((int)(s & 0xF0))==ShortMessage.NOTE_ON)) {
            this.transmit(m, timestamp);
        }
    }
    
    public void vstParameterChanged(int param,float val)
    {
        if((param!=owner.transmitparam)&&(param2ctr!=null)&&(param<param2ctr.length)&&(param>=0)&&(param<param2chan.length))
        {
            int ctrval=(int)(val*127);
            int ctrnum=param2ctr[param];
            int chan=param2chan[param];
            this.transmitControlChange(ctrnum,chan,ctrval);
        }
    }
    
    ShortMessage msg;
    
    private void transmitControlChange(int ctr,int chan,int val)
    {        
        try{
            //System.out.println("Transmit "+ctr+" "+val);
          msg.setMessage(msg.CONTROL_CHANGE,chan,ctr, val);
          this.transmit(msg,-1);
        } catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    private void controller2param(int ctrnum,int chan,int ctrval)
    {
        if((ctr2param!=null)&&(128>ctrnum)&&(ctrnum>=0)&&(chan<16)&&(chan>=0))
        {
            if(ctrval<128) {
                int param=ctr2param[chan][ctrnum];
                if(param!=-1) {
                  float val=((float)ctrval)/127.0f;
                  owner.sendParameterToHost(param, val);                
                }
            }
        }
    }
    
    public Info getDeviceInfo(){
        return new MidiDeviceImplInfo("VstParameter2Midi","VstParameter2Midi Device");
    }
    
    public static void main(String[] args)
    {
        Configuration c=new Configuration(null);
        try{
        java.io.OutputStream out= new java.io.FileOutputStream("config.xml");
        c.CreateDefautSettings(out);
        } catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
}
