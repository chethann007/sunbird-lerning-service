package org.sunbird.model.adminutil;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.List;

/**
 * Represents an admin utility request containing a list of admin utility data items.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AdminUtilRequest implements Serializable {

    @JsonProperty("data")
    private List<AdminUtilRequestData> data = null;
    
    private static final long serialVersionUID = 8702012703305240394L;

    /**
     * Default constructor for serialization.
     */
    public AdminUtilRequest() {
    }

    /**
     * Constructor with data parameter.
     * 
     * @param data the list of admin utility request data
     */
    public AdminUtilRequest(List<AdminUtilRequestData> data) {
        this.data = data;
    }

    /**
     * Gets the data list.
     * 
     * @return the list of admin utility request data
     */
    @JsonProperty("data")
    public List<AdminUtilRequestData> getData() {
        return data;
    }

    /**
     * Sets the data list.
     * 
     * @param data the list of admin utility request data
     */
    @JsonProperty("data")
    public void setData(List<AdminUtilRequestData> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("data", data)
                .toString();
    }
}

