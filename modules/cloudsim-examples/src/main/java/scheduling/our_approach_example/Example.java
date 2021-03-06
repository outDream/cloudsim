package scheduling.our_approach_example;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.power.PowerHost;

import scheduling.our_approach.AllocationMapping;
import scheduling.our_approach.OverallExchangeProcessor;
import scheduling.our_approach.utility.SchedulingConstants;
import scheduling.our_approach.utility.SchedulingHelper;

public class Example {
	public static void main(String[] args) throws IOException {
		int num_all_cloudlets = 5;
		
		SchedulingHelper.initOutput(SchedulingConstants.our_log_file+num_all_cloudlets, SchedulingConstants.our_result_file+num_all_cloudlets, SchedulingConstants.our_result_temp_file+num_all_cloudlets);
		OutputStream result_output = SchedulingHelper.getOutputStream(SchedulingConstants.our_result_file+num_all_cloudlets);
		OutputStream mediate_result_output = SchedulingHelper.getOutputStream(SchedulingConstants.our_result_temp_file+num_all_cloudlets);
		OutputStream originOutput = Log.getOutput();
		
		AllocationMapping mapping = new AllocationMapping(num_all_cloudlets);
		
		List<Vm> vmList = new ArrayList<Vm>();
		List<Cloudlet> cloudletList = new ArrayList<Cloudlet>();
		List<PowerHost> hostList = new ArrayList<PowerHost>();
		SchedulingHelper.simulation(cloudletList, hostList, vmList, mapping, SchedulingConstants.our_initial_vmAllocationPolicy);
		SchedulingHelper.outputResultToResultFile("Initial", originOutput, result_output, mapping, 1);
		
		OverallExchangeProcessor[] processors = null;
		int numProcessor = SchedulingConstants.NUMBER_OF_PROCESSORS;
		List<Cloudlet> tempCloudlets = null;
		int iCnt = 1;
		while(iCnt <= SchedulingConstants.NUMBER_OF_ITERATIONS) {
			SchedulingHelper.outputToResultFile(originOutput, result_output, "$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$"+iCnt+"th OVERALL　ITERATION$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
			//some processor along to search more search space
			processors = new OverallExchangeProcessor[numProcessor];
			for(int i=0; i<numProcessor; i++) {
				processors[i] = new OverallExchangeProcessor(i+1, mapping, hostList, cloudletList, vmList, result_output, mediate_result_output, originOutput);
				processors[i].run();
			}
			
			//select the max fitness processor
			double max_fitness = Double.MIN_VALUE;
			int max_num = -1;
			for(int i=0; i<numProcessor; i++) {
				if(max_fitness<processors[i].getLocalMapping().getFitness()){
					max_fitness = processors[i].getLocalMapping().getFitness();
					max_num = i;
				}
			}
			//update mapping
			int hostId;
			for(int i=0; i<mapping.getNumVms(); i++) {
				hostId = processors[max_num].getLocalMapping().getHostOfVm(i);
				mapping.setHostOfVm(i, hostId);
			}
			
			tempCloudlets=SchedulingHelper.getCopyOfCloudlets(cloudletList);
			SchedulingHelper.simulation(tempCloudlets, hostList, vmList, mapping, SchedulingConstants.our_normal_vmAllocationPolicy);
			SchedulingHelper.outputResultToResultFile(iCnt++ +"th iteration ", originOutput, result_output, mapping, 1);
		}
		
		tempCloudlets=SchedulingHelper.getCopyOfCloudlets(cloudletList);
		SchedulingHelper.simulation(tempCloudlets, hostList, vmList, mapping, SchedulingConstants.our_normal_vmAllocationPolicy);
		SchedulingHelper.outputResultToResultFile("Final", originOutput, result_output, mapping, 1);
	}
}
