package org.blackdog.linkguardian.service;

import java.net.URL;
import java.net.URLConnection;

/**
 * Created by alexisparis on 16/03/16.
 */
public class LinkTarget {

    private String stringUrl;

    private URL url;

    private String contentType;

    private int responseCode = 0;

    private URLConnection connection;

    private TargetDeterminationError error;

    private Exception exception;

    public boolean isHtml()
    {
        return contentType != null && contentType.startsWith("text/html");
    }

    public String getHeaderLocation()
    {
        return connection == null ? null : connection.getHeaderField("location");
    }

    public boolean isRedirection()
    {
        return responseCode >= 300 && responseCode < 400;
    }

    public boolean isClientError()
    {
        return responseCode >= 400 && responseCode < 500;
    }

    public boolean isServerError()
    {
        return responseCode >= 500 && responseCode < 600;
    }

    public String getStringUrl() {
        return stringUrl;
    }

    public URL getUrl() {
        return url;
    }

    public String getContentType() {
        return contentType;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public URLConnection getConnection() {
        return connection;
    }

    public TargetDeterminationError getError() {
        return error;
    }

    public Exception getException() {
        return exception;
    }

    public void setStringUrl(String stringUrl) {
        this.stringUrl = stringUrl;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public void setConnection(URLConnection connection) {
        this.connection = connection;
    }

    public void setError(TargetDeterminationError error) {
        this.error = error;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    @Override
    public String toString() {
        return "LinkTarget{" +
            "stringUrl='" + stringUrl + '\'' +
            ", url=" + url +
            ", contentType='" + contentType + '\'' +
            ", responseCode=" + responseCode +
            ", connection=" + connection +
            ", error=" + error +
            ", exception=" + exception +
            '}';
    }
}
