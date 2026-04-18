/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *--------------------------------------------------------------------------------------------*/

package com.github.copilot.sdk.json;

import com.fasterxml.jackson.annotation.JsonProperty;

/** Result from sessionFs.stat. */
public class SessionFsStatResult {

    @JsonProperty("isFile")
    private boolean isFile;

    @JsonProperty("isDirectory")
    private boolean isDirectory;

    @JsonProperty("size")
    private double size;

    @JsonProperty("mtime")
    private String mtime;

    @JsonProperty("birthtime")
    private String birthtime;

    /** Returns whether the path is a file. */
    public boolean isFile() {
        return isFile;
    }

    /** Sets whether the path is a file. */
    public void setFile(boolean isFile) {
        this.isFile = isFile;
    }

    /** Returns whether the path is a directory. */
    public boolean isDirectory() {
        return isDirectory;
    }

    /** Sets whether the path is a directory. */
    public void setDirectory(boolean isDirectory) {
        this.isDirectory = isDirectory;
    }

    /** Returns the file size in bytes. */
    public double getSize() {
        return size;
    }

    /** Sets the file size in bytes. */
    public void setSize(double size) {
        this.size = size;
    }

    /** Returns the ISO 8601 timestamp of last modification. */
    public String getMtime() {
        return mtime;
    }

    /** Sets the ISO 8601 timestamp of last modification. */
    public void setMtime(String mtime) {
        this.mtime = mtime;
    }

    /** Returns the ISO 8601 timestamp of creation. */
    public String getBirthtime() {
        return birthtime;
    }

    /** Sets the ISO 8601 timestamp of creation. */
    public void setBirthtime(String birthtime) {
        this.birthtime = birthtime;
    }
}
