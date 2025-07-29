package com.club69.mediaconvert.pipeline;


public enum PipelineStepName {
    FETCH_JOB("FETCH_JOB"), // Fetching job from database
    PROCESS_FFMPEG("PROCESS_FFMPEG"), // Processing Media with FFmpeg
    PROCESS_SHAKA_PACKAGER("PROCESS_SHAKA_PACKAGER"), // Processing Media with Shaka Packager
    UPLOAD_FILES_S3("UPLOAD_FILES_S3"), // Uploading the Media Files to s3
    UPDATE_JOB_STATUS("UPDATE_JOB_STATUS"); // Updating the status of the jobs in the database

    public String name;
    PipelineStepName(String name) { this.name = name; }
}
