import java.io.IOException;
import java.io.FilterOutputStream;
import java.io.OutputStream;
import java.util.Date;


public class RTOutputStream extends FilterOutputStream{
  DataMonitor monitor;
  
  RTOutputStream(OutputStream outStream){
     super(outStream);
     monitor = new DataMonitor();
  }

  public void write(int b) throws IOException{
    Date start = new Date(); 
    super.write(b);
    monitor.addSample(1, start, new Date());
  }

  public void write(byte[] data) throws IOException{
    Date start = new Date();
    super.write(data);
    monitor.addSample(data.length, start, new Date());
  }

  public void write(byte[] data, int off, int len) throws IOException{
    Date start = new Date();
    super.write(data, off, len);
    monitor.addSample(data.length, start, new Date());
  }

  public float averageRate(){
    return monitor.getAverageRate();
  }

  public float lastRate(){
    return monitor.getLastRate();
  }



}
