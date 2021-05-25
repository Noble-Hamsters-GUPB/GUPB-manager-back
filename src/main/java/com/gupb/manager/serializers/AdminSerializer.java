package com.gupb.manager.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.gupb.manager.model.Admin;
import com.gupb.manager.model.Student;

import java.io.IOException;
import java.util.Set;

public class AdminSerializer extends JsonSerializer<Admin> {
    @Override
    public void serialize(Admin admin, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeNumberField("id", admin.getId());
        gen.writeStringField("emailAddress", admin.getEmailAddress());
        gen.writeEndObject();
    }
}
