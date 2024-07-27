package http.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;

public class DurationAdapter extends TypeAdapter<Duration> {
    @Override
    public void write(JsonWriter jsonWriter, Duration duration) throws IOException {
        String toWrite = duration != null ? duration.toString() : "null";
        jsonWriter.value(toWrite);
    }

    @Override
    public Duration read(JsonReader jsonReader) throws IOException {
        String fromReader = jsonReader.nextString();
        if (fromReader.equals("null")) {
            return null;
        }
        return Duration.parse(fromReader);
    }
}
