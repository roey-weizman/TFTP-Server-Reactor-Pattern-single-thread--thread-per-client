package bgu.spl171.net.impl.echo;

    import java.io.BufferedReader;
    import java.io.BufferedWriter;
    import java.io.IOException;
    import java.io.InputStreamReader;
    import java.io.OutputStreamWriter;
    import java.net.ServerSocket;
    import java.net.Socket;
import java.nio.channels.ShutdownChannelGroupException;
import java.util.logging.Level;
    import java.util.logging.Logger;
     
    public class serverdemo implements Runnable {
     
        private int port;
     
        public serverdemo(int port) {
            this.port = port;
        }
     
        public void run() {
            try (ServerSocket socket = new ServerSocket(port)) {
                while (true) {
                    // accept() blocks until a client connects to us
                    // It returns a socket connected to the client.
                    final Socket client = socket.accept();
                    new Thread(() -> {
                        try (BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()))) {
     
                            String line;
                            while ((line = in.readLine()) != null) {
                                System.out.println(line);
                                out.write(line+"meir");
                                out.newLine();
                                out.flush();
                            }
     
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        } finally {
                            try {
                                client.close();
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }).start();
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            
        }
        
     
        public static void main(String[] args) {
            new serverdemo(7777).run();
        }
    }
