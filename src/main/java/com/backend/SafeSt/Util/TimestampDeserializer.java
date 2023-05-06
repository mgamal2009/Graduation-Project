package com.backend.SafeSt.Util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.sql.Timestamp;

public class TimestampDeserializer extends StdDeserializer<Timestamp> {

    protected TimestampDeserializer() {
        super(Timestamp.class);
    }

    @Override
    public Timestamp deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        return Timestamp.valueOf(parser.readValueAs(String.class));
    }
}