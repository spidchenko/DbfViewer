/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dbf.swing;


import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author spidchenko.d
 */
class BadColumnNameException extends Exception {
    BadColumnNameException(String details){
        super(details);
    }
}

class Field{
    private String name;    //Название столбца
    private int size;       //Ширина столбца в байтах
    private int offset;     //Смещение от начала записи в байтах
        
    public Field(String newName, int newSize, int newOffset){
        name = newName;
        size = newSize;
        offset = newOffset;
    }
        
    static Field[] initializeFieldsArray(int fieldsCount){
        Field [] fieldArrayReturn = new Field [fieldsCount];  //Массив столбцов
        
        for (int i = 0; i< fieldArrayReturn.length; i++){
                fieldArrayReturn[i] = new Field("", 0, 0);
            }
        return fieldArrayReturn;
    }
//<editor-fold defaultstate="collapsed" desc="SETERS-GETERS">
    void setName(String newName){
        name = newName;
    }
    
    void setSize(int newSize){
        size = newSize;
    }
    
    void setOffset(int newOffset){
        offset = newOffset;
    }
    
    String getName(){
        return name;
    }
    
    int getSize(){
        return size;
    }
    int getOffset(){
        return offset;
    }
//</editor-fold>
}

class DbfFile {
    static final int SERVICE_HEADER_LENGTH = 32;//Первые 32 байта файла - служебная информация
    static final int FIELD_DESCRIPTION_LENGTH = 32;  //Длина описания столбца - 32 байта
    static final int CURRENT_FIELD_NAME = 9;    //0-9 байты
    static final int CURRENT_FIELD_LENGTH = 16; //16й байт с длиной текущего столбца
    static Charset fileCharset = null;//Charset.forName(new appSettings().fields.getDbfEncoding());//Charset.forName("cp866");//Кодировка
    private int fieldsDescribeHeaderLength = 0;  //Длина заголовка с описанием столбцов (поиск 0x0D байта, не делать так, может быть в описании колонок!)
    private int headerLength = 0;                //Полная длина заголовка в байтах (из 8-9 байта)
    private int numOfFields = 0;                 //Количество столбцов таблицы
    private int numOfRecords = 0;                //Количество записей в таблице
    private int oneRecordLength = 0;             //Длина одной записи в байтах
    private Field [] fieldArray;
        //------
    private String filePath = "";//E:\\0302.dbf"; 
    private Object[][] tableData;               //Данные для отображения в jTable
    
        
    public DbfFile(String filePathToOpen, String charset){
        filePath = filePathToOpen;
        fileCharset = Charset.forName(charset);
        
        //--
        FileInputStream inputStream = null;
        byte[] byteBufferArray = new byte[1024];   //Байтовый буфер для чтения. Самая длинная запись которую я видел - 505 байт, пусть будет в 2 раза больше
                
        try{
            inputStream = new FileInputStream(filePath);
            inputStream.read(byteBufferArray, 0, SERVICE_HEADER_LENGTH);
            //Делаем unsigned byte массив [4-7] байтов (количество записей, старший байт справа)
            int [] byteArray = new int[4];
            for(int i = 0; i<4; i++){
                byteArray[i] = (byteBufferArray[i+4]>0)?byteBufferArray[i+4]:(byteBufferArray[i+4] & 0xFF);
            }
            //сдвигаем байты (старший слева) и получаем количество записей (32бит число)
            numOfRecords = byteArray[0]|(byteArray[1]<<8)|(byteArray[2]<<16)|(byteArray[3]<<24);
            //Делаем unsigned byte массив [8-9] байтов (количество байт в заголовке, старший байт справа)
            for(int i = 0; i<2; i++){
                byteArray[i] = (byteBufferArray[i+8]>0)?byteBufferArray[i+8]:(byteBufferArray[i+8] & 0xFF);
            }
            //сдвигаем байты (старший слева) и получаем длину одной записи (16бит число)
            headerLength = byteArray[0]|(byteArray[1]<<8);
            //headerLength+=1;
            //Делаем unsigned byte массив [10-11] байтов (длина одной записи, старший байт справа)
            for(int i = 0; i<2; i++){
                byteArray[i] = (byteBufferArray[i+10]>0)?byteBufferArray[i+10]:(byteBufferArray[i+10] & 0xFF);
            }           
            //сдвигаем байты (старший слева) и получаем длину одной записи (16бит число)
            oneRecordLength = byteArray[0]|(byteArray[1]<<8);
                                
            while(inputStream.read()!=0xD){     //Поиск конца заголовка: байт 0xD
                fieldsDescribeHeaderLength++;
            }
        
            //Считаем количество столбцов в таблице
            numOfFields = (headerLength - SERVICE_HEADER_LENGTH - 1)/FIELD_DESCRIPTION_LENGTH;
            
            inputStream = new FileInputStream(filePath);    //Откроем еше раз, чтобы вернуться в начало файла, как иначе хз
            inputStream.skip(SERVICE_HEADER_LENGTH);    //Пропустили служебный предзаголовок

        //Парсим описания столбцов таблицы (fieldArray):
            fieldArray = Field.initializeFieldsArray(numOfFields);  //Инициализируем массив столбцов
            for (int i = 0; i < fieldArray.length; i++){
                inputStream.read(byteBufferArray, 0, FIELD_DESCRIPTION_LENGTH);    //32 байта 
                //Название столбца (вытащили из байтового массива и убрали пробелы с конца, перевели в верхний регистр одной коммандой! >:3 )
                //new String корректно отработает с default charset ASCII, на линуксе или в Японии с UTF Default будут проблемы 
                fieldArray[i].setName(new String(Arrays.copyOfRange(byteBufferArray, 0, CURRENT_FIELD_NAME)).trim().toUpperCase());  //9 байт
                //Размер столбца
                if (byteBufferArray[CURRENT_FIELD_LENGTH] > 0){
                    fieldArray[i].setSize(byteBufferArray[CURRENT_FIELD_LENGTH]);
                } else{
                    fieldArray[i].setSize(byteBufferArray[CURRENT_FIELD_LENGTH] & 0xFF);
                }
                //Сдвиг от начала записи в байтах
                if (i != 0){
                    fieldArray[i].setOffset(fieldArray[i-1].getOffset()+fieldArray[i-1].getSize());
                } else{
                    fieldArray[i].setOffset(0);
                }
            }
        //---    

        //Парсим строки таблицы (tableData):
            String currentLine = "";
            //Файловый курсор сейчас перед 0xD [0xD, 0x0]
            inputStream = new FileInputStream(filePath);  //Откроем еше раз, чтобы вернуться в начало файла, как иначе хз
            inputStream.skip(headerLength+1);       //Пропустили весь заголовок +1 байт
                       
            tableData = new String [numOfRecords][numOfFields];
            for (int i = 0; i < numOfRecords; i++){
                //Считали одну запись в byteBufferArray
                inputStream.read(byteBufferArray, 0, oneRecordLength);
                //Декодировали массив byteBufferArray, обернутый в байтбуффер в UTF-16 
                //Имеем на выходе строку UTF-16 с полями из DBF файла
                currentLine = fileCharset.decode(ByteBuffer.wrap(byteBufferArray,0, oneRecordLength)).toString();
            //    currentLine = Charset.forName("windows-1251").decode(ByteBuffer.wrap(byteBufferArray,0, oneRecordLength)).toString(); 
                for(int j =0; j < numOfFields; j++){
                    tableData[i][j] = currentLine.substring(fieldArray[j].getOffset(),
                    fieldArray[j].getOffset() + fieldArray[j].getSize()).trim();
                }
            }
        //---    
        } catch (IOException ex) {
            Logger.getLogger(DbfFile.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (inputStream != null) {try {
                inputStream.close();
                } catch (IOException ex) {
                    Logger.getLogger(DbfFile.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
            //--
    }//End of DbfFile Constructor method
    
//<editor-fold defaultstate="collapsed" desc="GETERS-SETERS">
    int getNumOfRecords(){
        return numOfRecords;
    }
    int getNumOfFields(){
        return numOfFields;
    }
    
    Field[] getFieldArray(){
        return fieldArray;
    }
    
     String[] getTableTitles(){
        //Формируем для таблицы jTable данные
        //Извлекли из массива только названия столбцов для отображения:
        String tableTitles[] = new String[fieldArray.length];
        for(int i = 0; i < fieldArray.length; i++){
            tableTitles[i] = fieldArray[i].getName();
        }
        return tableTitles;
    }
 
     String[] getTableTitles(String[] columsToShow) throws BadColumnNameException{        //Возвращает массив с названиями столбцов
        String onlyNames[] = new String[fieldArray.length];
        for(int i = 0; i < fieldArray.length; i++){
            onlyNames[i] = fieldArray[i].getName();
        }
        String tableTitles[] = new String[columsToShow.length];//Максимальная длина
        String fieldArrayInString = Arrays.toString(onlyNames);
        
        for(int i = 0; i < columsToShow.length; i++){
            if(fieldArrayInString.contains(columsToShow[i].toUpperCase())){
                tableTitles[i] = columsToShow[i].toUpperCase();
            } else
                if(columsToShow[i].equals("№")){
                    tableTitles[i] = "№";
                }
                else{
                    //System.out.println("Column "+columsToShow[i].toUpperCase()+" not found in "+fieldArrayInString);     //NEED THROW SOMETHNIG HERE!!!!!!!
                    //Выбрасываем ошибку!
                    throw new BadColumnNameException(columsToShow[i].toUpperCase());
                    //JOptionPane.showMessageDialog(null,"Столбец \""+columsToShow[i].toUpperCase()+"\" не найден в списке столбцов этого файла: \n"+fieldArrayInString+"\nПроверьте настройки приложения.", "Ошибка!", JOptionPane.ERROR_MESSAGE);
                }
        }
        //System.out.print(Arrays.toString(tableTitles));
        return tableTitles;
        //Есть массив названий необходимых колонок
        //поиск таких названий в fieldArray
        //формируем массив строк и возвращаем если есть названия
     }
     
    Object[][] getTableDataToShow(){    //Возвращает массив записей для jTable
        return tableData;
    }
    
    Object[][] getTableDataToShow(String[] columsToShow) throws BadColumnNameException{
        String [] titles = getTableTitles(columsToShow);
        Object [][] newTableData = new String [numOfRecords][titles.length];
        int currentColumnOffset = 0;
        //Вот здесь магия выбора столбцов по названию
        //Проходим по количеству столбцов, которые нужно выбрать (k),
        //проходим по названиям столбцов (j), если нашли тот, который нужно выбрать, то
        //копируем весь столбец(по i вниз) из tableData в newTableData
        for(int k = 0; k < titles.length; k++){
            for (int j = 0; j < numOfFields; j++){
                if(fieldArray[j].getName().equalsIgnoreCase(titles[k])){
                    for(int i = 0; i < numOfRecords; i++){
                        newTableData[i][k] = tableData[i][j];
                    }           
                }//Костыль для нумерации строчек
                else if(titles[k].equals("№")){
                    for(int i = 0; i < numOfRecords; i++){
                        newTableData[i][k] = Integer.toString(i+1);
                    }
                }
            }            
        }

        
        
        return newTableData;    
        }
        //System.out.println(Arrays.toString(titles));
        //!
    
    int getOneRecordLength(){
        return oneRecordLength;
    }
//</editor-fold>
    
    void printFileInfo(){   //Печать служебной информации, заголовков в консоль
        System.out.print("\n");
        System.out.format("fieldsDescribeHeaderLength:%4d \nheaderLength: %4d \nnumOfFields:%3d \nnumOfRecords:%4d \noneRecordLength:%4d\n\n", fieldsDescribeHeaderLength, headerLength, numOfFields, numOfRecords, oneRecordLength);
            
        for (Field fieldArray1 : fieldArray) {
            System.out.format("%10s | %5d | %5d \n", fieldArray1.getName(), fieldArray1.getSize(), fieldArray1.getOffset());
        }
        System.out.print("\n");        
    }
    
    void printRecords(){    //Печать содержимого файла в консоль
        for(int i = 0; i < numOfRecords; i++){
            for(int j = 0; j < numOfFields; j++){
                System.out.printf("%"+fieldArray[j].getSize()+"s",getTableDataToShow()[i][j]);
            }            
        System.out.print("\n");
        }
    }
    
    String [] getDetalsOfPayment(String detalsOfPaymentRow){
        String[] detalsOfPayment = new String[numOfRecords];           //Данные для поиска кода оплаты
        int numFieldToGet=0;
        //поиск столбца
        for(int i = 0; i < numOfFields; i++){
            if (fieldArray[i].getName().equals(detalsOfPaymentRow)){
                numFieldToGet = i;
                break;
            }     
        }
        
        for(int i = 0; i < numOfRecords; i++){
            detalsOfPayment[i] = tableData[i][numFieldToGet].toString();
        }
        return detalsOfPayment;
    }

    
}