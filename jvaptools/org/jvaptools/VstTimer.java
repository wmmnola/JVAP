/*
 * VstTimer.java
 *
 * Created on 21. Februar 2006, 20:23
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
public class VstTimer {
    
    double sampleposition;
    
    double samplerate;
    
    double bpm;
    
    double sampleperbeat;
    
    public double ppqoffset;
    
    boolean HostSync;
    
    java.util.Vector<VstSequencer> sequencer=new java.util.Vector<VstSequencer>();
    
    /** Creates a new instance of VstTimer */
    public VstTimer(long startpos,long samplerate,double bpm) {
       this.setSamplepos(startpos);
       this.bpm=120;
       this.setSamplerate(samplerate);
       this.setBPM(bpm);
       this.info=null;
       this.HostSync=false;
       this.ppqoffset=0;
    }
    
    public void addSequencer(VstSequencer s){
        sequencer.add(s);
    }
    
    public void removeSequencer(VstSequencer s) {
        sequencer.remove(s);
    }
    
    public double getBPM(){
        return this.bpm;
    }
    
    public double getMPQ() {
        return 60.000f/this.bpm;
    }
    
    public double getPPQ(double samplepos) {
       return ppqoffset+samplepos/this.sampleperbeat;    
    }
    
    public double getPPQ()
    {
        return this.getPPQ(this.sampleposition);
    }
    
    public double getSamplePosition(double ppq)
    {
        return ppq*this.sampleperbeat-ppqoffset;
    }
    
    public int getSampleOffset(double ppq) {
        return (int)(this.getSamplePosition(ppq)-sampleposition);
    }
    
    public void processEvents(int sampleFrames)
    {
        for(int i=0;i<this.sequencer.size();i++) {
            this.sequencer.get(i).processEvents(this.getPPQ(this.sampleposition+sampleFrames));
        }
    }
    
    public void setSamplepos(long samplepos) {
        this.sampleposition=samplepos;
    }
    
    public void setSamplerate(long samplerate) {
        this.samplerate=samplerate;
        this.sampleperbeat=(double)(samplerate)/bpm*60.0d;
    }
    
    public void setBPM(double bpm) {
        this.bpm=bpm;
        this.sampleperbeat=(double)(samplerate)/bpm*60.0d;
    }
    
    public void advance(long samples) {
        this.sampleposition+=samples;
    }
     
    public jvst.wrapper.valueobjects.VSTTimeInfo info;
    public double TimeInfoSamplePos;
    
    public void setTimingInfo(jvst.wrapper.valueobjects.VSTTimeInfo info) {
        this.info=info;
        if(info!=null) {
            int flags=info.getFlags();             
            if((((flags&info.VST_TIME_TEMPO_VALID)!=0)&&(this.bpm!=info.getTempo()))||(this.samplerate!=info.getSampleRate())){ 
                double oldpqq=this.getPPQ();
                this.bpm=info.getTempo();
                this.setSamplerate((long)info.getSampleRate());
                this.ppqoffset-=(this.getPPQ()-oldpqq); 
                if((this.ppqoffset<(1.0d/100000.0d))&&(this.ppqoffset>(-1.0d/100000.0d))) this.ppqoffset=0.0d;
            }               
            //if((flags&info.VST_TIME_TRANSPORT_PLAYING)!=0) this.sampleposition=(long)info.getSamplePos();
        }
        this.TimeInfoSamplePos=this.sampleposition;
    }
    
    public String getHostTimeInfo() {
        String erg="";
        if(info!=null) {
          int flags=info.getFlags();
          erg=erg+"SamplePos: ";
          erg=erg+info.getSamplePos()+" Rate: "+info.getSampleRate();
         if((flags&info.VST_TIME_BARS_VALID)!=0) erg+=" VST_TIME_BARS_VALID: "+info.getBarStartPos();
          if((flags&info.VST_TIME_CLOCK_VALID)!=0) erg+=" VST_TIME_CLOCK_VALID: "+info.getSamplesToNextClock();
          if((flags&info.VST_TIME_CYCLE_POS_VALID)!=0) erg+=" VST_TIME_CYCLE_POS_VALID: "+info.getCycleStartPos()+" "+info.getCycleEndPos();
          if((flags&info.VST_TIME_NANOS_VALID)!=0) erg+=" VST_TIME_NANOS_VALID: "+info.getNanoSeconds();
          if((flags&info.VST_TIME_PPQ_POS_VALID)!=0) erg+=" VST_TIME_PPQ_POS_VALID: "+info.getPPQPos();
          if((flags&info.VST_TIME_SMPTE_VALID)!=0) erg+=" VST_TIME_SMPTE_VALID: "+info.getSMPTEFrameRate()+" "+info.getSMPTEOffset();
          if((flags&info.VST_TIME_TEMPO_VALID)!=0) erg+=" VST_TIME_TEMPO_VALID: "+info.getTempo();
          if((flags&info.VST_TIME_TIME_SIG_VALID)!=0) erg+=" VST_TIME_TIME_SIG_VALID: "+info.getTimeSigNumerator()+"/"+info.getTimeSigDenominator();
          if((flags&info.VST_TIME_AUTOMATION_READING)!=0) erg+=" VST_TIME_AUTOMATION_READING ";
          if((flags&info.VST_TIME_AUTOMATION_WRITING)!=0) erg+=" VST_TIME_AUTOMATION_WRITING ";          
          if((flags&info.VST_TIME_TRANSPORT_CHANGED)!=0) erg+=" VST_TIME_TRANSPORT_CHANGED ";
          if((flags&info.VST_TIME_TRANSPORT_CYCLE_ACTIVE)!=0) erg+=" VST_TIME_TRANSPORT_CYCLE_ACTIVE ";
          if((flags&info.VST_TIME_TRANSPORT_PLAYING)!=0) erg+=" VST_TIME_TRANSPORT_PLAYING ";
          if((flags&info.VST_TIME_TRANSPORT_RECORDING)!=0) erg+=" VST_TIME_TRANSPORT_RECORDING ";          
        }
        return erg;
    }
    
}
