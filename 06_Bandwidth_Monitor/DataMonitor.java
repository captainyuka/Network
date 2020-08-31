import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class DataMonitor{
  protected List<DataSample> samples;
  protected Date epoch;

  public DataMonitor(){
    samples = new LinkedList<>();
    epoch = new Date(); 
  }

  public void addSample(long byteCount, Date start, Date end){
    this.samples.add(new DataSample(byteCount, start, end));
  }

  /*
   * Get the data rate of a given sample.
   */
  public float getRateFor(int sidx){
    float rate = 0.0f;
    int scnt = this.samples.size();

    if(scnt > sidx && sidx >= 0){
      DataSample s = samples.get(sidx);
      Date start = s.start;
      Date end = s.end;

      if(start == null && sidx >1){
        DataSample prev = samples.get(sidx - 1);
        start = prev.end;
      }

      if(start != null && end != null){
        long msec = end.getTime() - start.getTime();
        rate = 1000 * (float)s.byteCount / (float)msec;
      }
    }

    return rate;
  }

  // Get the rate of the last sample
  public float getLastRate(){
    return getRateFor( samples.size() - 1);
  }

  // Get the average rate over all samples.
  public float getAverageRate(){
    long msCount = 0;
    long byteCount = 0;
    Date start;
    Date finish;
    int scnt = samples.size();

    for(int i = 0; i < scnt; i++){
      DataSample ds = samples.get(i);
      
      if(ds.start != null)
        start = ds.start;
      else if( i > 0 )
        start = ds.end;
      else
        start = epoch;

      if(ds.end != null)
        finish = ds.end;
      else if(i < scnt - 1)
        finish = ds.start;
      else
        finish = new Date();

      // Only include this sample if we could
      // figure out a start and finish time for it.
      if ( start != null && finish != null ){
        byteCount += ds.byteCount;
        msCount += finish.getTime() - start.getTime();
      }

    }

    float rate = -1.0f;

    if(msCount > 0){
      rate = 1000 * byteCount / (float)msCount;
    }

    return rate;
  }
}
