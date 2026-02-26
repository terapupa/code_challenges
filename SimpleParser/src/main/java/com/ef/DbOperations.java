package com.ef;

import org.apache.commons.dbutils.DbUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import static com.ef.Parser.printError;

public class DbOperations {


    private String dbServerUrl;
    private String dbUserName;
    private String dbPassword;
    private String dbName;

    private String dbUrl;

    private Connection conn = null;
    private Statement stmt = null;

    public DbOperations() {
        Properties dbProps = new Properties();
        try {
            dbProps.load(getClass().getClassLoader().getResourceAsStream("db.properties"));
            Class.forName(dbProps.getProperty("DB_DRIVER_CLASS"));
        } catch (IOException | ClassNotFoundException e) {
            printError(e.getMessage(), true);
        }
        this.dbServerUrl = dbProps.getProperty("DB_SERVER_URL");
        this.dbUserName = dbProps.getProperty("DB_USERNAME");
        this.dbPassword = dbProps.getProperty("DB_PASSWORD");
        this.dbName = dbProps.getProperty("DB_NAME");
        this.dbUrl = dbServerUrl + dbName;
    }

    public void setDbServerUrl(String dbServerUrl) {
        this.dbServerUrl = dbServerUrl;
        this.dbUrl = this.dbServerUrl + dbName;
    }

    public String getDbServerUrl() {
        return this.dbServerUrl;
    }

    public void setDbUserName(String dbUserName) {
        this.dbUserName = dbUserName;
    }

    public String getDbUserName() {
        return this.dbUserName;
    }

    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }

    public String getDbPassword() {
        return this.dbPassword;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
        this.dbUrl = this.dbServerUrl + this.dbName;
    }

    public String getDbName() {
        return this.dbName;
    }

    /**
     * Create DB, create the DB scheme and clean the DB tables if it's necessary
     *
     * @param clearTables - <code>clearTables</code>=true - remove data from all tables
     */
    public void checkDBExistsAndCreate(boolean clearTables) {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = DriverManager.getConnection(dbServerUrl, dbUserName, dbPassword);
            System.out.println("Database connection created successfully...");
            stmt = conn.createStatement();
            String sql = "CREATE DATABASE IF NOT EXISTS " + dbName;
            stmt.executeUpdate(sql);
            System.out.println("Database " + dbName + " init successfully...");
            checkTablesExistsAndCreate(clearTables);
        } catch (Exception e) {
            printError(e.getMessage(), true);
        } finally {
            DbUtils.closeQuietly(stmt);
            DbUtils.closeQuietly(conn);
        }
    }

    private void checkTablesExistsAndCreate(boolean clearTables) {
        try {
            createConnectionAndStatement();
            String sql = "CREATE TABLE IF NOT EXISTS request " +
                    "(id INT(64) NOT NULL AUTO_INCREMENT, time_stamp DATETIME(3), ip VARCHAR(40)," +
                    " PRIMARY KEY(id))";
            stmt.addBatch(sql);
            sql = "CREATE TABLE IF NOT EXISTS blocked_ip " +
                    "(ip VARCHAR(40), request_number INT, " +
                    "mesured_at DATETIME(3), duration VARCHAR(25), threshold INT, reason VARCHAR(255)," +
                    " PRIMARY KEY(ip, request_number, mesured_at, duration, threshold))";
            stmt.addBatch(sql);
            if (clearTables) {
                stmt.addBatch("TRUNCATE blocked_ip");
                stmt.addBatch("TRUNCATE request");
            }
            stmt.executeBatch();
            System.out.println("Tables init successfully...");
        } catch (Exception e) {
            printError(e.getMessage(), true);
        } finally {
            closeConnectionAndStatement();
        }
    }

    /**
     * Create DB connection and statement
     *
     * @throws SQLException
     */
    public void createConnectionAndStatement() throws SQLException {
        conn = DriverManager.getConnection(dbUrl, dbUserName, dbPassword);
        stmt = conn.createStatement();
    }

    /**
     * Close DB connection and statement
     *
     * @throws SQLException
     */
    public void closeConnectionAndStatement() {
        DbUtils.closeQuietly(stmt);
        DbUtils.closeQuietly(conn);
    }

    /**
     * Add <code>items<code/> to the DB table "Request"
     * <code>createConnectionAndStatement<code/> and <code>closeConnectionAndStatement<code/>should be called separately
     *
     * @param items
     */
    public void insertRequestsNoConnection(ArrayList<RequestItem> items) {
        try {
            for (RequestItem item : items) {
                String ipStr = "'" + item.getIpAddress() + "'";
                String dateTimeStr = "'" + item.getDateTimeStr() + "'";
                String sql = "INSERT INTO request (ip, time_stamp) VALUES ("
                        + ipStr + ", " + dateTimeStr + ")";
                stmt.addBatch(sql);
            }
            stmt.executeBatch();
            System.out.println(items.size() + " records added to the table 'Request'...");
        } catch (Exception e) {
            printError(e.getMessage(), true);
        }
    }

    /**
     * Calculate blocked IPs and add them to the DB table "BlockedIp"
     *
     * @param startDate
     * @param duration
     * @param threshold
     */
    public void calculateBlockedIpAddr(Date startDate, Parser.Duration duration, Integer threshold) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String startTimeStr = "'" + format.format(startDate) + "'";
        String endTimeStr = "'" + format.format(new Date(startDate.getTime() +
                Parser.Duration.getTimeMillis(duration))) + "'";
        String durationStr = "'" + duration.name() + "'";
        Statement stmt1 = null;
        try {
            createConnectionAndStatement();
            String sql = "SELECT ip, count(ip) as number FROM request WHERE " +
                    "(time_stamp>" + startTimeStr + " AND time_stamp<" + endTimeStr + ")" +
                    " GROUP BY ip HAVING number>" + threshold;
            ResultSet rs = stmt.executeQuery(sql);
            stmt1 = conn.createStatement();
            while (rs.next()) {
                String ip = rs.getString("ip");
                String ipStr = "'" + ip + "'";
                int number = rs.getInt("number");
                String reason = "'" + "From " + format.format(startDate) + " : " + number +
                        " requests from IP:" + ip + " exceeded " + duration.name() + " threshold=" + threshold + "'";
                System.out.println(reason);
                sql = "INSERT IGNORE INTO blocked_ip (ip, request_number, mesured_at, duration, threshold, reason) VALUES (" +
                        ipStr + "," + number + "," + startTimeStr + "," + durationStr + "," + threshold + "," + reason + ")";
                stmt1.executeUpdate(sql);
            }
            rs.close();
            System.out.println("Table 'BlockedIp' filled successfully...");
        } catch (Exception e) {
            printError(e.getMessage(), true);
        } finally {
            DbUtils.closeQuietly(stmt1);
            closeConnectionAndStatement();
        }
    }
}
