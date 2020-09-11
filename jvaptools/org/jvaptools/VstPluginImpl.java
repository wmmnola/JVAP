/*
 * VstPluginImpl.java
 *
 * Created on 24. Januar 2006, 00:04
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
import jvst.wrapper.valueobjects.*;

/**
 *
 * @author Daniel
 */
public abstract class VstPluginImpl extends jvst.wrapper.VSTPluginAdapter implements SequencerFactory {
    
    jvst.wrapper.valueobjects.VSTEvents[] Events;
    jvst.wrapper.valueobjects.VSTMidiEvent[][] EventBuffer;
    
    javax.sound.midi.ShortMessage HostMidiMessage;
    
    private org.jvaptools.VstPluginMidiDevice device;
    
    public org.jvaptools.VstTimer timer;
    public boolean enabletimer;
    public boolean enablemidi;
    public boolean enableautomation;
    public String directory;
    
    int aktevents;
    Object eventlock;
    int eventcount;
    
    int num_programs;
    protected int program;
    protected VstSettings[] Programs;
    
    public VstSettings GlobalSettings;
    
    /** Creates a new instance of VstPluginImpl */
    public VstPluginImpl(long wrapper) {
        super(wrapper);
        this.timer=new org.jvaptools.VstTimer(0, 44100, 120);
        eventlock=new Object();
        aktevents=0;
        eventcount=0;
        Events=new jvst.wrapper.valueobjects.VSTEvents[2];
        Events[0]=new jvst.wrapper.valueobjects.VSTEvents();
        Events[1]=new jvst.wrapper.valueobjects.VSTEvents();
        EventBuffer=new jvst.wrapper.valueobjects.VSTMidiEvent[2][];
        EventBuffer[0]=new jvst.wrapper.valueobjects.VSTMidiEvent[255];
        EventBuffer[1]=new jvst.wrapper.valueobjects.VSTMidiEvent[255];        
        for(int i=0;i<EventBuffer[0].length;i++) {
            EventBuffer[0][i]=new jvst.wrapper.valueobjects.VSTMidiEvent();
            EventBuffer[0][i].setByteSize(24);
            EventBuffer[0][i].setType(jvst.wrapper.valueobjects.VSTMidiEvent.VST_EVENT_MIDI_TYPE);
            EventBuffer[0][i].setReserved1((byte)0);
            EventBuffer[0][i].setReserved2((byte)0);
            EventBuffer[1][i]=new jvst.wrapper.valueobjects.VSTMidiEvent();
            EventBuffer[1][i].setByteSize(24);
            EventBuffer[1][i].setType(jvst.wrapper.valueobjects.VSTMidiEvent.VST_EVENT_MIDI_TYPE);
            EventBuffer[1][i].setReserved1((byte)0);
            EventBuffer[1][i].setReserved2((byte)0);
        }
        Events[0].setNumEvents(0);
        Events[0].setEvents(EventBuffer[0]);
        Events[1].setNumEvents(0);
        Events[1].setEvents(EventBuffer[1]);
        device=new org.jvaptools.VstPluginMidiDevice(this);
        HostMidiMessage=new javax.sound.midi.ShortMessage();        
        this.setNumPrograms(0, null);
        this.GlobalSettings=new VstSettings();
        this.program=0;
        this.programsAreChunks(true);
        enabletimer=true;
        enablemidi=true;
        enableautomation=true;   
        this.directory=this.getLogBasePath();
        log(directory);
        if(this.directory==null)
            this.directory=System.getProperty("user.dir");        
  
    }
        
    public void setNumPrograms(int num,VstSettings protosettings) {
        this.num_programs=num;
        Programs=new VstSettings[num_programs];
        for(int i=0;i<this.num_programs;i++) {
            Programs[i]=new VstSettings(protosettings);
        }
        this.program=0;
    }
    
    public org.jvaptools.VstPluginMidiDevice getMidiDevice() {
        return device;
    }
    
    public void sendMidiEvent(byte b1,byte b2,byte b3,int delta,int notelen,int noteoffset,byte detune,byte noteoffvel,int flags)
    {
        synchronized(eventlock) {
            if(this.eventcount<255) {               
               EventBuffer[this.aktevents][this.eventcount].setDeltaFrames(delta);
               EventBuffer[this.aktevents][this.eventcount].setFlags(flags);
               EventBuffer[this.aktevents][this.eventcount].setNoteLength(notelen);
               EventBuffer[this.aktevents][this.eventcount].setNoteOffset(noteoffset);      
               EventBuffer[this.aktevents][this.eventcount].setDetune(detune);
               EventBuffer[this.aktevents][this.eventcount].setNoteOffVelocity(noteoffvel);
               byte[] data=EventBuffer[this.aktevents][this.eventcount].getData();  
               data[0]=b1;
               data[1]=b2;
               data[2]=b3;
               //EventBuffer[this.aktevents][this.eventcount].setData(data);
               this.eventcount++;
            }  else log("Midi event buffer overflow!");
        }
        
    }  
    
    boolean hasProcess=false;
    
    public boolean canParameterBeAutomated(int index) {
        return true;
    }
    
    public void setNumOutputs(int o)
    {
        super.setNumOutputs(o);
        this.hasProcess=(o>0);
    }
    
    public void send(javax.sound.midi.MidiMessage message, long timeStamp)
    {
        byte[] data=message.getMessage();
        switch(message.getLength()) {
            case 1: this.sendMidiEvent(data[0], (byte)0,(byte)0,0,0,0,(byte)0,(byte)0,0);
                    break;
            case 2: this.sendMidiEvent(data[0], data[1],(byte)0,0,0,0,(byte)0,(byte)0,0);
                    break;
            case 3: this.sendMidiEvent(data[0], data[1],data[2],0,0,0,(byte)0,(byte)0,0);
                    break;
        }
        if(!this.hasProcess)
            this.sendMidiEvents();
    }
    
    public void sendMidiEvents() {        
        int oldevents=this.aktevents;
        int newevents=(oldevents==0)?1:0;
        int oldnum=0;
        synchronized(eventlock) {
            oldnum=this.eventcount;
            this.aktevents=newevents;
            this.eventcount=0;
        }
        //log("send midi events "+oldnum);
        Events[oldevents].setNumEvents(oldnum);
        if(oldnum>0){
           this.sendVstEventsToHost(Events[oldevents]);
        }
    }
    
    int timeInfoRefreshRate = 50;
    
    int lastTimeInfoDuration =0;
    
    public void vstTimer(int sampleFrames) {
        //if((this.timer.info==null)||(lastTimeInfoDuration>this.timer.samplerate/50)) {
           lastTimeInfoDuration =0; 
           this.timer.setTimingInfo(this.getTimeInfo(
                   jvst.wrapper.valueobjects.VSTTimeInfo.VST_TIME_PPQ_POS_VALID|
                   jvst.wrapper.valueobjects.VSTTimeInfo.VST_TIME_TEMPO_VALID|
                   jvst.wrapper.valueobjects.VSTTimeInfo.VST_TIME_BARS_VALID|
                   jvst.wrapper.valueobjects.VSTTimeInfo.VST_TIME_CLOCK_VALID|
                   jvst.wrapper.valueobjects.VSTTimeInfo.VST_TIME_CYCLE_POS_VALID|
                   jvst.wrapper.valueobjects.VSTTimeInfo.VST_TIME_NANOS_VALID|
                   jvst.wrapper.valueobjects.VSTTimeInfo.VST_TIME_SMPTE_VALID|
                   jvst.wrapper.valueobjects.VSTTimeInfo.VST_TIME_TIME_SIG_VALID
                 ));
        //} else {
        //    lastTimeInfoDuration+=sampleFrames;
        //}        
        this.timer.processEvents(sampleFrames);
        this.timer.advance(sampleFrames);
    }
    
    public void process(float[][] inputs, float[][] outputs, int sampleFrames) {
      if(this.enabletimer) this.vstTimer(sampleFrames);
      if(this.enablemidi) this.sendMidiEvents();   
      if(this.enableautomation) this.sendAutomationData();
  }

    public void processReplacing(float[][] inputs, float[][] outputs, int sampleFrames) {   
      if(this.enabletimer) this.vstTimer(sampleFrames);
      if(this.enablemidi) this.sendMidiEvents();   
      if(this.enableautomation) this.sendAutomationData();
  }
    
   public boolean processVariableIo (VSTVariableIO vario) {
       if(vario==null) return true;
       else {
           float[][] inputs=vario.getInputs();
           float[][] outputs=vario.getOutputs();
           int sampleframes=vario.getNumSamplesInput();
           this.processReplacing(inputs, outputs, sampleframes);
           vario.setNumSamplesInputProcessed(sampleframes);
           vario.setNumSamplesOutputProcessed(sampleframes);
           log("processVariableIO");
       }
       return true;
   }
    
     public int processEvents (jvst.wrapper.valueobjects.VSTEvents ev) {
    for (int i = 0; i < ev.getNumEvents(); i++) {
      if (ev.getEvents()[i].getType() == jvst.wrapper.valueobjects.VSTEvent.VST_EVENT_MIDI_TYPE) {
         jvst.wrapper.valueobjects.VSTMidiEvent event = (jvst.wrapper.valueobjects.VSTMidiEvent)ev.getEvents()[i];
          byte[] midiData = event.getData();
          try{
          HostMidiMessage.setMessage((int)(midiData[0] & 0xFF),
                  (int)(midiData[1] & 0xFF),(int)(midiData[2] & 0xFF));
          } catch(javax.sound.midi.InvalidMidiDataException e)
          {
              log(e.toString()+"\n"+e.getStackTrace());
          }
          this.onMidiEvent(HostMidiMessage,-1);
          device.transmit(HostMidiMessage, -1);         
      }
    }
    return 1;	// want more
  }
     
  public void onMidiEvent(javax.sound.midi.MidiMessage message, long timeStamp) {
      
  }
     
  public int getNumPrograms() { return this.num_programs; }
  public int getNumParams() 
  { if(this.num_programs>0) 
      return this.Programs[this.program].parametercount; 
    else return 0;
  } 
  public int getProgram() { return this.program; } 
  public void setProgram(int index) {
      //log("setProgram"+index);
     this.program=index;   
     this.onProgramChange();
  }
  
  public Object getParameter(String key){
      return this.Programs[this.program].getParameter(key);
  }
  
  int AutomationDataIndex;
  Object DataIndexLock=new Object();
  Object[] AutomationLock;
  float[][] AutomationData;
  boolean[][] IsAutomated;
  boolean[] Automated=new boolean[2];
  
  /* Creates a new VST parameter after initialization. If the parameter exists already nothing is created.
   * To inform the host please call ioChanged();
   * @param name Name of the new VST parameter
   * @param label Label of the new VST parametr
   * @param values The values the parameter can have. If this parameter is null, the parameter represents a number from 0.0 to 1.0.
   */
  public void newVSTParameter(String name,String label,java.util.Vector<String> values) {
      if(!this.Programs[this.program].parameterName.contains(name)) {
          for(int i=0;i<this.getNumPrograms();i++) {
              this.Programs[i].newVstParameter(name, label, values);
          }
          this.initParameterAutomation();
      }
  }
  
  public void initParameterAutomation() {
      AutomationDataIndex=0;  
      AutomationLock=new Object[2];
      AutomationLock[0]=new Object();
      AutomationLock[1]=new Object();      
      AutomationData=new float[2][];
      AutomationData[0]=new float[this.getNumParams()];
      AutomationData[1]=new float[this.getNumParams()];
      IsAutomated=new boolean[2][];
      IsAutomated[0]=new boolean[this.getNumParams()];
      IsAutomated[1]=new boolean[this.getNumParams()];      
  }
  
  public void open() {
      /*this.directory=this.getDirectory();
      if(this.directory==null)
            this.directory=System.getProperty("user.dir");        */
      this.initParameterAutomation();
      //Load global settings
      try{
          java.io.File f=new java.io.File(this.directory, this.getEffectName()+"_settings.dat");
          java.io.ObjectInputStream in=new java.io.ObjectInputStream(new java.io.FileInputStream(f));
          this.GlobalSettings=(VstSettings)in.readObject();
          in.close();
      } catch(Exception e) { log(e.getMessage()); }
  }
  
  public void close() {
      //Save global settings
      try{
          java.io.File f=new java.io.File(this.directory, this.getEffectName()+"_settings.dat");
          java.io.ObjectOutputStream out=new java.io.ObjectOutputStream(new java.io.FileOutputStream(f));
          out.writeObject(this.GlobalSettings);
          out.flush();
          out.close();
      } catch(Exception e) { log(e.getMessage()); }      
  }
  
  /*Use this metjod to change vst parameters, automation data is then later send to the vst host*/
  public void setParameter(String key,Object value) {
      this.Programs[this.program].setParameter(key, value);
      if(this.Programs[this.program].isVstParameter(key)) {
          int num=this.Programs[this.program].VstParameterNumber(key);
          if(num!=-1) {
            //this.setParameterAutomated(num,this.Programs[this.program].getParameter(num));
            int index=0;
            synchronized(DataIndexLock){
              index=AutomationDataIndex;
            }
            synchronized(AutomationLock[index]){
                Automated[index]=true;
                IsAutomated[index][num]=true;
                AutomationData[index][num]=this.Programs[this.program].getParameter(num);
            }  
          }
      }
  }

  /* This must be called from process to acually send the changed parameters*/
  public void sendAutomationData() {
      int index=0;
      synchronized(DataIndexLock) {
          index=AutomationDataIndex;
          AutomationDataIndex=(AutomationDataIndex==0)?1:0;
      }
      synchronized(AutomationLock[index]) {
          if(Automated[index]) {
              for(int i=0;i<this.getNumParams();i++) {
                  if(IsAutomated[index][i])
                  {                      
                      IsAutomated[index][i]=false;
                      // Send automation data
                      this.setParameterAutomated(i,AutomationData[index][i]);
                  }
              }
              Automated[index]=false;
          }
      }
  }
  /*Sends all vst parameters to the host*/
  public void sendParameterToHost()
  {
      int index=0;
      synchronized(DataIndexLock) {
          index=AutomationDataIndex;          
      }
      synchronized(AutomationLock[index]) {
              for(int i=0;i<this.getNumParams();i++) {                                       
                      IsAutomated[index][i]=true;
                      AutomationData[index][i]=this.getParameter(i);                  
              }
              Automated[index]=true;          
      }
  }
  
  public void onProgramChange() {
  //    this.sendParameterToHost();
  }

  public void setProgramName(String name) {
      if(this.num_programs>0) 
        this.Programs[this.program].setName(name);
  }

  public String getProgramName() {
        if(this.num_programs>0)  {
           String erg=this.Programs[this.program].getName();
           if(VstSettings.DEFAULT_NAME.equals(erg))
               erg=erg+" "+this.program;
           return erg;
        }
        else return "";
}

  public String getParameterLabel(int index) {    
    if(this.num_programs>0) 
           return this.Programs[this.program].getParameterLabel(index);
        else return "";
  }

  public String getParameterDisplay(int index) {          
      if(this.num_programs>0) 
           return this.Programs[this.program].getParameterDisplay(index);
        else return "";
  }

  public String getParameterName(int index) {       
    if(this.num_programs>0) 
           return this.Programs[this.program].getParameterName(index);
        else return "";
  }

  
  /* This method must not be called from the Java program but only from the vst host!!*/
  public void setParameter(int index, float value) {
      if(this.num_programs>0) {
           this.Programs[this.program].setParameter(index,value);  
           this.onParameterChange(index);
      }
  }
    
  public void onParameterChange(int index) {
  //    this.sendParameterToHost();
  }

  
  public float getParameter(int index) { 
    float erg=0f;
    if(this.num_programs>0) 
           erg= this.Programs[this.program].getParameter(index);
    //log("getParameter "+index+" erg "+erg);
    return erg;
  }

public String getProgramNameIndexed(int category, int index) {
      if(this.num_programs>0)  {
           String erg=this.Programs[index].getName();
           if(VstSettings.DEFAULT_NAME.equals(erg))
               erg=erg+" "+index;
           return erg;
        }
        else return "";
  }

  public void onSaveData()
  {
      
  }

  public void onLoadData()
  {
      //this.sendParameterToHost();
  }

  
   public int getChunk(byte data[][], boolean isPreset)
  {
       this.onSaveData(); 
      if(data.length>0)
      {          
          try{
          java.io.ByteArrayOutputStream bytestream=new java.io.ByteArrayOutputStream();
          java.io.ObjectOutputStream objectStream=new java.io.ObjectOutputStream(bytestream);
          if(isPreset)
              objectStream.writeObject(this.Programs[this.program]);
          else 
              objectStream.writeObject(this.Programs);
          objectStream.close();
          bytestream.close();
          byte[] bytearray=bytestream.toByteArray();          
          data[0]=bytearray;
          return bytearray.length;
          } catch(java.io.IOException e)
          {
              log("Fehler beim Speichern der Einstellungen! "+e.getStackTrace());
              e.printStackTrace();
              return 0;
          }
      } else return 0;
  }
  
  public int setChunk(byte data[], int byteSize, boolean isPreset)
  {
      if(byteSize>0) {          
          VstSettings[] newsettings=null;
          if(isPreset) newsettings=new VstSettings[1];
          try{
              java.io.ByteArrayInputStream bytestream=new java.io.ByteArrayInputStream(data);
              java.io.ObjectInputStream objectstream=new java.io.ObjectInputStream(bytestream);
              if(isPreset)
                  newsettings[0]=(VstSettings)objectstream.readObject();
              else newsettings=(VstSettings[])objectstream.readObject();
              if(isPreset)
                  this.Programs[this.program]=newsettings[0];
              else
                  this.Programs=newsettings;
              this.onLoadData();
              return byteSize;
          } catch(Exception e)
          {
              log("Fehler beim Laden der Einstellungen! "+e.getStackTrace());
              e.printStackTrace();
              return 0;
          }
      } else return 0;
  }  
     
  
 // midi program names:
// No Program names!!

  public int getMidiProgramName(int channel, MidiProgramName mpn) {
    return 0;
  }

  public int getCurrentMidiProgram (int channel, MidiProgramName mpn) {    
    return -1;
  }

  public int getMidiProgramCategory (int channel, MidiProgramCategory cat) {
      return 0;
  }

  public boolean hasMidiProgramsChanged (int channel) {
    return false;
  }

  public boolean getMidiKeyName (long channel, MidiKeyName key) {
    return false;
  }

  public boolean string2Parameter(int index, String value) {   
    //return wt.string2Parameter(index, value);
    //this.setParameter(index,value);
    return false;
  }
    
  public javax.sound.midi.Sequencer getSequencer(javax.sound.midi.MidiDevice outDevice)
    {
        try {
            javax.sound.midi.Sequencer s=javax.sound.midi.MidiSystem.getSequencer(false);
            if(outDevice!=null) {
                s.getTransmitter().setReceiver(outDevice.getReceiver());
            }            
            s.open();
            return s;
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }  
}
