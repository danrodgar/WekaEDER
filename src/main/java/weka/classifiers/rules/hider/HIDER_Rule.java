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
 *    HIDER_Rule.java
 *    Copyright (C) 2011 Marta Hernando, Daniel Rodriguez. Universidad de Alcala
 *
 */
package weka.classifiers.rules.hider;

import java.util.ArrayList;
import java.util.Enumeration;

import weka.core.*;
import weka.classifiers.rules.*;
import weka.classifiers.rules.hider.gene.*;

/**
 * Class that represents a rule in this classifier.
 */
public class HIDER_Rule extends Rule {

	private double mClass; // Value of the class of the rule
	private int mNumAttributes; // Number of attributes of the rule
	private int mClassIndex; // Position of the class
	private Instances dataSetInstances; // Set of instances
	private Gene[] mCond; // Set of genes of the rule
	private int mCorrect, mIncorrect; // number of correctly or incorrectly instances
	private double fitness; // Value of fitness

	/**
	 * Constructor.
	 * 
	 * @param rule_class Class that the rule belongs to
	 * @param num_attributes Number of attributes involved.
	 * @param class_index Class attribute index.
	 */
	public HIDER_Rule(double ruleClass) {
		mClass = ruleClass;
	}
	
	@Override
	/**
	 * Creates and initializes a rule.
	 */
	public void grow(Instances dSetInstances) throws Exception {
		
		dataSetInstances = dSetInstances;
		mNumAttributes = dataSetInstances.numAttributes();
		mClassIndex = dataSetInstances.classIndex();

		// Initializes the types of the array of mCond
		mCond = new Gene[mNumAttributes];
		for (int h = 0; h < mNumAttributes; h++) {
			if (h != mClassIndex) {
				if (dataSetInstances.attribute(h).isNominal()) {
					int numElements = dataSetInstances.attribute(h).numValues();
					Enumeration<Object> list = dataSetInstances.attribute(h)
							.enumerateValues();
					mCond[h] = new Discrete(numElements, list);
				} else if (dataSetInstances.attribute(h).isNumeric()) {
					mCond[h] = new Continuous();
				}
			}
		}
	}

	/**
	 * He verifies if two rules are equal.
	 * 
	 * @param rule
	 * @return boolean
	 */
	public boolean equalRule(HIDER_Rule rule) {
		for (int i = 0; i < size(); i++) {
			if (i!=this.mClassIndex)
			{
				if (getGene(i).equal(rule.getGene(i)) == false) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Checks if the given instance is contained by the non-refined version of
	 * the rule according to the comparison modes.
	 * 
	 * @param instance
	 * @return true if the instance is contained, false if not
	 */
	public boolean check(Instance instance) {
		assert (instance != null);
		int counter = 0;

		for (int i = 0; i <size(); i++) {
			if (i != mClassIndex) {
				if (getGene(i).getComparisonTypes() == 0) {
					counter++;
				} else if ((getGene(i)).check(instance, i) == false)
					return false;
			}
		}
		return counter != size();
	}

	/**
	 * Checks if the given instance is contained by the refined version of the
	 * rule according to the comparison modes.
	 * 
	 * @param instance
	 * @return true if the instance is contained, false if not
	 */
	public boolean checkRefined(Instance instance) {
		assert (instance != null);
		int counter = 0;

		for (int i = 0; i < size(); i++) {
			if (i != mClassIndex) {
				if (getGene(i).getComparisonTypes() == 0) {
					counter++;
				} else if ((getGene(i)).checkRefined(instance, i) == false)
					return false;
			}
		}
		return counter != size();
	}

	/**
	 * Clones a rule.
	 * 
	 * @return the new clone.
	 */
	@Override
	public HIDER_Rule clone() {
		
		HIDER_Rule rule = new HIDER_Rule(mClass);
		
		try {
			
			rule.grow(dataSetInstances);

			for (int i = 0; i < size(); i++) {
				if (i != mClassIndex) {
					if (getGene(i).isNumeric()) {
						((Continuous) rule.getGene(i)).setLowerLimit(((Continuous) getGene(i)).getLowerLimit());
						((Continuous) rule.getGene(i)).setUpperLimit(((Continuous) getGene(i)).getUpperLimit());

						((Continuous) rule.getGene(i)).setLowerRefinedLimit(((Continuous) getGene(i)).getLowerRefinedLimit());
						((Continuous) rule.getGene(i)).setUpperRefinedLimit(((Continuous) getGene(i)).getUpperRefinedLimit());
						
					} else if (getGene(i).isNominal()) {
						// ----------IMPLEMENTAR METODO PARA BINARIO----------------------------
						((Discrete) rule.getGene(i)).setBits(((Discrete) getGene(i)).getBits());
					}
				}
			}
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return rule;
	}

	/**
	 * Returns the description of a rule.
	 * 
	 * @return a description of the rule
	 */
	public String toString(boolean bRefined, Instances m_DataSetInstances,
			HIDER_Rule ruleMaxMin) {
		String result = "if ";
		String att_name;
		int counter = 0;

		for (int i = 0; i < size(); i++) {
			if (i != mClassIndex) {
				att_name = m_DataSetInstances.attribute(i).name();

				if (getGene(i).getComparisonTypes() == 0) {
					counter++;
				} else {
					if (getGene(i).isNumeric()) {
						if (bRefined) {
							result += ((Continuous) getGene(i)).toStringRefined(att_name,ruleMaxMin.getGene(i));
						} else
							result += getGene(i).toString(att_name,ruleMaxMin.getGene(i));
					} else if (getGene(i).isNominal()) {
						result += getGene(i).toString(att_name,ruleMaxMin.getGene(i));
					}
				}
			}
		}

		result += "then class = " + String.valueOf(mClass) + "\nCorrect: "
				+ getCorrect() + ", Incorrect: " + this.getIncorrect();

		return (counter == size()) ? "" : result;
	}

	/**
	 * Coverage of the rule.
	 */
	public double coverage() {
		double coverage = 1.0;
		for (int j = 0; j < size(); j++) {
			if (j != mClassIndex) {
				coverage *= getGene(j).coverage();
			}
		}
		return coverage;
	}


	/**
	 * Checks if the given instance is contained by the non-refined version of
	 * the rule.
	 * 
	 * @param instance
	 * @return true if the instance is contained, false if not
	 */
	@Override
	public boolean covers(Instance datum) {
		assert (datum != null);

		for (int i = 0; i < size(); i++) {
			if (i != mClassIndex) {
				if (getGene(i).contains(datum, i) == false) {
					return false;
				}
			}
		}
		return true;
	}

	
	/**
	 * Checks if the given instances are contained by the non-refined version of
	 * the rule.
	 * 
	 * @param instance
	 * @return true if the instance is contained, false if not
	 */	
	public boolean cover(ArrayList<Instance> m_Data)
	{
		for (Instance instance : m_Data) {
			if (covers(instance)) {
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * Checks if the given instance is contained by the refined version of the
	 * rule.
	 * 
	 * @param instance
	 * @return true if the instance is contained, false if not
	 */
	public boolean coversRefined(Instance datum) {
		assert (datum != null);

		for (int i = 0; i < size(); i++) {
			if (i != mClassIndex) {
				if (getGene(i).containsRefined(datum, i) == false) {
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * Prints the rule.
	 * @return String
	 */
	public String printRule() {
		String result = "";
		Gene g = null;

		for (int i = 0; i < getCond().length; i++) {
			g = getGene(i);
			if (i != mClassIndex) {
				{
					if (g.isNominal()) {
						result += ((Discrete) g).printDiscrete();
					} else if (g.isNumeric()) {
						result += ((Continuous) g).printContinuous();
					}
				}
			}

			if (i != (getCond().length - 1)) {
				result += "/";
			}
		}

		return result;
	}
	
	@Override
	public double size() {
		return mNumAttributes;
	}
	
	/**
	 * Returns value of fitness
	 * 
	 * @return fitness
	 */
	public double getFitness() {
		return fitness;
	}

	/**
	 * Sets value of fitness
	 * 
	 * @param fitness
	 */
	public void setFitness(double fitness) {
		this.fitness = fitness;
	}

	/**
	 * Sets class of rule.
	 * 
	 * @param aclass
	 */
	public void setClass(double aclass) {
		mClass = aclass;
	}

	/**
	 * Sets a gene of rule.
	 * 
	 * @param pos
	 * @param gene
	 */
	public void setGene(int pos, Gene gene) {
		mCond[pos] = gene;
	}

	/**
	 * Returns a gene of rule.
	 * 
	 * @param pos
	 * @return
	 */
	public Gene getGene(int pos) {
		return mCond[pos];
	}

	/**
	 * Return genes
	 * 
	 * @return mCond
	 */
	public Gene[] getCond() {
		return mCond;
	}

	/**
	 * Returns the number of correctly classified instances.
	 * 
	 * @return Returns the number of correctly classified instances.
	 */
	public int getCorrect() {
		return mCorrect;
	}

	/**
	 * Sets the number of correctly classified instances.
	 * 
	 * @param mCorrect
	 */
	public void setCorrect(int mCorrect) {
		this.mCorrect = mCorrect;
	}

	/**
	 * Returns the number of incorrectly classified instances.
	 * 
	 * @return Returns the number of incorrectly classified instances.
	 */
	public int getIncorrect() {
		return mIncorrect;
	}

	/**
	 * Sets the number of incorrectly classified instances.
	 * 
	 * @param mCorrect
	 */
	public void setIncorrect(int mIncorrect) {
		this.mIncorrect = mIncorrect;
	}

	/**
	 * Returns the class which the rule belongs to.
	 * 
	 * @return Returns the class which the rule belongs to.
	 */
	public double getRuleClass() {
		return mClass;
	}	

	@Override
	public boolean hasAntds() {
		return false;
	}

	@Override
	public double getConsequent() {
		return 0;
	}

	@Override
	public String getRevision() {
		return null;
	}
	
	
		
	//METHOD TO METRIC
	/**
	 * Returns the number of instances covered by condition Cond. (Cond->Class)
	 * @param population
	 * @return
	 */
	public double nCond(ArrayList<Instance> setExample) {
			int n = 0;
			for (Instance instance : setExample) {
				if ((covers(instance))) {
						n += 1.;
				}
			}
			return n;
	}
	
	/**
	 * Returns the number of instances not covered by condition Cond. (Cond->Class)
	 * @param population
	 * @return
	 */
	public double notnCond(ArrayList<Instance> setExample) {
			int n = 0;
			for (Instance instance : setExample) {
				if ((covers(instance))==false) {
						n += 1.;
				}
			}
			return n;
	}
	
	/**
	 * Returns the number of instances satisfying both the antecedent and the consequent part of a rule.
	 * @param setExample
	 * @return
	 */
	public double nCondClass(ArrayList<Instance> setExample) {
			int n = 0;
			for (Instance instance : setExample) {
				if ((covers(instance))) {
					if (instance.classValue() == getRuleClass()) {
						n += 1.;
					}
				}
			}
			return n;
	}
	
	/**
	 * Returns the number of instances not satisfying both the antecedent and the consequent part of a rule.
	 * @param setExample
	 * @return
	 */
	public double notnCondClass(ArrayList<Instance> setExample) {
			int n = 0;
			for (Instance instance : setExample) {
				if ((covers(instance))==false) {
					if (instance.classValue() != getRuleClass()) {
						n += 1.;
					}
				}
			}
			return n;
	}
	
	/**
	 * Returns the number of instances that satisfy the target class.
	 * @param setExample
	 * @return
	 */
	public double nClass(ArrayList<Instance> setExample)
	{
		int n = 0;
		for (Instance instance : setExample) {
			if (instance.classValue() == getRuleClass()) {
				n += 1.;
			}
		}
		return n;
	}
	
	/**
	 * Returns the number of instances that do not satisfy the target class.
	 * @param setExample
	 * @return
	 */
	public double notnClass(ArrayList<Instance> setExample)
	{
		int n = 0;
		for (Instance instance : setExample) {
			if (instance.classValue() != getRuleClass()) {
				n += 1.;
			}
		}
		return n;
	}	
	
}