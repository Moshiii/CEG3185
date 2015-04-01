package lab6.server;

import lab6.client.ClientLogic;

/**
 * Created by Peng on 30/03/2015.
 */
public class ServerLogic extends ClientLogic{

    private Server server;
    private ClientLogic client;
    private int id = 0;

    public ServerLogic (Server server) {
        super();
        this.server = server;
    }


}
