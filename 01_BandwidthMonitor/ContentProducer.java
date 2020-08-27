import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;


public class ContentProducer{
  protected ContentProducer src  = null;
  protected ContentConsumer dest = null;
  protected DataMonitor     monitor = new DataMonitor();


  public ContentProducer(ContentProducer src){
    this.src = src;
  }

  public ContentProducer(ContentConsumer dest){
    this.dest = dest;
  }

  public void setSrc(ContentProducer src){
    this.src = src;
  }

  public void setDest(ContentConsumer dest){
    this.dest = dest;
  }

  public boolean produceAll(){
    int productionCapacity = 44;
    boolean success = false;
    if( dest != null ){
      byte[] data = produce( productionCapacity );
      while( data != null ){
        success = dest.consume(data);
        if(success)
          data = produce( productionCapacity );
        else
          data = null;
      }
    }

    return success;
  }

  // Produce a chunk of data, within the given limit. 
  public byte[] produce(long limit){
    // Record the start time
    Date start = new Date();

    boolean success;
    byte[] data = null;
    sucess = preProduction(limit);
    if(success)
      data = doProduction(limit);
    
    if( success && data != null)
      success = postProduction(data, limit);
    
    // Record the data sample in our monitor.
    monitor.addSample(data.length, start, new Date());

    // Pass the data on to our destionation, if present
    if( data != null && dest != null )
      dest.consume(data);

    return data;
  }

  // Default preconsumption procedure. 
  protected boolean preProduce(long limit){
    return true;
  }

  // Default production procedure: ask for data from our source, 
  // if present, and pass along unmodified (e.g., a no-op)
  protected byte[] doProduction(long limit){
    byte[] data = null;
    if(src != null)
      data = src.produce(limit);
    
    return data;
  }

  // Default postconsumption procedure. 
  protected boolean postProduction(byte[] data, long limit){
    return true;
  }



}
