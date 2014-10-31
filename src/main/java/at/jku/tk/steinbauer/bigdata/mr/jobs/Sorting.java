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

import at.jku.tk.steinbauer.bigdata.mr.TrafficAnalysis;

/**
 * Performs the sorting phase of the algorithm
 * 
 * @author matthias
 */
public class Sorting {

	/**
	 * Just swaps key and value ... the framework will sort data by key 
	 * 
	 * @author matthias
	 */
	public static class KeyValueSwap extends Mapper<Object, Text, LongWritable, Text> {

		@Override
		protected void map(Object rowKey, Text line, Context context) throws IOException, InterruptedException {
			String[] split = line.toString().split("\\t");
			Text value = new Text(split[0]);
			LongWritable key = new LongWritable(Long.parseLong(split[1]));
			context.write(key, value);
		}
		
	}
	
	/**
	 * Just output what we got, this is just a trick to get the sorter running
	 * 
	 * @author matthias
	 */
	public static class IdentityReducer extends Reducer<LongWritable, Text, LongWritable, Text> {

		@Override
		protected void reduce(LongWritable key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
			for(Text t:values) {
				context.write(key, t);
			}
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
		job.setMapperClass(KeyValueSwap.class);
		job.setReducerClass(IdentityReducer.class);
		job.setOutputKeyClass(LongWritable.class);
		job.setOutputValueClass(Text.class);
		job.setSortComparatorClass(LongWritable.DecreasingComparator.class);
		FileInputFormat.addInputPath(job, inputFile);
		FileOutputFormat.setOutputPath(job, outputFile);
		return job.waitForCompletion(true);
	}
	
}
