// File: src/media/StorageManager.java
package media;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Η κλάση StorageManager διαχειρίζεται την αποθήκευση και ανάκτηση των δεδομένων της εφαρμογής σε αρχεία JSON.
 */
public class StorageManager {
    private static final String FILE_PATH = "assets/medialab/data.json";
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(java.time.LocalDate.class, new LocalDateAdapter())
            .setPrettyPrinting()
            .create();

    /**
     * Φορτώνει τα δεδομένα από το αρχείο JSON και επιστρέφει ένα αντικείμενο DataModel.
     *
     * @return Το DataModel που περιέχει τα tasks, τις κατηγορίες και τις προτεραιότητες.
     */
    public static DataModel loadData() {
        try (FileReader reader = new FileReader(FILE_PATH)) {
            Type type = new TypeToken<DataModel>() {}.getType();
            return gson.fromJson(reader, type);
        } catch (IOException e) {
            e.printStackTrace();
            // Δημιουργία προεπιλεγμένων δεδομένων αν το αρχείο δεν βρεθεί
            DataModel defaultData = new DataModel(
                    new java.util.ArrayList<>(),
                    new java.util.ArrayList<>(List.of("Default")),
                    new java.util.ArrayList<>(List.of("Default"))
            );
            saveData(defaultData);
            return defaultData;
        }
    }

    /**
     * Αποθηκεύει τα δεδομένα στο αρχείο JSON.
     *
     * @param data Το DataModel που περιέχει τα δεδομένα προς αποθήκευση.
     */
    public static void saveData(DataModel data) {
        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            gson.toJson(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Η εσωτερική κλάση LocalDateAdapter προσαρμόζει τη μορφή serialization και deserialization του LocalDate.
     */
    private static class LocalDateAdapter implements JsonSerializer<java.time.LocalDate>, JsonDeserializer<java.time.LocalDate> {
        @Override
        public JsonElement serialize(java.time.LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.toString());
        }

        @Override
        public java.time.LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return java.time.LocalDate.parse(json.getAsString());
        }
    }
}
