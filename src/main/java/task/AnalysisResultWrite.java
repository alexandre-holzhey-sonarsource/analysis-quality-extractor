package task;

import com.google.gson.Gson;
import extractor.ApiConnector;
import extractor.ProjectAnalysis;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import model.Component;
import model.ProjectAnalysisQuality;

import static task.AnalysisResultFromFile.OUTPUT_FOLDER_BASE;
import static task.AnalysisResultFromFile.OUTPUT_FOLDER_TARGET;

public class AnalysisResultWrite {
  private static final String PEACH_URL = "https://peach.sonarsource.com";

  private static final List<String> projects = List.of(
    "pharmacy"
  );

  public static void main(String[] args) throws IOException {
    ApiConnector apiConnector = new ApiConnector(PEACH_URL);
    ProjectAnalysis projectAnalysis = new ProjectAnalysis(apiConnector, null);

    boolean base = true; // Base = CI, Target = AutoScan

    String outputFolder;
    if (base) {
      outputFolder = OUTPUT_FOLDER_BASE;
    } else {
      outputFolder = OUTPUT_FOLDER_TARGET;
    }

    List<Component> baseComponents = apiConnector.getProjects(projects);

    Gson gson = new Gson();
    Files.createDirectories(Paths.get(outputFolder));

    ExecutorService executor = Executors.newFixedThreadPool(10);
    for (Component baseComponent : baseComponents) {
      executor.submit(() -> {
        try {
          ProjectAnalysisQuality paq = projectAnalysis.toAnalysisQuality(baseComponent);
          ProjectAnalysisQuality paqExtracted = projectAnalysis.extractResult(paq);
          try (FileWriter fileWriter = new FileWriter(outputFolder + paqExtracted.getBaseComponent().getKey().replaceAll(":", "_"))) {
            gson.toJson(paqExtracted, fileWriter);
          }
        } catch (Exception e) {
          System.err.println("[ERROR] Fail to download " + baseComponent + ": " + e.getMessage());
        }
      });

    }
    executor.shutdown();
  }
}
