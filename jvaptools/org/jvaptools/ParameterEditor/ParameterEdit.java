/*
 * ParameterEdit.java
 *
 * Created on 16. April 2006, 09:28
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

package org.jvaptools.ParameterEditor;

/**
 *
 * @author Daniel
 */
public class ParameterEdit extends javax.swing.JPanel {
    /** the VST-Plugin
     */
    protected org.jvaptools.VstPluginImpl plugin;
    /** the numer of the vst parameter to controll
     */
    protected int parameter; 
     /** Send changed values to plugin.
      */
    protected boolean sendChanges;
    /** parameter string value
     */
    protected String StringValue;
    /** parameter float value
     */
    protected float FloatValue;
    /** parameter name
     */
    protected String ParameterName;
    /** parameter label
     */
    protected String ParameterLabel;
    /** Creates a new instance of ParameterEdit */
    public ParameterEdit(org.jvaptools.VstPluginImpl plug,int parameter) {
        super();
        this.sendChanges=false;
        this.parameter=parameter;
        this.setVstPlugin(plug);
        this.sendChanges=true;
    }        
    /** Enables or disables notification of changes to the vst plugin
     * @param val if val is true, changed slider or text edit values will be send to the vst plugin
     */
    public void notifyVST(boolean val){
        this.sendChanges=val;
    }
   /** Refresh vst parameter values
     */
    public void vstValueChanged() {
        if(this.plugin!=null) {
            this.StringValue=plugin.getParameterDisplay(parameter);
            this.FloatValue=this.plugin.getParameter(parameter);            
        } else {
            this.StringValue="0.0000";
            this.FloatValue=0.0f;
        }       
    }
    /** Set vst plugin
     */
    public void setVstPlugin(org.jvaptools.VstPluginImpl plugin) {
        this.plugin=plugin;
        if(plugin!=null) {
           ParameterName=plugin.getParameterName(parameter);
           ParameterLabel=plugin.getParameterLabel(parameter);           
        } else {
            ParameterName="Name";
            ParameterLabel="Label";
        }
        vstValueChanged();        
    }
    /** sets the string value
     */
    public void setStringValue(String value) {
        this.StringValue=value;
        if(this.sendChanges&&plugin!=null) {
            plugin.setParameter(this.ParameterName, StringValue);
            this.FloatValue=this.plugin.getParameter(parameter);
        }
    }
    /** sets the float value
     */
    public void setFloatValue(float value) {
        this.FloatValue=value;
        if(this.sendChanges&&plugin!=null) {
            plugin.setParameter(parameter, value);
            this.StringValue=this.plugin.getParameterDisplay(parameter);
        }
    }
    
    
}
