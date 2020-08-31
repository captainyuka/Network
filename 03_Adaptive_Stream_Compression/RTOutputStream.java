import java.io.IOException;
import java.io.FilterOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.Arrays;


public class RTOutputStream extends FilterOutputStream{
  DataMonitor monitor;
  
  RTOutputStream(OutputStream outStream){
     super(outStream);
     monitor = new DataMonitor();
  }

  public void write(int b) throws IOException{
    Date start; 

    if(this.averageRate() < monitor.getMaxAvgRate()){
      byte[] data = toByteArray(b);
      byte[] compressedData = GZIPCompression.compress(data);
      int compressedB = fromByteArray(compressedData);
      start = new Date();
      super.write(compressedB);
    }else{
      start = new Date();
      super.write(b);
    }
    monitor.addSample(1, start, new Date());
  }

  public void write(byte[] data) throws IOException{
    Date start;

    if(this.averageRate() < monitor.getMaxAvgRate()){
      byte[] compressedData = GZIPCompression.compress(data);
      start = new Date();
      super.write(compressedData);
    }
    else{
      start = new Date();
      super.write(data);
    }
    monitor.addSample(data.length, start, new Date());
  }

  public void write(byte[] data, int off, int len) throws IOException{
    Date start;
    if(this.averageRate() < monitor.getMaxAvgRate()){
      data = Arrays.copyOfRange(data, off,off+len+1);
      byte[] compressedData = GZIPCompression.compress(data);
      start = new Date();
      super.write(compressedData);
    }
    else{
      start = new Date();
      super.write(data, off, len);
    }
    monitor.addSample(data.length, start, new Date());
  }

  public float averageRate(){
    return monitor.getAverageRate();
  }

  public float lastRate(){
    return monitor.getLastRate();
  }

  byte[] toByteArray(int value) {
     return  ByteBuffer.allocate(4).putInt(value).array();
  }

  int fromByteArray(byte[] bytes) {
      return ByteBuffer.wrap(bytes).getInt();
  }

}
