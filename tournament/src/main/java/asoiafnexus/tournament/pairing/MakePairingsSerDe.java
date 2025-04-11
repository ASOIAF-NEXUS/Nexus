package asoiafnexus.tournament.pairing;

import asoiafnexus.tournament.model.MakePairings;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class MakePairingsSerDe {
    public static class Serializer extends StdSerializer<MakePairings> {
        protected Serializer() {
            super(MakePairings.class);
        }

        @Override
        public void serialize(MakePairings makePairings, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeString(makePairings.name());
        }
    }

    public static class Deserializer extends StdDeserializer<MakePairings> {
        protected Deserializer() {
            super(MakePairings.class);
        }

        @Override
        public MakePairings deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
            var name = jsonParser.getValueAsString();
            return MakePairings.allStrategies.stream().filter(s -> s.name().equals(name)).findFirst().orElseThrow();
        }
    }
}
