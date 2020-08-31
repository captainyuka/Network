import java.net.*;
import java.io.*;
import java.util.concurrent.*;

public class Main {
  public static void main(String[] args) {
    EchoServer.main(args);
	//LocalPortScanner();
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
}

class EchoServer{
  public final static int PORT = 38480;

  public static void main(String[] args){
    ExecutorService pool = Executors.newFixedThreadPool(500);
    try(ServerSocket server = new ServerSocket(PORT)){
      while(true){
        try{
          Socket connection = server.accept();
          Callable<Void> task = new EchoTask(connection);
          pool.submit(task);
        }catch(IOException e){}
      }
    }catch(IOException e){
      e.printStackTrace();
    }
  }

  private static class EchoTask implements Callable<Void>{
    private Socket connection;

    EchoTask(Socket connection){
       this.connection = connection;
    }
    
    @Override
    public Void call()throws IOException{
      try{
        InputStream in = new BufferedInputStream(this.connection.getInputStream());
        OutputStream out = connection.getOutputStream();
        int c;
        while( (c=in.read()) != -1 ){
          out.write(c);
          out.flush();
        }
      }catch(IOException e){
        System.out.println(e);
      }finally{
        connection.close();
      }
      return null;
    }

  }


}

