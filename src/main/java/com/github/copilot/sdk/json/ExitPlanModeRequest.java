/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *--------------------------------------------------------------------------------------------*/

package com.github.copilot.sdk.json;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request to exit plan mode and continue with a selected action.
 * <p>
 * This is sent when the agent requests to exit plan mode, providing a summary
 * of the plan and available actions for the user to choose from.
 *
 * @since 1.4.0
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

    /**
     * Gets the summary of the plan or proposed next step.
     *
     * @return the summary text
     */
    public String getSummary() {
        return summary;
    }

    /**
     * Sets the summary of the plan or proposed next step.
     *
     * @param summary
     *            the summary text
     * @return this instance for method chaining
     */
    public ExitPlanModeRequest setSummary(String summary) {
        this.summary = summary;
        return this;
    }

    /**
     * Gets the full plan content, when available.
     *
     * @return the plan content, or {@code null} if not provided
     */
    public String getPlanContent() {
        return planContent;
    }

    /**
     * Sets the full plan content.
     *
     * @param planContent
     *            the plan content
     * @return this instance for method chaining
     */
    public ExitPlanModeRequest setPlanContent(String planContent) {
        this.planContent = planContent;
        return this;
    }

    /**
     * Gets the available actions the user can select.
     *
     * @return the list of actions, or an empty list if none
     */
    public List<String> getActions() {
        return actions == null ? Collections.emptyList() : Collections.unmodifiableList(actions);
    }

    /**
     * Sets the available actions.
     *
     * @param actions
     *            the list of actions
     * @return this instance for method chaining
     */
    public ExitPlanModeRequest setActions(List<String> actions) {
        this.actions = actions;
        return this;
    }

    /**
     * Gets the action recommended by the runtime.
     *
     * @return the recommended action, defaults to {@code "autopilot"}
     */
    public String getRecommendedAction() {
        return recommendedAction;
    }

    /**
     * Sets the recommended action.
     *
     * @param recommendedAction
     *            the recommended action
     * @return this instance for method chaining
     */
    public ExitPlanModeRequest setRecommendedAction(String recommendedAction) {
        this.recommendedAction = recommendedAction;
        return this;
    }
}
