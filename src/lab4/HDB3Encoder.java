package lab4;

public final class HDB3Encoder extends Encoder {

    protected HDB3Encoder() {}

    public final boolean checkEncodedValidity (String input) {
        char[] array = input.toCharArray();
        for (int i = 0; i < array.length; i ++) {
            if (array[i] != '+' && array[i] != '-' && array[i] != '0') {
                return false;
            }
        }
        return true;
    }

    public final String encode (String input) {
        boolean even = true;
        boolean positive = true;
        char[] array = input.toCharArray();
        StringBuilder output = new StringBuilder();

        for (int i = 0; i < array.length; i ++) {
            if ((i < array.length - 3) && array[i] == '0' && array[i + 1] == '0' && array[i + 2] == '0' && array[i + 3] == '0') {
                if (even) {
                    positive = !positive;
                    addChar(output, positive);
                }
                else {
                    output.append('0');
                }
                output.append("00");
                addChar(output, positive);
                even = true;
                i += 3;
            }
            else {
                if (array[i] == '1') {
                    even = !even;
                    positive = !positive;
                    addChar(output, positive);
                } else {
                    output.append('0');
                }
            }
        }

        return output.toString();
    }

    private final void addChar (StringBuilder output, boolean positive) {
        if (positive) {
            output.append('+');
        } else {
            output.append('-');
        }
    }

    public final String decode (String input) {
        boolean positive = true;
        char[] array = input.toCharArray();
        StringBuilder output = new StringBuilder();

        for (int i = 0; i < array.length; i ++) {
            if (array[i] == '0') {
                output.append('0');
            }
            else if (array[i] == '+') {
                if (positive) {
                    output.append('0');
                    if (i - 3 >= 0) {
                        output.replace(i-3, i-2, "0");
                    }
                }
                else {
                    output.append('1');
                }
                positive = true;
            }
            else {
                if (!positive) {
                    output.append('0');
                    if (i - 3 >= 0) {
                        output.replace(i-3, i-2, "0");
                    }
                }
                else {
                    output.append('1');
                }
                positive = false;
            }
        }

        return output.toString();
    }

    public static void main (String[] args) {
        Encoder enc = Encoder.getInstance(Encoder.HDB3);
        String test = "000011110000";
        String encoded = enc.encode(test);
        String decoded = enc.decode(encoded);
        System.out.println(encoded);
        System.out.println(test);
        System.out.println(decoded);
    }
}
