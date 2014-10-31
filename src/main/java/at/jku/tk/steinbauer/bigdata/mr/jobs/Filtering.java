package at.jku.tk.steinbauer.bigdata.mr.jobs;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.reduce.LongSumReducer;

import at.jku.tk.steinbauer.bigdata.mr.TrafficAnalysis;

/**
 * Performs the filtering phase of the data analysis
 * 
 */
public class Filtering {

	/**
	 * Mapper class
	 */
	public static class IntermediateMapper extends Mapper<Object, Text, Text, LongWritable> {

		@Override
		protected void map(Object rowKey, Text line, Context context) throws IOException, InterruptedException {
			String[] split = line.toString().split("\\t");
			Text key = new Text(split[0]);
			LongWritable value = new LongWritable(Long.parseLong(split[1]));
			if(value.get() >= 500) {
				context.write(key, value);
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
		job.setMapperClass(IntermediateMapper.class);
		job.setCombinerClass(LongSumReducer.class);
		job.setNumReduceTasks(0);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(LongWritable.class);
		FileInputFormat.addInputPath(job, inputFile);
		FileOutputFormat.setOutputPath(job, outputFile);
		return job.waitForCompletion(true);
	}
	
}
