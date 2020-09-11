/*
 * VstMidiDevice.java
 *
 * Created on 23. Januar 2006, 22:42
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
import java.util.List;

/**
 *
 * @author Daniel
 */
public abstract class MidiDeviceImpl implements javax.sound.midi.MidiDevice {
    
    private java.util.Vector<Receiver> receivers;
    private java.util.Vector<Transmitter> transmitters;
    private boolean open;
    
    /** Creates a new instance of VstMidiDevice */
    public MidiDeviceImpl() {
        receivers=new java.util.Vector<Receiver>();
        transmitters=new java.util.Vector<Transmitter>();
        open=false;
    }   
    
    protected class MidiDeviceImplInfo extends Info {
        public MidiDeviceImplInfo(String name,String description)
        {
            super(name,"org.jvst2javasoundc",description,"1.0");
        }
    }
    
    /*public Info getDeviceInfo(){
        return new MidiDeviceImplInfo("abstract");
    }*/


    /**
     * Opens the device, indicating that it should now acquire any
     * system resources it requires and become operational.
     *
     * <p>An application opening a device explicitly with this call
     * has to close the device by calling {@link #close}. This is
     * necessary to release system resources and allow applications to
     * exit cleanly.
     *
     * <p>
     * Note that some devices, once closed, cannot be reopened.  Attempts
     * to reopen such a device will always result in a MidiUnavailableException.
     *
     * @throws MidiUnavailableException thrown if the device cannot be
     * opened due to resource restrictions.
     * @throws SecurityException thrown if the device cannot be
     * opened due to security restrictions.
     *
     * @see #close
     * @see #isOpen
     */
    public void open() throws MidiUnavailableException
    {
       open=true;   
    }


    /**
     * Closes the device, indicating that the device should now release
     * any system resources it is using.
     *
     * <p>All <code>Receiver</code> and <code>Transmitter</code> instances
     * open from this device are closed. This includes instances retrieved
     * via <code>MidiSystem</code>.
     * 
     * @see #open
     * @see #isOpen
     */
    public void close()
    {   
        open=false;
        java.util.Vector<Receiver> res=new java.util.Vector<Receiver>(this.receivers);
        java.util.Vector<Transmitter> tra=new java.util.Vector<Transmitter>(this.transmitters);
        for(Receiver r:res) r.close();        
        for(Transmitter t:tra) t.close();
    }


    /**
     * Reports whether the device is open.
     *
     * @return <code>true</code> if the device is open, otherwise
     * <code>false</code>
     * @see #open
     * @see #close
     */
    
     public boolean isOpen(){
         return open;
     }


    /**
     * Obtains the current time-stamp of the device, in microseconds.
     * If a device supports time-stamps, it should start counting at
     * 0 when the device is opened and continue incrementing its
     * time-stamp in microseconds until the device is closed.
     * If it does not support time-stamps, it should always return
     * -1.
     * @return the current time-stamp of the device in microseconds,
     * or -1 if time-stamping is not supported by the device.
     */
    public long getMicrosecondPosition(){
        return -1;
    }


    /**
     * Obtains the maximum number of MIDI IN connections available on this
     * MIDI device for receiving MIDI data.
     * @return maximum number of MIDI IN connections, 
     * or -1 if an unlimited number of connections is available.
     */
    public int getMaxReceivers()
    {
        return -1;
    }


    /**
     * Obtains the maximum number of MIDI OUT connections available on this
     * MIDI device for transmitting MIDI data.
     * @return maximum number of MIDI OUT connections,
     * or -1 if an unlimited number of connections is available.
     */
    public int getMaxTransmitters()
    {
        return -1;
    }


    /**
     * Obtains a MIDI IN receiver through which the MIDI device may receive
     * MIDI data.  The returned receiver must be closed when the application
     * has finished using it.
     *
     * <p>Obtaining a <code>Receiver</code> with this method does not
     * open the device. To be able to use the device, it has to be
     * opened explicitly by calling {@link #open}. Also, closing the
     * <code>Receiver</code> does not close the device. It has to be
     * closed explicitly by calling {@link #close}.
     *
     * @return a receiver for the device.
     * @throws MidiUnavailableException thrown if a receiver is not available
     * due to resource restrictions
     * @see Receiver#close()
     */
    public Receiver getReceiver() throws MidiUnavailableException
    {        
        return new MidiDeviceImplReceiver(this);
    }

    class MidiDeviceImplReceiver implements Receiver {
        MidiDeviceImpl owner;
        public MidiDeviceImplReceiver(MidiDeviceImpl o)
        {
            owner=o;
            owner.receivers.add(this);
        }
        
        public void close(){
            owner.receivers.remove(this);
            owner=null;
        }
        
        public void send(MidiMessage message, long timeStamp)
        {
            if(owner!=null)
              owner.send(message, timeStamp);
        }
    }
    
    public abstract void send(MidiMessage message, long timeStamp);
    
    public void transmit(MidiMessage message, long timeStamp)
    {
        if(this.isOpen())
        for(Transmitter t:this.transmitters) {
            Receiver r=t.getReceiver();
            if(r!=null)
                r.send(message, timeStamp);
        }
    }

    /**
     * Returns all currently active, non-closed receivers
     * connected with this MidiDevice.
     * A receiver can be removed
     * from the device by closing it.
     * @return an unmodifiable list of the open receivers
     * @since 1.5
     */
    public List<Receiver> getReceivers(){
        return this.receivers;
    }


    /**
     * Obtains a MIDI OUT connection from which the MIDI device will transmit
     * MIDI data  The returned transmitter must be closed when the application
     * has finished using it.
     *
     * <p>Obtaining a <code>Transmitter</code> with this method does not
     * open the device. To be able to use the device, it has to be
     * opened explicitly by calling {@link #open}. Also, closing the
     * <code>Transmitter</code> does not close the device. It has to be
     * closed explicitly by calling {@link #close}.
     *
     * @return a MIDI OUT transmitter for the device.
     * @throws MidiUnavailableException thrown if a transmitter is not available
     * due to resource restrictions
     * @see Transmitter#close()
     */
    public Transmitter getTransmitter() throws MidiUnavailableException
    {
        return new MidiDeviceImplTransmitter(this);
    }


    class MidiDeviceImplTransmitter implements Transmitter {
        MidiDeviceImpl owner;
        Receiver sendTo;
        public MidiDeviceImplTransmitter(MidiDeviceImpl o){
            owner=o;
            owner.transmitters.add(this);
            sendTo=null;
        }
        
        public Receiver getReceiver()
        {
            return sendTo;
        }
        
        public void setReceiver(Receiver r)
        {
            this.sendTo=r;
        }
        
        public void close(){
            owner.transmitters.remove(this);
            sendTo=null;
        }
        
    }
    
    /**
     * Returns all currently active, non-closed transmitters
     * connected with this MidiDevice.
     * A transmitter can be removed
     * from the device by closing it.
     * @return an unmodifiable list of the open transmitters
     * @since 1.5
     */
    public List<Transmitter> getTransmitters()
    {
        return this.transmitters;
    }
            
}
