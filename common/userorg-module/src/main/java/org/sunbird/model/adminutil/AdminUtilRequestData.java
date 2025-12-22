package org.sunbird.model.adminutil;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

/**
 * Represents admin utility request data containing parent ID and sub information.
 */
public class AdminUtilRequestData implements Serializable {

    @JsonProperty("parentId")
    private String parentId;
    
    @JsonProperty("sub")
    private String sub;
    
    private static final long serialVersionUID = 351766241059464964L;

    /**
     * Default constructor for serialization.
     */
    public AdminUtilRequestData() {
    }

    /**
     * Constructor with parent ID and sub parameters.
     * 
     * @param parentId the parent identifier
     * @param sub the sub identifier
     */
    public AdminUtilRequestData(String parentId, String sub) {
        this.parentId = parentId;
        this.sub = sub;
    }

    /**
     * Gets the parent ID.
     * 
     * @return the parent identifier
     */
    @JsonProperty("parentId")
    public String getParentId() {
        return parentId;
    }

    /**
     * Sets the parent ID.
     * 
     * @param parentId the parent identifier
     */
    @JsonProperty("parentId")
    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    /**
     * Gets the sub identifier.
     * 
     * @return the sub identifier
     */
    @JsonProperty("sub")
    public String getSub() {
        return sub;
    }

    /**
     * Sets the sub identifier.
     * 
     * @param sub the sub identifier
     */
    @JsonProperty("sub")
    public void setSub(String sub) {
        this.sub = sub;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("parentId", parentId)
                .append("sub", sub)
                .toString();
    }
}

