import java.io.InputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Date;

public class RTInputStream extends FilterInputStream{
  private DataMonitor monitor;

  RTInputStream(InputStream inStream){
    super(inStream);
    monitor = new DataMonitor();
  }

  public int read() throws IOException{
    Date start = new Date();
    int b = super.read();
    monitor.addSample(1, start, new Date());
    
    byte[] data = toByteArray(b);
    if(GZIPCompression.isCompressed(data)){
      String s = GZIPCompression.decompress(data);
      b = Integer.parseInt(s);  
    }
    
    return b;
  }

  public int read(byte data[]) throws IOException{
    Date start = new Date();
    int cnt = super.read(data);
    monitor.addSample(cnt, start, new Date());

    if(GZIPCompression.isCompressed(data)){
      String str = GZIPCompression.decompress(data);
      data = str.getBytes("UTF-8");
    }

    return cnt;
  }

  public int read(byte data[], int off, int len) throws IOException{
    Date start = new Date();
    int cnt = super.read(data, off, len);
    monitor.addSample(cnt, start, new Date());

    if(GZIPCompression.isCompressed(data)){
      String str = GZIPCompression.decompress(data);
      data = str.getBytes("UTF-8");
    }

    return cnt;
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
