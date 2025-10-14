package com.sga.common.util;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.File;
import java.io.IOException;

public class FileConverter extends TypeAdapter<File> {
    @Override
    public void write(JsonWriter out, File file) throws IOException {
        if (file == null) {
            out.nullValue();
        } else {
            out.value(file.getAbsolutePath()); // Serialize the file path as a string
        }
    }

    @Override
    public File read(JsonReader in) throws IOException {
        if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
            in.nextNull();
            return null;
        } else {
            return new File(in.nextString()); // Deserialize the string back to a File object
        }
    }
}