/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *--------------------------------------------------------------------------------------------*/

package com.github.copilot.sdk.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response to an exit-plan-mode request.
 *
 * @since 1.0.7
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExitPlanModeResult {

    @JsonProperty("approved")
    private boolean approved = true;

    @JsonProperty("selectedAction")
    private String selectedAction;

    @JsonProperty("feedback")
    private String feedback;

    /**
     * Gets whether the user approved exiting plan mode. @return true if approved
     */
    public boolean isApproved() {
        return approved;
    }

    /** Sets whether approved. @param approved the flag @return this */
    public ExitPlanModeResult setApproved(boolean approved) {
        this.approved = approved;
        return this;
    }

    /**
     * Gets the selected action, if the user chose one. @return the selected action
     */
    public String getSelectedAction() {
        return selectedAction;
    }

    /**
     * Sets the selected action. @param selectedAction the selected action @return
     * this
     */
    public ExitPlanModeResult setSelectedAction(String selectedAction) {
        this.selectedAction = selectedAction;
        return this;
    }

    /** Gets optional feedback provided by the user. @return the feedback */
    public String getFeedback() {
        return feedback;
    }

    /** Sets the feedback. @param feedback the feedback @return this */
    public ExitPlanModeResult setFeedback(String feedback) {
        this.feedback = feedback;
        return this;
    }
}
