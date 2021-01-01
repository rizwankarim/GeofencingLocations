package com.example.geofencinglocations.api;

public class Api {
    private static final String ROOT_URL = "http://hysabkytab.com/locationhistory/Api.php?apicall=";
  //  private static final String ROOT_URL = "http://10.33.32.83/LocationHistory/V1/Api.php?apicall=";
    public static final String URL_CREATE_LIST = ROOT_URL + "createHistory";
    public static final String URL_READ_LIST = ROOT_URL + "getHistory&userId=";
    public static final String URL_READ_YES_LIST = ROOT_URL + "getVisitedHistory&userId=";
    public static final String URL_UPDATE_LIST = ROOT_URL + "updateHistoryStatus";
    public static final String URL_DELETE_LIST = ROOT_URL + "deleteHistory&placeId=";
}
