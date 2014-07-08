import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class DaoGenerator {
    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(1, "com.golovin.notes.data");

        setup(schema);

        new de.greenrobot.daogenerator.DaoGenerator().generateAll(schema, "../notes-main/src/main/java");
    }

    private static void setup(Schema schema) {
        Entity photos = schema.addEntity("Note");

        photos.addIdProperty().autoincrement().primaryKey();
        photos.addStringProperty("content");
        photos.addStringProperty("photoUri");
    }
}
