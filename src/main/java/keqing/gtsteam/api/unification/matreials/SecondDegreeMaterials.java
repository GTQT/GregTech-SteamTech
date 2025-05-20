package keqing.gtsteam.api.unification.matreials;

public class SecondDegreeMaterials {
    private static int startId = 100;

    private static final int END_ID = startId + 100;

    private static int getMaterialsId() {
        if (startId < END_ID) {
            return startId++;
        }
        throw new ArrayIndexOutOfBoundsException();
    }

    public SecondDegreeMaterials() {
    }
    public static void register() {

    }
}
