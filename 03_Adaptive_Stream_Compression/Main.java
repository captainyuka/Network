import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class Main {
    public static void main(String[] args){
        try(Socket socket = new Socket("time.nist.gov", 13)){
            socket.setSoTimeout(15000);
            InputStream in = socket.getInputStream();
            RTInputStream monitoredIn = new RTInputStream(in);
            InputStreamReader reader = new InputStreamReader(monitoredIn, "ASCII");
            StringBuilder time = new StringBuilder();
            for(int c = reader.read(); c != -1; c = reader.read()){
                time.append((char)c);
            }
            System.out.println(time);
            System.out.printf("Average Rate %f", monitoredIn.averageRate());
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
