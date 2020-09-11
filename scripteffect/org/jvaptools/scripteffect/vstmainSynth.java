/*
 * vstmainMIDI.java
 *
 * Created on 6. December 2009, 15:00
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

package org.jvaptools.scripteffect;

/**
 *
 * @author Daniel
 */
public class vstmainSynth extends org.jvaptools.VstPluginImpl {
    

public vstmainSynth(long wrapper)
{
    this(wrapper,4);
}
    
    /** Creates a new instance of vstmainMIDI */
public vstmainSynth(long wrapper,int numParams) {
    super(wrapper);
    org.jvaptools.VstSettings s=new org.jvaptools.VstSettings();
    for(int i=0;i<numParams;i++) {
        int num=(i/26);
        String numtext="";
        if(num!=0) numtext=""+num;
        s.newVstParameter(""+(char)('a'+(i%26))+numtext,"", null);    
    }
    s.setParameter("source", "import org.jvaptools.scripteffect.*;\n\n"+
      "org.jvaptools.VstPluginImpl plugin;\n\n"+
      "public void init(org.jvaptools.VstPluginImpl owner) {this.plugin=owner; }\n\n"+
      "public void close() {}\n\n"+
      "public void onMidiEvent(javax.sound.midi.MidiMessage message, long timeStamp) {}\n\n"+
      "public void processReplacing(vstmainSynth plugin,float a,float b,float c,float d, float[][] outputs, int sampleFrames)\n"+
      "{ \n"+
      "float[] out1 = outputs[0];\n"+
      "float[] out2 = outputs[1];\n"+
      "for (int i = 0; i < sampleFrames; i++) {\n"+
      "out1[i] = 0;\n"+
      "out2[i] = 0;\n"+
      "} }\n");
    this.script=new PluginScriptSynth() {
        org.jvaptools.VstPluginImpl plugin;
      public void init(org.jvaptools.VstPluginImpl owner){this.plugin=owner; }
    
    public void close(){}
    
    public void onMidiEvent(javax.sound.midi.MidiMessage message, long timeStamp){}

            @Override
            public void processReplacing(vstmainSynth plugin, float a, float b, float c, float d, float[][] outputs, int sampleFrames) {
                for (int i=0; i<sampleFrames; i++){
                    outputs[0][i] = 0;
                    outputs[1][i] = 0;
                }
            }
        };
    this.setNumPrograms(16, s);  
    log(this.getParameterName(0));
    log(""+this.getParameter(0));
    //this.setProgram(0);     
    this.setNumInputs(0);// no input
    this.setNumOutputs(2);
    this.hasVu(false);
    this.hasClip(false);
    this.canProcessReplacing(true);
    this.isSynth(true);
    this.setUniqueID(366783546);
    this.suspend();
}

  public void resume() {
     this.wantEvents(1); //deprecated as of vst2.4
     					 //keep it anyways to be backward compatible...
  }
  
  private PluginScriptSynth script=null;
  
  public void setPlugin(PluginScriptSynth script) {
      if(script!=null) {
          script.init(this);
          PluginScriptSynth old=this.script;
          this.script=script;
          if(old!=null)
            old.close();
          
      }
  }
  
  public vstguiSynth gui=null;

 public void onMidiEvent(javax.sound.midi.MidiMessage message, long timeStamp) {
      if(script!=null)
          script.onMidiEvent(message, timeStamp);
  }
  
public void onParameterChange(int index) {
    if(this.gui!=null) {
        gui.parameterChanged(index);
    }
}
 
public boolean ioChanged(){
    boolean erg=super.ioChanged();
    if(gui!=null) gui.ioChanged();
    return erg;
}

public void onProgramChange() {
    if(gui!=null)
        gui.programChanged(); 
}

public void onLoadData() {
    if(gui!=null)
        gui.programChanged(); 
}

public void onSaveData() {
    if(gui!=null)
        gui.saveData(); 
}
  
public void open()
{
    super.open();
    if(this.gui!=null)
        this.gui.init(this);
    if(this.script!=null)
        this.script.init(this);
}

public void close()
{
    if(this.script!=null)
        this.script.close();
   super.close();
}
    
 public void process(float[][] inputs, float[][] outputs, int sampleFrames) {
     script.processReplacing(this,this.getParameter(0),this.getParameter(1),this.getParameter(2),this.getParameter(3),
             outputs, sampleFrames);
      super.process(inputs, outputs, sampleFrames);      
  }

    public void processReplacing(float[][] inputs, float[][] outputs, int sampleFrames) {   
       script.processReplacing(this,this.getParameter(0),this.getParameter(1),this.getParameter(2),this.getParameter(3),
             outputs, sampleFrames);
      super.processReplacing(inputs, outputs, sampleFrames);
  }

     public String getEffectName() { return "Scripteffect-Synth"; }
  public String getVendorString() { return "JVAPTools"; }
  public String getProductString() { return "Java Scripteffect Synth VST-Plugin"; }
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
      
public boolean string2Parameter(int index, String value) {   
     return false;
  }

}
