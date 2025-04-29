
package zad1;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.*;

public class Server extends Thread{
    private String host;
    private int port;
    private Selector selector;
    private ServerSocketChannel socketChannel;

    private List<String> serverLog;

    private Map<String, List<String>> mapaLogow;


    private List<String[]> weryfikacjaClientow;


    public Server(String host, int port){
        this.host = host;
        this.port = port;
        serverLog = new ArrayList<>();
        mapaLogow = new LinkedHashMap<>();
        weryfikacjaClientow = new ArrayList<>();

        try {
            socketChannel = ServerSocketChannel.open();

            socketChannel.configureBlocking(false);
            socketChannel.socket().bind(new InetSocketAddress(host, port));

            selector = Selector.open();

            socketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    public void startServer(){
        this.start();
    }




    public void stopServer() {
        this.interrupt();
        selector.wakeup();
    }



    public String getServerLog(){
        StringBuilder stringBuilder = new StringBuilder();
        for(String str : serverLog){
            stringBuilder.append(str + '\n');
        }
        return stringBuilder.toString();
    }


    @Override
    public void run(){

        while(!this.isInterrupted()){

            try{
                selector.select(); // zebranie rządań
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while(iterator.hasNext()){
                    SelectionKey key = (SelectionKey) iterator.next();
                    iterator.remove();


                    if(key.isAcceptable()){    // czekamy na połączenie
                        acceptServer();
                        continue;
                    }


                    if(key.isReadable()){   // czekamy dane do odczytywania
                        SocketChannel sc = (SocketChannel) key.channel();
                        // obsługa zlecenia
                        ByteBuffer buffer = ByteBuffer.allocate(1024);

                        int bytesRead = sc.read(buffer);
                        if (bytesRead == -1) {

                            sc.close();
                            key.cancel();
                            return;
                        }

                        buffer.flip();
                        byte[] data = new byte[buffer.remaining()];
                        buffer.get(data);

                        String message = new String(data, StandardCharsets.UTF_8);

                        if(message.startsWith("login ")){
                            login(message, sc);
                        } else if(message.startsWith("bye and log transfer")){
                            byAndLogTransfer(sc);
                        } else if(message.startsWith("bye")){
                            by(sc);
                        } else {
                            date(message, sc);
                        }
                        continue;
                    }

                }
            } catch (IOException e){
                e.printStackTrace();
                break;
            } catch (Exception e){
                e.printStackTrace();
                System.exit(1);
            }
        }
    }




    public void acceptServer(){
        SocketChannel sc = null;
        try {
            sc = socketChannel.accept();
            sc.configureBlocking(false);
            sc.register(selector, SelectionKey.OP_READ);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }




    public void login(String message, SocketChannel sc){
        // dodać do listy logów "ID + logged in at "time""
        // otrzymuję host i port klienta

        try {
            InetSocketAddress inetSocketAddress =(InetSocketAddress) sc.getRemoteAddress();
            String host = inetSocketAddress.getHostName();
            int port = inetSocketAddress.getPort();

            String id = message.split(" ")[1];

            // dodaję do listy
            weryfikacjaClientow.add(new String[]{host, String.valueOf(port), id});

            String str = "logged in\n";

            ByteBuffer buffer1 = ByteBuffer.wrap(str.getBytes());

            sc.write(buffer1);

            serverLog.add(id + " logged in at " + LocalTime.now());

            if(!mapaLogow.containsKey(id)){
                mapaLogow.put(id, new ArrayList<>());
            }
            mapaLogow.get(id).add("=== " + id + " log start ===\n");
            mapaLogow.get(id).add("logged in\n");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }



    public void by(SocketChannel sc){

        try {
            String id = getIdClienta(sc);

            String str = "logged out";
            ByteBuffer buffer1 = ByteBuffer.wrap(str.getBytes());

            sc.write(buffer1);

            serverLog.add(id + " logged out at " + LocalTime.now());


            if(!mapaLogow.containsKey(id)){
                mapaLogow.put(id, new ArrayList<>());
            }

            mapaLogow.get(id).add("logged out");
            mapaLogow.get(id).add("=== " + id + " log end ===\n");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }




    public void byAndLogTransfer(SocketChannel sc){
        String id = getIdClienta(sc);

        serverLog.add(id + " logged out at " + LocalTime.now());

        if(!mapaLogow.containsKey(id)){
            mapaLogow.put(id, new ArrayList<>());
        }

        mapaLogow.get(id).add("logged out\n");
        mapaLogow.get(id).add("=== " + id + " log end ===\n");

        StringBuilder stringBuilder = new StringBuilder();

        for(String str : mapaLogow.get(id)){
            stringBuilder.append(str);
        }

        ByteBuffer buffer1 = ByteBuffer.wrap(stringBuilder.toString().getBytes());
        try {
            sc.write(buffer1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    public void date(String message, SocketChannel sc){
        try {
            String id = getIdClienta(sc);

            String from = message.split(" ")[0];
            String to = message.split(" ")[1];

            to = to.replaceAll("\\n$", "");

            String str = Time.passed(from, to);

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Request: " + message+"\nResult:\n" + str + '\n');


            ByteBuffer buffer1 = ByteBuffer.wrap(str.getBytes());

            sc.write(buffer1);

            serverLog.add(id + " request at " + LocalTime.now() + ": \"" + message + '\"');

            mapaLogow.get(id).add(stringBuilder.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    public String getIdClienta(SocketChannel socketChannel){

        InetSocketAddress inetSocketAddress = null;
        try {
            inetSocketAddress = (InetSocketAddress) socketChannel.getRemoteAddress();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String host = inetSocketAddress.getHostName();
        String port = String.valueOf(inetSocketAddress.getPort());

        String id = "";

        for(String[] str : weryfikacjaClientow){
            if(str[0].equals(host) && str[1].equals(port)){
                id = str[2];
            }
        }

        return id;
    }
}