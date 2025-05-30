---
subtitle: Comparison error codes
---

These error codes may be returned when running commands which use [Redgate comparison technology](https://documentation.red-gate.com/flyway/flyway-concepts/database-comparisons).
These include:
- [`check changes`](<Commands/Check/Check Changes>)
- [`check drift`](<Commands/Check/Check Drift>)
- [`diff`](<Commands/Diff>)
- [`diffText`](<Commands/DiffText>)
- [`generate`](<Commands/Generate>)
- [`model`](<Commands/Model>)
- [`prepare`](<Commands/Prepare>)
- [`snapshot`](<Commands/Snapshot>)

### `COMPARISON_ERROR`

- **Caused by:** An error due to invalid configuration or usage not caught by a more specific error code
- **Solution:** Ensure all configuration and usage is as per the [documentation] (Configuration)

### `COMPARISON_FAULT`

- **Caused by:** An unexpected error within Flyway (e.g. a null pointer exception)
- **Solution:** Please [contact support](http://redgatesupport.red-gate.com/home)

### `COMPARISON_BINARY_MISSING`

- **Caused by:** The libraries for the comparison technology could not be found
- **Solution:** Reinstall Flyway

### `COMPARISON_ENGINE_UPDATE_REQUIRED`

- **Caused by:** A version incompatibility between the version of Flyway being used and the Flyway files on disk, for example schema model or settings
- **Solution:** Update Flyway

### `COMPARISON_DATABASE_CONNECTION_FAILED`

- **Caused by:** Failure to connect to a database being used as a comparison source. Note that the underlying errors will be database-specific and generated by the underlying database driver.
- **Solution:** Test your database connection, ensuring that you have specified the JDBC URL and credentials correctly.

### `COMPARISON_REGISTRATION_FAILED`

- **Caused by:** Failure to register object model from a comparison source. This could be caused, for example, by an invalid schema model or an issue querying a database.
- **Solution:** See error message for more information on the issue and how to resolve.

### `COMPARISON_ARTIFACT_NOT_FOUND`

- **Caused by:** No comparison artifact detected. This is required for commands such as `generate` and `model`.
- **Solution:** Make sure that the artifact location you are looking to read from is the same as that you are outputting your comparison to in the `diff` command.

### `COMPARISON_ARTIFACT_DESERIALIZE_FAILED`

- **Caused by:** Generic error while attempting to read a comparison artifact generated by a comparison operation.
- **Solution:** See error message for more information. If the artifact has become corrupted in some way it may be worth rerunning the comparison to regenerate it. Also, ensure that you do not have parallel commands reading from and writing to the same location.

### `COMPARISON_ARTIFACT_SERIALIZE_FAILED`

- **Caused by:** Generic error while attempting to write out a comparison artifact.
- **Solution:** See error message for more information. This will be most commonly be a filesystem error, and will be fixed by making sure that you have access to write to the artifact location. Also, ensure that you do not have parallel commands reading from and writing to the same location.

### `COMPARISON_MAPPING_FAILED`

- **Caused by:** Generic error while attempting to compare two comparison sources, after having successfully registered their object models.
- **Solution:** See error message for more information on the issue and how to resolve.

### `COMPARISON_APPLY_TO_TARGET_FAILED`

- **Caused by:** Generic error while attempting to deploy to a target database or write to a target schema model.
- **Solution:** See error message for more information on the issue and how to resolve.

### `COMPARISON_GENERATE_DEPLOYMENT_SCRIPT_FAILED`

- **Caused by:** Generic error while attempting to generate a deployment script or versioned migration.
- **Solution:** See error message for more information on the issue and how to resolve.

### `COMPARISON_GENERATE_UNDO_SCRIPT_FAILED`

- **Caused by:** Generic error while attempting to generate an undo migration.
- **Solution:** See error message for more information on the issue and how to resolve.

### `COMPARISON_RETRIEVE_OBJECT_DETAILS_FAILED`

- **Caused by:** Generic error while attempting to generate SQL for repeatable migrations.
- **Solution:** See error message for more information on the issue and how to resolve.

### `COMPARISON_SNAPSHOT_FAILED`

- **Caused by:** Generic error while attempting to generate a snapshot.
- **Solution:** See error message for more information on the issue and how to resolve.
