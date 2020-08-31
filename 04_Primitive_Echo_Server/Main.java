import java.net.*;
import java.io.*;

class Main {
  
  public static void main(String[] args) throws IOException{
    RunEchoServer();
  }

  public static void LocalPortScanner(){
    for(int port = 1; port <= 65535; port++){
      try{
        ServerSocket server = new ServerSocket(port);
        System.out.println("The Following Port is Free:" + port + ".");
      }catch(IOException e){
        System.out.print(".");
      }
    }
  }

  public static void RunEchoServer() throws IOException{
    GreetClient client = new GreetClient();
    client.startConnection("127.0.0.1", 64537);
    String response = client.sendMessage("hello server");
    System.out.println(response);
  }
  
}

class EchoServer{
  private ServerSocket serverSocket;
  private Socket clientSocket;
  private PrintWriter out;
  private BufferedReader in;

  public void start(int port) throws IOException{
    serverSocket = new ServerSocket(port);
    clientSocket = serverSocket.accept();
    out = new PrintWriter(clientSocket.getOutputStream(), true);
    in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    String inputLine;
    while ((inputLine = in.readLine()) != null) {
      if (".".equals(inputLine)) {
        out.println("good bye");
        break;
      }
      out.println(inputLine);
    }
  }
}

class GreetClient {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
 
    public void startConnection(String ip, int port) throws IOException{
        clientSocket = new Socket(ip, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }
 
    public String sendMessage(String msg) throws IOException {
        out.println(msg);
        String resp = in.readLine();
        return resp;
    }
 
    public void stopConnection() throws IOException{
        in.close();
        out.close();
        clientSocket.close();
    }
}
