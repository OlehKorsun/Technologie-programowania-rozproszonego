
package zad1;

import java.util.List;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class ClientTask extends FutureTask<String> {

    private final Client client;
    private final List<String> requests;
    private final boolean showSendRes;

    private ClientTask(Client client, List<String> requests, boolean showSendRes) {
        super(new Callable<String>() {
            @Override
            public String call() throws Exception {
                StringBuilder stringBuilder = new StringBuilder();
                client.connect();
//                stringBuilder.append(client.send("login " + client.getId()) + '\n');
                String tmp = "";
                client.send("login " + client.getId());
                for(String str : requests){
//                    stringBuilder.append(client.send(str) + '\n');
                    tmp = client.send(str);
                    if(showSendRes) System.out.println(tmp);
                }
                stringBuilder.append(client.send("bye and log transfer"));

                return stringBuilder.toString();
            }
        });
        this.client = client;
        this.requests = requests;
        this.showSendRes = showSendRes;
    }

    public static ClientTask create(Client c, List<String> reqs, boolean showSendRes) {
        return new ClientTask(c, reqs, showSendRes);
    }
}

