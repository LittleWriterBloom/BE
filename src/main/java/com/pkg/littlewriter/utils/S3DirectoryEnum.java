package com.pkg.littlewriter.utils;

public enum S3DirectoryEnum {
    TEMPORARY("temporary/"),
    CHARACTER("character/"),
    BOOK("book/");
    private final String directoryName;

     S3DirectoryEnum(String directoryName) {
        this.directoryName = directoryName;
    }

    public String getDirectoryName() {
         return this.directoryName;
    }
}
