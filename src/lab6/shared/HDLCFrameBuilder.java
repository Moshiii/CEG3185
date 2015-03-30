package lab6.shared;

/**
 * Created by Peng on 30/03/2015.
 */
public class HDLCFrameBuilder {

    private static final int MAX_ADDRESS = (int)Math.pow(2, HDLCFrame.ADDRESS_LENGTH) - 1;
    private static final int MAX_WINDOW = (int)Math.pow(2, HDLCFrame.WINDOW_LENGTH) - 1;

    private int address;
    private int seqRcv;
    private boolean poll;
    private char frameType;
    private int seqSend;
    private String type;
    private String data;

    private HDLCFrameBuilder(int address, int seqRcv, boolean poll, char frameType) {
        this.address = address;
        this.seqRcv = seqRcv;
        this.poll = poll;
        this.frameType = frameType;
        if (frameType == 'I') {
            type = null;
        }
        else if (frameType == 'S') {
            seqSend = -1;
        }
    }

    private static boolean validAddress(int address) {
        if (address >= 0 && address <= MAX_ADDRESS) {
            return true;
        }
        System.out.println("Invalid address (must be between 0 and " + MAX_ADDRESS + ", inclusive): " + address);
        return false;
    }

    private static boolean validSeq(int seq) {
        if (seq >= 0 && seq <= MAX_WINDOW) {
            return true;
        }
        System.out.println("Invalid sequence number (must be between 0 and " + MAX_WINDOW + ", inclusive): " + seq);
        return false;
    }

    private static boolean validFrameType(char frameType) {
        if (HDLCFrame.VALID_FRAME_TYPES.indexOf(frameType) != -1) {
            return true;
        }
        System.out.println("Invalid frame type (must be 'I' or 'S'): " + frameType);
        return false;
    }

    private static boolean isValid(int address, int seqRcv, boolean poll, char frameType) {
        boolean flag = true;
        if (!validAddress(address)) {
            flag = false;
        }
        if (!validSeq(seqRcv)) {
            flag = false;
        }
        if (!validFrameType(frameType)) {
            flag = false;
        }
        return flag;
    }

    public static HDLCFrameBuilder getInstance(int address, int seqRcv, boolean poll, char frameType) {
        if (isValid(address, seqRcv, poll, frameType)) {
            return new HDLCFrameBuilder(address, seqRcv, poll, frameType);
        }
        return null;
    }

    public void putSeqSend(int seqSend) {
        if (frameType == 'I' && validSeq(seqSend)) {
            this.seqSend = seqSend;
        }
        else {
            System.out.println("S-frames do not contain a send sequence!");
        }
    }

    public void putData (String data) {
        this.data = data;
    }

    public boolean isValid() {
        boolean flag = true;

        return flag;
    }

    public HDLCFrame getHDLCFrame() {
        if (isValid()) {
            return new HDLCFrame(address, seqRcv, poll, frameType, seqSend, type, data);
        }
        return null;
    }
}
