package com.example.demo.util;

public class GlobalConstants {

    // GOOGLE AI
    public final static String API_ENDPOINT = "us-central1-speech.googleapis.com:443";
    public final static String RECOGNIZER = "projects/558041724681/locations/us-central1/recognizers/recognizerjan";
    public static final String PROJECT_ID = "recognizejan23";

    // GOOGLE CLOUD
    public static final String BUCKET_NAME = "testjan";
    public static final String CLOUD_STORAGE_PATH_PREFIX = "gs://";

    // MAILGUN
    public static final String MAILGUN_API_URL = "https://api.eu.mailgun.net/v3/recognize.am/messages";
    public static final String MAILGUN_API_KEY = "6bdd1c9ec4f28a667e40ae562b4fe310-e61ae8dd-fd5079e3";

    // APP OTHER
    public final static String FILE_TYPE_M4A = ".m4a";
    public final static String EMAIL_REGEXP = "^(?:[a-zA-Z0-9!#$%&'*+\\/=?^_`{|}~\\-]+(?:\\.[a-zA-Z0-9!#$%&'*+\\/=?^_`{|}~\\-]+)*)@" +
            "(?:(?:[a-zA-Z0-9](?:[a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,63}|" +
            "(?:\\[(?:(?:25[0-5]|2[0-4]\\d|[01]?\\d?\\d)\\.){3}(?:25[0-5]|2[0-4]\\d|[01]?\\d?\\d)\\]))$";
}
