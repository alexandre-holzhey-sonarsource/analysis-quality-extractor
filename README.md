# analysis-quality-extractor

## Send to peach branch

This is an alternative branch that will collect issues from Peach instead of SonarCloud.

The process consist of two steps:
- AnalysisResultWrite will collect all issues from all projects, and serialize them into files (under output_issues folder).
- AnalysisResultFromFile will read the previously collected files and compute some metrics on them. The output will be under "output" and "output_commons".

You will need an environment variable named "PEACH_TOKEN" with a peach token.

