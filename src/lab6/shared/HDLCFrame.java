package lab6.shared;

import java.util.StringTokenizer;

/**
 * Created by Peng on 30/03/2015.
 */
public class HDLCFrame {

    private static final String FLAG = "01111110";
    private static final String I_PREFIX = "0";
    private static final String S_PREFIX = "10";
    private static final String U_PREFIX = "11";
    private static final String SEPARATOR = ".";
    private static final int CONTROL_LENGTH = 8;
    private static final int TYPE_LENGTH = 2;
    private static final int MINIMUM_ADDRESS_LENGTH = 8;
    private static final int BYTE_LENGTH = 8;
    public static final String VALID_FRAME_TYPES = "ISU";
    public static final String RR = "00";
    public static final String RNR = "01";
    public static final String REJ = "10";
    public static final String SREJ = "11";
    public static final String SNRM = "00001";
    public static final String SNRME = "11011";
    public static final String SABM_DM = "11100";
    public static final String SABME = "11110";
    public static final String UI = "00000";
    public static final String UA = "00110";
    public static final String DISC_RD = "00010";
    public static final String SIM_RIM = "10000";
    public static final String UP = "00100";
    public static final String RSET = "11001";
    public static final String XID = "11101";
    public static final String FRMR = "10001";
    public static final int WINDOW_LENGTH = 3;
    public static final int MAX_DATA_LENGTH = 64;

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

    private static String toBinaryString(int number, int length) {
        String bin = Integer.toBinaryString(number);
        while(bin.length() < length) {
            bin = "0" + bin;
        }
        return bin;
    }

    private static boolean isValidBinary(String check) {
        for (int i = 0; i < check.length(); i ++) {
            if ("01".indexOf(check.charAt(i)) == -1) {
                return false;
            }
        }
        return true;
    }

    public static HDLCFrameBuilder getHDLCFrameBuilder(int address, boolean poll, char frameType) {
        return HDLCFrameBuilder.getInstance(address, poll, frameType);
    }

    public static HDLCFrame valueOf(String frame) {
        if (!frame.substring(0, FLAG.length()).equals(FLAG)) {
            System.out.println("Start flag is missing. Dropping frame.");
            return null;
        }
        frame = frame.substring(FLAG.length());

        StringTokenizer tokenizer = new StringTokenizer(frame, SEPARATOR);
        if (!tokenizer.hasMoreTokens()) {
            System.out.println("Missing SEPARATOR. Dropping frame.");
            return null;
        }
        String check = tokenizer.nextToken();
        if (check.length() < MINIMUM_ADDRESS_LENGTH || !isValidBinary(check)) {
            System.out.println("Address field is corrupted. Dropping frame.");
            return null;
        }
        int address = Integer.parseInt(check, 2);
        frame = tokenizer.nextToken();

        if (frame.length() < CONTROL_LENGTH + FLAG.length()) {
            System.out.println("Frame is too short. Dropping frame.");
            return null;
        }
        char frameType;
        if (frame.charAt(0) == '0') {
            frameType = 'I';
            frame = frame.substring(1);
        }
        else if (frame.charAt(0) == '1') {
            if (frame.charAt(1) == '0') {
                frameType = 'S';
            }
            else if (frame.charAt(1) == '1') {
                frameType = 'U';
            }
            else {
                System.out.println("Frame type is corrupted. Dropping frame.");
                return null;
            }
            frame = frame.substring(TYPE_LENGTH);
        }
        else {
            System.out.println("Frame type is corrupted. Dropping frame.");
            return null;
        }

        int seqSend = -1;
        String type = null;
        if (frameType == 'I') {
            check = frame.substring(0, WINDOW_LENGTH);
            if (!isValidBinary(check)) {
                System.out.println("Send sequence is corrupted. Dropping frame.");
                return null;
            }
            seqSend = Integer.parseInt(check, 2);
            frame = frame.substring(WINDOW_LENGTH);
        }
        else if (frameType == 'S' || frameType == 'U') {
            type = frame.substring(0, 2);
            if (!isValidBinary(type)) {
                System.out.println("Type is corrupted. Dropping frame.");
                return null;
            }
            frame = frame.substring(TYPE_LENGTH);
        }

        boolean poll;
        if (frame.charAt(0) == '1') {
            poll = true;
        }
        else if (frame.charAt(0) == '0') {
            poll = false;
        }
        else {
            System.out.println("Poll bit is corrupted. Dropping frame.");
            return null;
        }
        frame = frame.substring(1);

        int seqRcv = -1;
        check = frame.substring(0, WINDOW_LENGTH);
        if (isValidBinary(check)) {
            if (frameType == 'I' || frameType == 'S') {
                seqRcv = Integer.parseInt(check, 2);
            }
            else if (frameType == 'U') {
                type = type + check;
            }
        }
        else {
            System.out.println("Send sequence or type is corrupted. Dropping frame.");
            return null;
        }
        frame = frame.substring(WINDOW_LENGTH);

        int index = frame.indexOf(FLAG);
        if (index == -1) {
            System.out.println("End flag is missing. Dropping frame.");
            return null;
        }

        check = frame.substring(0, index);
        StringBuilder strngbldr = new StringBuilder();
        if (frameType == 'S' || frameType == 'U') {
            if (check.length() > 0) {
                System.out.println("S-frames and U-frames shouldn't contain data. Dropping frame.");
                return null;
            }
        }
        else if (frameType == 'I') {
            if (check.length() > MAX_DATA_LENGTH*BYTE_LENGTH || check.length() % BYTE_LENGTH != 0) {
                System.out.println("Data length not valid. Dropping frame.");
                return null;
            }
            else if (!isValidBinary(check)) {
                System.out.println("Data not valid. Dropping frame.");
                return null;
            }
            for (int i = 0; i < check.length()/BYTE_LENGTH; i ++) {
                strngbldr.append((char)Integer.parseInt(check.substring(i*BYTE_LENGTH, (i + 1)*BYTE_LENGTH), 2));
            }
        }
        String data = strngbldr.toString();

        HDLCFrameBuilder builder = HDLCFrame.getHDLCFrameBuilder(address, poll, frameType);
        if (builder == null) {
            return null;
        }

        if (frameType == 'I') {
            builder.setData(data);
            builder.setSeqRcv(seqRcv);
            builder.setSeqSend(seqSend);
        }
        else if (frameType == 'S') {
            builder.setSeqRcv(seqRcv);
            builder.setType(type);
        }
        else if (frameType == 'U') {
            builder.setType(type);
        }

        return builder.getHDLCFrame();
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(FLAG);
        builder.append(toBinaryString(address, MINIMUM_ADDRESS_LENGTH));
        builder.append(SEPARATOR);

        if (frameType == 'I') {
            builder.append(I_PREFIX);
            builder.append(toBinaryString(seqSend, WINDOW_LENGTH));
        }
        else if (frameType == 'S') {
            builder.append(S_PREFIX);
            builder.append(type);
        }
        else if (frameType == 'U') {
            builder.append(U_PREFIX);
            builder.append(type.substring(0, TYPE_LENGTH));
        }

        if (poll) {
            builder.append("1");
        }
        else {
            builder.append("0");
        }

        if (frameType == 'I' || frameType == 'S') {
            builder.append(toBinaryString(seqRcv, WINDOW_LENGTH));
        }
        else if (frameType == 'U') {
            builder.append(type.substring(TYPE_LENGTH, TYPE_LENGTH + WINDOW_LENGTH));
        }

        if (frameType == 'I') {
            for (int i = 0; i < data.length(); i ++) {
                builder.append(toBinaryString(data.charAt(i), BYTE_LENGTH));
            }
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

    //Running some simple tests
    public static void main (String[] args) {
        HDLCFrameBuilder builder = HDLCFrame.getHDLCFrameBuilder(127, true, 'I');
        builder.setData("Testing");
        builder.setSeqRcv(5);
        builder.setSeqSend(4);
        HDLCFrame test = builder.getHDLCFrame();
        String strng = test.toString();
        System.out.println(strng);
        test = HDLCFrame.valueOf(strng);
        System.out.println(test.getAddress());
        System.out.println(test.getPoll());
        System.out.println(test.getFrameType());
        System.out.println(test.getData());
        System.out.println(test.getSeqRcv());
        System.out.println(test.getSeqSend());
    }
}