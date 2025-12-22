package org.sunbird.model.adminutil;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

/**
 * Represents request parameters containing device ID, key, and message ID.
 */
public class Params implements Serializable {
    
    private static final long serialVersionUID = -8580469966189743283L;

    @JsonProperty("did")
    private String did;

    @JsonProperty("key")
    private String key;

    @JsonProperty("msgid")
    private String msgid;

    /**
     * Default constructor for serialization.
     */
    public Params() {
    }

    /**
     * Constructor with all parameters.
     * 
     * @param did the device identifier
     * @param key the key parameter
     * @param msgid the message identifier
     */
    public Params(String did, String key, String msgid) {
        this.did = did;
        this.key = key;
        this.msgid = msgid;
    }

    /**
     * Gets the device identifier.
     * 
     * @return the device identifier
     */
    @JsonProperty("did")
    public String getDid() {
        return did;
    }

    /**
     * Sets the device identifier.
     * 
     * @param did the device identifier
     */
    @JsonProperty("did")
    public void setDid(String did) {
        this.did = did;
    }

    /**
     * Gets the key parameter.
     * 
     * @return the key parameter
     */
    @JsonProperty("key")
    public String getKey() {
        return key;
    }

    /**
     * Sets the key parameter.
     * 
     * @param key the key parameter
     */
    @JsonProperty("key")
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Gets the message identifier.
     * 
     * @return the message identifier
     */
    @JsonProperty("msgid")
    public String getMsgid() {
        return msgid;
    }

    /**
     * Sets the message identifier.
     * 
     * @param msgid the message identifier
     */
    @JsonProperty("msgid")
    public void setMsgid(String msgid) {
        this.msgid = msgid;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("did", did)
                .append("key", key)
                .append("msgid", msgid)
                .toString();
    }
}

