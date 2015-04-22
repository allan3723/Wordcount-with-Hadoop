package org.myorg;
 
import java.io.IOException;
import java.util.*;
 
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.util.*;
 
public class Job2 {
 
  public static class Map extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text> {
    private Text word = new Text();
    private Text word2 = new Text();
 
    public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
      String line = value.toString();
      StringTokenizer tokenizer = new StringTokenizer(line, "\t");
      if (tokenizer.hasMoreTokens()) {
        word.set(tokenizer.nextToken());
        word2.set(tokenizer.nextToken());
        output.collect(word, word2);
      }
    }
  }
 
  public static class Reduce extends MapReduceBase implements Reducer<Text, Text, Text, Text> { 
      public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
	ArrayList<String> nodes = new ArrayList<String>();
	while (values.hasNext()) {
	    String val = new String(values.next().toString());
	    nodes.add(val);
	}

	if (nodes.remove("none")) {
	    for (String i:nodes) {
	        output.collect(key, new Text(i));
	    }
	}
    }
  }
 
  public static void main(String[] args) throws Exception {
    JobConf conf = new JobConf(Job2.class);
    conf.setJobName("job2");
 
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
