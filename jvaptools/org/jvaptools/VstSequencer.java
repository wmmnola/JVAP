/*
 * VstSequencer.java
 *
 * Created on 11. März 2006, 18:38
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

import javax.sound.midi.*;

/**
 *
 * @author Daniel
 */
public class VstSequencer extends MidiDeviceImpl implements javax.sound.midi.Sequencer  {
    
    /** Creates a new instance of VstSequencer */
    
    VstTimer timer;
    
    public VstSequencer(VstTimer timer) {
        this.timer=timer;
        if(timer!=null)
            timer.addSequencer(this);
    }
    
    public int[] addControllerEventListener(ControllerEventListener listener, int[] controllers) {
         //Fix!!
         return null;
     }
         
    public boolean addMetaEventListener(MetaEventListener listener) {
         //Fix!!
         return false;
     }
     
     int loopcount;
     
    public int getLoopCount() {
         return this.loopcount;
     }
         
     long loopstartpoint;
     long loopendpoint;
     
    public long getLoopEndPoint() {
       return this.loopendpoint;
     }

    public long getLoopStartPoint() {
       return this.loopstartpoint;
     }
  
   public  Sequencer.SyncMode getMasterSyncMode() {
         return Sequencer.SyncMode.INTERNAL_CLOCK;
     }
          
     Sequencer.SyncMode[] mastersyncmodes={Sequencer.SyncMode.INTERNAL_CLOCK};
     Sequencer.SyncMode[] slavesyncmodes={Sequencer.SyncMode.NO_SYNC};
     
   public  Sequencer.SyncMode[] getMasterSyncModes() {
         return mastersyncmodes;
     }
          
   public  long getMicrosecondLength() {
         //Fix!!
       if(seq!=null) {
           return seq.getMicrosecondLength();
       } else return 0;
     }
  
    public long getMicrosecondPosition() {
         //Fix!!
        return 0;
     }
 
     Sequence seq;
     
    public Sequence getSequence() {
         return seq;
     }
 
 public Sequencer.SyncMode 	getSlaveSyncMode() {
     return Sequencer.SyncMode.NO_SYNC;
 }
          
 public Sequencer.SyncMode[] 	getSlaveSyncModes() {
     return this.slavesyncmodes;
 }
     
 float tempofactor=1.0f;
         
 public float 	getTempoFactor() {
     return this.tempofactor;
 }
          
 public float 	getTempoInBPM() {
     return (float)this.timer.getBPM();
 }
  
 public float 	getTempoInMPQ() {
     return (float)this.timer.getMPQ();
 }
          
 public long 	getTickLength() {
     if(this.seq!= null) return this.seq.getTickLength();
     else return 0;
 }

 public long 	getTickPosition() {
     //Fix!!
     return 0;
 }
 
   boolean[] trackmutes;
 
 public boolean getTrackMute(int track) {
     if((trackmutes!=null)&&(track<trackmutes.length)) {
         return this.trackmutes[track];
     } else return false;
 }
 
 boolean[] tracksolo;
     
 public boolean getTrackSolo(int track) {
     if((tracksolo!=null)&&(track<tracksolo.length)) {
         return this.tracksolo[track];
     } else return false;         
 }
 boolean recording;
 
 public boolean isRecording() {
     return recording;
 }
          
 boolean running;
 
 public boolean isRunning() {
     return running;
 }

 java.util.Vector<Track> recordtracks=new java.util.Vector<Track>();
 
 public void 	recordDisable(Track track)
 {
     recordtracks.remove(track);
 }

 java.util.HashMap<Track,Integer> recordchannels=new  java.util.HashMap<Track,Integer>();
 
 public void 	recordEnable(Track track, int channel)
 {
     recordtracks.add(track);
     recordchannels.put(track,channel);
 }
          
 public int[] 	removeControllerEventListener(ControllerEventListener listener, int[] controllers) {
     // Fix!
     return null;
 }
 
 public void 	removeMetaEventListener(MetaEventListener listener) {
     //Fix!
 }
          
 public void 	setLoopCount(int count)
 {
     this.loopcount=count;
 }
          
 public void 	setLoopEndPoint(long tick) {
     this.loopendpoint=tick;
 }
          
public void 	setLoopStartPoint(long tick) {
     this.loopstartpoint=tick;
 }
          
 public void 	setMasterSyncMode(Sequencer.SyncMode sync) {
     //Fix!
 }
          
 public void 	setMicrosecondPosition(long microseconds) {
     // Fix!!
 }
     
 public void 	setSequence(java.io.InputStream stream) {
   //Fix    
 }
 
 public void 	setSequence(Sequence sequence) {
     this.seq=sequence;
 }

 public void 	setSlaveSyncMode(Sequencer.SyncMode sync) {
     //Fix!
 }
          
 public void 	setTempoFactor(float factor) {
     this.tempofactor=factor;
 }
          
 public void 	setTempoInBPM(float bpm) {
     //Fix!
 }
          
 public void 	setTempoInMPQ(float mpq) {
     //Fix!
 }
          
 public void 	setTickPosition(long tick) {
     //Fix!!
 }
          
 public void 	setTrackMute(int track, boolean mute) {
     //Fix!
 }
     
 public void 	setTrackSolo(int track, boolean solo) {
     //Fix!
 }
     
 public synchronized void start() {
     this.running=true;
     ppqoffset=timer.getPPQ();
 }
     
 double ppqpos;
 double ppqoffset;
 
 public synchronized void processEvents(double ppqpos) {
     if(this.running) {
         if(seq!=null) {
             //Fix!!
         }
     }
 }
 
 public void 	startRecording() {
     this.recording=true;
 }
          
 public void 	stop() {
     this.recording=false;
     this.running=false;
 }
          
 public void 	stopRecording() {
     this.recording=false;
 }
       
 public void send(javax.sound.midi.MidiMessage message, long timeStamp)
 {
        //if(this.isOpen()&&(owner!=null)) owner.send(message, timeStamp);
     //Fix!!
 }
 
 public Info getDeviceInfo(){
        return new MidiDeviceImplInfo("VstSequencer","VST-Sequencer implementation");
 }
    
}
