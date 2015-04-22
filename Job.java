package org.myorg;
 
import java.io.IOException;
import java.util.*;
 
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.util.*;
 
public class Job {
 
  public static class Map extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text> {
    private Text word = new Text();
    private Text word2 = new Text();
 
    public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
      String line = value.toString();
      StringTokenizer tokenizer = new StringTokenizer(line);
      word.set(tokenizer.nextToken());
      word2.set(tokenizer.nextToken());
      output.collect(word, word2);
    }
  }
 
  public static class Reduce extends MapReduceBase implements Reducer<Text, Text, Text, Text> {  
      private Text z = new Text();
      private Text edge = new Text();
      public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
      	z.set("none");
	ArrayList<Text> neighbors = new ArrayList<Text>();
      	while (values.hasNext()) {
	    Text y = new Text(values.next());
	    edge.set(key.toString()+" "+y.toString());
	    neighbors.add(y);
	    output.collect(edge, z);
	}

	for(Text m : neighbors) {
	    for(Text n : neighbors) {
		if (Integer.parseInt(m.toString()) < Integer.parseInt(n.toString())) {
		    edge.set(m.toString()+" "+n.toString());
		    output.collect(edge, key);
		}
	    }
	}
	
    }
  }
 
  public static void main(String[] args) throws Exception {
    JobConf conf = new JobConf(Job.class);
    conf.setJobName("job");
 
    conf.setOutputKeyClass(Text.class);
    conf.setOutputValueClass(Text.class);
 
    conf.setMapperClass(Map.class);
//    conf.setCombinerClass(Reduce.class);
    conf.setReducerClass(Reduce.class);
 
    conf.setInputFormat(TextInputFormat.class);
    conf.setOutputFormat(TextOutputFormat.class);
 
    FileInputFormat.setInputPaths(conf, new Path(args[0]));
    FileOutputFormat.setOutputPath(conf, new Path(args[1]));
 
    JobClient.runJob(conf);
  }
}
