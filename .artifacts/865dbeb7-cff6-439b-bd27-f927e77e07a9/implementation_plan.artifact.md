# Fix Unresolved Reference 'serialization'

The project fails to sync because the Kotlin serialization plugin is used in `build.gradle.kts` but not defined in the version catalog (`libs.versions.toml`). Additionally, there's an incorrect dependency usage of the plugin in the `dependencies` block.

## Proposed Changes

### Gradle Configuration

#### [MODIFY] [libs.versions.toml](file:///C:/Users/achuk/AndroidStudioProjects/FGCompanion/gradle/libs.versions.toml)
- Add `kotlin-serialization` to the `[plugins]` section.
- Add `kotlinx-serialization-json` to the `[libraries]` section and a corresponding version `kotlinxSerializationJson`.

#### [MODIFY] [build.gradle.kts](file:///C:/Users/achuk/AndroidStudioProjects/FGCompanion/app/build.gradle.kts)
- Update the `dependencies` block to use the serialization library instead of trying to use the plugin as a library.

## Verification Plan

### Automated Tests
- Run Gradle Sync to ensure the unresolved reference is fixed.
- Run `./gradlew assembleDebug` to verify the build.
