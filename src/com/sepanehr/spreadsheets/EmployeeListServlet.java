package com.sepanehr.spreadsheets;

import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * The Servlet to provide all the required employee list from Google Sheets
 * <p/>
 * Created by Xiaolei Yu on 11/12/2014.
 */
public class EmployeeListServlet extends HttpServlet {

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        SpreadsheetsProvider serviceProvider = SpreadsheetsProvider.getInstance();
        String webRoot = this.getServletContext().getRealPath("/");
        boolean isInitialized = serviceProvider.initAuthIfRequired(webRoot);
        List<String> employeeList = new ArrayList<String>();
        if (isInitialized) {
            employeeList = serviceProvider.getEmployeeList();
        }

        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        writer.print(new Gson().toJson(employeeList));
        writer.flush();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        this.doGet(request, response);
    }
}
