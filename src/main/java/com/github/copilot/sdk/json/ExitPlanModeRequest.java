/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *--------------------------------------------------------------------------------------------*/

package com.github.copilot.sdk.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request to exit plan mode and continue with a selected action.
 *
 * @since 1.0.7
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExitPlanModeRequest {

    @JsonProperty("summary")
    private String summary = "";

    @JsonProperty("planContent")
    private String planContent;

    @JsonProperty("actions")
    private List<String> actions;

    @JsonProperty("recommendedAction")
    private String recommendedAction = "autopilot";

    /** Gets the summary of the plan or proposed next step. @return the summary */
    public String getSummary() {
        return summary;
    }

    /** Sets the summary. @param summary the summary @return this */
    public ExitPlanModeRequest setSummary(String summary) {
        this.summary = summary;
        return this;
    }

    /** Gets the full plan content, when available. @return the plan content */
    public String getPlanContent() {
        return planContent;
    }

    /** Sets the plan content. @param planContent the plan content @return this */
    public ExitPlanModeRequest setPlanContent(String planContent) {
        this.planContent = planContent;
        return this;
    }

    /** Gets the available actions the user can select. @return the actions */
    public List<String> getActions() {
        return actions;
    }

    /** Sets the actions. @param actions the actions @return this */
    public ExitPlanModeRequest setActions(List<String> actions) {
        this.actions = actions;
        return this;
    }

    /**
     * Gets the action recommended by the runtime. @return the recommended action
     */
    public String getRecommendedAction() {
        return recommendedAction;
    }

    /**
     * Sets the recommended action. @param recommendedAction the recommended
     * action @return this
     */
    public ExitPlanModeRequest setRecommendedAction(String recommendedAction) {
        this.recommendedAction = recommendedAction;
        return this;
    }
}
