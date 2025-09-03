package com.enterprise.arch.agent.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import java.util.Set;

/**
 * Request model for repository architecture analysis.
 * 
 * This class represents a request to analyze a GitLab repository
 * for architectural patterns and generate corresponding diagrams.
 * 
 * @author Enterprise Architecture Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class AnalysisRequest {

    @JsonProperty("projectId")
    @NotBlank(message = "Project ID is required")
    private String projectId;

    @JsonProperty("repositoryUrl")
    private String repositoryUrl;

    @JsonProperty("branch")
    private String branch = "main";

    @JsonProperty("commitSha")
    private String commitSha;

    @JsonProperty("analysisType")
    private AnalysisType analysisType = AnalysisType.FULL;

    @JsonProperty("includedPaths")
    private List<String> includedPaths;

    @JsonProperty("excludedPaths")
    private List<String> excludedPaths;

    @JsonProperty("languages")
    private Set<String> languages;

    @JsonProperty("diagramTypes")
    private Set<DiagramType> diagramTypes;

    @JsonProperty("options")
    private AnalysisOptions options;

    // Constructors
    public AnalysisRequest() {}

    public AnalysisRequest(String projectId) {
        this.projectId = projectId;
    }

    // Getters and setters
    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getRepositoryUrl() {
        return repositoryUrl;
    }

    public void setRepositoryUrl(String repositoryUrl) {
        this.repositoryUrl = repositoryUrl;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getCommitSha() {
        return commitSha;
    }

    public void setCommitSha(String commitSha) {
        this.commitSha = commitSha;
    }

    public AnalysisType getAnalysisType() {
        return analysisType;
    }

    public void setAnalysisType(AnalysisType analysisType) {
        this.analysisType = analysisType;
    }

    public List<String> getIncludedPaths() {
        return includedPaths;
    }

    public void setIncludedPaths(List<String> includedPaths) {
        this.includedPaths = includedPaths;
    }

    public List<String> getExcludedPaths() {
        return excludedPaths;
    }

    public void setExcludedPaths(List<String> excludedPaths) {
        this.excludedPaths = excludedPaths;
    }

    public Set<String> getLanguages() {
        return languages;
    }

    public void setLanguages(Set<String> languages) {
        this.languages = languages;
    }

    public Set<DiagramType> getDiagramTypes() {
        return diagramTypes;
    }

    public void setDiagramTypes(Set<DiagramType> diagramTypes) {
        this.diagramTypes = diagramTypes;
    }

    public AnalysisOptions getOptions() {
        return options;
    }

    public void setOptions(AnalysisOptions options) {
        this.options = options;
    }

    @Override
    public String toString() {
        return "AnalysisRequest{" +
                "projectId='" + projectId + '\'' +
                ", repositoryUrl='" + repositoryUrl + '\'' +
                ", branch='" + branch + '\'' +
                ", commitSha='" + commitSha + '\'' +
                ", analysisType=" + analysisType +
                ", diagramTypes=" + diagramTypes +
                '}';
    }

    /**
     * Analysis type enumeration
     */
    public enum AnalysisType {
        FULL,           // Complete analysis of the entire repository
        INCREMENTAL,    // Analysis of only changed files
        QUICK,          // Fast analysis with limited depth
        CUSTOM          // Custom analysis based on specific parameters
    }

    /**
     * Analysis options for fine-tuning the analysis process
     */
    public static class AnalysisOptions {
        @JsonProperty("maxDepth")
        private Integer maxDepth = 10;

        @JsonProperty("includeTests")
        private Boolean includeTests = false;

        @JsonProperty("includeDependencies")
        private Boolean includeDependencies = true;

        @JsonProperty("detectPatterns")
        private Boolean detectPatterns = true;

        @JsonProperty("generateMetrics")
        private Boolean generateMetrics = true;

        @JsonProperty("aiEnhanced")
        private Boolean aiEnhanced = true;

        @JsonProperty("timeout")
        private Integer timeout = 300; // seconds

        // Getters and setters
        public Integer getMaxDepth() { return maxDepth; }
        public void setMaxDepth(Integer maxDepth) { this.maxDepth = maxDepth; }

        public Boolean getIncludeTests() { return includeTests; }
        public void setIncludeTests(Boolean includeTests) { this.includeTests = includeTests; }

        public Boolean getIncludeDependencies() { return includeDependencies; }
        public void setIncludeDependencies(Boolean includeDependencies) { this.includeDependencies = includeDependencies; }

        public Boolean getDetectPatterns() { return detectPatterns; }
        public void setDetectPatterns(Boolean detectPatterns) { this.detectPatterns = detectPatterns; }

        public Boolean getGenerateMetrics() { return generateMetrics; }
        public void setGenerateMetrics(Boolean generateMetrics) { this.generateMetrics = generateMetrics; }

        public Boolean getAiEnhanced() { return aiEnhanced; }
        public void setAiEnhanced(Boolean aiEnhanced) { this.aiEnhanced = aiEnhanced; }

        public Integer getTimeout() { return timeout; }
        public void setTimeout(Integer timeout) { this.timeout = timeout; }
    }
}