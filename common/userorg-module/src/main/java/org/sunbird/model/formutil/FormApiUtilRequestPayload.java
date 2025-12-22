package org.sunbird.model.formutil;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.sunbird.model.adminutil.Params;

import java.io.Serializable;

/**
 * Represents the complete form API utility request payload with metadata and request data.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FormApiUtilRequestPayload implements Serializable {
    
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
    private FormUtilRequest request;

    /**
     * Default constructor for serialization.
     */
    public FormApiUtilRequestPayload() {
    }

    /**
     * Constructor with all parameters.
     * 
     * @param id the request identifier
     * @param ver the version
     * @param ts the timestamp
     * @param params the request parameters
     * @param request the form utility request
     */
    public FormApiUtilRequestPayload(String id, String ver, long ts, Params params, FormUtilRequest request) {
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
     * Gets the form utility request.
     * 
     * @return the form utility request
     */
    @JsonProperty("request")
    public FormUtilRequest getRequest() {
        return request;
    }

    /**
     * Sets the form utility request.
     * 
     * @param request the form utility request
     */
    @JsonProperty("request")
    public void setRequest(FormUtilRequest request) {
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

