/*
 * PluginScriptSynth.java
 *
 * Created on 6. December 2009, 23:34
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
public interface PluginScriptSynth {
        
    public void init(org.jvaptools.VstPluginImpl owner);
    
    public void close();
    
    public void onMidiEvent(javax.sound.midi.MidiMessage message, long timeStamp);

    public void processReplacing(vstmainSynth plugin,float a,float b,float c,float d, float[][] outputs, int sampleFrames);
    
}
