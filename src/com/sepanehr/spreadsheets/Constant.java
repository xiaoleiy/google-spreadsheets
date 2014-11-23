package com.sepanehr.spreadsheets;

import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.ini4j.Ini;

import java.util.Arrays;
import java.util.List;

/**
 * The constant fields used by authentication and spreadsheets access
 *
 * Created by idealab on 11/15/2014.
 */
public class Constant {

    /**
     * The INI filename for credentials information
     */
    public static final String INI_CREDENTIAL_FILE = "credentials.ini";

    /**
     * The Google acount to access it's spreadsheets.
     * <p/>
     * <b>NOTE</b>:
     * You MUST enable the "Less secure apps" firstly at: https://www.google.com/settings/security/lesssecureapps
     */
    public static String GOOGLE_ACCOUNT;

    /**
     * The password for the provided Google account.
     */
    public static String GOOGLE_PASSWORD;

    /**
     * The field "EMAIL ADDRESS" of <b>service account</b> settings specified
     * in dedicated project in Google Developers Console.
     * <p/>
     * Refer to: https://console.developers.google.com/project
     */
    public static String SERVICE_ACCOUNT_ID;

    /**
     * The URL to access our spreadsheets data with visibility of private and projection of full
     */
    public static final String URL_SPREADSHEETS_PRIVATE_FULL
            = "https://spreadsheets.google.com/feeds/spreadsheets/private/full";


    /**
     * The PKCS #12-formatted private key downloaded from the <b>service account</b> settings
     * by clicking the button "Generate new P12 key".
     * <p/>
     * Refer to: https://developers.google.com/console/help/new/#serviceaccounts
     */
    public static String FILENAME_PRIVATEKEY_P12;

    /**
     * The client application name, which accesss the Google Spreadsheets service.
     * <p/>
     * Note: this can be given by yourself manually.
     */
    public static final String CLIENT_APP_NAME = "Sepane HR Tempalte";

    /**
     * The instance of json factory
     */
    public static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    /**
     * The scopes for accessing Google Sheets services by using oAuth2.
     */
    public static final List<String> SCOPE_SPREADSHEETS = Arrays.asList(
            "https://spreadsheets.google.com/feeds",
            "https://docs.google.com/feeds",
            "https://www.googleapis.com/auth/drive",
            "https://www.googleapis.com/auth/drive.file"
    );

    // title of the google sheet from which we retrieve employee list
    public static String SHEET_TITLE_EMPLOYEE_LIST;
    // title and columns of the google sheet to which we push
    // the clocking in/out records
    public static String SHEET_TITLE_TIMESHEET;
    public static String SHEET_COL_EMPLOYEE_NAME;
    public static String SHEET_COL_DATE;
    public static String SHEET_COL_TIME_CLICK_IN;
    public static String SHEET_COL_TIME_CLICK_OUT;

    public static void init(Ini ini) {
        GOOGLE_ACCOUNT = ini.get("google_account", "username").trim();
        GOOGLE_PASSWORD = ini.get("google_account", "password").trim();
        SERVICE_ACCOUNT_ID = ini.get("api_service", "service_account_id").trim();
        FILENAME_PRIVATEKEY_P12 = ini.get("api_service", "private_key_filename").trim();
        SHEET_TITLE_EMPLOYEE_LIST = ini.get("spreadsheet_title", "employee_list").trim();
        SHEET_TITLE_TIMESHEET = ini.get("spreadsheet_title", "time_keeping").trim();
        SHEET_COL_EMPLOYEE_NAME = ini.get("spreadsheet_column", "col_employee").trim();
        SHEET_COL_DATE = ini.get("spreadsheet_column", "col_date").trim();
        SHEET_COL_TIME_CLICK_IN = ini.get("spreadsheet_column", "col_timein").trim();
        SHEET_COL_TIME_CLICK_OUT = ini.get("spreadsheet_column", "col_timeout").trim();
    }
}
