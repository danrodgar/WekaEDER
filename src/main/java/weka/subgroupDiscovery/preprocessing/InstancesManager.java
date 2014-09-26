/*
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software
 *    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

/*
 *    InstanceManager.java
 *    Copyright (C) 2011 Marta Hernando, Daniel Rodriguez. Universidad de Alcala
 *
 */
package weka.subgroupDiscovery.preprocessing;

import java.util.ArrayList;

import weka.core.*;
import weka.subgroupDiscovery.edersd.*;
import weka.subgroupDiscovery.edersd.gene.*;

/**
 * Class to normalize and denormalize instances.
 * 
 * @author Marta
 *
 */
public class InstancesManager {
	
  /** The minimum values for numeric attributes. */
  private double[] m_MinArray;  
  /** The maximum values for numeric attributes. */
  private double[] m_MaxArray;
  /** Scale applied to source data after they have been normalized. By default m_mScale = 1 (no scale) */
  private double m_nScale;
  /** Displacement from 0.0. By default m_nTranslation = 0 (no displacement) */
  private double m_nTranslation;
  
  private int m_nClassIndex;
  /** Number of attributes (all instances, normalized or not, must have equal number of attributes) */
  private int m_nNumAttributes;
  /** Source instances */
  private Instances m_Instances;
  /** Normalized instances */
  private Instances m_NormInstances;
  /** Denormalized instances (it must coincide with source instances) */
  private Instances m_DenormInstances;

  
  /**
   * Constructor.
   */
  public InstancesManager(){
	  initialize(1.0, 0.0);
  }
  
  /**
   * Constructor.
   * @param nScale
   * @param nTranslation
   */
  public InstancesManager(double nScale, double nTranslation){
	  initialize(nScale, nTranslation);
  }
  
  /**
   * Sets initialized values.
   * @param nScale
   * @param nTranslation
   */
  private void initialize(double nScale, double nTranslation){ 
	  m_DenormInstances=null;
	  m_NormInstances=null;
	  m_MinArray=null;
	  m_MaxArray=null;
	  m_nScale = nScale;	  
	  m_nTranslation = nTranslation;	  
  }
  
  
  /**
   * Initializes min and max arrays with continuous values.
   */
  private void initializeMinMaxArrays(){
		if (m_MinArray == null) {	      
	      // Compute minimums and maximums
	      m_MinArray = new double[m_Instances.numAttributes()];
	      m_MaxArray = new double[m_Instances.numAttributes()];
	      for (int attribIdx = 0; attribIdx < m_Instances.numAttributes(); attribIdx++){
	    	  m_MinArray[attribIdx] = Double.NaN;
	      }
	      for (int instanceIdx = 0; instanceIdx < m_Instances.numInstances(); instanceIdx++) {
			  double[] value = m_Instances.instance(instanceIdx).toDoubleArray();
			  for (int attribIdx = 0; attribIdx < m_Instances.numAttributes(); attribIdx++) {
				  if (m_Instances.attribute(attribIdx).isNumeric() && (m_nClassIndex != attribIdx)) {
					  if (!Utils.isMissingValue(value[attribIdx])) {
						  if (Double.isNaN(m_MinArray[attribIdx])) {
							  m_MinArray[attribIdx] = m_MaxArray[attribIdx] = value[attribIdx];
						  }
						  else {
							 if (value[attribIdx] < m_MinArray[attribIdx]){
								 m_MinArray[attribIdx] = value[attribIdx];
							 }
							 if (value[attribIdx] > m_MaxArray[attribIdx]){
								 m_MaxArray[attribIdx] = value[attribIdx];
							 }
						  }
					  }
				  }
			  } 
	      }
		}
  }
  
  /**
   * Returns a normalized value.
   * @param nValue
   * @param nAttributeIdx
   * @return value
   */
  private double normalizeValue(double nValue, int nAttributeIdx){
  	  
	double value = (nValue - m_MinArray[nAttributeIdx]) * m_nScale / (m_MaxArray[nAttributeIdx] - m_MinArray[nAttributeIdx]) + m_nTranslation;

	return value;	  
  }

  /**
   * Returns a denormalized value.
   * @param nValue
   * @param nAttribIdx
   * @return value
   */
  private double deNormalizeValue(double nValue, int nAttribIdx){
	double value = nValue-m_nTranslation;
	value /= m_nScale;
	value *= (m_MaxArray[nAttribIdx] - m_MinArray[nAttribIdx]);
	value += m_MinArray[nAttribIdx];
	return value;
  }	 
  
  /**
   * Returns a instance that is converted normalized instance.
   * @param instance
   * @param bNormalize
   * @return
   * @throws Exception
   */
  private Instance convertInstance(Instance instance, boolean bNormalize) throws Exception {	  	 
	  	 
    Instance inst = null;
    if (instance instanceof SparseInstance) {
		double[] newVals = new double[instance.numAttributes()];
		int[] newIndices = new int[instance.numAttributes()];
		double[] vals = instance.toDoubleArray();
		int ind = 0;
		for (int attribIdx = 0; attribIdx < instance.numAttributes(); attribIdx++) {
			double value;
			if (instance.attribute(attribIdx).isNumeric() && (!Utils.isMissingValue(vals[attribIdx])) && (m_nClassIndex != attribIdx)) {
			  if (Double.isNaN(m_MinArray[attribIdx]) || (m_MaxArray[attribIdx] == m_MinArray[attribIdx])) {
			    value = 0;
			  }
			  else {
				  value=bNormalize ? normalizeValue(vals[attribIdx], attribIdx) : deNormalizeValue(vals[attribIdx], attribIdx);
				  if (Double.isNaN(value)) {
		              throw new Exception("A NaN value was generated while normalizing " 
		                                  + instance.attribute(attribIdx).name());
		            }
			  }
			  if (value != 0.0) {
			    newVals[ind] = value;
			    newIndices[ind] = attribIdx;
			    ind++;
			  }
			}
			else {
				value = vals[attribIdx];
				if (value != 0.0) {
					newVals[ind] = value;
					newIndices[ind] = attribIdx;
					ind++;
				}
			}
		}
		
		double[] tempVals = new double[ind];
		int[] tempInd = new int[ind];
		System.arraycopy(newVals, 0, tempVals, 0, ind);
		System.arraycopy(newIndices, 0, tempInd, 0, ind);
		inst = new SparseInstance(instance.weight(), tempVals, tempInd,instance.numAttributes());
    } 
    else {
      double[] vals = instance.toDoubleArray();
      for (int attribIdx = 0; attribIdx < m_nNumAttributes; attribIdx++) {
    	  if (instance.attribute(attribIdx).isNumeric() && (!Utils.isMissingValue(vals[attribIdx])) && (m_nClassIndex != attribIdx)) {
    		  if (Double.isNaN(m_MinArray[attribIdx]) || (m_MaxArray[attribIdx] == m_MinArray[attribIdx])) {
    			  vals[attribIdx] = 0;
    		  }
    		  else {
    			  vals[attribIdx] = bNormalize ? normalizeValue(vals[attribIdx], attribIdx) : deNormalizeValue(vals[attribIdx], attribIdx);
    			  if (Double.isNaN(vals[attribIdx])) {
    				  throw new Exception("A NaN value was generated while normalizing " 
	                                  		  + instance.attribute(attribIdx).name());
	    			  }
	    		  }
	    	  }
	      }	
	      inst = new DenseInstance(instance.weight(), vals);
	    }

    for (int attribIdx = 0; attribIdx < instance.numAttributes(); attribIdx++) {
	if (instance.attribute(attribIdx).isNominal())
	{
		//inst.setValue(attribIdx, instance.attribute(attribIdx).);		
	}
}
	    return inst;
	  }


 
  /**
   * Returns normalized sets of instances.
   * @param instances
   * @return m_NormInstances
   */
  public Instances normalize(Instances instances){
	  if(null==instances){
		  return null;
	  }
	  m_Instances = instances;	  
	  
	  m_nNumAttributes = m_Instances.numAttributes();	  
	  m_nClassIndex = m_Instances.classIndex();

	  initializeMinMaxArrays(); //it looks for ranges to normalize
	  
	  m_NormInstances = new Instances(m_Instances, 0,0); // to create an empty array
	  m_NormInstances.clear(); //I suppose this isn't necessary but I need to ensure that m_NormInstances is empty
      for (int instanceIdx = 0; instanceIdx<m_Instances.numInstances(); instanceIdx++){
  		try {
  			m_NormInstances.add(convertInstance(m_Instances.instance(instanceIdx), true));			
		} catch (Exception e) {
			e.printStackTrace();
		}
      }
      return m_NormInstances;
  }
  
  /**
   * Returns denormalized sets of instances. 
   * @param normalizedInstances
   * @return m_DenormInstances
   */
  public Instances denormalize(Instances normalizedInstances){
	  if(null==m_Instances){
		  return null;
	  }	  
	  m_DenormInstances = new Instances(normalizedInstances, 0,0); // to create an empty array
	  m_DenormInstances.clear(); //I suppose this isn't necessary but I need to ensure that m_NormInstances is empty
      for (int instanceIdx = 0; instanceIdx<normalizedInstances.numInstances(); instanceIdx++){
  		try {
  			m_DenormInstances.add(convertInstance(normalizedInstances.instance(instanceIdx), false));			
		} catch (Exception e) {
			e.printStackTrace();
		}
      }
      return m_DenormInstances;
  }


  /**
   * Returns normalized sets of rules.
   * @param normalizedInstances
   * @return
   */
  public ArrayList<EDERSD_Rule> denormalize(ArrayList<EDERSD_Rule> normalizedInstances){
  	  
	  ArrayList<EDERSD_Rule> rules=new ArrayList<EDERSD_Rule>();
	  
	  for (EDERSD_Rule rule:normalizedInstances)
	  {
		  try {			  			
			rules.add(convertRule(rule));
		  } catch (Exception e) {
			e.printStackTrace();
		  }
	  }
      
      return rules;
  }
  
  /**
   * Returns a rule that is converted normalized rule.
   * @param instance
   * @return
   * @throws Exception
   */
  private EDERSD_Rule convertRule(EDERSD_Rule instance) throws Exception {	  	 
	  	 	  	  	  
	    EDERSD_Rule rule=instance;
	    double valueLower=0.0;
	    double valueUpper=0.0;
	    	    
	    	for (int i=0;i<instance.getCond().length-1;i++)
	    	{
	    		if (m_nClassIndex != i) {
	    			Gene gen = instance.getGene(i);
	    			if (gen.isNumeric()) {
	    				if (((Continuous) gen).getLowerLimit() != -9999.9) {
	    					valueLower = deNormalizeValue(
	    							((Continuous) gen).getLowerLimit(), i);
	    					((Continuous) rule.getGene(i))
	    					.setLowerLimit(valueLower);
	    				}

	    				if (((Continuous) gen).getUpperLimit() != -9999.9) {
	    					valueUpper = deNormalizeValue(
	    							((Continuous) gen).getUpperLimit(), i);
	    					((Continuous) rule.getGene(i))
	    					.setUpperLimit(valueUpper);
	    				}
	    			}
	    		}
	    	}
	    		    	
		return rule;
  }

   
  /**
   * Returns normalized instances.
   * @return m_NormInstances
   */
  public Instances getNormalizedInstances(){
	  return m_NormInstances;
  }
  
  /**
   * Returns denormalized instances.
   * @return m_DenormInstances
   */
  public Instances getDenormalizedInstances(){
	  return m_DenormInstances;
  }  
  
  /**
   * Returns sets of instances.
   * @return m_Instances
   */
  public Instances getInstances() {
	  return m_Instances;
  }
  
}
