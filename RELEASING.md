# How to release

1. Run the **Prepare and push release** workflow (`publish.yml`) from `main` with the release version (e.g. `0.5.10`) and the next development version (e.g. `0.5.11-SNAPSHOT`).
   It commits the version bump, pushes an annotated `vX.Y.Z` tag and triggers **Java CI with Gradle** (`gradle.yml`) for that tag.
2. The `deploy` job in `gradle.yml` deploys to Maven Central and then creates the GitHub release.

## Recovering a partially failed release

If the Maven Central deploy succeeded but creating the GitHub release failed, don't re-run the job (Maven Central rejects re-deploys).
Instead, run **Java CI with Gradle** manually against the tag with `skipDeploy` enabled:

```
gh workflow run gradle.yml --ref vX.Y.Z -f skipDeploy=true
```
