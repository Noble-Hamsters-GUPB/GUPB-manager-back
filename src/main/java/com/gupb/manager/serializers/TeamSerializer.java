package com.gupb.manager.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.gupb.manager.model.Team;

import java.io.IOException;

public class TeamSerializer  extends JsonSerializer<Team> {
    @Override
    public void serialize(Team team, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("name", team.getName());
        gen.writeEndObject();
    }
}
