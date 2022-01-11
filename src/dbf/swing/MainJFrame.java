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
import java.io.File;
import java.io.PrintWriter;
import javax.swing.JOptionPane;

/**
 * Сохранять логи оплаченых! по имени выписки+ _good.txt, _bad.txt, _duplicate.txt
 * Сделать проверку счета контрагента и дебеткредит                             отобрать может только с этим счетом? нужно смотреть боевой файл-выписку
 * Сделать цикл оплат в отдельном потоке!                                       сложно, отложить
 * Ширину столбца по макс длине содержимого - сортировка и ленгтх первого?      так ли необходимо? вьювер есть, зачем еще красивей?
 * Выбрасывать эксэпшн при инвалидной структуре столбцов - пустое название и длина?
 * @author spidchenko.d
 */
class settingsFields implements Serializable{
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

class appSettings {
    settingsFields fields;
    
    appSettings(){
        this.fields = new settingsFields();
        try {
            File settingsFile = new File(settingsFields.SETTINGS_FILE_NAME);
            //Грузим из файла или создаем файл и пишем объект с пустыми полями
            if (settingsFile.createNewFile()){                              //Файла не было
                FileOutputStream fos = new FileOutputStream(settingsFile);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(fields);                                    //Записали в файл
                oos.flush();
                oos.close();
            }
            FileInputStream fis = new FileInputStream(settingsFile);
            ObjectInputStream ois = new ObjectInputStream(fis);
            try {
                fields = (settingsFields)ois.readObject();                //Прочитали из файла
            }catch (ClassNotFoundException ex) {
                Logger.getLogger(MainJFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (FileNotFoundException ex) {
            System.err.println("Файл с настройками не найден и не может быть создан.");
        } catch (IOException ex) {
            Logger.getLogger(appSettings.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    void saveAndReload(){
        try {
            File settingsFile = new File(settingsFields.SETTINGS_FILE_NAME);
            FileOutputStream fos = new FileOutputStream(settingsFile);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(fields);                                    //Записали в файл
            oos.flush();
            oos.close();
            
            FileInputStream fis = new FileInputStream(settingsFile);
            ObjectInputStream ois = new ObjectInputStream(fis);
            try {
                fields = (settingsFields)ois.readObject();                //Прочитали из файла
            }catch (ClassNotFoundException ex) {
                Logger.getLogger(MainJFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (FileNotFoundException ex) {
            System.err.println("Файл с настройками не найден и не может быть создан.");
        } catch (IOException ex) {
            Logger.getLogger(appSettings.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

public class MainJFrame extends javax.swing.JFrame {
    DbfFile currentFile = null;
    DBConnection dBConn = null;
    int goodPaymentsNum = 0;
    int badPaymentsNum = 0;
    int duplicatePaymentsNum = 0;
    javax.swing.table.DefaultTableModel tableModel;
    String[] columsToShow;
    
    appSettings currentSettings;
    
  
    /**
     * Creates new form MainJFrame
     */

    public MainJFrame() {
        initComponents();
        currentSettings = new appSettings();
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
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jProgressBar1 = new javax.swing.JProgressBar();
        jTextField1 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
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

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jTable1.setToolTipText("");
        jTable1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        jTable1.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(jTable1);

        jButton2.setText("Убрать лишние столбцы");
        jButton2.setEnabled(false);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("Оплатить");
        jButton3.setEnabled(false);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jTextField1.setEnabled(false);
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton1.setText("Найти");
        jButton1.setEnabled(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel1.setText("Поиск по описанию платежа:");
        jLabel1.setEnabled(false);

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
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 364, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2)
                    .addComponent(jButton3)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        JFileChooser fileChooser = new JFileChooser(".");   //Curent folder
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Файлы .dbf","dbf");
        fileChooser.setFileFilter(filter);
        int ret = fileChooser.showOpenDialog(null);
        if (ret == JFileChooser.APPROVE_OPTION) 
        try{
            
            java.io.File file = fileChooser.getSelectedFile();
            currentFile = new DbfFile(file, currentSettings.fields.getDbfEncoding());
            //System.out.println(file.getName());
            Object[][] tableData = currentFile.getTableDataToShow();
            String [] tableTitles = currentFile.getTableTitles();
            
            //Вывод информации в jTable
            jTable1.setModel(new javax.swing.table.DefaultTableModel(
                tableData,
                tableTitles
            ));
            
            //Оптимизация ширины столбцов для показа
            int currentFieldMaxLength = 0;
            for(int j = 0; j < currentFile.getNumOfFields(); j++){
                currentFieldMaxLength = 0;
                for(int i = 0; i < currentFile.getNumOfRecords(); i++){
                    if(tableData[i][j].toString().length() > currentFieldMaxLength){
                        currentFieldMaxLength = tableData[i][j].toString().length();
                    }
                }
                //System.out.printf("%3d | %5d\n", j, currentFieldMaxLength);
                jTable1.getColumnModel().getColumn(j).setMinWidth(currentFieldMaxLength*9);
            }
            jTable1.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);  //Никакого авторесайза столбцов по ширине окна!            
            
            //Поможем сборщику мусора?
            tableData = null;
            tableTitles = null;

            jScrollPane1.setToolTipText(""); //Очистили текст подсказки
            jButton2.setEnabled(true);       //Активировали кнопку "убрать.." 
           
         }catch(BadFileException badFileExc){
             JOptionPane.showMessageDialog(null, "Ошибка открытия .dbf файла.\nВозможно он поврежден или имеет недопустимый формат", "Невозможно открыть файл!", JOptionPane.ERROR_MESSAGE);
         }
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        System.exit(0); //Освободить еще какие-нибудь ресурсы?
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        //currentSettings = new appSettings();    //Переинициализация настроек перед использованием не нужна, теперь мы изменям объект настроек в отдельном окне
        try{
            String[] columsToShow = new String [4];
            columsToShow[0] = "№";
            columsToShow[1] = currentSettings.fields.getDbfColumnSumName();
            columsToShow[2] = currentSettings.fields.getDbfColumnBankAccountName();
            columsToShow[3] = currentSettings.fields.getDbfColumnDescriptionName();
            jTable1.setModel(new javax.swing.table.DefaultTableModel(
                    currentFile.getTableDataToShow(columsToShow),
                    currentFile.getTableTitles(columsToShow)
                ));

            jTable1.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);  //Ресайз последнего столбца таблицы

            //Подгоним руками ширину столбцов
            jTable1.getColumnModel().getColumn(0).setMinWidth(40);  //№
            jTable1.getColumnModel().getColumn(0).setMaxWidth(40);
            jTable1.getColumnModel().getColumn(1).setMinWidth(60);  //Сумма
            jTable1.getColumnModel().getColumn(1).setMaxWidth(60);
            jTable1.getColumnModel().getColumn(2).setMinWidth(110); //Номер счета
            jTable1.getColumnModel().getColumn(2).setMaxWidth(110);
            jTable1.getColumnModel().getColumn(3).setMinWidth(1200);//Описание платежа. MaxWidth определяется ресайзом окна
            
            jButton3.setEnabled(true);      //Активировали кнопку "оплатить"
            jLabel1.setEnabled(true);       //Активировали блок поиска
            jTextField1.setEnabled(true);   //~~
            jButton1.setEnabled(true);      //~~
            
            
        }catch(BadColumnNameException badExc){
            JOptionPane.showMessageDialog(null, "Не найден столбец \""+badExc.getMessage()+"\"\nПроверьте настройки.", "Ошибка", JOptionPane.ERROR_MESSAGE);
            //System.out.println("Bad column Name Exc :"+badExc.getMessage());
        }
        
     
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        AboutJDialog dialog = new AboutJDialog(new javax.swing.JFrame(), true);
        dialog.setVisible(true);
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
                SettingsJDialog dialog = new SettingsJDialog(new javax.swing.JFrame(), true, currentSettings);  //Передаем объект настроек для изменения
                dialog.setVisible(true);
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        jProgressBar1.setMaximum(currentFile.getNumOfRecords());
        dBConn = new DBConnection();
        dBConn.init();
        
        Pattern codePattern = Pattern.compile("\\d{13}");   //13 цифр подряд
        String [] paymentsSum = currentFile.getDetalsOfPayment(currentSettings.fields.getDbfColumnSumName());                       //Настройки - столбец "сумма платежа"
        String [] paymentsDescription = currentFile.getDetalsOfPayment(currentSettings.fields.getDbfColumnDescriptionName());       //Настройки - столбец "описание платежа"
        String [] paymentsBankAccount = currentFile.getDetalsOfPayment(currentSettings.fields.getDbfColumnBankAccountName());       //Настройки - столбец "номер счета контрагента"
        
//        int paymentsDescriptionObjectSize = 0;
//        for (int i = 0; i < paymentsDescription.length; i++)
//            paymentsDescriptionObjectSize += paymentsDescription[i].length();
//        paymentsDescriptionObjectSize += 64*paymentsDescription.length;
//        System.out.println("paymrentsDescriptionObjectSize = "+paymentsDescriptionObjectSize+ " bytes");
        goodPaymentsNum = 0;
        badPaymentsNum = 0;
        duplicatePaymentsNum = 0;
        
        //Запустим новый поток с циклом выставления оплат в базу
        //так приложение не будет зависать на время работы с базой 
        Thread doPayments = new Thread(new Runnable(){
            public void run(){
                try{
                    File fileSuccess = new File(currentFile.getFile().getName()+"_Success.txt");
                    File fileFail = new File(currentFile.getFile().getName()+"_Fail.txt");
                    File fileDuplicates = new File(currentFile.getFile().getName()+"_Duplicates.txt");
                    if(!fileSuccess.exists()){
                        fileSuccess.createNewFile();
                    }
                    if(!fileFail.exists()){
                        fileFail.createNewFile();
                    }
                    if(!fileDuplicates.exists()){
                        fileDuplicates.createNewFile();
                    }
                    
                    PrintWriter outSuccess = new PrintWriter(fileSuccess.getAbsoluteFile(), "UTF8");
                    PrintWriter outFail = new PrintWriter(fileFail.getAbsoluteFile(), "UTF8");
                    PrintWriter outDuplicates = new PrintWriter(fileDuplicates.getAbsoluteFile(), "UTF8");
                    
                    
                    
                    //System.out.println(currentFile.getFile().getName());
                    try{
                        
                        for (int i = 0; i < paymentsDescription.length; i++){
                            
                            if (!paymentsBankAccount[i].equals(currentSettings.fields.getDbfBankAccountNumber())){  
                                jProgressBar1.setValue(i);      //Двигаем прогресс бар в другом потоке!
                                continue;       //Пропускаем запись если номер счета контрагента != (Настройки - номер счета контрагента)
                            }                            

                            Matcher matcher = codePattern.matcher(paymentsDescription[i]);
                            if ((matcher.find())&&                                                      //Если нашли в описании платежа 13 цифр подряд
                                (paymentsSum[i].equals(currentSettings.fields.getDbfCorrectSum()))){    //..и сумма платежа == (Настройки - сумма платежа)
                                  
                                if (dBConn.writePayment(matcher.group())){
                                    goodPaymentsNum++;
                                    outSuccess.println(("Оплачено: "+paymentsSum[i]+" грн. Описание платежа \""+paymentsDescription[i]+"\""));
                                }else{      //writePayment вернул false
                                    duplicatePaymentsNum++;
                                    outDuplicates.println("Возможно повторный платеж: "+paymentsSum[i]+" грн. Описание платежа \""+paymentsDescription[i]+"\"");
                                }
                            } else {        //тройное условие вернуло false
                                outFail.println("Ошибка в номере платежа или сумме: "+paymentsSum[i]+" грн. Описание платежа \""+paymentsDescription[i]+"\"");
                                System.out.println("Ошибка в номере платежа или сумме: "+paymentsSum[i]+" грн. Описание платежа \""+paymentsDescription[i]+"\"");
                                badPaymentsNum++;
                            }

                            jProgressBar1.setValue(i);      //Двигаем прогресс бар в другом потоке!
                        }
                    } finally{
                        //Закрыли файловые потоки:
                        outSuccess.close();
                        outFail.close();
                        outDuplicates.close();
                    }
                    PaymentsDoneJDialog dialog = new PaymentsDoneJDialog(new javax.swing.JFrame(), true, goodPaymentsNum, badPaymentsNum, duplicatePaymentsNum);
                    dialog.setVisible(true);
                } catch (NullPointerException nullEx){
                    JOptionPane.showMessageDialog(null, "Нет соединения с базой данных.\nПроверьте настройки и попробуйте еще раз", "Ошибка БД!", JOptionPane.ERROR_MESSAGE);
                } catch (IOException ex) {
                    Logger.getLogger(MainJFrame.class.getName()).log(Level.SEVERE, null, ex);
                } finally{
                    //System.out.println("Поток doPayments завершился!");                 //!!!
                    dBConn.closeConnection();
                    jProgressBar1.setValue(0);
                }
            }
        });
        doPayments.start();
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
        String textToFind = jTextField1.getText();
        boolean isFoundSomething = false;
        jTable1.clearSelection();
        for(int i = 0; i < currentFile.getNumOfRecords(); i++){
            if (jTable1.getValueAt(i, 3).toString().contains(textToFind)){//3 - magic number
                jTable1.addRowSelectionInterval(i, i);
                isFoundSomething = true;
            }
        }
        if (!isFoundSomething){
            JOptionPane.showMessageDialog(null, "Совпадений не найдено", "Результат поиска", JOptionPane.INFORMATION_MESSAGE);
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
    javax.swing.JLabel jLabel1;
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
    javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}
