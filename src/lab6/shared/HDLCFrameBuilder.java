package lab6.shared;

/**
 * Created by Peng on 30/03/2015.
 */
public class HDLCFrameBuilder {

    private static final int MAX_WINDOW = (int)Math.pow(2, HDLCFrame.WINDOW_LENGTH) - 1;

    private int address;
    private int seqRcv = -1;
    private boolean poll;
    private char frameType;
    private int seqSend = -1;
    private String data = null;
    private String type = null;

    private HDLCFrameBuilder(int address, boolean poll, char frameType) {
        this.address = address;
        this.poll = poll;
        this.frameType = frameType;
    }

    private static boolean validAddress(int address) {
        if (address >= 0) {
            return true;
        }
        System.out.println("Invalid address (must be non-negative): " + address);
        return false;
    }

    private static boolean validFrameType(char frameType) {
        if (HDLCFrame.VALID_FRAME_TYPES.indexOf(frameType) != -1) {
            return true;
        }
        System.out.println("Invalid frame type (must be 'I', 'S', or 'U'): " + frameType);
        return false;
    }

    private static boolean validSeq(int seq) {
        if (seq >= 0 && seq <= MAX_WINDOW) {
            return true;
        }
        System.out.println("Invalid sequence number (must be between 0 and " + MAX_WINDOW + ", inclusive): " + seq);
        return false;
    }

    private static boolean isValidSType(String type) {
        if (type.equals(HDLCFrame.RR) || type.equals(HDLCFrame.RNR) || type.equals(HDLCFrame.REJ)
                || type.equals(HDLCFrame.SREJ)) {
            return true;
        }
        return false;
    }

    private static boolean isValidUType(String type) {
        if (type.equals(HDLCFrame.SNRM) || type.equals(HDLCFrame.SNRME) || type.equals(HDLCFrame.SABM_DM)
                || type.equals(HDLCFrame.SABME) || type.equals(HDLCFrame.UI) || type.equals(HDLCFrame.UA)
                || type.equals(HDLCFrame.DISC_RD) || type.equals(HDLCFrame.SIM_RIM) || type.equals(HDLCFrame.UP)
                || type.equals(HDLCFrame.RSET) || type.equals(HDLCFrame.XID) || type.equals(HDLCFrame.FRMR)) {
            return true;
        }
        return false;
    }

    private static boolean isValid(int address, boolean poll, char frameType) {
        boolean flag = true;

        if (!validAddress(address)) {
            flag = false;
        }

        if (!validFrameType(frameType)) {
            flag = false;
        }

        return flag;
    }

    public static HDLCFrameBuilder getInstance(int address, boolean poll, char frameType) {
        if (isValid(address, poll, frameType)) {
            return new HDLCFrameBuilder(address, poll, frameType);
        }
        return null;
    }

    public void setSeqRcv(int seqRcv) {
        if ((frameType == 'I' || frameType == 'S') && validSeq(seqRcv)) {
            this.seqRcv = seqRcv;
        }
        else {
            System.out.println("U-frames do not contain a receive sequence!");
        }
    }

    public void setSeqSend(int seqSend) {
        if (frameType == 'I' && validSeq(seqSend)) {
            this.seqSend = seqSend;
        }
        else {
            System.out.println("S-frames and U-frames do not contain a send sequence!");
        }
    }

    public void setData (String data) {
        if (frameType == 'I') {
            if (data != null && data.length() <= HDLCFrame.MAX_DATA_LENGTH) {
                this.data = data;
            }
            else {
                System.out.println("Invalid data!");
            }
        }
        else {
            System.out.println("S-frames and U-frames do not contain data!");
        }
    }

    public void setType (String type) {
        if (frameType == 'S') {
            if (type != null && isValidSType(type)) {
                this.type = type;
            }
            else {
                System.out.println("Invalid S-frame type!");
            }
        }
        else if (frameType == 'U') {
            if (type != null && isValidUType(type)) {
                this.type = type;
            }
            else {
                System.out.println("Invalid U-frame type!");
            }
        }
        else {
            System.out.println("I-frames do not contain a type!");
        }
    }

    private boolean isValid() {
        boolean flag = true;

        if (frameType == 'I') {
            if (seqRcv == -1) {
                System.out.println("Receive sequence not set!");
                flag = false;
            }
            if (seqSend == -1) {
                System.out.println("Send sequence not set!");
                flag = false;
            }
            if (data == null) {
                System.out.println("Data not set!");
                flag = false;
            }
        }
        else if (frameType == 'S') {
            if (seqRcv == -1) {
                System.out.println("Receive sequence not set!");
                flag = false;
            }
            if (type == null) {
                System.out.println("Type not set!");
                return false;
            }
        }
        else if (frameType == 'U') {
            if (type == null) {
                System.out.println("Type not set!");
                return false;
            }
        }

        return flag;
    }

    public HDLCFrame getHDLCFrame() {
        if (isValid()) {
            return new HDLCFrame(address, seqRcv, poll, frameType, seqSend, type, data);
        }
        return null;
    }
}