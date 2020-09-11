/*
 * ParameterSlider.java
 *
 * Created on 13. April 2006, 09:11
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

/** This Class provides a slider to edit a VST-Parameter. 
 * It is used by the parameter panel.
 *
 * @author daniel
 */
public class ParameterSlider extends ParameterEdit {
    /** text-editor for the parameter value
     */
    protected javax.swing.JTextField valueedit;
    /** slider for the parameter value
     */
    protected javax.swing.JSlider valueslider;
    /* Label for the parameter name
     */
    protected javax.swing.JLabel name;
    /* Label for the parameter label
     */
    protected javax.swing.JLabel label;
    /** Creates a new instance of ParameterSlider.
     * @param plug the VST-Plugin
     * @param param number of vst parameter to control
     * @param showName set true to display the parametername
     * @param showValue set true to display the parameter value
     * @param vertical set true if the slider should be vertically
     */
    public ParameterSlider(org.jvaptools.VstPluginImpl plug,int param,
            boolean showName,boolean showValue,boolean vertical) {
        super(plug, param);
        this.setLayout(new java.awt.BorderLayout());
        this.valueslider=new javax.swing.JSlider(
           (vertical)?javax.swing.JSlider.VERTICAL:javax.swing.JSlider.HORIZONTAL);
        this.valueslider.setValue((int)(this.FloatValue*100));
        this.valueslider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                ParameterSliderChanged();
            }
        });
        this.add(valueslider,java.awt.BorderLayout.CENTER); 
        if(showName) {
           name=new javax.swing.JLabel();
           name.setHorizontalAlignment(name.CENTER);
           name.setText(this.ParameterName);
           this.add(name,(vertical)?java.awt.BorderLayout.NORTH:java.awt.BorderLayout.WEST); 
        } else name=null;
        if(showValue) {
            javax.swing.JPanel valuepanel=new javax.swing.JPanel();
            valueedit=new javax.swing.JTextField();
            valueedit.setPreferredSize(new java.awt.Dimension(40, 17));
            valueedit.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyPressed(java.awt.event.KeyEvent e) {
                    if(e.getKeyCode()==e.VK_ENTER){
                        ParameterTextChanged();
                    }
                }
            }
            );
            valueedit.addFocusListener(new java.awt.event.FocusAdapter() {
                 public void focusLost(java.awt.event.FocusEvent e) {
                     ParameterTextChanged();
                 }
            }
            );
            valueedit.setText(this.StringValue);
            valuepanel.add(valueedit);
            label=new javax.swing.JLabel();
            label.setText(this.ParameterLabel);
            valuepanel.add(label);
            this.add(valuepanel,(vertical)?java.awt.BorderLayout.SOUTH:java.awt.BorderLayout.EAST);
        } else {
            label=null;
            valueedit=null;
        }
        this.vstValueChanged();
    }
    /** sets a new parameter value based on the edit field.
     *
     */    
    public void ParameterTextChanged() {
        if(valueedit!=null) { 
            this.setStringValue(valueedit.getText());
            this.valueedit.setText(this.StringValue);
            boolean oldsend=this.sendChanges;
            this.sendChanges=false;
            if(valueslider!=null) this.valueslider.setValue((int)(this.FloatValue*100));
            this.sendChanges=oldsend;
        }
    }
    /** sets a new parameter value based on the slider.
     *
     */    
    public void ParameterSliderChanged() {
        if(this.sendChanges) {
          this.setFloatValue(((float)(this.valueslider.getValue()))/100.0f);
          if(valueedit!=null) { 
            this.valueedit.setText(this.StringValue);
          }
        }
    }
    /** Refresh slider and text field based on the vst values
     */
    public void vstValueChanged() {
        super.vstValueChanged();
        boolean oldsend=this.sendChanges;
        this.sendChanges=false;
        if(valueedit!=null) this.valueedit.setText(this.StringValue);
        if(valueslider!=null) this.valueslider.setValue((int)(this.FloatValue*100));
        this.sendChanges=oldsend;
    }
    /** Set vst plugin
     */
    public void setVstPlugin(org.jvaptools.VstPluginImpl plugin) {
        super.setVstPlugin(plugin);
        if(name!=null) name.setText(this.ParameterName);
        if(label!=null) label.setText(this.ParameterLabel);              
    }
    
}
