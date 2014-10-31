package at.jku.tk.steinbauer.bigdata.mr.jobs;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.reduce.LongSumReducer;

import at.jku.tk.steinbauer.bigdata.mr.TrafficAnalysis;
import at.jku.tk.steinbauer.bigdata.mr.WebTrafficElement;

import com.google.gson.Gson;

/**
 * Performs the grouping phase of the data analysis
 * 
 * @author matthias
 */
public class Grouping {

	/**
	 * Mapper class
	 */
	public static class JsonTrafficMapper extends Mapper<Object, Text, Text, LongWritable> {
		
		private Gson gson = new Gson();
		
		@Override
		protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			String str = value.toString().trim();
			if(str.startsWith("{") && str.endsWith("}")) {
				WebTrafficElement elem = gson.fromJson(str, WebTrafficElement.class);
				String from = elem.getFrom().trim().length() > 0 ? elem.getFrom().trim() : "undefined";
				context.write(new Text(from), new LongWritable(1));
			}
		}
	}
	
	/**
	 * Reducer class // could also be done by IntSumReducer
	 */
	public static class TrafficReducer extends Reducer<Text, LongWritable, Text, LongWritable> {
		
		@Override
		protected void reduce(Text key, Iterable<LongWritable> values, Context context) throws IOException, InterruptedException {
			int sum = 0;
			for(LongWritable cur : values) {
				sum += cur.get();
			}
			context.write(key, new LongWritable(sum));
		}
	}
	
	/**
	 * Run this job
	 * 
	 * @param configuration
	 * @param inputFile
	 * @param outputFile
	 * @return
	 * @throws IOException 
	 * @throws InterruptedException 
	 * @throws ClassNotFoundException 
	 */
	public static boolean runJob(Configuration configuration, Path inputFile, Path outputFile) throws IOException, ClassNotFoundException, InterruptedException {
		Job job = Job.getInstance(configuration);
		job.setJarByClass(TrafficAnalysis.class);
		job.setMapperClass(JsonTrafficMapper.class);
		job.setCombinerClass(LongSumReducer.class);
		job.setReducerClass(TrafficReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(LongWritable.class);
		FileInputFormat.addInputPath(job, inputFile);
		FileOutputFormat.setOutputPath(job, outputFile);
		return job.waitForCompletion(true);
	}
}

