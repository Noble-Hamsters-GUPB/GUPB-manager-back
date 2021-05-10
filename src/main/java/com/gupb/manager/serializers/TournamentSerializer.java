package com.gupb.manager.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.gupb.manager.model.Tournament;

import java.io.IOException;

public class TournamentSerializer extends JsonSerializer<Tournament> {

    @Override
    public void serialize(Tournament tournament, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeNumberField("id", tournament.getId());
        gen.writeStringField("name", tournament.getName());
        gen.writeEndObject();
    }
}
