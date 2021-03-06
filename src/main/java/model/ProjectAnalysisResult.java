package model;

import java.util.List;
import java.util.Map;

public class ProjectAnalysisResult {

  private List<Issue> issues;
  private String serverVersion;
  private PluginsInstalled pluginsInstalled;
  private List<QualityProfile> qualityProfiles;
  private Map<String, Integer> locPerLanguages;
  // Store components (files) present in the analysis
  private List<Component> components;

  public List<Issue> getIssues() {
    return issues;
  }

  public ProjectAnalysisResult setIssues(List<Issue> issues) {
    this.issues = issues;
    return this;
  }

  public String getServerVersion() {
    return serverVersion;
  }

  public ProjectAnalysisResult setServerVersion(String serverVersion) {
    this.serverVersion = serverVersion;
    return this;
  }

  public PluginsInstalled getPluginsInstalled() {
    return pluginsInstalled;
  }

  public ProjectAnalysisResult setPluginsInstalled(PluginsInstalled pluginsInstalled) {
    this.pluginsInstalled = pluginsInstalled;
    return this;
  }

  public List<QualityProfile> getQualityProfiles() {
    return qualityProfiles;
  }

  public ProjectAnalysisResult setQualityProfiles(List<QualityProfile> qualityProfiles) {
    this.qualityProfiles = qualityProfiles;
    return this;
  }

  public Map<String, Integer> getLocPerLanguages() {
    return locPerLanguages;
  }

  public void setLocPerLanguages(Map<String, Integer> locPerLanguages) {
    this.locPerLanguages = locPerLanguages;
  }

  public List<Component> getComponents() {
    return components;
  }

  public void setComponents(List<Component> components) {
    this.components = components;
  }
}
