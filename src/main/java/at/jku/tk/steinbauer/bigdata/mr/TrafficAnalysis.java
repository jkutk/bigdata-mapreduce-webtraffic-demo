package at.jku.tk.steinbauer.bigdata.mr;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import at.jku.tk.steinbauer.bigdata.mr.jobs.Filtering;
import at.jku.tk.steinbauer.bigdata.mr.jobs.Grouping;
import at.jku.tk.steinbauer.bigdata.mr.jobs.Sorting;

/**
 * Traffic analysis example for the Big Data class 
 * 
 * @author matthias
 */
public class TrafficAnalysis {
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		Configuration configuration = new Configuration();
		
		if(args.length < 2) {
			System.err.println("You need to specify input and output path");
			System.exit(-1);
		}else{
			long jobUid = System.currentTimeMillis();
			Path inputFile = new Path(args[0]);
			
			String parentPathName = "/tmp/ij_" + jobUid;
			Path intermediate = new Path(parentPathName);
			
			Path intermediateGrouped = new Path(parentPathName + "/grouped");
			Path intermediateFiltered = new Path(parentPathName + "/filtered");
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss-S");
			Path outputFile = new Path(args[1] + "/" + sdf.format(new Date()));
			
			Grouping.runJob(configuration, inputFile, intermediateGrouped);
			Filtering.runJob(configuration, intermediateGrouped, intermediateFiltered);
			// sorting is having a huge memory penalty -> make sure it is done last
			Sorting.runJob(configuration, intermediateFiltered, outputFile);
			
			FileSystem fs = FileSystem.get(configuration);
			fs.delete(intermediate, true);
		}
	}

}
