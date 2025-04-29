
package zad1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public class Client {

    private final String host;
    private final int port;
    private final String id;

    private SocketChannel channel;

    public Client(String host, int port, String id) {
        this.host = host;
        this.port = port;
        this.id = id;
    }

    public void connect() {
        try {
            channel = SocketChannel.open();
            channel.configureBlocking(false);
            channel.connect(new InetSocketAddress(host, port));

            while (!channel.finishConnect()) {
                // czekam aż połączenie się nawiąże
                TimeUnit.MILLISECONDS.sleep(50);
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to connect: " + e.getMessage(), e);
        }
    }

    public String send(String req) {
        try {
            ByteBuffer writeBuffer = ByteBuffer.wrap((req).getBytes());
            channel.write(writeBuffer);

            ByteBuffer readBuffer = ByteBuffer.allocate(1024);

            readBuffer.clear();

            int readBytes = channel.read(readBuffer);

            int totalWait = 0;
            while ((readBytes = channel.read(readBuffer)) == 0 && totalWait < 2000) {
                Thread.sleep(50);
                totalWait += 50;
            }


            readBuffer.flip();
            byte[] data = new byte[readBuffer.remaining()];
            readBuffer.get(data);

            return new String(data, StandardCharsets.UTF_8);

        } catch (IOException e) {
            throw new RuntimeException("Error during send: " + e.getMessage(), e);
        } catch (InterruptedException e){
            e.printStackTrace();
        }
        return null;
    }


    public String getId(){
        return id;
    }
}