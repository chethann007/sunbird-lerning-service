package org.sunbird.model.formutil;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

/**
 * Represents a form utility request with type, subtype, action, and component information.
 */
public class FormUtilRequest implements Serializable {

    private static final long serialVersionUID = 351766241059464964L;

    @JsonProperty("type")
    private String type;

    @JsonProperty("subType")
    private String subType;

    @JsonProperty("action")
    private String action;

    @JsonProperty("component")
    private String component;

    /**
     * Default constructor for serialization.
     */
    public FormUtilRequest() {
    }

    /**
     * Gets the type.
     * 
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type.
     * 
     * @param type the type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Gets the subtype.
     * 
     * @return the subtype
     */
    public String getSubType() {
        return subType;
    }

    /**
     * Sets the subtype.
     * 
     * @param subType the subtype
     */
    public void setSubType(String subType) {
        this.subType = subType;
    }

    /**
     * Gets the action.
     * 
     * @return the action
     */
    public String getAction() {
        return action;
    }

    /**
     * Sets the action.
     * 
     * @param action the action
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     * Gets the component.
     * 
     * @return the component
     */
    public String getComponent() {
        return component;
    }

    /**
     * Sets the component.
     * 
     * @param component the component
     */
    public void setComponent(String component) {
        this.component = component;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("type", type)
                .append("subType", subType)
                .append("action", action)
                .append("component", component)
                .toString();
    }
}

