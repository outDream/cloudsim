/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim;

import java.util.ArrayList;
import java.util.Iterator;

import org.cloudbus.cloudsim.power.dvfs.AbstractGovernor;
import org.cloudbus.cloudsim.power.dvfs.ConservativeGovernor;
import org.cloudbus.cloudsim.power.dvfs.MyGovernor;
import org.cloudbus.cloudsim.power.dvfs.OnDemandGovernor;
import org.cloudbus.cloudsim.power.dvfs.PerformanceGovernor;
import org.cloudbus.cloudsim.power.dvfs.PowerSaveGovernor;
import org.cloudbus.cloudsim.power.dvfs.UserSpaceGovernor;
import org.cloudbus.cloudsim.provisioners.PeProvisioner;
import org.cloudbus.cloudsim.xml.DvfsDatas;

/**
 * CloudSim Pe (Processing Element) class represents CPU unit, defined in terms of Millions
 * Instructions Per Second (MIPS) rating.<br>
 * <b>ASSUMPTION:<b> All PEs under the same Machine have the same MIPS rating.
 * 
 * @author Manzur Murshed
 * @author Rajkumar Buyya
 * @since CloudSim Toolkit 1.0
 */
public class Pe {

	/** Denotes Pe is FREE for allocation. */
	public static final int FREE = 1;

	/** Denotes Pe is allocated and hence busy in processing Cloudlet. */
	public static final int BUSY = 2;

	/**
	 * Denotes Pe is failed and hence it can't process any Cloudlet at this moment. This Pe is
	 * failed because it belongs to a machine which is also failed.
	 */
	public static final int FAILED = 3;

	/** The id. */
	private int id;

	// FOR SPACE SHARED RESOURCE: Jan 21
	/** The status of Pe: FREE, BUSY, FAILED: . */
	private int status;

	/** The pe provisioner. */
	protected PeProvisioner peProvisioner;

		protected AbstractGovernor gov;
		
	    // Pe frequencies (%) available
	    private ArrayList<Double> frequencies; 
	    
	    protected int nbFreq;
	    /** Frequency current index
	     * index of the frequency Array.
	     */
	    private int indexFreq=0;
    
	/**
	 * Allocates a new Pe object.
	 * 
	 * @param id the Pe ID
	 * @param peProvisioner the pe provisioner
	 * @pre id >= 0
	 * @pre peProvisioner != null
	 * @post $none
	 */
	public Pe(int id, PeProvisioner peProvisioner) {
		setId(id);
		setPeProvisioner(peProvisioner);

		// when created it should be set to FREE, i.e. available for use.
		status = FREE;
	}
	
		public Pe(int id, PeProvisioner peProvisioner, ArrayList<Double> frequencies_, String gov_, DvfsDatas configDvfs) {
			setId(id);
			setPeProvisioner(peProvisioner);
			gov = setGovernorMode(gov_, configDvfs);
			frequencies = frequencies_;
			nbFreq = frequencies.size();
	
			if(gov.getDefautIndexFreq()==-1)//TODO 这里原来实现应该错了，加了个if语句
				gov.setDefautIndexFreq(nbFreq - 1);
			setIndexFreq(gov.getDefautIndexFreq());
			setDefautStartFrequency();
	
			// when created it should be set to FREE, i.e. available for use.
			status = FREE;
		}

        private void setDefautStartFrequency() {
            Log.printLine("getindexfreq = " + getIndexFreq());
            if(getIndexFreq()>=0 && getIndexFreq() < nbFreq) {
                double new_mips = getPercentStep()/100*peProvisioner.getMaxMips(); 
                setMips(new_mips);
                Log.printLine("For " + gov.getName() + " mode , the defaut start Frequency is : " + getMips());
            } else {
                setIndexFreq(0);
                Log.printLine("Error while setting the Start Frequency. Please verify your Dvfs configuration. ");
                Log.printLine("The Frequency has been set to the Minimum available. ");
            }   
        }
        
         private  AbstractGovernor setGovernorMode(String mode_ , DvfsDatas configDvfs) {
            Log.printLine("govv = " + mode_);
            if(mode_.equalsIgnoreCase("OnDemand"))
                 return new OnDemandGovernor(configDvfs.getHashMapOnDemand());
            else if(mode_.equalsIgnoreCase("PowerSave"))
                 return new PowerSaveGovernor();
            else if(mode_.equalsIgnoreCase("Performance"))
                 return new PerformanceGovernor();
            else if(mode_.equalsIgnoreCase("Conservative"))
                 return new ConservativeGovernor(configDvfs.getHashMapConservative());
            else if(mode_.equalsIgnoreCase("UserSpace"))
                 return new UserSpaceGovernor(configDvfs.getHashMapUserSpace());
            else if(mode_.equalsIgnoreCase("My"))
                return new MyGovernor(configDvfs.getHashMapMy());
            else {
                Log.printLine("Error while loading DVFS governor. Performance governor has been loaded !");
                return new PerformanceGovernor();  
            }
        }

	/**
	 * Sets the id.
	 * 
	 * @param id the new id
	 */
	protected void setId(int id) {
		this.id = id;
	}

	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Sets the MIPS Rating of this Pe.
	 * 
	 * @param d the mips
	 * @pre mips >= 0
	 * @post $none
	 */
	public void setMips(double d) {
		getPeProvisioner().setMips(d);
	}

	/**
	 * Gets the MIPS Rating of this Pe.
	 * 
	 * @return the MIPS Rating
	 * @pre $none
	 * @post $result >= 0
	 */
	public int getMips() {
		return (int) getPeProvisioner().getMips();
	}
	
		public int getMaxMips() {
			return (int) getPeProvisioner().getMaxMips();
		}

	/**
	 * Gets the status of this Pe.
	 * 
	 * @return the status of this Pe
	 * @pre $none
	 * @post $none
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * Sets Pe status to free, meaning it is available for processing. This should be used by SPACE
	 * shared hostList only.
	 * 
	 * @pre $none
	 * @post $none
	 */
	public void setStatusFree() {
		setStatus(FREE);
	}

	/**
	 * Sets Pe status to busy, meaning it is already executing Cloudlets. This should be used by
	 * SPACE shared hostList only.
	 * 
	 * @pre $none
	 * @post $none
	 */
	public void setStatusBusy() {
		setStatus(BUSY);
	}

	/**
	 * Sets this Pe to FAILED.
	 * 
	 * @pre $none
	 * @post $none
	 */
	public void setStatusFailed() {
		setStatus(FAILED);
	}

	/**
	 * Sets Pe status to either <tt>Pe.FREE</tt> or <tt>Pe.BUSY</tt>
	 * 
	 * @param status Pe status, <tt>true</tt> if it is FREE, <tt>false</tt> if BUSY.
	 * @pre $none
	 * @post $none
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * Sets the pe provisioner.
	 * 
	 * @param peProvisioner the new pe provisioner
	 */
	protected void setPeProvisioner(PeProvisioner peProvisioner) {
		this.peProvisioner = peProvisioner;
	}

	/**
	 * Gets the Pe provisioner.
	 * 
	 * @return the Pe provisioner
	 */
	public PeProvisioner getPeProvisioner() {
		return peProvisioner;
	}

		private void setNewFrequency(int desc) {
//			System.out.println("In Function \"Set_NewFrequency\" , desc =  " + desc + " / index freq =" + getIndexFreq() + " freq%=" +getPercentStep());
//			System.out.println("OLD MIPS was = " + (getPercentStep()/100*peProvisioner.getMaxMips()));
			switch (desc) {
			case -2://TODO 实验加上这块
				setIndexFreq(0);
				break;
			case -1:
				decrIndexFreq();
				break;
			case 1:
				incrIndexFreq();
				break;
			case 2:
				setIndexFreqMax();
				break;
			}
//			System.out.println("In Function \"Set_NewFrequency\" , desc =  " + desc + " / index freq =" + getIndexFreq() + " freq%=" +getPercentStep());
//			System.out.println("New MIPS will be = " + (getPercentStep()/100*peProvisioner.getMaxMips()));
	
			double new_MIPS = getPercentStep() / 100 * peProvisioner.getMaxMips();
			setMips(new_MIPS);
//			System.out.println("After Update Pe MIPS : " + peProvisioner.getMips());
		}
	
		public int getIndexFreq() {
			return indexFreq;
		}
	
		protected void setIndexFreqMax() {
			indexFreq = nbFreq - 1;
		}
	
		private void incrIndexFreq() {
			if (indexFreq < nbFreq - 1)
				indexFreq++;
		}
	
		private void decrIndexFreq() {
			if (indexFreq > 0)
				indexFreq--;
		}
	
		private void setIndexFreq(int index_freq) {
			this.indexFreq = index_freq;
		}
	
		protected boolean changeToMaxFrequency() {
			if (gov.getName().equalsIgnoreCase("OnDemand")) {
				Log.printLine("lllllllllllllllllllllllll");
				setNewFrequency(2);
				return true;
			} else if(gov.getName().equalsIgnoreCase("MyGovernor")){
				Log.printLine("2222222222222222222222222");
				setNewFrequency(2);
				return true;
			}
			
			else
				return false;
		}
	
		protected int changeFrequency() {
			int desc = gov.SpecificDecision(peProvisioner.getUtilization() * 100);
			if (desc == 2 && getIndexFreq() == (nbFreq - 1))
				return 0;
			else if (desc == -1 && getIndexFreq() == 0)
				return 0;
			else if (desc != 0)
				setNewFrequency(desc);
			return desc;
		}
	
		/**
		 * @return the actual percent step
		 */
		private double getPercentStep() {
			return frequencies.get(indexFreq);
		}
		
		public void setFrequency(int frequency) {
            setIndexFreq(frequency);
            double newMips = getPercentStep() / 100 * peProvisioner.getMaxMips();
            setMips(newMips);
        }
		
		public boolean increaseFrequency() {
			if(getIndexFreq() < nbFreq - 1) {
				incrIndexFreq();
				double newMips = getPercentStep() / 100 * peProvisioner.getMaxMips();
		        setMips(newMips);
				return true;
			}
			return false;
		}
		
		public boolean decreaseFrequency() {
			if(getIndexFreq() > 0) {
				decrIndexFreq();
				double newMips = getPercentStep() / 100 * peProvisioner.getMaxMips();
		        setMips(newMips);
				return true;
			}
			return false;
		}
}
