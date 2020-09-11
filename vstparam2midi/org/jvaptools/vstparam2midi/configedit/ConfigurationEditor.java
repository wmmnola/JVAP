/*
 * ConfigurationEditor.java
 *
 * Created on 29. Januar 2006, 08:51
 */

package org.jvaptools.vstparam2midi.configedit;
import javax.swing.*;
import javax.swing.filechooser.*;
import javax.sound.midi.*;

/**
 *
 * @author  Daniel
 */
public class ConfigurationEditor extends javax.swing.JFrame {
        
    
    public javax.swing.table.DefaultTableModel tablemodel;
    public java.io.File file;
    public boolean usedctr[][];
    int lastctr;
    int lastchan;
    
    /** Creates new form ConfigurationEditor */
    public ConfigurationEditor() { 
        file=null;
        usedctr=new boolean[16][128];
        for(int i=0;i<16;i++)
            for(int j=0;j<128;j++)
                usedctr[i][j]=false;
        lastchan=1;
        lastctr=0;
        initComponents();
        this.MidiOutCombo.addItem("none");
        this.MidiInCombo.addItem("none");        
        try{
            MidiDevice.Info[] info=MidiSystem.getMidiDeviceInfo();
            for(int i=0;i<info.length;i++) {
                try{
                  MidiDevice d=MidiSystem.getMidiDevice(info[i]);
                  if(!(d instanceof Synthesizer)&&(!(d instanceof Sequencer)))
                  {
                      if(d.getMaxReceivers()!=0) this.MidiOutCombo.addItem(info[i].getName());
                      if(d.getMaxTransmitters()!=0) this.MidiInCombo.addItem(info[i].getName());                      
                  }
                } catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        } catch(Exception e)
        {
            e.printStackTrace();
        }
        tablemodel=(javax.swing.table.DefaultTableModel)this.ParameterTable.getModel();
        tablemodel.addTableModelListener(new javax.swing.event.TableModelListener () {
            public void tableChanged(javax.swing.event.TableModelEvent event) {
                if((event.getColumn()==3)||(event.getColumn()==4))
                {
                    for(int i=0;i<16;i++)
                       for(int j=0;j<128;j++)
                          usedctr[i][j]=false;
                    for(int i=0;i<tablemodel.getRowCount();i++) {
                        int ctr=Integer.parseInt(""+tablemodel.getValueAt(i, 3));
                        int chan=Integer.parseInt(""+tablemodel.getValueAt(i, 4));
                        if((chan<1)||(chan>16)||(ctr<0)||(ctr>127))
                            javax.swing.JOptionPane.showMessageDialog(null,"Illegal Controller value "+ctr+" channel "+chan+"!");
                        else if(usedctr[chan-1][ctr]) 
                            javax.swing.JOptionPane.showMessageDialog(null,"Controller value "+ctr+" channel "+chan+" dublicate!");
                        else usedctr[chan-1][ctr]=true;
                    }
                    try{
                    lastctr=Integer.parseInt(""+tablemodel.getValueAt(event.getFirstRow(), 3));
                    lastchan=Integer.parseInt(""+tablemodel.getValueAt(event.getFirstRow(), 4));                    
                    } catch(Exception e)
                    {                     
                    }
                }
            }
        });
        this.ParameterTable.getColumnModel().getColumn(0).setPreferredWidth(20);
        this.ParameterTable.doLayout();
        file=null;
    }
    
    public boolean LoadSettings(java.io.InputStream input)
    {
        java.util.Properties p=new java.util.Properties();
        for(int i=0;i<16;i++)
            for(int j=0;j<128;j++)
                usedctr[i][j]=false;
        try{
          p.loadFromXML(input);
          this.NameTextField.setText(LoadProperty(p,"Name"));        
          this.MidiInCombo.setEditable(true);
          this.MidiOutCombo.setEditable(true);
          this.MidiOutCombo.setSelectedItem(LoadProperty(p,"MIDI-Out"));
          this.MidiInCombo.setSelectedItem(LoadProperty(p,"MIDI-In"));
          this.MidiInCombo.setEditable(false);
          this.MidiOutCombo.setEditable(false);          
          String numparams=LoadProperty(p,"ParameterCount");
          int c=Integer.parseInt(numparams);          
          for(int i=0;i<c;i++) {
              String parametername=LoadProperty(p,"Parameter"+(i+1)+"Name");
              String parameterlabel=LoadProperty(p,"Parameter"+(i+1)+"Label");
              String parametercontroller=LoadProperty(p,"Parameter"+(i+1)+"Controller");  
              String parametervalue=LoadProperty(p,"Parameter"+(i+1)+"DefValue");                
              int ctr=Integer.parseInt(parametercontroller);
              String parameterchannel=LoadProperty(p,"Parameter"+(i+1)+"Channel");              
              int chan=Integer.parseInt(parameterchannel);   
              if((ctr>=0)&&(ctr<128)&&(chan-1>=0)&&(chan-1<16)) usedctr[chan-1][ctr]=true;
              lastctr=ctr;
              lastchan=chan;
              String valuecount=LoadProperty(p,"Parameter"+(i+1)+"ValueCount");
              int vc=Integer.parseInt(valuecount);
              String paramvals="";
              if(vc>0) {                  
                  for(int j=0;j<vc;j++)
                  {
                      String paramval=LoadProperty(p, "Parameter"+(i+1)+"Value"+(j+1));
                      paramvals=paramvals+paramval+";";
                  }
              }
            Object[] data=new Object[7];
            data[0]=new Integer(this.ParameterTable.getRowCount()+1);
            data[1]=parametername;
            data[2]=parameterlabel;
            data[3]=new Integer(ctr);
            data[4]=new Integer(chan);
            data[5]=paramvals;
            data[6]=parametervalue;            
            tablemodel.addRow(data);               
          }      
          this.ParamCount.setText(""+tablemodel.getRowCount());  
          return true;
        } catch(Exception e)
        {
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(this,"Error in config-file: "+e.getMessage());
            return false;
        }
    }
    
    public int nextCtr() {
        int i;
        for(i=lastctr;usedctr[lastchan-1][i]&&i<128;i++);
        if(i==128) return 0;
        else return i;
    }
    
    public int nextChan() {
        return lastchan;
    }
    
    public boolean SaveSettings(java.io.OutputStream output)
    {
        java.util.Properties p=new java.util.Properties();
        try{
          for(int i=0;i<16;i++)
            for(int j=0;j<128;j++)
                usedctr[i][j]=false;
          p.setProperty("Name",this.NameTextField.getText());        
          p.setProperty("MIDI-Out",""+this.MidiOutCombo.getSelectedItem());
          p.setProperty("MIDI-In",""+this.MidiInCombo.getSelectedItem());
          p.setProperty("ParameterCount",""+this.tablemodel.getRowCount());
          int c=this.tablemodel.getRowCount();          
          for(int i=0;i<c;i++) {
              p.setProperty("Parameter"+(i+1)+"Name", ""+this.tablemodel.getValueAt(i, 1));
              p.setProperty("Parameter"+(i+1)+"Label",""+this.tablemodel.getValueAt(i, 2));
              p.setProperty("Parameter"+(i+1)+"Controller",""+this.tablemodel.getValueAt(i, 3));                            
              p.setProperty("Parameter"+(i+1)+"Channel",""+this.tablemodel.getValueAt(i, 4));            
              int ctr=Integer.parseInt(""+this.tablemodel.getValueAt(i, 3));
              int chan=Integer.parseInt(""+this.tablemodel.getValueAt(i, 4));
              if(usedctr[chan-1][ctr]) throw new Exception("Controller value "+ctr+" channel "+chan+" dublicate!");
              else usedctr[chan-1][ctr]=true;
              String paramvals=""+this.tablemodel.getValueAt(i, 5);  
              String[] values=paramvals.split(";",0);
              int vc=values.length;
              if(values.length==1) {
                  if("".equals(values[0])) vc=0;
              }
              p.setProperty("Parameter"+(i+1)+"ValueCount", ""+vc);
              if(vc>0) {                  
                  for(int j=0;j<vc;j++)
                  {
                      p.setProperty("Parameter"+(i+1)+"Value"+(j+1), values[j]);
                  }
              }
              p.setProperty("Parameter"+(i+1)+"DefValue", ""+this.tablemodel.getValueAt(i, 6));
          }      
          p.storeToXML(output,"Created by VST-Parameter to MIDI Configuration-Editor.");
          return true;
        } catch(Exception e)
        {
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(null,"Error in config-file: "+e.getMessage());
            return false;
        }
    }
 
    private String LoadProperty(java.util.Properties p,String key) throws Exception
    {
        String erg=p.getProperty(key);
       // if(erg==null) throw new java.util.prefs.InvalidPreferencesFormatException("Property "+key+" is missing!");
        return erg;
    }

    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jToolBar1 = new javax.swing.JToolBar();
        OpenButton = new javax.swing.JButton();
        SaveButton = new javax.swing.JButton();
        Settings = new javax.swing.JPanel();
        MainSettings = new javax.swing.JPanel();
        NameLabel = new javax.swing.JLabel();
        NameTextField = new javax.swing.JTextField();
        Midioutlabel = new javax.swing.JLabel();
        Midiinlabel = new javax.swing.JLabel();
        MidiOutCombo = new javax.swing.JComboBox();
        MidiInCombo = new javax.swing.JComboBox();
        Parametersettings = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        ParameterTable = new javax.swing.JTable();
        Parametermain = new javax.swing.JPanel();
        NewParam = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        ParamCount = new javax.swing.JTextField();
        UpButton = new javax.swing.JButton();
        DownButton = new javax.swing.JButton();
        DublicateButton = new javax.swing.JButton();
        DeleteButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Configuration Editor");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        OpenButton.setText("Open File...");
        OpenButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OpenButtonActionPerformed(evt);
            }
        });

        jToolBar1.add(OpenButton);

        SaveButton.setText("Save File");
        SaveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SaveButtonActionPerformed(evt);
            }
        });

        jToolBar1.add(SaveButton);

        getContentPane().add(jToolBar1, java.awt.BorderLayout.NORTH);

        Settings.setLayout(new java.awt.BorderLayout());

        Settings.setBorder(new javax.swing.border.EtchedBorder());
        MainSettings.setLayout(new java.awt.GridBagLayout());

        MainSettings.setPreferredSize(new java.awt.Dimension(550, 150));
        NameLabel.setText("Name:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(15, 15, 15, 0);
        MainSettings.add(NameLabel, gridBagConstraints);

        NameTextField.setPreferredSize(new java.awt.Dimension(150, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        MainSettings.add(NameTextField, gridBagConstraints);

        Midioutlabel.setText("MIDI-Out:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 15, 15, 0);
        MainSettings.add(Midioutlabel, gridBagConstraints);

        Midiinlabel.setText("MIDI-In:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 15, 15, 0);
        MainSettings.add(Midiinlabel, gridBagConstraints);

        MidiOutCombo.setPreferredSize(new java.awt.Dimension(150, 22));
        MidiOutCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MidiOutComboActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        MainSettings.add(MidiOutCombo, gridBagConstraints);

        MidiInCombo.setPreferredSize(new java.awt.Dimension(150, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        MainSettings.add(MidiInCombo, gridBagConstraints);

        Settings.add(MainSettings, java.awt.BorderLayout.NORTH);

        Parametersettings.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setPreferredSize(new java.awt.Dimension(520, 300));
        ParameterTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nr.", "Name", "Label", "MIDI-Controller", "MIDI-Chanel", "Values", "Default Value"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, true, true, true, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(ParameterTable);

        Parametersettings.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        NewParam.setText("New Parameter");
        NewParam.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NewParamActionPerformed(evt);
            }
        });

        Parametermain.add(NewParam);

        jLabel1.setText("Parameter Count:");
        Parametermain.add(jLabel1);

        ParamCount.setEditable(false);
        ParamCount.setText("0");
        ParamCount.setPreferredSize(new java.awt.Dimension(30, 19));
        Parametermain.add(ParamCount);

        UpButton.setText("Move Up");
        UpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                UpButtonActionPerformed(evt);
            }
        });

        Parametermain.add(UpButton);

        DownButton.setText("Move Down");
        DownButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DownButtonActionPerformed(evt);
            }
        });

        Parametermain.add(DownButton);

        DublicateButton.setText("Dublicate");
        DublicateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DublicateButtonActionPerformed(evt);
            }
        });

        Parametermain.add(DublicateButton);

        DeleteButton.setText("Delete");
        DeleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DeleteButtonActionPerformed(evt);
            }
        });

        Parametermain.add(DeleteButton);

        Parametersettings.add(Parametermain, java.awt.BorderLayout.NORTH);

        Settings.add(Parametersettings, java.awt.BorderLayout.CENTER);

        getContentPane().add(Settings, java.awt.BorderLayout.CENTER);

        pack();
    }
    // </editor-fold>//GEN-END:initComponents

    private void DeleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteButtonActionPerformed
// TODO add your handling code here:
        int retval=javax.swing.JOptionPane.showConfirmDialog(this,"Delete Parameter?");
        if(retval==javax.swing.JOptionPane.YES_OPTION) {
        int[] s=this.ParameterTable.getSelectedRows();
        for(int i=s.length-1;i>=0;i--)
          this.tablemodel.removeRow(s[i]);
        this.renumber();
        }
        this.ParamCount.setText(""+tablemodel.getRowCount());
    }//GEN-LAST:event_DeleteButtonActionPerformed

    private void DublicateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DublicateButtonActionPerformed
// TODO add your handling code here:
               int[] s=this.ParameterTable.getSelectedRows();               
        if((s!=null)&&(s.length > 0)) {
           for(int i=0;i<s.length;i++)
           {
               Object[] data=new Object[7];        
               data[0]=new Integer(this.ParameterTable.getRowCount()+1);
               data[1]=""+this.tablemodel.getValueAt(s[i], 1);
               data[2]=""+this.tablemodel.getValueAt(s[i], 2);
               try{
                this.lastctr=Integer.parseInt(""+this.tablemodel.getValueAt(s[i], 3));
                this.lastchan=Integer.parseInt(""+this.tablemodel.getValueAt(s[i], 4));        
               } catch(Exception e) {e.printStackTrace();}
               data[3]=new Integer(this.nextCtr());
               this.lastctr=(Integer)data[3];
               data[4]=new Integer(this.nextChan());
               this.lastchan=(Integer)data[4];
               data[5]=""+this.tablemodel.getValueAt(s[i], 5);
               data[6]=""+this.tablemodel.getValueAt(s[i], 6);               
               this.usedctr[lastchan-1][lastctr]=true;
               this.tablemodel.addRow(data);
           }
        }
        this.ParamCount.setText(""+tablemodel.getRowCount());
    }//GEN-LAST:event_DublicateButtonActionPerformed

    private void DownButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DownButtonActionPerformed
// TODO add your handling code here:
                int[] s=this.ParameterTable.getSelectedRows();
        if((s!=null)&&(s.length > 0)) {
            int max=0;
            for(int i=0;i<s.length;i++)
            {
              if(s[i]>max) max=s[i];
            } 
            max=max+1;
            if(max>=tablemodel.getRowCount()) max=tablemodel.getRowCount()-1;
            int c=0;
            for(int i=s.length-1;i>=0;i--)
            {
               this.tablemodel.moveRow(s[i],s[i], max-c);
               c++;
            }
            this.ParameterTable.getSelectionModel().setSelectionInterval(max-c+1, max);
        }
        this.renumber();

    }//GEN-LAST:event_DownButtonActionPerformed

    boolean cancel;
    
    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
// TODO add your handling code here:
        cancel=false;
        int retval=javax.swing.JOptionPane.showConfirmDialog(this,"Save data?");
        if(retval==javax.swing.JOptionPane.CANCEL_OPTION) cancel=true;
        else
        if(retval==javax.swing.JOptionPane.YES_OPTION) this.SaveButtonActionPerformed(null);
        if(!cancel) System.exit(0);
    }//GEN-LAST:event_formWindowClosing

    private void SaveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SaveButtonActionPerformed
// TODO add your handling code here:
        cancel=false;
        java.io.File oldfile=file;
        if(file!=null) {
          int retval=javax.swing.JOptionPane.showConfirmDialog(this,"Overwrite "+file.getPath()+" ?");
          if(retval==javax.swing.JOptionPane.CANCEL_OPTION) cancel=true;
          else
          if(retval!=javax.swing.JOptionPane.YES_OPTION) file=null;          
        } 
        if((!cancel)&&(file==null)) {
            JFileChooser chooser = new JFileChooser();
            FileFilter filter = new FileFilter() {
           public boolean accept(java.io.File f){
               return ("config.xml".equals(f.getName()))||f.isDirectory();
           }
           public String getDescription() {
               return "Config Files";
           }
        };
        try{
          chooser.setCurrentDirectory(new java.io.File(System.getProperty("user.dir")));
        } catch(Exception e)
        {
           e.printStackTrace();
        }
        chooser.setFileFilter(filter);
        chooser.setSelectedFile(new java.io.File("config.xml"));
        int returnVal = chooser.showSaveDialog(this);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            file=chooser.getSelectedFile();
            if(file.exists()) {
                int retval=javax.swing.JOptionPane.showConfirmDialog(this,"Overwrite "+file.getPath()+" ?");
                if(retval!=javax.swing.JOptionPane.YES_OPTION) { file=oldfile; cancel=true; } 
            }
        } else { file=oldfile; cancel=true; }
        }       
        if((!cancel)&&(file!=null))
        {
            try{
                java.io.FileOutputStream f = new java.io.FileOutputStream(file);
                boolean saved=this.SaveSettings(f);
                f.close();
                if(saved) javax.swing.JOptionPane.showMessageDialog(this,"File "+file.getPath()+" saved!");
                else cancel=true;
            } catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_SaveButtonActionPerformed

    private void OpenButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OpenButtonActionPerformed
        JFileChooser chooser = new JFileChooser();
        FileFilter filter = new FileFilter() {
           public boolean accept(java.io.File f){
               return ("config.xml".equals(f.getName()))||f.isDirectory();
           }
           public String getDescription() {
               return "Config Files";
           }
        };
    try{
    chooser.setCurrentDirectory(new java.io.File(System.getProperty("user.dir")));
    } catch(Exception e)
    {
        e.printStackTrace();
    }
    chooser.setFileFilter(filter);
    int returnVal = chooser.showOpenDialog(this);
    if(returnVal == JFileChooser.APPROVE_OPTION) {
        try {
            file=chooser.getSelectedFile();
            java.io.FileInputStream f=new java.io.FileInputStream(file);
            this.tablemodel.setRowCount(0);
            this.LoadSettings(f);
            f.close();
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    }//GEN-LAST:event_OpenButtonActionPerformed

    private void UpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_UpButtonActionPerformed
// TODO add your handling code here:
        int[] s=this.ParameterTable.getSelectedRows();
        if((s!=null)&&(s.length > 0)) {
            int min=this.tablemodel.getRowCount();
            for(int i=0;i<s.length;i++)
            {
              if(s[i]<min) min=s[i];
            } 
            min=min-1;
            if(min<0) min=0;
            int c=0;
            for(int i=0;i<s.length;i++)
            {
               this.tablemodel.moveRow(s[i],s[i], min+c);
               c++;
            }
            this.ParameterTable.getSelectionModel().setSelectionInterval(min, min+c-1);
        }
        this.renumber();
    }//GEN-LAST:event_UpButtonActionPerformed

    public void renumber()
    {
        for(int i=0;i<this.tablemodel.getRowCount();i++) {
            this.tablemodel.setValueAt(new Integer(i+1), i, 0);
        }
    }
    
    private void NewParamActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NewParamActionPerformed
// TODO add your handling code here:      
        Object[] data=new Object[7];
        if(this.tablemodel.getRowCount()>0) {
        data[0]=new Integer(this.ParameterTable.getRowCount()+1);
        data[1]=""+this.tablemodel.getValueAt(this.ParameterTable.getRowCount()-1, 1)+"2";
        data[2]=""+this.tablemodel.getValueAt(this.ParameterTable.getRowCount()-1, 2);
        /*try{
          this.lastctr=Integer.parseInt(""+this.tablemodel.getValueAt(this.ParameterTable.getRowCount()-1, 3));
          this.lastchan=Integer.parseInt(""+this.tablemodel.getValueAt(this.ParameterTable.getRowCount()-1, 4));        
        } catch(Exception e) {e.printStackTrace();}*/
        data[3]=new Integer(this.nextCtr());
        this.lastctr=(Integer)data[3];
        data[4]=new Integer(this.nextChan());
        this.lastchan=(Integer)data[4];
        data[5]=""+this.tablemodel.getValueAt(this.ParameterTable.getRowCount()-1, 5);
        data[6]=""+this.tablemodel.getValueAt(this.ParameterTable.getRowCount()-1, 6);
        } else {
        data[0]=new Integer(this.ParameterTable.getRowCount()+1);
        data[1]="Name";
        data[2]="Label";
       data[3]=new Integer(this.nextCtr());
        this.lastctr=(Integer)data[3];
        data[4]=new Integer(this.nextChan());
        this.lastchan=(Integer)data[4];
        data[5]="Value1;Value2;";
        data[6]="0";
        }
        this.usedctr[lastchan-1][lastctr]=true;
        tablemodel.addRow(data);   
        this.ParamCount.setText(""+tablemodel.getRowCount());
    }//GEN-LAST:event_NewParamActionPerformed

    private void MidiOutComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MidiOutComboActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_MidiOutComboActionPerformed
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try{
        javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch(Exception e) {
            e.printStackTrace();
        }
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ConfigurationEditor().setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton DeleteButton;
    private javax.swing.JButton DownButton;
    private javax.swing.JButton DublicateButton;
    private javax.swing.JPanel MainSettings;
    private javax.swing.JComboBox MidiInCombo;
    private javax.swing.JComboBox MidiOutCombo;
    private javax.swing.JLabel Midiinlabel;
    private javax.swing.JLabel Midioutlabel;
    private javax.swing.JLabel NameLabel;
    private javax.swing.JTextField NameTextField;
    private javax.swing.JButton NewParam;
    private javax.swing.JButton OpenButton;
    private javax.swing.JTextField ParamCount;
    private javax.swing.JTable ParameterTable;
    private javax.swing.JPanel Parametermain;
    private javax.swing.JPanel Parametersettings;
    private javax.swing.JButton SaveButton;
    private javax.swing.JPanel Settings;
    private javax.swing.JButton UpButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar jToolBar1;
    // End of variables declaration//GEN-END:variables
    
}
