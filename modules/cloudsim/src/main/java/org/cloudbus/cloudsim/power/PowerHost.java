/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.power;

import java.util.List;

import org.cloudbus.cloudsim.HostDynamicWorkload;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.VmScheduler;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.power.models.PowerModel;
import org.cloudbus.cloudsim.power.models.PowerModelSpecPowerDVFS;
import org.cloudbus.cloudsim.provisioners.BwProvisioner;
import org.cloudbus.cloudsim.provisioners.RamProvisioner;

/**
 * PowerHost class enables simulation of power-aware hosts.
 * 
 * If you are using any algorithms, policies or workload included in the power package please cite
 * the following paper:
 * 
 * Anton Beloglazov, and Rajkumar Buyya, "Optimal Online Deterministic Algorithms and Adaptive
 * Heuristics for Energy and Performance Efficient Dynamic Consolidation of Virtual Machines in
 * Cloud Data Centers", Concurrency and Computation: Practice and Experience (CCPE), Volume 24,
 * Issue 13, Pages: 1397-1420, John Wiley & Sons, Ltd, New York, USA, 2012
 * 
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 2.0
 */
public class PowerHost extends HostDynamicWorkload {

	/** The power model. */
	private PowerModel powerModel;

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
	public PowerHost(
			int id,
			RamProvisioner ramProvisioner,
			BwProvisioner bwProvisioner,
			long storage,
			List<? extends Pe> peList,
			VmScheduler vmScheduler,
			PowerModel powerModel) {
		super(id, ramProvisioner, bwProvisioner, storage, peList, vmScheduler);
		setPowerModel(powerModel);
	}
	
		public PowerHost(int id, RamProvisioner ramProvisioner, BwProvisioner bwProvisioner, long storage, List<? extends Pe> peList, VmScheduler vmScheduler, PowerModel powerModel, boolean enableOnOff, boolean enableDvfs) {
	        super(id, ramProvisioner, bwProvisioner, storage, peList, vmScheduler);
	
	        setPowerModel(powerModel);
	        setEnableDVFS(enableDvfs);
	        setEnableONOFF(enableOnOff);
	    }

	/**
	 * Gets the power. For this moment only consumed by all PEs.
	 * 
	 * @return the power
	 */
	public double getPower() {
		return getPower(getUtilizationOfCpu());
	}

	/**
	 * Gets the power. For this moment only consumed by all PEs.
	 * 
	 * @param utilization the utilization
	 * @return the power
	 */
	protected double getPower(double utilization) {
		double power = 0;
		try {
			power = getPowerModel().getPower(utilization);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		return power;
	}

	/**
	 * Gets the max power that can be consumed by the host.
	 * 
	 * @return the max power
	 */
	public double getMaxPower() {
		double power = 0;
		try {
			power = getPowerModel().getPower(1);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		return power;
	}

	/**
	 * Gets the energy consumption using linear interpolation of the utilization change.
	 * 
	 * @param fromUtilization the from utilization
	 * @param toUtilization the to utilization
	 * @param time the time
	 * @return the energy
	 */
	public double getEnergyLinearInterpolation(double fromUtilization, double toUtilization, double time) {
//		if (fromUtilization == 0) {
//			return 0;
//		}
			//TODO 这里可能考虑了主机睡眠技术
			if (fromUtilization == 0 && isEnableONOFF()) {
				return 0;
			}
		double fromPower = getPower(fromUtilization);
		double toPower = getPower(toUtilization);
		//System.out.println("Host "+this.getId()+"  Power-------------------------------------"+(fromPower + (toPower - fromPower) / 2));
		return (fromPower + (toPower - fromPower) / 2) * time;
	}

	/**
	 * Sets the power model.
	 * 
	 * @param powerModel the new power model
	 */
	protected void setPowerModel(PowerModel powerModel) {
		this.powerModel = powerModel;
	}

	/**
	 * Gets the power model.
	 * 
	 * @return the power model
	 */
	public PowerModel getPowerModel() {
		return powerModel;
	}

		/**
		 * 
		 * return the Max Power consume by the host regarding its current frequency
		 *
		 * We do a 'if' on the enableDVFS value because we have to be sure that we
		 * use the PowerModelSpecDVFS powermodel
		 * 
		 * Methods getPMax and getPMin didn't have been add to the Interface
		 * PowerModel because in this case it would impact all other models, even
		 * those that not use multiple power value in relation with the CPU
		 * frequency
		 * 
		 * @param frequency
		 * @return
		 */
		public double getPMax(int frequency) {
			if (isEnableDVFS()) {
				PowerModelSpecPowerDVFS tmp_model = (PowerModelSpecPowerDVFS) getPowerModel();
				return tmp_model.getPMax(frequency);
			} else
				return 0;
		}
	
		/**
		 * return the Min Power consume by the host regarding its current frequency
		 * 
		 * same comment as the getPMax() method.
		 * 
		 * @param frequency
		 * @return
		 */
		public double getPMin(int frequency) {
			if (isEnableDVFS()) {
				PowerModelSpecPowerDVFS tmp_model = (PowerModelSpecPowerDVFS) getPowerModel();
				return tmp_model.getPMin(frequency);
			} else
				return 0;
		}
}
