/*
 * ParameterPanel.java
 *
 * Created on 16. April 2006, 09:16
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

/** Panel to edit all vst parameters
 *
 * @author Daniel
 */
public class ParameterPanel extends javax.swing.JPanel {
    
    /** the VST-Plugin
     */
    protected org.jvaptools.VstPluginImpl plugin;
    /** the edit panels for the parameters
     */
    protected ParameterEdit[] edit;
    /** number of vst parameters
     */
    protected int numparams;
    /** Creates a new instance of ParameterPanel 
     * @param plug the vst plugin object
     */
    public ParameterPanel(org.jvaptools.VstPluginImpl plug) {
        super();
        this.setVstPlugin(plug);
    }    
    
    /** Refresh parameter with number index
     * @param index number of changed parameter
     */
    public void vstValueChanged(int index) {
        if((index<numparams)&&(edit[index]!=null))
              edit[index].vstValueChanged();        
    }
    /** Set vst plugin
     */
    public void setVstPlugin(org.jvaptools.VstPluginImpl plugin) {
        this.plugin=plugin;
        if(plugin!=null) {
            if(edit!=null) {
                for(int i=0;i<edit.length;i++)
                    if(edit[i]!=null) this.remove(edit[i]);
            }
            this.numparams=this.plugin.getNumParams();
            edit=new ParameterEdit[numparams];
            for(int i=0;i<edit.length;i++) {
                edit[i]=new ParameterSlider(plugin, i, true, true, false);
                this.add(edit[i]);
            }
        } else {
            this.numparams=0;
            this.edit=new ParameterEdit[0];
        }
    }

    
}
