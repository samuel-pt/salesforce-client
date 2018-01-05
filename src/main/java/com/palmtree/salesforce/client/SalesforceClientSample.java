package com.palmtree.salesforce.client;

import com.palmtree.salesforce.client.model.Report;

import java.util.List;

/**
 * Sample class to test SalesforceClientPartner
 */
public class SalesforceClientSample {

  public static void main(String args[]) throws Exception {
    if (args.length == 0) {
//      args = new String[]{
//          "https://test.salesforce.com", "dhowell@paxata.com.connector",
//          "xxsw2#EDCeOWbdPamoerBoRfN0SMqsbb1s",
//          "3MVG9ZL0ppGP5UrAlCz.ZYsX5YyrThdDm.mduWwPSOVJCELTDB11lheQKvy83AHr8cbRTRZaj6NFlq9Dhdvc6",
//          "6580566496015084510"};
      args = new String[]{
          "https://login.salesforce.com", "mraj@paxata.com",
          "mathanABcd12..[][]c01t6Sn7Vwx2HP3Eno3AESMg",
          "3MVG9ZL0ppGP5UrAlCz.ZYsX5YyrThdDm.mduWwPSOVJCELTDB11lheQKvy83AHr8cbRTRZaj6NFlq9Dhdvc6",
          "6580566496015084510"};
    }
    restSample(args);
  }

  private static void restSample(String[] args) throws Exception {
    String loginUrl = args[0];
    String userId = args[1];
    String password = args[2];
    String clientId = args[3];
    String clientSecret = args[4];

    SalesforceClient client = new SalesforceClient(loginUrl, userId, password, clientId, clientSecret);

    client.login();

    List<Report> reports = client.getTabularReports();
    System.out.println("Total no.of reports " + reports.size());
    System.out.println("Report Details");
    for (Report report : reports) {
      System.out.println("Report Id : " + report.getId());
      System.out.println("Report Name : " + report.getName());
//      System.out.println("Run Report Async : " + client.runASyncReport(report.getId()));
//      System.exit(0);
//      System.out.println(report.getDescribeUrl());
//      ReportData reportData = client.runSyncReport(report.getId());
//      reportData.getFields();
//      System.out.println("Report data : " + reportData);
    }

    client.logout();

  }

}
