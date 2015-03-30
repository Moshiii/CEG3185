package lab6.shared;

/**
 * Created by Peng on 30/03/2015.
 */
public class HDLCFrame {

    private static final String FLAG = "01111110";
    public static final String VALID_FRAME_TYPES = "IS";
    public static final String RR = "00";
    public static final String RNR = "01";
    public static final String REJ = "10";
    public static final String SREJ = "11";
    public static final int ADDRESS_LENGTH = 8;
    public static final int WINDOW_LENGTH = 3;

    private final int address;
    private final int seqRcv;
    private final boolean poll;
    private final char frameType;
    private final int seqSend;
    private final String type;
    private final String data;

    protected HDLCFrame(int address, int seqRcv, boolean poll, char frameType, int seqSend,
                      String type, String data) {
        this.address = address;
        this.seqRcv = seqRcv;
        this.poll = poll;
        this.frameType = frameType;
        this.seqSend = seqSend;
        this.type = type;
        this.data = data;
    }

    public static HDLCFrameBuilder getHDLCFrameBuilder(int address, int seqRcv, boolean poll, char frameType) {
        return HDLCFrameBuilder.getInstance(address, seqRcv, poll, frameType);
    }

    public static HDLCFrame valueOf(String frame) {
        if (!frame.substring(0, 8).equals(FLAG)) {
            System.out.println("Frame flag is incorrect so the frame will be dropped.");
            return null;
        }
        int address = 0;
        int seqRcv = 0;
        boolean poll = true;
        char frameType = 'I';
        int seqSend = 0;
        String type = "00";
        String data = "Whatever";
        HDLCFrameBuilder builder = HDLCFrame.getHDLCFrameBuilder(address, seqRcv, poll, frameType);
        return builder.getHDLCFrame();
    }

    private static String toBinaryString(int number, int length) {
        String bin = Integer.toBinaryString(number);
        while(bin.length() < length) {
            bin = "0" + bin;
        }
        return bin;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(FLAG);
        builder.append(toBinaryString(address, ADDRESS_LENGTH));
        if (frameType == 'I') {
            builder.append("0");
            builder.append(toBinaryString(seqSend, WINDOW_LENGTH));
        }
        else {
            builder.append("10");
            builder.append(type);
        }
        if (poll) {
            builder.append("1");
        }
        builder.append(FLAG);
        return builder.toString();
    }

    public int getAddress() {
        return address;
    }

    public int getSeqRcv() {
        return seqRcv;
    }

    public boolean getPoll() {
        return poll;
    }

    public char getFrameType() {
        return frameType;
    }

    public int getSeqSend() {
        return seqSend;
    }

    public String getType() {
        return type;
    }

    public String getData() {
        return data;
    }

    public static void main (String[] args) {
        System.out.println(HDLCFrame.toBinaryString(10, 6));
    }
}
