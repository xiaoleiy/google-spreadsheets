package com.sepanehr.spreadsheets;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * The Servlet to update/add timesheet entry to Google Sheet as the employee clicking in/out.
 *
 * Created by Xiaolei Yu on 11/15/2014.
 */
public class TimesheetEntryServlet extends HttpServlet {

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        SpreadsheetsProvider serviceProvider = SpreadsheetsProvider.getInstance();
        String webRoot = this.getServletContext().getRealPath("/");
        boolean isInitialized = serviceProvider.initAuthIfRequired(webRoot),
                isClockSuccessful = false;
        if (!isInitialized) {
            PrintWriter writer = response.getWriter();
            writer.print(isClockSuccessful);
            writer.flush();
            return;
        }

        String employeeName = request.getParameter(Constant.SHEET_COL_EMPLOYEE_NAME);
        String date = request.getParameter(Constant.SHEET_COL_DATE);
        boolean isClockIn = Boolean.parseBoolean(request.getParameter("isClockIn"));

        SpreadsheetsProvider instance = SpreadsheetsProvider.getInstance();
        if (isClockIn) {
            String timeClickIn = request.getParameter(Constant.SHEET_COL_TIME_CLICK_IN);
            isClockSuccessful = instance.clickIn(employeeName, date, timeClickIn);
        } else {
            String timeClickOut = request.getParameter(Constant.SHEET_COL_TIME_CLICK_OUT);
            isClockSuccessful = instance.clickOut(employeeName, date, timeClickOut);
        }

        PrintWriter writer = response.getWriter();
        writer.print(isClockSuccessful);
        writer.flush();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        this.doPost(request, response);
    }
}
