package lab6.client;

import lab6.shared.HDLCFrame;
import lab6.shared.HDLCFrameBuilder;

import java.util.Hashtable;
import java.util.LinkedList;

/**
 * Created by Peng on 30/03/2015.
 */
public class ClientLogic {

    private static class Message {
        private String msg;
        private int address;

        private Message(String msg, int address) {
            this.msg = msg;
            this.address = address;
        }
    }

    private static final int WINDOW_SIZE = (int)Math.pow(2, HDLCFrame.WINDOW_LENGTH) - 1;
    private static final int LEADER_ADDRESS = 0;
    private static final int NUM_WINDOWS = 3;
    private Client client;
    private int id;
    private LinkedList<Message> queue = new LinkedList<>();

    /*
    Index 0: expecting ACK for this frame
    Index 1: the next sequence number to be sent
    Index 2: expecting to receive this frame
     */
    private Hashtable<Integer, Integer[]> windows = new Hashtable<>();
    private static final int INDEX_ACK = 0;
    private static final int INDEX_SEND = 1;
    private static final int INDEX_RCV = 2;
    private int outstanding = 0;

    protected ClientLogic() {}

    public ClientLogic (Client client, int id) {
        this.client = client;
        this.id = id;
    }

    private boolean windowOpen() {
        return (outstanding < WINDOW_SIZE);
    }

    private void updateAck(int address, int seqNum) {
        if (!windows.containsKey(address)) {
            System.out.println("Received an acknowledgement for a message that wasn't sent.");
            return;
        }

        Integer[] window = windows.get(address);
        if (seqNum != window[INDEX_ACK]) {
            System.out.println("The received acknowledgement is not the one expected.");
            return;
        }
        window[INDEX_ACK] = (window[INDEX_ACK] + 1)%WINDOW_SIZE;
        outstanding --;
    }

    private int getNextSendSeq(int address) {
        if (!windowOpen()) {
            System.out.println("Outstanding send slots full.");
            return -1;
        }

        if (!windows.containsKey(address)) {
            Integer[] window = new Integer[NUM_WINDOWS];
            for (int i = 0; i < NUM_WINDOWS; i ++) {
                window[i] = 0;
            }
            windows.put(address, window);
        }

        Integer[] window = windows.get(address);
        if (window[INDEX_ACK] == -1) {
            window[INDEX_ACK] = 0;
        }
        int seqNum = window[INDEX_SEND];
        window[INDEX_SEND] = (window[INDEX_SEND] + 1)%WINDOW_SIZE;
        outstanding ++;

        return seqNum;
    }

    private boolean updateRcv(int address, int seqNum) {
        if(!windows.containsKey(address)) {
            Integer[] window = new Integer[NUM_WINDOWS];
            for (int i = 0; i < NUM_WINDOWS; i ++) {
                window[i] = 0;
            }
            window[INDEX_ACK] = -1;
            windows.put(address, window);
        }

        Integer[] window = windows.get(address);
        if (seqNum == window[INDEX_RCV]) {
            window[INDEX_RCV] = (window[INDEX_RCV] + 1)%WINDOW_SIZE;
            return true;
        }

        System.out.println("Was not expecting to receive this sequence number.");
        return false;
    }

    private void sendFrame(HDLCFrame frame) {
        //Implement sending from the client to the server
    }

    private String handleIType(HDLCFrame frame) {
        //ACK the frame
        return frame.getData();
    }

    private void handleSType(HDLCFrame frame) {

    }

    private void handleUType(HDLCFrame frame) {
        if (frame.getType().equals(HDLCFrame.SNRM)) {
            HDLCFrameBuilder builder = HDLCFrame.getHDLCFrameBuilder(LEADER_ADDRESS, false, 'U');
            builder.setType(HDLCFrame.UA);
            HDLCFrame toSend = builder.getHDLCFrame();
            sendFrame(toSend);
        }
        else {
            System.out.println("Undefined U-frame behaviour.");
        }
    }

    private void sendMessage() {
        HDLCFrameBuilder builder;
        if (queue.size() == 0) {
            builder = HDLCFrame.getHDLCFrameBuilder(LEADER_ADDRESS, false, 'S');
            builder.setSeqRcv(0);
            builder.setType(HDLCFrame.RR);

        }
        else {
            Message msg = queue.poll();
            builder = HDLCFrame.getHDLCFrameBuilder(msg.address, false, 'I');
            builder.setSeqSend(getNextSendSeq(msg.address));
            builder.setSeqRcv(0);
            builder.setData(msg.msg);
        }
        HDLCFrame toSend = builder.getHDLCFrame();
        sendFrame(toSend);
    }

    public void enqueueInput(String input, int address) {
        while (true) {
            if (input.length() > HDLCFrame.MAX_DATA_LENGTH) {
                queue.add(new Message(input.substring(0, HDLCFrame.MAX_DATA_LENGTH), address));
                input = input.substring(HDLCFrame.MAX_DATA_LENGTH);
            }
            else if (input.length() > 0) {
                queue.add(new Message(input, address));
                break;
            }
            else {
                break;
            }
        }
    }

    public String handleMessage(String msg) {
        HDLCFrame frame = HDLCFrame.valueOf(msg);
        if (frame == null) {
            return null;
        }

        boolean poll = frame.getPoll();
        if (poll) {
            sendMessage();
        }

        if (frame.getFrameType() == 'I') {
            return handleIType(frame);
        }
        else if (frame.getFrameType() == 'S') {
            handleSType(frame);
        }
        else if (frame.getFrameType() == 'U') {
            handleUType(frame);
        }
        return null;
    }
}
