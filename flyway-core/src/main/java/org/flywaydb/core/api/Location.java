/*-
 * ========================LICENSE_START=================================
 * flyway-core
 * ========================================================================
 * Copyright (C) 2010 - 2025 Red Gate Software Ltd
 * ========================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
package org.flywaydb.core.api;

import lombok.Getter;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A location to load migrations from.
 */
public final class Location implements Comparable<Location> {
    /**
     * The prefix for classpath locations.
     */
    private static final String CLASSPATH_PREFIX = "classpath:";
    /**
     * The prefix for filesystem locations.
     */
    public static final String FILESYSTEM_PREFIX = "filesystem:";
    /**
     * The prefix for AWS S3 locations.
     */
    private static final String AWS_S3_PREFIX = "s3:";
    /**
     * The prefix for Google Cloud Storage locations.
     */
    private static final String GCS_PREFIX = "gcs:";

    /**
     * @return The prefix part of the location. Can be either classpath: or filesystem:.
     */
    @Getter
    private final String prefix;
    /**
     * The path part of the location.
     */
    private String rawPath;
    /**
     * The first folder in the path. This will equal rawPath if the path does not contain any wildcards
     *
     * @return The root part of the path part of the location.
     */
    @Getter
    private String rootPath;
    /**
     * @return The regex that matches wildcards in the original path. Null if the original path did not contain any wildcards.
     */
    @Getter
    private Pattern pathRegex = null;

    public Location(String descriptor) {
        String normalizedDescriptor = descriptor.trim();

        if (normalizedDescriptor.contains(":")) {
            prefix = normalizedDescriptor.substring(0, normalizedDescriptor.indexOf(":") + 1);
            rawPath = normalizedDescriptor.substring(normalizedDescriptor.indexOf(":") + 1);
        } else {
            prefix = CLASSPATH_PREFIX;
            rawPath = normalizedDescriptor;
        }

        if (isClassPath()) {
            if (rawPath.startsWith("/")) {
                rawPath = rawPath.substring(1);
            }
            if (rawPath.endsWith("/")) {
                rawPath = rawPath.substring(0, rawPath.length() - 1);
            }
            processRawPath();
        } else if (isFileSystem()) {
            processRawPath();
            rootPath = new File(rootPath).getPath();

            if (pathRegex == null) {
                // if the original path contained no wildcards, also normalise it
                rawPath = new File(rawPath).getPath();
            }
        } else if (!isAwsS3() && !isGCS()) {
            throw new FlywayException("Unknown prefix for location (should be one of filesystem:, classpath:, gcs:, or s3:): "
                                              + normalizedDescriptor);
        }

        if (rawPath.endsWith(File.separator)) {
            rawPath = rawPath.substring(0, rawPath.length() - 1);
        }
    }

    /**
     * Process the rawPath into a rootPath and a regex.
     * Supported wildcards:
     * **: Match any 0 or more directories
     * *: Match any sequence of non-separator characters
     * ?: Match any single character
     */
    private void processRawPath() {
        if (rawPath.contains("*") || rawPath.contains("?")) {
            // we need to figure out the root, and create the regex

            String separator = isFileSystem() ? File.separator : "/";
            String escapedSeparator = separator.replace("\\", "\\\\").replace("/", "\\/");

            // split on either of the path separators
            String[] pathSplit = rawPath.split("[\\\\/]");

            StringBuilder rootPart = new StringBuilder();
            StringBuilder patternPart = new StringBuilder();

            boolean endsInFile = false;
            boolean skipSeparator = false;
            boolean inPattern = false;
            for (String pathPart : pathSplit) {
                endsInFile = false;

                if (pathPart.contains("*") || pathPart.contains("?")) {
                    inPattern = true;
                }

                if (inPattern) {
                    if (skipSeparator) {
                        skipSeparator = false;
                    } else {
                        patternPart.append("/");
                    }

                    String regex;
                    if ("**".equals(pathPart)) {
                        regex = "([^/]+/)*?";

                        // this pattern contains the ending separator, so make sure we skip appending it after
                        skipSeparator = true;
                    } else {
                        endsInFile = pathPart.contains(".");

                        regex = pathPart;
                        regex = regex.replace(".", "\\.");
                        regex = regex.replace("?", "[^/]");
                        regex = regex.replace("*", "[^/]+?");
                    }

                    patternPart.append(regex);
                } else {
                    rootPart.append(separator).append(pathPart);
                }
            }

            // We always append a separator before each part, so ensure we skip it when setting the final rootPath
            rootPath = rootPart.length() > 0 ? rootPart.substring(1) : "";

            // Again, skip first separator
            String pattern = patternPart.substring(1);

            // Replace the temporary / with the actual escaped separator
            pattern = pattern.replace("/", escapedSeparator);

            // Append the rootPath if it is non-empty
            if (rootPart.length() > 0) {
                pattern = rootPath.replace(separator, escapedSeparator) + escapedSeparator + pattern;
            }

            // if the path did not end in a file, then append the file match pattern
            if (!endsInFile) {
                pattern = pattern + escapedSeparator + "(?<relpath>.*)";
            }

            pathRegex = Pattern.compile(pattern);
        } else {
            rootPath = rawPath;
        }
    }

    /**
     * @return Whether the given path matches this locations regex. Will always return true when the location did not contain any wildcards.
     */
    public boolean matchesPath(String path) {
        if (pathRegex == null) {
            return true;
        }
        return pathRegex.matcher(path).matches();
    }

    /**
     * Returns the path relative to this location. If the location path contains wildcards, the returned path will be relative
     * to the last non-wildcard folder in the path.
     */
    public String getPathRelativeToThis(String path) {
        if (pathRegex != null && pathRegex.pattern().contains("?<relpath>")) {
            Matcher matcher = pathRegex.matcher(path);
            if (matcher.matches()) {
                String relPath = matcher.group("relpath");
                if (relPath != null && relPath.length() > 0) {
                    return relPath;
                }
            }
        }

        return rootPath.length() > 0 ? path.substring(rootPath.length() + 1) : path;
    }

    /**
     * Checks whether this denotes a location on the classpath.
     *
     * @return {@code true} if it does, {@code false} if it doesn't.
     */
    public boolean isClassPath() {
        return CLASSPATH_PREFIX.equals(prefix);
    }

    /**
     * Checks whether this denotes a location on the filesystem.
     *
     * @return {@code true} if it does, {@code false} if it doesn't.
     */
    public boolean isFileSystem() {
        return FILESYSTEM_PREFIX.equals(prefix);
    }

    /**
     * Checks whether this denotes a location in AWS S3.
     *
     * @return {@code true} if it does, {@code false} if it doesn't;
     */
    public boolean isAwsS3() {
        return AWS_S3_PREFIX.equals(prefix);
    }

    /**
     * Checks whether this denotes a location in Google cloud storage.
     *
     * @return {@code true} if it does, {@code false} if it doesn't;
     */
    public boolean isGCS() {
        return GCS_PREFIX.equals(prefix);
    }

    /**
     * Checks whether this location is a parent of this other location.
     *
     * @param other The other location.
     * @return {@code true} if it is, {@code false} if it isn't.
     */
    @SuppressWarnings("SimplifiableIfStatement")
    public boolean isParentOf(Location other) {
        if (pathRegex != null || other.pathRegex != null) {
            return false;
        }
        if (isClassPath() && other.isClassPath()) {
            return (other.getDescriptor() + "/").startsWith(getDescriptor() + "/");
        }
        if (isFileSystem() && other.isFileSystem()) {
            return (other.getDescriptor() + File.separator).startsWith(getDescriptor() + File.separator);
        }
        return false;
    }

    /**
     * @return The path part of the location.
     */
    public String getPath() {
        return rawPath;
    }

    /**
     * @return The complete location descriptor.
     */
    public String getDescriptor() {
        return prefix + rawPath;
    }

    @Override
    public int compareTo(Location o) {
        return getDescriptor().compareTo(o.getDescriptor());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Location location = (Location) o;
        return getDescriptor().equals(location.getDescriptor());
    }

    @Override
    public int hashCode() {
        return getDescriptor().hashCode();
    }

    /**
     * @return The complete location descriptor.
     */
    @Override
    public String toString() {
        return getDescriptor();
    }
}
