package scheduling.our_approach.utility;

import org.cloudbus.cloudsim.power.models.PowerModel;
import org.cloudbus.cloudsim.power.models.PowerModelSpecPowerHpProLiantMl110G3PentiumD930;
import org.cloudbus.cloudsim.power.models.PowerModelSpecPowerHpProLiantMl110G4Xeon3040;
import org.cloudbus.cloudsim.power.models.PowerModelSpecPowerHpProLiantMl110G5Xeon3075;
import org.cloudbus.cloudsim.power.models.PowerModelSpecPowerIbmX3250XeonX3470;
import org.cloudbus.cloudsim.power.models.PowerModelSpecPowerIbmX3550XeonX5670;
import org.cloudbus.cloudsim.power.models.PowerModelSpecPowerIbmX3550XeonX5675;

public class SchedulingConstants {
	public final static boolean ENABLE_OUTPUT = true;
	public final static boolean OUTPUT_TO_FILE = false;
	public final static boolean OUTPUT_CSV    = false;
	public final static String OutputFolder = "output";
	
	public final static int RANDOM_SEED = 50; 
	
	public final static double SCHEDULING_INTERVAL = 1;
	public final static double SIMULATION_LIMIT = 24 * 60 * 60 * 2;
	
	public final static int CLOUDLET_LENGTH	= 200* (int) SIMULATION_LIMIT;
	public final static int CLOUDLET_PES = 1;
	
	public final static int NUMBER_OF_CASE = 1;
	public final static int NUMBER_OF_CLOUDLETS = 150;//10
	public final static int NUMBER_OF_HOSTS = 30;//50
	
	//1000  2000
	public final static String DISTRIBUTION="Gaussion"; 
	//Uniformly
	//public final static int VM_MIPS_MAX = 2000;
	//public final static int VM_MIPS_MIN = 500;
	//public final static int CLOUDLET_START_TIME_MAX=3600*24;  //3600*0-24
	//public final static int CLOUDLET_START_TIME_MIN=3600*0;   
	public final static int CLOUDLET_EXECUTION_TIME_MAX = 3600*16;  //3600*14-16
	public final static int CLOUDLET_EXECUTION_TIME_MIN = 3600*14;
	//Gaussion
	public final static int VM_MIPS_MEAN = 2000; //3000
	public final static int VM_MIPS_DEV = 1000;   //100 500
	//public final static int CLOUDLET_EXECUTION_TIME_MEAN = 3600*30;  //3600*14
	//public final static int CLOUDLET_EXECUTION_TIME_DEV = 3600*2;    //3600*2
	
	public final static int DefautFrequency = 5;       //FF 5;     Base 5;     Ours 1
	public final static boolean ENABLE_DVFS = false;   //FF false; Base false; Ours true
	public final static boolean ENABLE_ONOFF = true;
	
	//*****************************DVFS-XXXX-EX************************************//
	//5 20 5
	//5 10 10
	public final static int NUMBER_OF_ITERATIONS = 5;
	public final static int NUMBER_OF_PROCESSORS = 10;
	public final static int NUMBER_OF_ITERATION_PER_PROCESSOR = 10;
	
	public final static String our_normal_vmAllocationPolicy = "normal";
	public final static String our_initial_vmAllocationPolicy = "init";
	public final static String our_initial_vmAllocationPolicy_method = "FF";   //FF or MBFD
	
	public final static String log_str=VM_MIPS_MEAN+"_"+VM_MIPS_DEV;
	
	public final static String our_log_file = "log/DVFS_"+our_initial_vmAllocationPolicy_method+"_EX_"+log_str+"_Log";
	public final static String our_result_file = "result/DVFS_"+our_initial_vmAllocationPolicy_method+"_EX_"+log_str+"_Result";
	public final static String our_result_temp_file = "result/DVFS_"+our_initial_vmAllocationPolicy_method+"_EX_"+log_str+"_Temp_Result";
	
	//*****************************No DVFS FF************************************//
	public final static String ff_vmAllocationPolicy = "ff";
	public final static String ff_log_File = "log/Base_FF_f"+SchedulingConstants.DefautFrequency+"_"+log_str+"_Log";
	public final static String ff_result_File = "result/Base_FF_f"+SchedulingConstants.DefautFrequency+"_"+log_str;
	
	//*****************************DVFS FF************************************//
	public final static String dvfs_ff_vmAllocationPolicy = "dvfs_ff";
	public final static String dvfs_ff_log_File = "log/DVFS_FF_f"+SchedulingConstants.DefautFrequency+"_"+log_str+"_Log";
	public final static String dvfs_ff_result_File = "result/DVFS_FF_f"+SchedulingConstants.DefautFrequency+"_"+log_str;
	
	//*****************************No DVFS MBFD************************************//
	public final static String base_vmAllocationPolicy = "mbfd";
	public final static String base_log_File = "log/Base_MBFD_f"+SchedulingConstants.DefautFrequency+"_"+log_str+"_Log";
	public final static String base_result_File = "result/Base_MBFD_f"+SchedulingConstants.DefautFrequency+"_"+log_str;
	
	//*****************************DVFS MBFD************************************//
	public final static String dvfs_mbfd_vmAllocationPolicy = "dvfs_mbfd";
	public final static String dvfs_mbfd_log_File = "log/DVFS_MBFD_f"+SchedulingConstants.DefautFrequency+"_"+log_str+"_Log";
	public final static String dvfs_mbfd_result_File = "result/DVFS_MBFD_f"+SchedulingConstants.DefautFrequency+"_"+log_str;
	
	//*****************************Minimum Used Host(DVFS)************************************//
	public final static String mu_vmAllocationPolicy = "mu";
	public final static String mu_log_File = "log/Base_MU_f"+SchedulingConstants.DefautFrequency+"_"+log_str+"_Log";
	public final static String mu_result_File = "result/Base_MU_f"+SchedulingConstants.DefautFrequency+"_"+log_str;
	
	//*****************************Minimum Used Host(DVFS)************************************//
	public final static String swpmm_vmAllocationPolicy = "swpmm";
	public final static String swpmm_log_File = "log/Base_SWPMM_f"+SchedulingConstants.DefautFrequency+"_"+log_str+"_Log";
	public final static String swpmm_result_File = "result/Base_SWPMM_f"+SchedulingConstants.DefautFrequency+"_"+log_str;
	
	public final static String pthread_code="pthread_energy";
	public final static String pthread_code_our="pthread_energy_our";
	public final static String mpi_code="mpi_energy";
	public final static String mpi_code_our="mpi_energy_our";
	
	
	
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
	public final static int[] HOST_MIPS	 = {2933*4, 2660*2};
	public final static int[] HOST_PES	 = { 1, 1 };
	public final static int[] HOST_RAM	 = {100000000, 100000000};//{ 4096, 4096 };
	public final static int HOST_BW		 = 100000000; // 1 Gbit/s
	public final static int HOST_STORAGE = 100000000; // 1 GB
	
	public final static PowerModel[] HOST_POWER = {
		new PowerModelSpecPowerIbmX3250XeonX3470(),
		new PowerModelSpecPowerHpProLiantMl110G5Xeon3075()//PowerModelSpecPowerHpProLiantMl110G3PentiumD930()//
	};
}