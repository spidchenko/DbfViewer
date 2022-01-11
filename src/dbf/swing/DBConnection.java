package dbf.swing;

import javax.swing.*;
import java.sql.*;

/**
 * @author spidchenko.d
 */
public class DBConnection {

    //bill 11_2_33_4_55_66666 (13 цифр)
    //формируется из таких полей:
    //11-предмет, 2-язык перевода, 33-место сдачи, 4-область, 55-город/район, 66666-login пользователя

    private boolean isInitDone = false;

    private Connection con = null;
    private Statement stmt = null;
    private Statement updateStmt = null;
    private ResultSet rs = null;

    /**
     * Init Database connection
     *
     * @return boolean - is init OK
     */
    boolean init() {     //Возвращает true если все норм
        boolean returnStatus = false;
        appSettings currentSettings = new appSettings();

        try {
            con = DriverManager.getConnection("jdbc:mysql://" + currentSettings.fields.getdBUrl() + ":3306/"
                            + currentSettings.fields.getdBTableName(),
                    currentSettings.fields.getdBUser(),
                    currentSettings.fields.getdBPassword());
            stmt = con.createStatement();
            updateStmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            System.out.println("Database connection initialization OK!");
            isInitDone = true;
            returnStatus = true;
        } catch (SQLException sqlEx) {
            JOptionPane.showMessageDialog(null, sqlEx.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            sqlEx.printStackTrace();

        }
        return returnStatus;
    }

    int checkAndUpdateBills(String[] problemsToReturn) {  //Возвращает количество обновленных строчек
        int rowUpdatedCounter = 0;
        if (!isInitDone) init();
        try {
            String query = """
                    SELECT subject_user.id AS S_U_ID, subject.number AS SUBJECT_N, lang.number AS LANG_N, city_passage.number AS CITY_PASSAGE_N, area.number AS AREA_N, city.number AS CITY_N, region.number AS REGION_N, city_type, users.log AS U_LOGIN, bill
                    FROM `subject_user`
                    JOIN `users` ON users.id = subject_user.user_id
                    JOIN `subject` ON subject_id = subject.id
                    JOIN `lang` ON lang.id = lang_id
                    JOIN `city_passage` ON city_passage.id = city_passage
                    JOIN `area` ON area.id = obl
                    LEFT OUTER JOIN `city` ON city.id = area
                    LEFT OUTER JOIN `region` ON region.id = area
                    WHERE subject_user.end = 1
                    ORDER BY `users`.`id` ASC""";
            rs = stmt.executeQuery(query);

            int i = 0;
            int j = 0;

            while (rs.next()) {
                //----- get IDs from DB
                String subjectUserId = rs.getString("S_U_ID");
                String subjectId = rs.getString("SUBJECT_N");
                String langId = rs.getString("LANG_N");
                String cityPassageId = rs.getString("CITY_PASSAGE_N");
                String areaId = rs.getString("AREA_N");
                String cityType = rs.getString("city_type");
                String cityId = rs.getString("CITY_N");
                String regionId = rs.getString("REGION_N");
                String userId4Digit = Integer.toString(rs.getInt("U_LOGIN"));
                String bill = rs.getString("bill");

                if (bill == null) {
                    bill = "";
                }

                if (regionId == null) {
                    regionId = "";
                }

                if (langId == null) {
                    langId = "0";
                }

                if (cityId == null) {
                    cityId = "";
                }


                //----- Формируем новый bill:
                StringBuilder newBill = new StringBuilder();
                newBill.append(subjectId);
                newBill.append(langId);
                newBill.append(cityPassageId);
                newBill.append(areaId);
                newBill.append((cityType.equals("0")) ? regionId : cityId);
                newBill.append(userId4Digit);
                //-----

                String status;

                if (bill.equals(newBill.toString())) {
                    status = "BILL OK";
                } else if (bill.equals("")) {
                    updateStmt.executeUpdate("UPDATE `subject_user` SET `bill` = \"" + newBill + "\" WHERE `subject_user`.`id` = " + subjectUserId);
                    status = "ROW UPDATED!";
                    rowUpdatedCounter++;
                } else {
                    status = "WTF?";
                }

                if (status.equals("WTF?"))
                    problemsToReturn[i++] = "Код в базе <" + bill + "> отличается от вычисленного <" + newBill + ">!";
                /*if (status.equals("WTF?"))*/
                System.out.printf("%4d. %2s | %s | %2s | %1s | %4s | %2s | %5s | %13s << = >> %13s -%s\n", j++, subjectId, langId, cityPassageId, areaId, cityId, regionId, userId4Digit, bill, newBill, status);
                if (bill.equals(newBill.toString())) System.out.println("  -GOTCHA!");
                else {
                    System.out.println("  x WRONG!");
                }
            }
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        } finally {
            //close connection, stmt and result set here
            try {
                if (con != null) con.close();
            } catch (SQLException se) { /*can't do anything */ }
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException se) { /*can't do anything */ }
            try {
                if (updateStmt != null) updateStmt.close();
            } catch (SQLException se) { /*can't do anything */ }
            try {
                if (rs != null) rs.close();
            } catch (SQLException se) { /*can't do anything */ }
            isInitDone = false; //init Undone
        }
        return rowUpdatedCounter;
    }

    boolean writePayment(String billToFind) throws NullPointerException {    //Принимает строку из 13ти цифр кода оплаты
        boolean writeStatus = false;
        int paymentId;
        if (billToFind.matches("\\d{13}")) {     //Проверка аргумента перед SQL запросом
            try {
                stmt = con.createStatement();
                updateStmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
                rs = stmt.executeQuery("SELECT `id`, `payment`, COUNT(`id`) AS ROW_NUMBER FROM `subject_user` WHERE `bill` LIKE \"" + billToFind + "\"");
                rs.next();
                if ((rs.getString("ROW_NUMBER").equals("1")) && (rs.getInt("payment") == 0)) {
                    paymentId = rs.getInt("id");
                    System.out.println("Payment id = " + paymentId);
                    updateStmt.executeUpdate("UPDATE `subject_user` SET payment = 1 WHERE `id` = " + paymentId);
                    writeStatus = true;
                    System.out.println("Bill paid");
                }
            } catch (SQLException sqlEx) {
                JOptionPane.showMessageDialog(null, sqlEx.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                sqlEx.printStackTrace();
            }

        } else {
            //System.err.println("Bill not 13 digits!");
            //Проверим аргумент функции еще раз перед запросом в базу
            JOptionPane.showMessageDialog(null, "Bill not 13 digits!", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return writeStatus;
    }

    void closeConnection() {
        try {
            if (con != null) con.close();
        } catch (SQLException se) { /*can't do anything */ }
        try {
            if (stmt != null) stmt.close();
        } catch (SQLException se) { /*can't do anything */ }
        try {
            if (updateStmt != null) updateStmt.close();
        } catch (SQLException se) { /*can't do anything */ }
        try {
            if (rs != null) rs.close();
        } catch (SQLException se) { /*can't do anything */ }
        isInitDone = false; //init Undone      
    }
}
