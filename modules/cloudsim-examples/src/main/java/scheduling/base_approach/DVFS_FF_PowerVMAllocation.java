package scheduling.base_approach;

import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.power.PowerHost;
import org.cloudbus.cloudsim.power.PowerVmAllocationPolicyAbstract;
import org.cloudbus.cloudsim.power.models.PowerModelSpecPower_BAZAR_ME;

public class DVFS_FF_PowerVMAllocation extends PowerVmAllocationPolicyAbstract{

	public DVFS_FF_PowerVMAllocation(List<? extends Host> list) {
		super(list);
		// TODO Auto-generated constructor stub
	}

	public PowerHost findHostForVm(Vm vm) {
		PowerHost allocatedHost = null;
		for (PowerHost host : this.<PowerHost> getHostList()) {
            double maxAvailableMips = host.getTotalMaxMips()-(host.getTotalMips()-host.getAvailableMips());
            if(host.getVmScheduler().getMaxPeCapacity()>=vm.getCurrentRequestedMaxMips()
            		&& maxAvailableMips>=vm.getCurrentRequestedTotalMips()
            		&& host.getRamProvisioner().isSuitableForVm(vm, vm.getCurrentRequestedRam()) 
    				&& host.getBwProvisioner().isSuitableForVm(vm, vm.getCurrentRequestedBw())){
            	allocatedHost = host;
            	break;
            }
        }
		
		if(allocatedHost!=null){
        	if(!allocatedHost.isSuitableForVm(vm))
        		allocatedHost.MakeSuitableHostForVm(vm);
			return allocatedHost;
        }
        
		return super.findHostForVm(vm);
	}
	
	protected double getPowerBeforeAllocation(PowerHost host){
    	double hostUtilizationMips = getUtilizationOfCpuMips(host);
    	double pePotentialUtilization = hostUtilizationMips / host.getTotalMaxMips();
    	double power = 0;
    	try {
    		power = ((PowerModelSpecPower_BAZAR_ME)host.getPowerModel()).getPower(pePotentialUtilization, 4);
    	} catch(Exception e) {
    		e.printStackTrace();
            System.exit(0);
    	}
    	return power;
    }
    
    protected double getPowerAfterAllocation(PowerHost host, Vm vm) {
        double power = 0;
        try {
            power = ((PowerModelSpecPower_BAZAR_ME)host.getPowerModel()).getPower(getMaxUtilizationAfterAllocation(host, vm), 4);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
        return power;
    }

    protected double getMaxUtilizationAfterAllocation(PowerHost host, Vm vm) {
        double requestedTotalMips = vm.getCurrentRequestedTotalMips();
        double hostUtilizationMips = getUtilizationOfCpuMips(host);
        double hostPotentialUtilizationMips = hostUtilizationMips + requestedTotalMips;
        double pePotentialUtilization = hostPotentialUtilizationMips / host.getTotalMaxMips();
        return pePotentialUtilization;
    }

    protected double getUtilizationOfCpuMips(PowerHost host) {
        double hostUtilizationMips = 0;
        for (Vm vm2 : host.getVmList()) {
            hostUtilizationMips += host.getTotalAllocatedMipsForVm(vm2);
        }
        return hostUtilizationMips;
    }
	
	@Override
	public List<Map<String, Object>> optimizeAllocation(List<? extends Vm> vmList) {
		return null;
	}
}
