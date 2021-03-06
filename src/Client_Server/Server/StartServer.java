package Client_Server.Server;

/**
 * Created by Benjamin Probst on 01.10.2017.
 * Diese Klasse dient zum starten des Servers als eigenen Thread. Konnte leider nicht anders gelöst werden
 **/

public class StartServer extends Thread {
    private Server server;

    public StartServer(){
    }

    public void run(){
        try {
            server = new Server();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start(){
        Thread thread = new Thread(this);
        thread.start();
    }

}
