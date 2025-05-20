package keqing.gtsteam.api.unification.matreials;

public class HigherDegreeMaterials {

    private static int startId = 200;

    private static final int END_ID = startId + 100;

    private static int getMaterialsId() {
        if (startId < END_ID) {
            return startId++;
        }
        throw new ArrayIndexOutOfBoundsException();
    }

    public HigherDegreeMaterials() {
    }
    public static void register() {

    }
}
