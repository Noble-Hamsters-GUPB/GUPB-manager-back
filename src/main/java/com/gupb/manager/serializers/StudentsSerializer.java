package com.gupb.manager.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.gupb.manager.model.Student;
import com.gupb.manager.model.Team;

import java.io.IOException;
import java.util.Set;

public class StudentsSerializer extends JsonSerializer<Set<Student>> {
    @Override
    public void serialize(Set<Student> students, JsonGenerator gen, SerializerProvider serializers) throws IOException {

        gen.writeStartArray();
        for(Student student : students) {
            gen.writeStartObject();
            gen.writeNumberField("id", student.getId());
            gen.writeStringField("email", student.getEmailAddress());
            gen.writeStringField("index", student.getIndexNumber());
            gen.writeEndObject();
        }
        gen.writeEndArray();
    }
}
