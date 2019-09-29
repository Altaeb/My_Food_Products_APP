
package maelumat.almuntaj.abdalfattah.altaeb.network.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import maelumat.almuntaj.abdalfattah.altaeb.models.AllergenResponse;
import maelumat.almuntaj.abdalfattah.altaeb.models.AllergensWrapper;

import java.io.IOException;
import java.util.*;

/**
 * Custom deserializer for {@link maelumat.almuntaj.abdalfattah.altaeb.models.AllergensWrapper AllergensWrapper}
 *
 * @author Lobster 2018-03-04
 * @author ross-holloway94 2018-03-14
 */
public class AllergensWrapperDeserializer extends StdDeserializer<AllergensWrapper> {
    public AllergensWrapperDeserializer() {
        super(AllergensWrapper.class);
    }

    @Override
    public AllergensWrapper deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        List<AllergenResponse> allergens = new ArrayList<>();

        JsonNode mainNode = jp.getCodec().readTree(jp);
        Iterator<Map.Entry<String, JsonNode>> mainNodeIterator = mainNode.fields();

        while (mainNodeIterator.hasNext()) {
           final Map.Entry<String, JsonNode> subNode = mainNodeIterator.next();
            JsonNode namesNode = subNode.getValue().get(DeserializerHelper.NAMES_KEY);

            if (namesNode != null) {
                Map<String, String> names = DeserializerHelper.extractNames(namesNode);

                if (subNode.getValue().has(DeserializerHelper.WIKIDATA_KEY)) {
                    allergens.add(new AllergenResponse(subNode.getKey(), names, subNode.getValue().get(DeserializerHelper.WIKIDATA_KEY).toString()));
                } else {
                    allergens.add(new AllergenResponse(subNode.getKey(), names));
                }
            }
        }

        AllergensWrapper wrapper = new AllergensWrapper();
        wrapper.setAllergens(allergens);

        return wrapper;
    }
}
