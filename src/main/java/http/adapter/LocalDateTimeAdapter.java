package http.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
    @Override
    public void write(JsonWriter jsonWriter, LocalDateTime localDateTime) throws IOException {
        String toWrite = "null";
        if (localDateTime != null) {
            toWrite = localDateTime.toString();
        }
        jsonWriter.value(toWrite);
    }

    @Override
    public LocalDateTime read(JsonReader jsonReader) throws IOException {
        String fromReader = jsonReader.nextString();
        if (fromReader.equals("null")) {
            return null;
        }
        return LocalDateTime.parse(fromReader);
    }
}
