/*
 * vstmain.java
 *
 * Created on 8. März 2006, 19:02
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
public class vstmain extends org.jvaptools.VstPluginImpl { 
    

public vstmain(long wrapper)
{
    this(wrapper,4);
}
    
    /** Creates a new instance of vstmain */
public vstmain(long wrapper,int numParams) {
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
      "public void processReplacing(vstmain plugin,float a,float b,float c,float d,float[][] inputs, float[][] outputs, int sampleFrames)\n"+
      "{ \n"+
      "float[] out1 = outputs[0];\n"+
      "float[] out2 = outputs[1];\n"+
      "float[] in1 = inputs[0];\n"+
      "float[] in2 = inputs[1];\n"+
      "for (int i = 0; i < sampleFrames; i++) {\n"+
      "out1[i] = in1[i];\n"+
      "out2[i] = in2[i];\n"+
      "} }\n");
    this.script=new PluginScript() {    
        org.jvaptools.VstPluginImpl plugin;
      public void init(org.jvaptools.VstPluginImpl owner){this.plugin=owner; }
    
    public void close(){}
    
    public void onMidiEvent(javax.sound.midi.MidiMessage message, long timeStamp){}
public void processReplacing(vstmain plugin,float a,float b,float c,float d,float[][] inputs, float[][] outputs, int sampleFrames)
{ 
 float[] out1 = outputs[0];
      float[] out2 = outputs[1];
      float[] in1 = inputs[0];
      float[] in2 = inputs[1];
     for (int i = 0; i < sampleFrames; i++) {
        out1[i] = in1[i];
        out2[i] = in2[i];
     }
}
};
    this.setNumPrograms(16, s);  
    log(this.getParameterName(0));
    log(""+this.getParameter(0));
    //this.setProgram(0);     
    this.setNumInputs(2);// no input
    this.setNumOutputs(2);
    this.hasVu(false);
    this.hasClip(false);
    this.canProcessReplacing(true);
    this.isSynth(false);
    this.setUniqueID(366786785);
    this.suspend();
}

  private PluginScript script=null;
  
  public void setPlugin(PluginScript script) {      
      if(script!=null) {
          script.init(this);
          PluginScript old=this.script;
          this.script=script;
          if(old!=null)
            old.close();
          
      }
  }
  
  public vstgui gui=null;

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
             inputs, outputs, sampleFrames);             
      super.process(inputs, outputs, sampleFrames);      
  }

    public void processReplacing(float[][] inputs, float[][] outputs, int sampleFrames) {   
      script.processReplacing(this,this.getParameter(0),this.getParameter(1),this.getParameter(2),this.getParameter(3), 
             inputs, outputs, sampleFrames);  
      super.processReplacing(inputs, outputs, sampleFrames);
  }

     public String getEffectName() { return "Scripteffect"; }
  public String getVendorString() { return "JVAPTools"; }
  public String getProductString() { return "Java Scripteffect VST-Plugin"; }
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
