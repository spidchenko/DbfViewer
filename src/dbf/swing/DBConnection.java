/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dbf.swing;

import java.io.IOException;
import java.sql.*;
import javax.swing.JOptionPane;
/**
 *
 * @author spidchenko.d
 */
public class DBConnection {
    
    //ВРЕМЕННЫЕ НАСТРОЙКИ, ПАРАМЕТРЫ СОЕДИНЕНИЯ БУДУТ ХРАНИТЬСЯ В ОТДЕЛЬНОМ ФАЙЛЕ
    
    //bill ХХ_Х_ХХ_Х_ХХ_ХХХXХ (13 цифр)
    //формируется из таких полей:
    //ХХ-предмет, Х-язык перевода, ХХ-место сдачи, Х-область, ХХ-город/район, ХХХXХ-id пользователя+10000

    //Если буква "і" отображается в консоли как "?":
    //набираем в консоли ChCp 1251, в ответ получаем
    //Текущая кодовая страница: 1251. Теперь запускаем
    //программу и убеждаемся, что все работает

    
    //localhost SQL server:
    //private static final String URL = "jdbc:mysql://localhost:3306/khersontes_db";
   // private static final String USER = "root";
   // private static final String PASS = "root";
    

    
    private final String query = "SELECT subject_user.id AS S_U_ID, subject.number AS SUBJECT_N, lang.number AS LANG_N, city_passage.number AS CITY_PASSAGE_N, area.number AS AREA_N, city.number AS CITY_N, region.number AS REGION_N, city_type, users.id AS U_ID, bill\n" +
                        "FROM `subject_user`\n" +
                        "JOIN `users` ON users.id = subject_user.user_id\n" +
                        "JOIN `subject` ON subject_id = subject.id\n" +
                        "JOIN `lang` ON lang.id = lang_id\n" +
                        "JOIN `city_passage` ON city_passage.id = city_passage\n" +
                        "JOIN `area` ON area.id = obl\n" +
                        "LEFT OUTER JOIN `city` ON city.id = area\n" +
                        "LEFT OUTER JOIN `region` ON region.id = area\n" +
                        "WHERE subject_user.end = 1";
    
    private boolean isInitDone = false;
    
    private Connection con = null;
    private Statement stmt = null;
    private Statement updateStmt = null;
    private ResultSet rs = null;
    
    
    
    boolean init(){     //Возвращает true если все норм 
        boolean returnStatus = false;
        appSettings currentSettings = new appSettings();
        
        try{
            con = DriverManager.getConnection("jdbc:mysql://"+currentSettings.fields.getdBUrl()+":3306/"
                    +currentSettings.fields.getdBTableName(),
                    currentSettings.fields.getdBUser(),
                    currentSettings.fields.getdBPassword());
            stmt = con.createStatement();
            updateStmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            System.out.println("Database connection initialization OK!"); 
            isInitDone = true;
            returnStatus = true;
        } catch (SQLException sqlEx) {
            //sqlEx.printStackTrace();
            //Героически молчим, красный текст в окне и так появися, а трэйс не поможет понять ошибку
        }
        //Подумать, нужно ли здесь файнали, это же инициализация
//        } finally {
//            //close connection ,stmt and resultset here
//            try { con.close(); } catch(SQLException se) { /*can't do anything */ }
//            try { stmt.close(); } catch(SQLException se) { /*can't do anything */ }
//            try { updateStmt.close(); } catch(SQLException se) { /*can't do anything */ }
//            try { rs.close(); } catch(SQLException se) { /*can't do anything */ }
//        }
        return returnStatus;
    }
    
    int checkAndUpdateBills(String [] problemsToReturn) throws IOException{  //Возвращает количество обновленных строчек
        int rowUpdatedCounter = 0;
        if (!isInitDone) init();
        try{
            rs = stmt.executeQuery(query);
            
            String subjectUserId = "";
            String subjectId = "";
            String langId = "";
            String cityPassageId = "";
            String areaId = "";
            String cityType = "";
            String cityId = "";
            String regionId = "";
            int userId = 0;
            String userId4Digit = "";
            String bill = "";
            String newBill = "";
            String status = "";

            int i = 0;
            int j = 0;

            while(rs.next()){
                //----- get IDs from DB
                subjectUserId = rs.getString("S_U_ID");
                subjectId = rs.getString("SUBJECT_N");
                langId = rs.getString("LANG_N");
                cityPassageId = rs.getString("CITY_PASSAGE_N");
                areaId = rs.getString("AREA_N");
                cityType = rs.getString("city_type");
                cityId = rs.getString("CITY_N");
                regionId = rs.getString("REGION_N");
                userId = rs.getInt("U_ID");
                userId4Digit = Integer.toString(userId + 10000);
                bill = rs.getString("bill");

                if (bill == null) bill = "";                    //если bill в базе "null", то будет выброшен NullPointerException
                if (regionId == null) regionId = "";            //если regionId в базе "null" и человек менял место жительства, то это может сделать плохо =) 
                if (langId == null) langId = "0";               //"null" как поле в запросе это не текст, это null-object
                if (cityId == null) cityId = "";                //Проблем не было, но пусть будет на всякий случай, зачем лишний null-объект?

                //----- Формируем новый bill:
                newBill = "";
                newBill+=subjectId;
                newBill+=langId;         
                newBill+=cityPassageId;
                newBill+=areaId;
                newBill+=(cityType.equals("0"))? regionId : cityId;
                newBill+=userId4Digit;
                //-----

                if (bill.equals(newBill)) {
                    status = "BILL OK";
                } else if (bill.equals("")){
                    updateStmt.executeUpdate("UPDATE `subject_user` SET `bill` = \""+newBill+"\" WHERE `subject_user`.`id` = "+subjectUserId);
                    status = "ROW UPDATED!";
                    rowUpdatedCounter++;
                } else {
                    status = "WTF?";
                }

                //String status = (bill.equals(newBill))? "OK!" : "ERROR ON THIS LINE!";
                //System.out.print(++i +". ");
                if (status.equals("WTF?")) problemsToReturn[i++] = "Код в базе <"+bill+"> отличается от вычисленного <"+newBill+">!";
                /*if (status.equals("WTF?"))*/ System.out.printf("%4d. %2s | %s | %2s | %1s | %4s | %2s | %5s | %13s << = >> %13s -%s\n",j++, subjectId, langId, cityPassageId, areaId, cityId, regionId, userId4Digit, bill, newBill, status);
                //if (bill.equals(newBill)) System.out.println("  -GOTCHA!");
                //else {System.out.println("  x WRONG!");}
            }
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        } finally {
            //close connection, stmt and resultset here
            try {if(con != null) con.close(); } catch(SQLException se) { /*can't do anything */ }
            try {if(stmt != null) stmt.close(); } catch(SQLException se) { /*can't do anything */ }
            try {if(updateStmt != null) updateStmt.close(); } catch(SQLException se) { /*can't do anything */ }
            try {if(rs != null) rs.close(); } catch(SQLException se) { /*can't do anything */ }
            isInitDone = false; //init Undone
        }          
        return rowUpdatedCounter;
    }
    
    boolean writePayment(String billToFind) throws NullPointerException{    //Принимает строку из 13ти цифр кода оплаты
        boolean writeStatus = false;
        int paymentId = 0;
        if (billToFind.matches("\\d{13}")){     //Проверка аргумента перед SQL запросом
            try{
                stmt = con.createStatement();
                updateStmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
                rs = stmt.executeQuery("SELECT `id`, `payment`, COUNT(`id`) AS ROW_NUMBER FROM `subject_user` WHERE `bill` LIKE \""+billToFind+"\"");
                rs.next();
                if ((rs.getString("ROW_NUMBER").equals("1"))&&(rs.getInt("payment") == 0)){
                    paymentId = rs.getInt("id");
                    System.out.println("Payment id = "+paymentId);                                              //!!!
                    updateStmt.executeUpdate("UPDATE `subject_user` SET payment = 1 WHERE `id` = "+paymentId);
                    writeStatus = true;
                    System.out.println("Bill paid");                                                            //!!!
                } else {
                    //System.err.println("Bill <"+billToFind+"> not unique or already paid!");
                }

            } catch (SQLException sqlEx) {
                sqlEx.printStackTrace();
            }
            
                    
        } else{
            //System.err.println("Bill not 13 digits!");
            //Проверим аргумент функции еще раз перед запросом в базу
            JOptionPane.showMessageDialog(null, "Bill not 13 digits!", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return writeStatus;
    }
    
    void closeConnection(){
        try {if(con != null) con.close(); } catch(SQLException se) { /*can't do anything */ }
        try {if(stmt != null) stmt.close(); } catch(SQLException se) { /*can't do anything */ }
        try {if(updateStmt != null) updateStmt.close(); } catch(SQLException se) { /*can't do anything */ }
        try {if(rs != null) rs.close(); } catch(SQLException se) { /*can't do anything */ }
        isInitDone = false; //init Undone      
    }
}
