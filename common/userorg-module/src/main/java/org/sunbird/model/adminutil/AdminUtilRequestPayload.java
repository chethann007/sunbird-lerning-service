package org.sunbird.model.adminutil;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

/**
 * Represents the complete admin utility request payload with metadata and request data.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AdminUtilRequestPayload implements Serializable {
    
    private static final long serialVersionUID = -2362783406031347676L;

    @JsonProperty
    private String id;

    @JsonProperty
    private String ver;

    @JsonProperty
    private long ts;

    @JsonProperty
    private Params params;

    @JsonProperty
    private AdminUtilRequest request;

    /**
     * Default constructor for serialization.
     */
    public AdminUtilRequestPayload() {
    }

    /**
     * Constructor with all parameters.
     * 
     * @param id the request identifier
     * @param ver the version
     * @param ts the timestamp
     * @param params the request parameters
     * @param request the admin utility request
     */
    public AdminUtilRequestPayload(String id, String ver, long ts, Params params, AdminUtilRequest request) {
        this.id = id;
        this.ver = ver;
        this.ts = ts;
        this.params = params;
        this.request = request;
    }

    /**
     * Gets the request ID.
     * 
     * @return the request identifier
     */
    @JsonProperty("id")
    public String getId() {
        return id;
    }

    /**
     * Sets the request ID.
     * 
     * @param id the request identifier
     */
    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the version.
     * 
     * @return the version
     */
    @JsonProperty("ver")
    public String getVer() {
        return ver;
    }

    /**
     * Sets the version.
     * 
     * @param ver the version
     */
    @JsonProperty("ver")
    public void setVer(String ver) {
        this.ver = ver;
    }

    /**
     * Gets the timestamp.
     * 
     * @return the timestamp
     */
    @JsonProperty("ts")
    public long getTs() {
        return ts;
    }

    /**
     * Sets the timestamp.
     * 
     * @param ts the timestamp
     */
    @JsonProperty("ts")
    public void setTs(long ts) {
        this.ts = ts;
    }

    /**
     * Gets the request parameters.
     * 
     * @return the request parameters
     */
    @JsonProperty("params")
    public Params getParams() {
        return params;
    }

    /**
     * Sets the request parameters.
     * 
     * @param params the request parameters
     */
    @JsonProperty("params")
    public void setParams(Params params) {
        this.params = params;
    }

    /**
     * Gets the admin utility request.
     * 
     * @return the admin utility request
     */
    @JsonProperty("request")
    public AdminUtilRequest getRequest() {
        return request;
    }

    /**
     * Sets the admin utility request.
     * 
     * @param request the admin utility request
     */
    @JsonProperty("request")
    public void setRequest(AdminUtilRequest request) {
        this.request = request;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("ver", ver)
                .append("ts", ts)
                .append("params", params)
                .append("request", request)
                .toString();
    }
}

