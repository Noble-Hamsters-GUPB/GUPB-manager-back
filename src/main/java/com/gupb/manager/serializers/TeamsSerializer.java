package com.gupb.manager.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.gupb.manager.model.Team;

import java.io.IOException;
import java.util.Set;

public class TeamsSerializer extends JsonSerializer<Set<Team>>  {
    @Override
    public void serialize(Set<Team> teams, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartArray();
        for(Team team : teams) {
            gen.writeStartObject();
            gen.writeNumberField("id", team.getId());
            gen.writeStringField("name", team.getName());
            gen.writeEndObject();
        }
        gen.writeEndArray();
    }
}
