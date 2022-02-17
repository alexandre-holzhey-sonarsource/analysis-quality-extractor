package extractor;

import com.amazonaws.services.logs.AWSLogs;
import com.amazonaws.services.logs.model.FilterLogEventsRequest;
import com.amazonaws.services.logs.model.FilterLogEventsResult;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import model.logs.CloudWatchEntry;

public class MetricsConnector {

  private static final Logger LOGGER = Logger.getLogger(MetricsConnector.class.getName());
  private static final Gson GSON = new Gson();

  private static final String LOG_GROUP = "/SonarCloud/Autoscan/WorkerForJava/Fargate/Task/squad-1";
  private static final List<String> targetMetrics = Arrays.asList(
    "JavaAnalysisFinishedCount",
    "WorkerForJavaCloneDuration",
    "WorkerForJavaTaskDuration",
    "DownloadedArtifactsPercentage",
    "ParsedArtifactsCount",
    "ConstructDependencyGraphDuration",
    "DownloadDependenciesDuration",
    "ResolveDependenciesDuration",
    "WorkerForJavaQueueLatency"
  );

  private final AWSLogs logsClient;

  public MetricsConnector(AWSLogs logsClient) {
    this.logsClient = logsClient;
  }

  public void getLogs(String projectKey, String branch, Date analysisDate) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(analysisDate);
    calendar.add(Calendar.HOUR, +1);
    long endTime = calendar.getTimeInMillis();
    calendar.add(Calendar.HOUR, -2);
    long startTime = calendar.getTimeInMillis();

    FilterLogEventsRequest fr = new FilterLogEventsRequest()
      .withStartTime(startTime)
      .withEndTime(endTime)
      .withFilterPattern("{($.log_type = \"METRICS\") && ($.project=\"" + projectKey + "\") && ($.branch=\"" + branch + "\")}")
      .withLogGroupName(LOG_GROUP);

    FilterLogEventsResult result = logsClient.filterLogEvents(fr);

    result.getEvents().forEach(outputLogEvent -> {
      CloudWatchEntry cw = GSON.fromJson(outputLogEvent.getMessage(), CloudWatchEntry.class);
      cw.getCloudWatchMetrics().getMetrics().forEach(m -> {
        m.getMetrics().forEach(m2 -> {
          if (targetMetrics.contains(m2.getName())) {
            JsonObject json = GSON.fromJson(outputLogEvent.getMessage(), JsonObject.class);
            LOGGER.info(m2.getName() + ": " + json.get(m2.getName()).getAsString());
          }
        });
      });
    });

  }

}
