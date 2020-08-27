import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

public class ContentConsumer{
  protected ContentProducer src  = null;
  protected ContentConsumer dest = null;
  protected DataMonitor monitor   = new DataMonitor();

  public ContentConsumer(ContentProducer src){
    this.src = src;
  }

  public ContentConsumer(ContentConsumer dest){
    this.dest = dest;
  }

  public void setSrc(ContentProducer src){
    this.src = src;
  }

  public void setDest(ContentConsumer dest){
    this.dest = dest;
  }

  public boolean ConsumeAll(){
    boolean success = false;
    if( this.src != null ){
      byte[] data = src.produce(0);
      while(data != null){
        success = consume(data);
        data = src.produce(0);
      }
    }

    return success;
  }

  // Consume a chuck of data
  public boolean consume(byte[] data){
    // Log the start of the consumption cycle
    Date start = new Date();

    boolean success;

    success = preConsume(data);
    if(success)
      success = doConsume(data);
    if(success)
      success = postConsume(data);

    // Mark the end of our consumption cycle
    monitor.addSample(data.length, start, new Date());

    // Pass the data on to the next consumer in the chain
    // if present.
    if( dest != null )
      dest.consume(data);

    return success;
  }

  protected boolean preConsume(byte[] data){
    return true;
  }

  // Default consumption method
  protected boolean doConsume(byte[] data){
    return true;
  }

  // Default post-consume method:log the data consumption
  // size and finish time with monitor
  protected boolean postConsume(byte[] data){
    return true;
  }
}
