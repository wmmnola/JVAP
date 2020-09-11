/*
 * vstmain.java
 *
 * Created on 26. Januar 2006, 23:14
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.jvaptools.vstparam2midi;

/**
 *
 * @author Daniel
 */

import org.jvaptools.*;

public class vstmain extends VstPluginImpl { 
    
    Configuration c;
    javax.sound.midi.MidiDevice out;
    javax.sound.midi.MidiDevice in;
    public int transmitparam;
    
    /** Creates a new instance of vstmain */
public vstmain(long wrapper) {
    super(wrapper);
    c=new Configuration(this);
    log(getClass().getResource("/config.xml").toString());
    c.LoadSettings(getClass().getResourceAsStream("/config.xml"));
    this.setNumPrograms(16, c.getProtoSettings());
    this.transmitparam=this.Programs[0].parameterName.indexOf("TransmitOnLoad");
    //this.setProgram(0);     
    this.setNumInputs(0);// no input
    this.setNumOutputs(2);
    this.hasVu(false);
    this.hasClip(false);
    this.canProcessReplacing(true);
    this.isSynth(true);
    this.setUniqueID(3214242);
    this.suspend();
    try{
      javax.sound.midi.MidiDevice d=this.getMidiDevice();
      out=this.getOutDevice(c.getOutDevName());  
      in=this.getInDevice(c.getInDevName()); 
      if((out==null)&&(!"none".equals(c.getOutDevName()))) {
          javax.swing.JOptionPane.showMessageDialog(null,"Error: Could not open midi out "+c.getOutDevName());
      }
      if((in==null)&&(!"none".equals(c.getInDevName()))) {
          javax.swing.JOptionPane.showMessageDialog(null,"Error: Could not open midi in "+c.getInDevName());          
      }
      if(out!=null) out.open();
      if(in!=null) in.open();
      if(d!=null) d.open(); 
      if(c!=null) c.open();      
      // VST In -> Midi Out
      if((out!=null)&&(d!=null)) d.getTransmitter().setReceiver(out.getReceiver());
      // VST-Parameter -> Midi Out
      if((out!=null)&&(c!=null)) c.getTransmitter().setReceiver(out.getReceiver());
      // Midi In -> VST-Parameter
      if((in!=null)&&(c!=null)) in.getTransmitter().setReceiver(c.getReceiver());
      // Midi In -> VST Out
      if((in!=null)&&(d!=null)) in.getTransmitter().setReceiver(d.getReceiver());
      
    } catch(Exception e)
    {
        log(""+e.getStackTrace());
    }
}

public void open()
{
    super.open();
}

public void close()
{
    if(in!=null) in.close();
    if(out!=null) out.close();
    if(this.getMidiDevice()!=null) this.getMidiDevice().close();
    if(c!=null) c.close();
    super.close();
}

public javax.sound.midi.MidiDevice getOutDevice(String name) throws Exception{
    javax.sound.midi.MidiDevice erg=null;
    javax.sound.midi.MidiDevice.Info[] mi=javax.sound.midi.MidiSystem.getMidiDeviceInfo();
    for(int i=0;i<mi.length;i++) {
        //log(mi[i].getName());
        if(mi[i].getName().equals(name))
        {
            try{
                javax.sound.midi.MidiDevice tmpd=null;
                tmpd=javax.sound.midi.MidiSystem.getMidiDevice(mi[i]);
                if(tmpd.getMaxReceivers()!=0) {
                    erg=tmpd;
                }
            } catch(Exception e)
            {                
            }
        }
    }
    return erg;
}

public javax.sound.midi.MidiDevice getInDevice(String name) throws Exception{
    javax.sound.midi.MidiDevice erg=null;
    javax.sound.midi.MidiDevice.Info[] mi=javax.sound.midi.MidiSystem.getMidiDeviceInfo();
    for(int i=0;i<mi.length;i++) {
        //log(mi[i].getName());
        if(mi[i].getName().equals(name))
        {
            try{
                javax.sound.midi.MidiDevice tmpd=null;
                tmpd=javax.sound.midi.MidiSystem.getMidiDevice(mi[i]);
                if(tmpd.getMaxTransmitters()!=0) {
                    erg=tmpd;
                }
            } catch(Exception e)
            {                
            }
        }
    }
    return erg;
}
    
     public String getEffectName() { return c.getName(); }
  public String getVendorString() { return "Music-Logic"; }
  public String getProductString() { return "vstparameter to MIDI conversion: "+c.getName(); }
  public boolean setBypass(boolean value) { return false; }
  public int getPlugCategory() { return this.PLUG_CATEG_UNKNOWN; }
  
  public int canDo(String feature) {
    int ret = this.CANDO_NO;

    if (this.CANDO_PLUG_RECEIVE_VST_EVENTS.equals(feature)) ret = this.CANDO_YES;
    if (this.CANDO_PLUG_REVEIVE_VST_MIDI_EVENT.equals(feature)) ret = this.CANDO_YES;    
    if (this.CANDO_PLUG_SEND_VST_EVENTS.equals(feature)) ret = this.CANDO_YES;
    if (this.CANDO_PLUG_SEND_VST_MIDI_EVENT.equals(feature)) ret = this.CANDO_YES;
    
    return ret;
  }
  
  public void sendParameterToHost(int param,float val)
  {
      //log("send Parameter to Host");
      super.setParameter(param, val);
      this.setParameterAutomated(param, val);
  }
  
  public void transmitAllParameterToMidi(boolean check)
  {   if(!check||"on".equals(this.Programs[this.program].getParameter("TransmitOnLoad")))
      for(int i=0;i<this.getNumParams();i++)
          c.vstParameterChanged(i,this.getParameter(i));
  }
  
  public void onLoadData()
  {
      this.transmitAllParameterToMidi(true);
  }
  
  public void onProgramChange()
  {
      this.transmitAllParameterToMidi(true);
  }
  
  public void setParameter(int index, float value) {
      //log("setParameter");
      super.setParameter(index, value);
      c.vstParameterChanged(index,value);
  }
      
public boolean string2Parameter(int index, String value) {   
     return false;
  }

}
