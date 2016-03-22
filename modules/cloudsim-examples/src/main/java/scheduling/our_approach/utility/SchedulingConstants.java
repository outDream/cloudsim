package scheduling.our_approach.utility;

public class SchedulingConstants {
	public final static boolean ENABLE_OUTPUT = true;
	public final static boolean OUTPUT_TO_FILE = true;
	public final static boolean OUTPUT_CSV    = false;
	public final static String OutputFolder = "output";
	
	public final static int RANDOM_SEED = 50; 
	
	public final static double SCHEDULING_INTERVAL = 1;
	public final static double SIMULATION_LIMIT = 24 * 60 * 60 * 2;
	
	public final static int CLOUDLET_LENGTH	= 200* (int) SIMULATION_LIMIT;
	public final static int CLOUDLET_PES = 1;
	
	public final static int NUMBER_OF_CASE = 20;
	public final static int NUMBER_OF_CLOUDLETS = 50;
	public final static int NUMBER_OF_HOSTS = 50;//50
	
	//1000  200
	public final static int VM_MIPS_MAX = 1500;
	public final static int VM_MIPS_MIN = 1000; 
	public final static int VM_MIPS_MEAN = 1000;
	public final static int VM_MIPS_DEV = 500;
	
	//14-16
	public final static int CLOUDLET_EXECUTION_TIME_MAX = 3600*16;
	public final static int CLOUDLET_EXECUTION_TIME_MIN = 3600*14;
	public final static int CLOUDLET_EXECUTION_TIME_MEAN = 3600*8;
	public final static int CLOUDLET_EXECUTION_TIME_DEV = 500;
	
	public final static int DefautFrequency = 1;     //FF 5;     Base 5;     Ours 1
	public final static boolean ENABLE_DVFS = true;  //FF false; Base false; Ours true
	public final static boolean ENABLE_ONOFF = true;
	
	//*****************************FF************************************//
	public final static String ff_vmAllocationPolicy = "ff";
	public final static String ff_log_File = "log/FF_f"+SchedulingConstants.DefautFrequency+"_Log";
	public final static String ff_result_File = "result/FF_f"+SchedulingConstants.DefautFrequency;
	
	//*****************************Base************************************//
	public final static String base_log_File = "log/Base_f"+SchedulingConstants.DefautFrequency+"_Log";
	public final static String base_result_File = "result/Base_f"+SchedulingConstants.DefautFrequency;
	public final static String base_vmAllocationPolicy = "base";
	
	//*****************************Our************************************//
	//5 20 5
	//5 10 10
	public final static int NUMBER_OF_ITERATIONS = 5;
	public final static int NUMBER_OF_PROCESSORS = 10;
	public final static int NUMBER_OF_ITERATION_PER_PROCESSOR = 10;
	
	public final static String our_log_file = "log/Our_Log";
	public final static String our_result_file = "result/Our_Result";
	public final static String our_result_temp_file = "result/Our_temp_Result";
	
	public final static String our_normal_vmAllocationPolicy = "normal";
	public final static String our_initial_vmAllocationPolicy = "init";
	
	/*
	 * VM instance types:
	 *   High-Memory Extra Large Instance: 3.25 EC2 Compute Units, 8.55 GB // too much MIPS
	 *   High-CPU Medium Instance: 2.5 EC2 Compute Units, 0.85 GB
	 *   Extra Large Instance: 2 EC2 Compute Units, 3.75 GB
	 *   Small Instance: 1 EC2 Compute Unit, 1.7 GB
	 *   Micro Instance: 0.5 EC2 Compute Unit, 0.633 GB
	 *   We decrease the memory size two times to enable oversubscription
	 */
	public final static int VM_TYPES	= 4;
	public final static int[] VM_MIPS	= { 2500, 2000, 1000, 500 };
	public final static int[] VM_PES	= { 1, 1, 1, 1 };
	public final static int[] VM_RAM	= {0,0,0,0};//{ 870,  1740, 1740, 613 };
	public final static int VM_BW		= 0;//100000; // 100 Mbit/s
	public final static int VM_SIZE		= 0;//2500; // 2.5 GB
	
	/*
	 * Host types:
	 *   HP ProLiant ML110 G4 (1 x [Xeon 3040 1860 MHz, 2 cores], 4GB)
	 *   HP ProLiant ML110 G5 (1 x [Xeon 3075 2660 MHz, 2 cores], 4GB)
	 *   We increase the memory size to enable over-subscription (x4)
	 */
	public final static int HOST_TYPES	 = 2;
	public final static int[] HOST_MIPS	 = { 1860, 2660 };
	public final static int[] HOST_PES	 = { 1, 1 };
	public final static int[] HOST_RAM	 = {100000000, 100000000};//{ 4096, 4096 };
	public final static int HOST_BW		 = 100000000; // 1 Gbit/s
	public final static int HOST_STORAGE = 100000000; // 1 GB
}