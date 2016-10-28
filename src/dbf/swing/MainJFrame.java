/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dbf.swing;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.*;

/**
 * СДЕЛАТЬ ПРОВЕРКУ НА СИГНАТУРУ ДБФ ФАЙЛА!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!111111111111111111
 * Сделать объект настроек и хранить настройки в файле!
 * Сделать проверку суммы платежа, счет контрагента и дебеткредит?
 * Сделать цикл оплат в отдельном потоке!
 * Может нумерацию сделать добавив столбец и переместив его в начало?
 * Ширину столбца по макс. длине содержимого - сортировка и ленгтх первого?
 * Выбрасывать эксэпшн при инвалидной структуре столбцов - пустое название и длина?
 * @author spidchenko.d
 */
class settings implements Serializable{
    final static String SETTINGS_FILE_NAME = "Settings.dat";
    private String dBUrl = "";
    private String dBTableName = "";
    private String dBUser = "";
    private String dBPassword = "";
    private String dbfEncoding = "";
    private String dbfCorrectSum = "";
    private String dbfBankAccountNumber = "";
    private String dbfColumnSumName = "";
    private String dbfColumnDescriptionName = "";
    private String dbfColumnBankAccountName = "";
    
     
    boolean save(){
        //Сохраняем в файл
        return true;
    }
    
//<editor-fold defaultstate="collapsed" desc="GETERS-SETTERS">
    public String getdBUrl() {
        return dBUrl;
    }
    
    public String getdBTableName() {
        return dBTableName;
    }
    
    public String getdBUser() {
        return dBUser;
    }
    
    public String getdBPassword() {
        return dBPassword;
    }
    
    public String getDbfEncoding() {
        return dbfEncoding;
    }
    
    public String getDbfCorrectSum() {
        return dbfCorrectSum;
    }
    
    public String getDbfBankAccountNumber() {
        return dbfBankAccountNumber;
    }
    
    public String getDbfColumnSumName() {
        return dbfColumnSumName;
    }
    
    public String getDbfColumnDescriptionName() {
        return dbfColumnDescriptionName;
    }
    
    public String getDbfColumnBankAccountName() {
        return dbfColumnBankAccountName;
    }
    
    public void setdBUrl(String dBUrl) {
        this.dBUrl = dBUrl;
    }
    
    public void setdBTableName(String dBTableName) {
        this.dBTableName = dBTableName;
    }
    
    public void setdBUser(String dBUser) {
        this.dBUser = dBUser;
    }
    
    public void setdBPassword(String dBPassword) {
        this.dBPassword = dBPassword;
    }
    
    public void setDbfEncoding(String dbfEncoding) {
        this.dbfEncoding = dbfEncoding;
    }
    
    public void setDbfCorrectSum(String dbfCorrectSum) {
        this.dbfCorrectSum = dbfCorrectSum;
    }
    
    public void setDbfBankAccountNumber(String dbfBankAccountNumber) {
        this.dbfBankAccountNumber = dbfBankAccountNumber;
    }
    
    public void setDbfColumnSumName(String dbfColumnSumName) {
        this.dbfColumnSumName = dbfColumnSumName;
    }
    
    public void setDbfColumnDescriptionName(String dbfColumnDescriptionName) {
        this.dbfColumnDescriptionName = dbfColumnDescriptionName;
    }
    
    public void setDbfColumnBankAccountName(String dbfColumnBankAccountName) {
        this.dbfColumnBankAccountName = dbfColumnBankAccountName;
    }
//</editor-fold>
}

public class MainJFrame extends javax.swing.JFrame {
    DbfFile currentFile = null;
    DBConnection dBConn = null;
    int goodPaymentsNum = 0;
    int badPaymentsNum = 0;
    int paidPaymentsNum = 0;
    javax.swing.table.DefaultTableModel tableModel;
    settings appS;
    
  
    /**
     * Creates new form MainJFrame
     */
    
    public MainJFrame() {
        initComponents();
        
        //currentFile = new DbfFile("E:\\test.dbf");      //33 18f
        //currentFile = new DbfFile("E:\\bi89096.dbf"); //33 27f
        //currentFile = new DbfFile("E:\\0302.dbf");    //34 31f
        //currentFile = new DbfFile("E:\\postal.dbf");
        //currentFile.printFileInfo();
        //jTable1.setModel(new javax.swing.table.DefaultTableModel(
        //        currentFile.getTableDataToShow(),
        //        currentFile.getTableTitles()
        //));
        //jTable1.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);  //Никакого ресайза!
        //if (jTable1.getColumnModel().getColumnCount() > 0) {
        //    for(int i = 0; i< currentFile.getNumOfFields(); i++){
        //        jTable1.getColumnModel().getColumn(i).setMinWidth(currentFile.getFieldArray()[i].getSize()*8);
        //    }
        //}
       
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox<>();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jProgressBar1 = new javax.swing.JProgressBar();
        jButton1 = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Модуль обработки платежей участников пробного ЗНО");

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollPane1.setToolTipText("Для начала работы откройте .dbf файл");

        jTable1.setToolTipText("");
        jScrollPane1.setViewportView(jTable1);

        jLabel2.setText("Выберите название столбца с суммой платежа:");

        jLabel3.setText("Выберите название столбца с описанием платежа: ");

        jComboBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox2ActionPerformed(evt);
            }
        });

        jButton2.setText("Выбрать");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("Оплатить");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton1.setText("jButton1");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jMenu1.setText("Файл");

        jMenuItem1.setText("Открыть .dbf");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);
        jMenu1.add(jSeparator1);

        jMenuItem3.setText("Выход");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem3);

        jMenuBar1.add(jMenu1);

        jMenu3.setText("Опции");

        jMenuItem5.setText("Заполнить поля \"bill\" в БД");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem5);

        jMenuItem4.setText("Настройки");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem4);

        jMenuBar1.add(jMenu3);

        jMenu2.setText("Справка");

        jMenuItem2.setText("О программе");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem2);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jProgressBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1076, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton3)
                        .addGap(53, 53, 53)
                        .addComponent(jButton1)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2)
                        .addComponent(jLabel3)
                        .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton2)
                        .addComponent(jButton3)
                        .addComponent(jButton1)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 317, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Файлы .dbf","dbf");
        fileChooser.setFileFilter(filter);
        int ret = fileChooser.showOpenDialog(null);
        if (ret == JFileChooser.APPROVE_OPTION) {
            java.io.File file = fileChooser.getSelectedFile();
            currentFile = new DbfFile(file.toString());
            jTable1.clearSelection();
            jTable1.setModel(new javax.swing.table.DefaultTableModel(
                currentFile.getTableDataToShow(),
                currentFile.getTableTitles()
            ));
            jTable1.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);  //Никакого ресайза!
            if (jTable1.getColumnModel().getColumnCount() > 0) {
                for(int i = 0; i< currentFile.getNumOfFields(); i++){
                    jTable1.getColumnModel().getColumn(i).setMinWidth(currentFile.getFieldArray()[i].getSize()*8);
                }
            }
            //PZDC!
            //jTextField1.setText(Arrays.toString(currentFile.getTableTitles()).substring(1,Arrays.toString(currentFile.getTableTitles()).length()-1));
            
            //Здесь работает
            jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(currentFile.getTableTitles()));
            jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(currentFile.getTableTitles()));
            
            jScrollPane1.setToolTipText(""); //Очистили текст подсказки
            //currentFile.printFileInfo();
            //currentFile.printRecords();

        }
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        System.exit(0); //Освободить еще какие-нибудь ресурсы?
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jComboBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox2ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        
        String[] columsToShow = new String [3];
        columsToShow[0] = "№";
        columsToShow[1] = jComboBox1.getSelectedItem().toString();
        columsToShow[2] = jComboBox2.getSelectedItem().toString();
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
                currentFile.getTableDataToShow(columsToShow),
                currentFile.getTableTitles(columsToShow)
            ));
        
        jTable1.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);  //Никакого ресайза!
        jTable1.getColumnModel().getColumn(0).setMinWidth(50);
        jTable1.getColumnModel().getColumn(0).setMaxWidth(50);
        
        jTable1.getColumnModel().getColumn(1).setMinWidth(60);
        jTable1.getColumnModel().getColumn(1).setMaxWidth(60);
        jTable1.getColumnModel().getColumn(2).setMinWidth(1200);
        //String [] paymentsDescription = currentFile.getDetalsOfPayment(jComboBox2.getSelectedItem().toString());
        //for(int i = 0; i < paymentsDescription.length; i++)
        //    System.out.println(paymentsDescription[i]);
        
        //System.out.println(Arrays.toString(columsToShow));
        //System.out.println(numColumsToShow);      
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        AboutJDialog dialog = new AboutJDialog(new javax.swing.JFrame(), true);
        dialog.setVisible(true);
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
                SettingsJDialog dialog = new SettingsJDialog(new javax.swing.JFrame(), true);
                dialog.setVisible(true);
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        //jTable1.addColumn(new javax.swing.table.TableColumn(0, 200));
        //jTable1.addRowSelectionInterval(1, 10);
        jProgressBar1.setMaximum(currentFile.getNumOfRecords());
        dBConn = new DBConnection();
        try {
            dBConn.init();
        } catch (IOException ex) {
            Logger.getLogger(MainJFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        Pattern codePattern = Pattern.compile("\\d{13}");   //13 цифр подряд
        String [] paymentsSum = currentFile.getDetalsOfPayment(jComboBox1.getSelectedItem().toString());                //Настройки
        String [] paymentsDescription = currentFile.getDetalsOfPayment(jComboBox2.getSelectedItem().toString());        //Настройки
        goodPaymentsNum = 0;
        badPaymentsNum = 0;
        paidPaymentsNum = 0;
        
        //Запустим новый поток с циклом выставления оплат в базу
        Thread doPayments = new Thread(new Runnable(){
            public void run(){
                for (int i = 0; i < paymentsDescription.length; i++){
            
                    Matcher matcher = codePattern.matcher(paymentsDescription[i]);
                    if ((matcher.find())&&(paymentsSum[i].equals(appS.getDbfCorrectSum()))){                                                   //Настройки
                        if (dBConn.writePayment(matcher.group())){
                            goodPaymentsNum++;
                        }else{
                            paidPaymentsNum++;
                        }
                    } else {
                        System.out.println("Ошибка в номере платежа или сумме: "+paymentsSum[i]+" грн. Описание платежа \""+paymentsDescription[i]+"\"");
                        badPaymentsNum++;
                    }
//                    try {
//                        Thread.sleep(100);
//                    } catch (InterruptedException ex) {
//                        Logger.getLogger(MainJFrame.class.getName()).log(Level.SEVERE, null, ex);
//                    }
                    jProgressBar1.setValue(i);
                }
                System.out.println("Поток doPayments завершился!");
                dBConn.closeConnection();
                jProgressBar1.setValue(0);
                PaymentsDoneJDialog dialog = new PaymentsDoneJDialog(new javax.swing.JFrame(), true, goodPaymentsNum, badPaymentsNum, paidPaymentsNum);
                dialog.setVisible(true);
            }
        });
        doPayments.start();
        //
        
        
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        CheckBillJDialog dialog;
        try {
            dialog = new CheckBillJDialog(new javax.swing.JFrame(), true);
            dialog.setVisible(true);
        } catch (IOException ex) {
            Logger.getLogger(MainJFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        appS = new settings();
        //appS.setdBUrl("321");
        
//        appS.setdBUrl("localhost");
//        appS.setdBTableName("khersontes_db");
//        appS.setdBUser("root");
//        appS.setdBPassword("root");
//        appS.setDbfEncoding("windows-1251");
//        appS.setDbfCorrectSum("111.00");
//        appS.setDbfBankAccountNumber("35229201089096");
//        appS.setDbfColumnSumName("FIELD5");
//        appS.setDbfColumnDescriptionName("FIELD9");
//        appS.setDbfColumnBankAccountName("FIELD?");

        try {
            File settingsFile = new File(settings.SETTINGS_FILE_NAME);
            //Грузим из файла или создаем файл и пишем объект с пустыми полями
            if (settingsFile.createNewFile()){                              //Файла не было
                FileOutputStream fos = new FileOutputStream(settingsFile);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(appS);                                      //Записали в файл
                oos.flush();
                oos.close();
            }
            FileInputStream fis = new FileInputStream(settingsFile);
            ObjectInputStream ois = new ObjectInputStream(fis);
            try {
                appS = (settings)ois.readObject();                //Прочитали из файла
            }catch (ClassNotFoundException ex) {
                Logger.getLogger(MainJFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            System.out.println(appS.getdBUrl());
            
        } catch (FileNotFoundException ex) {
            System.err.println("Файл с настройками не найден и не может быть создан.");
        } catch (IOException ex) {
            Logger.getLogger(settings.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainJFrame().setVisible(true);
                
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    javax.swing.JButton jButton1;
    javax.swing.JButton jButton2;
    javax.swing.JButton jButton3;
    javax.swing.JComboBox<String> jComboBox1;
    javax.swing.JComboBox<String> jComboBox2;
    javax.swing.JLabel jLabel2;
    javax.swing.JLabel jLabel3;
    javax.swing.JMenu jMenu1;
    javax.swing.JMenu jMenu2;
    javax.swing.JMenu jMenu3;
    javax.swing.JMenuBar jMenuBar1;
    javax.swing.JMenuItem jMenuItem1;
    javax.swing.JMenuItem jMenuItem2;
    javax.swing.JMenuItem jMenuItem3;
    javax.swing.JMenuItem jMenuItem4;
    javax.swing.JMenuItem jMenuItem5;
    javax.swing.JProgressBar jProgressBar1;
    javax.swing.JScrollPane jScrollPane1;
    javax.swing.JPopupMenu.Separator jSeparator1;
    javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
