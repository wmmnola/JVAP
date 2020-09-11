/*
 * VstPluginMidiDevice.java
 *
 * Created on 24. Januar 2006, 19:44
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
public class VstPluginMidiDevice extends MidiDeviceImpl {
    
    org.jvaptools.VstPluginImpl owner;
    
    /** Creates a new instance of VstPluginMidiDevice */
    public VstPluginMidiDevice(org.jvaptools.VstPluginImpl owner) {
        this.owner=owner;
    }
    
    public Info getDeviceInfo(){
        return new MidiDeviceImplInfo("VstMidi","VST-MIDI/IO implementation");
    }
    
    public void send(javax.sound.midi.MidiMessage message, long timeStamp)
    {
        if(this.isOpen()&&(owner!=null)) owner.send(message, timeStamp);
    }
    
}
