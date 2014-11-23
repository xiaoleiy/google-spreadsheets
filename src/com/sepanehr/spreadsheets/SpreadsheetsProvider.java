package com.sepanehr.spreadsheets;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.gdata.client.ClientLoginAccountType;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.*;
import org.ini4j.Ini;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * The content provider for the Servlet to access Google Sheet objects (spreadsheet, worksheet, row, col...)
 * <p/>
 * Created by Xiaolei Yu on 11/15/2014.
 */
public class SpreadsheetsProvider {

    /**
     * The google sheet which holds the employee list
     */
    private SpreadsheetEntry employeeListSheet;

    /**
     * The google sheet which holds the employees' clicking records
     */
    private SpreadsheetEntry clockingTimesheet;

    /**
     * The google sheets service for further sheets operation
     */
    private SpreadsheetService googleSheetService;

    private static class SINGLETON {
        private static final SpreadsheetsProvider INNER = new SpreadsheetsProvider();
    }

    /**
     * To get the singleton instance of SpreadsheetsProvider
     *
     * @return The singleton instance of SpreadsheetsProvider
     */
    public static SpreadsheetsProvider getInstance() {
        return SINGLETON.INNER;
    }

    /**
     * Get all the employee list from Google sheets
     *
     * @return all the employee list
     */
    public List<String> getEmployeeList() {
        List<String> employeeList = new ArrayList<String>();
        try {
            WorksheetEntry defaultWorksheet = employeeListSheet.getDefaultWorksheet();
            URL cellFeedUrl = new URI(defaultWorksheet.getCellFeedUrl().toString()
                    + "?min-row=2&min-col=1&max-col=1").toURL();
            CellFeed cellFeed = googleSheetService.getFeed(cellFeedUrl, CellFeed.class);

            // add the cell value to employee list
            for (CellEntry cell : cellFeed.getEntries()) {
                employeeList.add(cell.getCell().getValue().trim());
            }
        } catch (Exception e) {
            System.out.println("[ERROR] Exception in security/IO/inner service happened " +
                    "during Google Sheets data retrieval.");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        return employeeList;
    }

    /**
     * Insert a new row at Google sheet "timesheet", when the employee clicking in
     *
     * @param employeeName employee name
     * @param date         the date (format: 2014-01-01) when the employee clicking in
     * @param time         the time (format: 12:00:10) when the employee clicking in
     * @return Whether the clicking in is success
     */
    public boolean clickIn(String employeeName, String date, String time) {
        try {
            // Fetch the list feed of the worksheet.
            URL listFeedUrl = clockingTimesheet.getDefaultWorksheet().getListFeedUrl();

            // Create a local representation of the new row.
            ListEntry row = new ListEntry();
            row.getCustomElements().setValueLocal(Constant.SHEET_COL_DATE, date);
            row.getCustomElements().setValueLocal(Constant.SHEET_COL_EMPLOYEE_NAME, employeeName);
            row.getCustomElements().setValueLocal(Constant.SHEET_COL_TIME_CLICK_IN, time);

            // Send the new row to the API for insertion.
            this.googleSheetService.insert(listFeedUrl, row);
        } catch (Exception e) {
            System.out.println("[ERROR] Exception in security/inner service happened " +
                    "during inserting new row into Google Sheet.");
            System.out.println(e.getMessage());
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * Insert a new row at Google sheet "timesheet", when the employee clicking out
     *
     * @param employeeName employee name
     * @param date         the date (format: 2014-01-01) when the employee clicking out
     * @param time         the time (format: 12:00:10) when the employee clicking out
     * @return Whether the clicking out is success
     */
    public boolean clickOut(String employeeName, String date, String time) {
        try {
            // Fetch the list feed of the worksheet.
            WorksheetEntry defaultWorksheet = clockingTimesheet.getDefaultWorksheet();
            URL listFeedUrl = defaultWorksheet.getListFeedUrl();

            // Create a local representation of the new row.
            ListEntry row = new ListEntry();
            row.getCustomElements().setValueLocal(Constant.SHEET_COL_DATE, date);
            row.getCustomElements().setValueLocal(Constant.SHEET_COL_EMPLOYEE_NAME, employeeName);
            row.getCustomElements().setValueLocal(Constant.SHEET_COL_TIME_CLICK_OUT, time);

            // Send the new row to the API for insertion.
            this.googleSheetService.insert(listFeedUrl, row);
        } catch (Exception e) {
            System.out.println("[ERROR] Exception in security/inner service happened " +
                    "during inserting new row into Google Sheet.");
            System.out.println(e.getMessage());
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * Authenticate our client application against the Google Sheets googleSheetService.
     *
     * @return whether the authentication is successfully initialized
     */
    public boolean initAuthIfRequired(String contextPath) {

        // Directly return true if the sheet objects already initialized.
        if (this.employeeListSheet != null && this.clockingTimesheet != null) {
            return true;
        }

//        System.setProperty("https.proxyHost", "127.0.0.1");
//        System.setProperty("https.proxyPort", "8580");
        List<SpreadsheetEntry> spreadsheets = null;

        try {
            String credentialIniFile = contextPath + File.separator + "res" + File.separator + Constant.INI_CREDENTIAL_FILE;
            Ini ini = new Ini(new File(credentialIniFile));
            Constant.init(ini);

            // construct the authentication credentials
            HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            String privateKeyPath = contextPath + File.separator + "res"
                    + File.separator + Constant.FILENAME_PRIVATEKEY_P12;
            GoogleCredential credential = new GoogleCredential.Builder()
                    .setTransport(httpTransport)
                    .setJsonFactory(Constant.JSON_FACTORY)
                    .setServiceAccountId(Constant.SERVICE_ACCOUNT_ID)
                    .setServiceAccountScopes(Constant.SCOPE_SPREADSHEETS)
                    .setServiceAccountPrivateKeyFromP12File(new File(privateKeyPath))
                    .build();
            googleSheetService = new SpreadsheetService(Constant.CLIENT_APP_NAME);
            googleSheetService.setOAuth2Credentials(credential);
            googleSheetService.setProtocolVersion(SpreadsheetService.Versions.V3);

            // Define the URL to request.  This should never be changed.
            URL SPREADSHEET_FEED_URL = new URL(Constant.URL_SPREADSHEETS_PRIVATE_FULL);

            // Make a request to the API and get all spreadsheets.
            googleSheetService.setConnectTimeout(36000000);
            googleSheetService.setUserCredentials(Constant.GOOGLE_ACCOUNT, Constant.GOOGLE_PASSWORD, ClientLoginAccountType.GOOGLE);
            SpreadsheetFeed feed = googleSheetService.getFeed(SPREADSHEET_FEED_URL, SpreadsheetFeed.class);
            spreadsheets = feed.getEntries();
            if (spreadsheets == null || spreadsheets.isEmpty()) {
                System.out.println("There is no spreadsheets fully belongs to the account: ");
            }
        } catch (Exception e) {
            System.out.println("[ERROR] Exception in security/IO/inner service happened during " +
                    "Google Sheets service authentication.");
            System.out.println(e.getMessage());
            e.printStackTrace();
            return false;
        }

        // init the spreadsheet objects after authentication succeed
        for (SpreadsheetEntry spreadsheet : spreadsheets) {
            String spreadsheetTitle = spreadsheet.getTitle().getPlainText();
            if (Constant.SHEET_TITLE_EMPLOYEE_LIST.equalsIgnoreCase(spreadsheetTitle)) {
                employeeListSheet = spreadsheet;
            } else if (Constant.SHEET_TITLE_TIMESHEET.equalsIgnoreCase(spreadsheetTitle)) {
                clockingTimesheet = spreadsheet;
            }
        }

        return true;
    }
}
