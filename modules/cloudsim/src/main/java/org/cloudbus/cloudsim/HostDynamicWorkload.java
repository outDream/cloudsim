/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.lists.PeList;
import org.cloudbus.cloudsim.provisioners.BwProvisioner;
import org.cloudbus.cloudsim.provisioners.RamProvisioner;

/**
 * The class of a host supporting dynamic workloads and performance degradation.
 * 
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 2.0
 */
public class HostDynamicWorkload extends Host {

	/** The utilization mips. */
	private double utilizationMips;

	/** The previous utilization mips. */
	private double previousUtilizationMips;

	/** The state history. */
	private final List<HostStateHistoryEntry> stateHistory = new LinkedList<HostStateHistoryEntry>();

	/**
	 * Instantiates a new host.
	 * 
	 * @param id the id
	 * @param ramProvisioner the ram provisioner
	 * @param bwProvisioner the bw provisioner
	 * @param storage the storage
	 * @param peList the pe list
	 * @param vmScheduler the VM scheduler
	 */
	public HostDynamicWorkload(
			int id,
			RamProvisioner ramProvisioner,
			BwProvisioner bwProvisioner,
			long storage,
			List<? extends Pe> peList,
			VmScheduler vmScheduler) {
		super(id, ramProvisioner, bwProvisioner, storage, peList, vmScheduler);
		setUtilizationMips(0);
		setPreviousUtilizationMips(0);
	}

	/*
	 * (non-Javadoc)
	 * @see cloudsim.Host#updateVmsProcessing(double)
	 */
	@Override
	public double updateVmsProcessing(double currentTime) {
		double smallerTime = super.updateVmsProcessing(currentTime);
		setPreviousUtilizationMips(getUtilizationMips());
		setUtilizationMips(0);
		double hostTotalRequestedMips = 0;

		for (Vm vm : getVmList()) {
			getVmScheduler().deallocatePesForVm(vm);
		}

		for (Vm vm : getVmList()) {
			getVmScheduler().allocatePesForVm(vm, vm.getCurrentRequestedMips());
		}

		for (Vm vm : getVmList()) {
			double totalRequestedMips = vm.getCurrentRequestedTotalMips();
				if(totalRequestedMips > vm.getMips())
	                totalRequestedMips = vm.getMips();
			
			double totalAllocatedMips = getVmScheduler().getTotalAllocatedMipsForVm(vm);

			if (!Log.isDisabled()) {
				Log.formatLine(
						"%.2f: [Host #" + getId() + "] Total allocated MIPS for VM #" + vm.getId()
								+ " (Host #" + vm.getHost().getId()
								+ ") is %.2f, was requested %.2f out of total %.2f (%.2f%%)",
						CloudSim.clock(),
						totalAllocatedMips,
						totalRequestedMips,
						vm.getMips(),
						totalRequestedMips / vm.getMips() * 100);

				List<Pe> pes = getVmScheduler().getPesAllocatedForVM(vm);
				StringBuilder pesString = new StringBuilder();
				for (Pe pe : pes) {
					pesString.append(String.format(" PE #" + pe.getId() + ": %.2f.", pe.getPeProvisioner()
							.getTotalAllocatedMipsForVm(vm)));
				}
				Log.formatLine(
						"%.2f: [Host #" + getId() + "] MIPS for VM #" + vm.getId() + " by PEs ("
								+ getNumberOfPes() + " * " + getVmScheduler().getPeCapacity() + ")."
								+ pesString,
						CloudSim.clock());
			}

			if (getVmsMigratingIn().contains(vm)) {
				Log.formatLine("%.2f: [Host #" + getId() + "] VM #" + vm.getId()
						+ " is being migrated to Host #" + getId(), CloudSim.clock());
			} else {
				if (totalAllocatedMips + 0.1 < totalRequestedMips) {
					Log.formatLine("%.2f: [Host #" + getId() + "] Under allocated MIPS for VM #" + vm.getId()
							+ ": %.2f", CloudSim.clock(), totalRequestedMips - totalAllocatedMips);
				}

				vm.addStateHistoryEntry(
						currentTime,
						totalAllocatedMips,
						totalRequestedMips,
						(vm.isInMigration() && !getVmsMigratingIn().contains(vm)));

				if (vm.isInMigration()) {
					Log.formatLine(
							"%.2f: [Host #" + getId() + "] VM #" + vm.getId() + " is in migration",
							CloudSim.clock());
					totalAllocatedMips /= 0.9; // performance degradation due to migration - 10%
				}
			}

			setUtilizationMips(getUtilizationMips() + totalAllocatedMips);
			hostTotalRequestedMips += totalRequestedMips;
		}

		addStateHistoryEntry(
				currentTime,
				getUtilizationMips(),
				hostTotalRequestedMips,
				(getUtilizationMips() > 0));

		return smallerTime;
	}
	
		/**
		 * Check if DVFS is active on the Host. If yes, dvfs method is called.
		 */
		public void isDvfsActivatedOnHost() {
			// dvfs call
			if (isEnableDVFS())
				applyDvfsOnHost();
		}
		
		public void dvfs() {
			if(isEnableDVFS()){
				double usedMips = this.getTotalMips()-this.getAvailableMips();
				while(usedMips <= this.getTotalMips()){
					int[] frequencies = new int[getPeList().size()];
		    		double oldTotalMips = this.getTotalMips();
		    		Log.printLine("Host#"+this.getId()+" Before,Available Mips:"+this.getAvailableMips());
		    		for(int i=0; i<getPeList().size(); i++) {
		    			frequencies[i]=getPeList().get(i).getIndexFreq();
		    			Log.printLine(frequencies[i]+" ");
		    			getPeList().get(i).decreaseFrequency();
		    		}
		    		
		    		double totalMips = this.getTotalMips();
		    		double newAvailableMips = totalMips-oldTotalMips+this.getAvailableMips();
		    		Log.printLine("NewAvailableMips:"+newAvailableMips);
		    		if(usedMips <= this.getTotalMips() && oldTotalMips!=this.getTotalMips()) {
		    			this.getVmScheduler().setAvailableMips(newAvailableMips);
		    			Log.printLine("Host#"+this.getId()+" After,Available Mips:"+this.getAvailableMips());
		    		}else{
		    			if(usedMips>this.getTotalMips()){
		    				for(int i=0; i<getPeList().size(); i++) {
			    				getPeList().get(i).setFrequency(frequencies[i]);
			    			}
		    			}
		    			Log.printLine("Host#"+this.getId()+" Decreasing failed: now max avaiable mips="+(this.getTotalMaxMips()-this.getTotalMips()+this.getAvailableMips()));
		    			break;
		    		}
				}
			}
		}
	
		/**
		 * DVFS method
		 * 
		 * for each Pe , 'changeFrequency' methode is called
		 * then this method check if VM size has to be Decrease or Increase
		 * regarding the Pe.changeFrequency return value.
		 */
		private void applyDvfsOnHost() {
			Log.printLine("Host #"+this.getId()+" Applying DVFS");
			Log.printLine("Host #"+this.getId()+": Before DVFS, Available MIPS="+this.getAvailableMips());
			for (Pe pe : this.<Pe> getPeList()) {
				double utilPe = pe.getPeProvisioner().getUtilization() * 100;
				int cur_mips = pe.getMips();
				// System.out.println("PE " + pe.getId() + " Utilization == " + utilPe);
	
				int res = pe.changeFrequency();
				// System.out.println("PE " + pe.getId() +" New frequency =" + pe.getMips());
				double new_AvailableMips = getAvailableMips() + (pe.getMips() - cur_mips);
	
				/*
				 * it means that the CPU frequency change caused an overflow of Host* Capacity (Available Mips of Host < 0 ! ) 
				 * All VM of that Host will be reduce in order to fit them on the Host
				 */
				if (new_AvailableMips < 0)
					decreaseVmMips();
				else // TODO 这里原来没有else，似乎错了，我在这里加了一个else,使其成为分支选择结构
					setAvailableMips(new_AvailableMips);
				// System.out.println("Available mips  = " + getAvailableMips());
				// if the cpu frequency has been increased, we can regrow the VMs size.
				if (res == 1 || res == 2)
					regrowVmMips();
			}
			Log.printLine("Host #"+this.getId()+": After DVFS, Available MIPS="+this.getAvailableMips());
		}

	/**
	 * Gets the completed vms.
	 * 
	 * @return the completed vms
	 */
	public List<Vm> getCompletedVms() {
		List<Vm> vmsToRemove = new ArrayList<Vm>();
		for (Vm vm : getVmList()) {
			if (vm.isInMigration()) {
				continue;
			}
			if (vm.getCurrentRequestedTotalMips() == 0) {
				vmsToRemove.add(vm);
			}
		}
		return vmsToRemove;
	}

	/**
	 * Gets the max utilization among by all PEs.
	 * 
	 * @return the utilization
	 */
	public double getMaxUtilization() {
		return PeList.getMaxUtilization(getPeList());
	}

	/**
	 * Gets the max utilization among by all PEs allocated to the VM.
	 * 
	 * @param vm the vm
	 * @return the utilization
	 */
	public double getMaxUtilizationAmongVmsPes(Vm vm) {
		return PeList.getMaxUtilizationAmongVmsPes(getPeList(), vm);
	}

	/**
	 * Gets the utilization of memory.
	 * 
	 * @return the utilization of memory
	 */
	public double getUtilizationOfRam() {
		return getRamProvisioner().getUsedRam();
	}

	/**
	 * Gets the utilization of bw.
	 * 
	 * @return the utilization of bw
	 */
	public double getUtilizationOfBw() {
		return getBwProvisioner().getUsedBw();
	}

	/**
	 * Get current utilization of CPU in percentage.
	 * 
	 * @return current utilization of CPU in percents
	 */
	public double getUtilizationOfCpu() {
		double utilization = getUtilizationMips() / getTotalMips();
		if (utilization > 1 && utilization < 1.01) {
			utilization = 1;
		}
		return utilization;
	}

	/**
	 * Gets the previous utilization of CPU in percentage.
	 * 
	 * @return the previous utilization of cpu
	 */
	public double getPreviousUtilizationOfCpu() {
		double utilization = getPreviousUtilizationMips() / getTotalMips();
		if (utilization > 1 && utilization < 1.01) {
			utilization = 1;
		}
		return utilization;
	}

	/**
	 * Get current utilization of CPU in MIPS.
	 * 
	 * @return current utilization of CPU in MIPS
	 */
	public double getUtilizationOfCpuMips() {
		return getUtilizationMips();
	}

	/**
	 * Gets the utilization mips.
	 * 
	 * @return the utilization mips
	 */
	public double getUtilizationMips() {
		return utilizationMips;
	}

	/**
	 * Sets the utilization mips.
	 * 
	 * @param utilizationMips the new utilization mips
	 */
	protected void setUtilizationMips(double utilizationMips) {
		this.utilizationMips = utilizationMips;
	}

	/**
	 * Gets the previous utilization mips.
	 * 
	 * @return the previous utilization mips
	 */
	public double getPreviousUtilizationMips() {
		return previousUtilizationMips;
	}

	/**
	 * Sets the previous utilization mips.
	 * 
	 * @param previousUtilizationMips the new previous utilization mips
	 */
	protected void setPreviousUtilizationMips(double previousUtilizationMips) {
		this.previousUtilizationMips = previousUtilizationMips;
	}

	/**
	 * Gets the state history.
	 * 
	 * @return the state history
	 */
	public List<HostStateHistoryEntry> getStateHistory() {
		return stateHistory;
	}

	/**
	 * Adds the state history entry.
	 * 
	 * @param time the time
	 * @param allocatedMips the allocated mips
	 * @param requestedMips the requested mips
	 * @param isActive the is active
	 */
	public void addStateHistoryEntry(double time, double allocatedMips, double requestedMips, boolean isActive) {
		HostStateHistoryEntry newState = new HostStateHistoryEntry(time, allocatedMips, requestedMips, isActive);
		if (!getStateHistory().isEmpty()) {
			HostStateHistoryEntry previousState = getStateHistory().get(getStateHistory().size() - 1);
			if (previousState.getTime() == time) {
				getStateHistory().set(getStateHistory().size() - 1, newState);
				return;
			}
		}
		getStateHistory().add(newState);
	}

}
