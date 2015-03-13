package lab4;

public abstract class Encoder {

    public static final String HDB3 = "HDB3";

    public static final Encoder getInstance (String encoder) {
        if (encoder == null) {
            System.out.println("Null value passed in as encoder type.");
            return null;
        }

        if (encoder == HDB3) {
            return new HDB3Encoder();
        }
        else {
            System.out.println(encoder + " is not a supported encoder type.");
            return null;
        }
    }

    protected Encoder() {}

    public final boolean checkBinaryValidity (String input) {
        char[] array = input.toCharArray();
        for (int i = 0; i < array.length; i ++) {
            if (array[i] != '0' && array[i] != '1') {
                return false;
            }
        }
        return true;
    }

    public abstract boolean checkEncodedValidity (String input);

    public abstract String encode (String input);

    public abstract String decode (String input);
}
