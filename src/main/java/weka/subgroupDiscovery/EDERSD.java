package weka.subgroupDiscovery;

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
 *    EDERSD.java
 *    Copyright (C) 2011 Marta Hernando, Daniel Rodriguez. Universidad de Alcala
 *
 */

import java.util.*;

import weka.classifiers.*;
import weka.subgroupDiscovery.edersd.*;
import weka.subgroupDiscovery.edersd.algorithm.*;
import weka.subgroupDiscovery.edersd.evaluation.*;
import weka.subgroupDiscovery.edersd.gene.*;
import weka.subgroupDiscovery.edersd.operator.crossover.*;
import weka.subgroupDiscovery.edersd.operator.mutation.*;
import weka.subgroupDiscovery.edersd.operator.selection.*;
import weka.subgroupDiscovery.preprocessing.InstancesManager;
import weka.core.*;
import weka.core.Capabilities.*;
import weka.core.TechnicalInformation.*;

/**
 * <pre>
 * -Q weka.classifiers.bayes.net.search.SearchAlgorithm
 *  Search algorithm
 * </pre>
 * 
 * @author Marta
 * 
 */
public class EDERSD extends AbstractClassifier implements
		WeightedInstancesHandler {
	/**
	 * For serialization
	 */
	static final long serialVersionUID = -9102297038837585135L;

	// General parameters
	private double m_ErrorPercentage = 0.1;
	private double m_DecreasedPercentage = 0.1;
	private int m_NumIndividuals = 90;
	private int m_NumGenerations = 250;
	private int m_NumCopiedIndividuals = 5;
	private boolean m_UseRefined = false;
	private long m_RandomSeed = System.currentTimeMillis(); // 26476518184518L;

	// Parameters to normalize.
	private double m_Scale = 20;
	private double m_Translation = 0;

	private Algorithm m_Algorithm; // The algorithm to use
	private Fitness m_Fitness = FitnessFactory.getFunctionEvaluation("WRACC"); // The fitness to use
	private Crossover m_Crossover = CrossoverFactory.getCrossoverOperator("CrossoverOnePoint");
	private Mutation m_Mutation = MutationFactory.getMutationOperator("MutateAleatory");
	private Selection m_Selection = SelectionFactory.getSelectionOperator("RouletteWheel");

	private ArrayList<EDERSD_Rule> m_Rules;
	private Instances m_DataSetInstances;
	private EDERSD_Rule m_RuleMaxMin; // Rule with max and min value of instances.
	
	private String m_ValueClass ="true";	//Value class selected.
	private ArrayList<Instance> m_aux = new ArrayList<Instance>();

	/**
	 * Generates the classifier.
	 * 
	 * @param instances the data to train the classifier with
	 * @throws Exception if classifier can't be built successfully
	 */
	public void buildClassifier(Instances instances) throws Exception {
		getCapabilities().testWithFail(instances);		
		setDataSetInstances(instances);
		
		//Puts the selected class.
		setValueClass(getDataSetInstances().valueClassAttribute());
		EDERSD();
	}

	/**
	 * Builds rules.
	 * 
	 * @throws java.lang.Exception
	 */
	public void EDERSD() throws Exception {
		// Initialize the variables
		int mNumAttributes = getDataSetInstances().numAttributes();
		int mClassIndex = getDataSetInstances().classIndex();
		ArrayList<Integer> m_ClassOrder = calculateClassOrder(getDataSetInstances());
		Random randomGenerator = new Random(getRandomSeed());
		int class_value;
		int m_TargetInstances = 0;

		//Puts selected class_value
		class_value = getDataSetInstances().posValueClassAttribute();				
		
		//Aux set of data.
		for (int i = 0; i < getDataSetInstances().numInstances(); i++) {
			Instance inst = (Instance) getDataSetInstances().instance(i).copy();
			m_aux.add(inst);		
		}
		
		
		// ---------------Preprocessing--------------------------------------

		// An instance of the class InstancesManager
		InstancesManager im = new InstancesManager(getScale(), getTranslation());
		// Normalizes the set of instances
		setDataSetInstances(im.normalize(getDataSetInstances()));
					
		// Initialize the set of data (This set will be reduce) with set normalized.
		ArrayList<Instance> m_Data = new ArrayList<Instance>();
		for (int i = 0; i < getDataSetInstances().numInstances(); i++) {
			Instance inst = (Instance) getDataSetInstances().instance(i).copy();
			//Initialize the weight of the instance to 1
			inst.setWeight(1.0);
			m_Data.add(inst);			
		}

		// Initialize the max-min rule and set of rules
		m_Rules = new ArrayList<EDERSD_Rule>(); // Set of rules

		m_RuleMaxMin = new EDERSD_Rule(m_ClassOrder.get(0));
		m_RuleMaxMin.grow(getDataSetInstances());
		m_RuleMaxMin = initInstanceGeneric((double) m_ClassOrder.get(0),getDataSetInstances(), mNumAttributes);

		// Initialize the function fitness
		m_Fitness.setParameter("errorPercentage", getErrorPercentage());
		m_Fitness.setParameter("totalVolume",getVolumen(mNumAttributes, mClassIndex, m_RuleMaxMin));
		m_Fitness.setParameter("random", randomGenerator);

		// Initialize the algorithm
		m_Algorithm = AlgorithmFactory.getAlgorithm("GeneticAlgorithm",m_Fitness);

		m_Algorithm.setInputParameter("numIndividuals", getNumIndividuals());
		m_Algorithm.setInputParameter("numGenerations", getNumGenerations());
		m_Algorithm.setInputParameter("numCopiedIndividuals",getNumCopiedIndividuals());
		m_Algorithm.setInputParameter("dataSetInstances", getDataSetInstances());
		m_Algorithm.setInputParameter("ruleMaxMin", m_RuleMaxMin);
		m_Algorithm.setInputParameter("random", randomGenerator);

		// Mutation and Crossover
		m_Crossover.setParameter("instances", getDataSetInstances());
		m_Crossover.setParameter("random", randomGenerator);

		m_Mutation.setParameter("numAttributes", mNumAttributes);
		m_Mutation.setParameter("classIndex", mClassIndex);
		m_Mutation.setParameter("ruleMaxMin", m_RuleMaxMin);
		m_Mutation.setParameter("random", randomGenerator);

		// Selection Operator
		m_Selection.setParameter("numIndividuals", m_NumIndividuals);
		m_Selection.setParameter("random", randomGenerator);

		// Add the operators to the algorithm
		m_Algorithm.addOperator("crossover", m_Crossover);
		m_Algorithm.addOperator("mutation", m_Mutation);
		m_Algorithm.addOperator("selection", m_Selection);

		m_TargetInstances = getNumInstaces((double) class_value,getDataSetInstances());

		//for (int kk = 0; kk < 30 && m_Data.size() > m_DataSetInstances.numInstances() - m_TargetInstances; kk++){
		for (int kk = 0; kk < 5; kk++){	
			m_Algorithm.setInputParameter("class_value", class_value);
			m_Algorithm.setInputParameter("m_Data", m_Data);

			EDERSD_Rule rule = m_Algorithm.execute();	

			if (rule != null) {
				
				rule = refineRule(rule, getDataSetInstances(), m_Data,mNumAttributes, m_RuleMaxMin);				
				m_Data=modifyWeights(rule,m_Data);	
				m_TargetInstances=getTargetInstances(rule,m_Data,m_TargetInstances);
				
				if (!equalsEDERSD(rule, getRules())) {				
					setRule(rule);
				}				
			}
		}

		// Denormalizes the set of rules
		setRules(im.denormalize(getRules()));
	}

	/**
	 * Modifies weight of the instances which are contained in the set of data.
	 * 
	 * @param individual
	 * @param mData
	 * @param mTargetInstances
	 * @return mData
	 */
	private ArrayList<Instance> modifyWeights(EDERSD_Rule individual,ArrayList<Instance> mData) {
		ArrayList<Instance> new_data = new ArrayList<Instance>();

		for(Instance instance:mData)
		{
//			boolean is_contained = m_UseRefined ? individual.containsRefined(instance) : individual.contains(instance);
			boolean is_contained = individual.covers(instance);

			if (is_contained) 
			{
				instance.setWeight(decreasedWeight(instance.weight()));
			}			
			new_data.add(instance);
		}

		mData.clear();
		for (Instance newData:new_data)
		{
			mData.add(newData);
		}
		
		return mData;
	}
		
	/**
	 * Reduces the value of the weight 
	 * @param weight
	 * @return value of the weight
	 */
	public double decreasedWeight(double weight)
	{
		return weight-(weight*getDecreasedPercentage());
	}
	
	/**
	 * Returns the number of instances without to cover.
	 * @param individual
	 * @param mData
	 * @param mTargetInstances
	 * @return
	 */
	private int getTargetInstances(EDERSD_Rule individual,ArrayList<Instance> mData,int mTargetInstances)
	{
		for(Instance instance:mData)
		{
//			boolean is_contained = m_UseRefined ? individual.containsRefined(instance) : individual.contains(instance);
			boolean is_contained = individual.covers(instance);

			if (is_contained) 
			{
				mTargetInstances--;
			}
		}
		
		return mTargetInstances;						
	}
	
	/**
	 * To calculate the range
	 * 
	 * @param mNumAttributes
	 * @param classIndex
	 * @param ruleMaxMin
	 * @return
	 */
	private double getVolumen(int mNumAttributes, int classIndex,
			EDERSD_Rule ruleMaxMin) {
		double range;
		double mTotalVolume = 1.0;

		for (int j = 0; j < mNumAttributes; j++) {
			if (j != classIndex) {
				if (ruleMaxMin.getGene(j).isNumeric()) {
					range = ((Continuous) ruleMaxMin.getGene(j)).getUpperLimit()- ((Continuous) ruleMaxMin.getGene(j)).getLowerLimit();

					// ((Continuous)ruleMaxMin.getGene(j)).setLowerLimit(((Continuous)ruleMaxMin.getGene(j)).getLowerLimit()
					// - range * 0.03);
					 ((Continuous)ruleMaxMin.getGene(j)).setUpperLimit(((Continuous)ruleMaxMin.getGene(j)).getUpperLimit()
					 + range * 0.03);

					mTotalVolume *= Math.abs(range);
				} else if (ruleMaxMin.getGene(j).isNominal()) {
					mTotalVolume *= ((Discrete) ruleMaxMin.getGene(j))
							.getNumberOfBits();
				}
			}
		}

		return mTotalVolume;
	}

	/**
	 * Verifies if two rules are equal.
	 * 
	 * @param rule
	 * @param rules
	 * @return boolean
	 */
	private boolean equalsEDERSD(EDERSD_Rule rule, ArrayList<EDERSD_Rule> rules) {
		for (EDERSD_Rule ruleAux : rules) {
			if (rule.equalRule(ruleAux)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Initializes the vector with the maximum and minimal values (continuous
	 * values) and maximums (discrete values)
	 * 
	 * @throws Exception
	 */
	private EDERSD_Rule initInstanceGeneric(double targetClass,
			Instances mDataSetInstances, int mNumAttributes) throws Exception {
		EDERSD_Rule auxRuleMaxMin = new EDERSD_Rule(targetClass);
		auxRuleMaxMin.grow(mDataSetInstances);

		// Initializes the generic rule with the maximum and minimal values.
		for (int i = 0; i < mNumAttributes; i++) {
			if (i != mDataSetInstances.classIndex()) {
				if (mDataSetInstances.get(0).attribute(i).isNumeric()) {
					((Continuous) auxRuleMaxMin.getGene(i))
							.setLowerLimit(999.0f);
					((Continuous) auxRuleMaxMin.getGene(i))
							.setUpperLimit(-999.0f);
				} else if (mDataSetInstances.get(0).attribute(i).isNominal()) {
					((Discrete) auxRuleMaxMin.getGene(i)).setFull();
				}
			}
		}

		// Place the maximum and minimal values for every continuous attribute.
		for (Instance inst : mDataSetInstances) {
			for (int j = 0; j < mNumAttributes; j++) {
				if (j != mDataSetInstances.classIndex()) {
					if (inst.attribute(j).isNumeric()) {
						if (((Continuous) auxRuleMaxMin.getGene(j))
								.getUpperLimit() < inst.value(j)) {
							((Continuous) auxRuleMaxMin.getGene(j))
									.setUpperLimit(inst.value(j));
						}
						if (((Continuous) auxRuleMaxMin.getGene(j))
								.getLowerLimit() > inst.value(j)) {
							((Continuous) auxRuleMaxMin.getGene(j))
									.setLowerLimit(inst.value(j));
						}
					}
				}
			}
		}
		return auxRuleMaxMin;

	}

	/**
	 * To calculate the number of instances of a class
	 * 
	 * @param targetClass
	 * @param mDataSetInstances
	 * @return
	 */
	private int getNumInstaces(double targetClass, Instances mDataSetInstances) {
		int mTargetInstances = 0;

		for (Instance inst : mDataSetInstances) {
			if (inst.classValue() == targetClass) {
				mTargetInstances++;
			}
		}
		return mTargetInstances;
	}

	/**
	 * Refines a rule.
	 * 
	 * @param individual
	 *            Rule to be refined.
	 */
	private EDERSD_Rule refineRule(EDERSD_Rule individual,Instances m_DataSetInstances, ArrayList<Instance> m_Data,int mNumAttributes, EDERSD_Rule ruleMaxMin) {
		double[] mini = new double[mNumAttributes], maxi = new double[mNumAttributes], falmin = new double[mNumAttributes], falmax = new double[mNumAttributes];
		double rango = 0.0;

		for (int i = 0; i < mNumAttributes; i++) {
			if (i != m_DataSetInstances.classIndex()) {
				if (m_DataSetInstances.get(0).attribute(i).isNumeric()) {
					rango = ((Continuous) ruleMaxMin.getGene(i)).getUpperLimit()- ((Continuous) ruleMaxMin.getGene(i)).getLowerLimit();
					falmin[i] = ((Continuous) ruleMaxMin.getGene(i)).getLowerLimit() + rango * 0.02;
					falmax[i] = ((Continuous) ruleMaxMin.getGene(i)).getUpperLimit() - rango * 0.02;

					mini[i] = 1e6f;
					maxi[i] = -1e6f;
				}
			}
		}

		int correct = 0, incorrect = 0;
		for (Instance instance : m_Data) {

			if (individual.covers(instance)) {
				if (instance.classValue() == individual.getRuleClass()) { //Si la clase es la misma que intentamos predecir...
					correct++;
					for (int j = 0; j < mNumAttributes; j++) {
						if (instance.attribute(j).isNumeric()) {
							if (maxi[j] < instance.value(j)) {
								maxi[j] = instance.value(j);
							}
							if (mini[j] > instance.value(j)) {
								mini[j] = instance.value(j);
							}
						}
					}
				} else {
					incorrect++;
				}
			}
		}

		individual.setCorrect(correct);
		individual.setIncorrect(incorrect);

		for (int i = 0; i < mNumAttributes; i++) {
			if (i != m_DataSetInstances.classIndex()) {
				if (individual.getGene(i).isNumeric()) {
					if (isUseRefined()) {
						((Continuous) individual.getGene(i)).setLowerRefinedLimit(mini[i]);
						((Continuous) individual.getGene(i)).setUpperRefinedLimit(maxi[i]);
					}
					if (((Continuous) individual.getGene(i)).getLowerLimit() <= falmin[i]
							&& ((Continuous) individual.getGene(i)).getUpperLimit() <= falmax[i]) {individual.getGene(i).setAttributeComparison(1);
					} else if (((Continuous) individual.getGene(i)).getLowerLimit() >= falmin[i]
							&& ((Continuous) individual.getGene(i)).getUpperLimit() >= falmax[i]) {individual.getGene(i).setAttributeComparison(2);
					} else if (((Continuous) individual.getGene(i)).getLowerLimit() >= falmin[i]&& ((Continuous) individual.getGene(i)).getUpperLimit() <= falmax[i]) {individual.getGene(i).setAttributeComparison(3);
					}
				} else if (individual.getGene(i).isNominal()) {
					individual.getGene(i).setAttributeComparison(4);
				}
			}
		}
		return individual;
	}

	/**
	 * Calculates the order which has to be considered to build rules for a
	 * target class.
	 */
	public ArrayList<Integer> calculateClassOrder(Instances m_DataSetInstances) {

		ArrayList<Integer> mClassOrder = new ArrayList<Integer>();
		int[] class_counts = new int[m_DataSetInstances.classAttribute()
				.numValues()];

		for (Instance inst : m_DataSetInstances) {
			class_counts[(int) inst.classValue()]++;
		}

		for (int i = 0; i < m_DataSetInstances.classAttribute().numValues(); i++) {
			int min_index = 0, min = Integer.MAX_VALUE;

			for (int j = 0; j < m_DataSetInstances.classAttribute().numValues(); j++) {
				if (class_counts[j] < min && class_counts[j] > -1) {
					min_index = j;
					min = class_counts[j];
				}
			}
			class_counts[min_index] = -1;
			mClassOrder.add(min_index);
		}

		return mClassOrder;
	}

	/**
	 * Extracts the class with more appearances in the set of data.
	 * 
	 * @param m_DataSetInstances
	 * @param m_Data
	 * @return value of class
	 */
	public String countTargetInstance(Instances m_DataSetInstances,
			ArrayList<Instance> m_Data) {
		int[] class_counts = new int[m_DataSetInstances.classAttribute()
				.numValues()];
		String[] class_names = new String[m_DataSetInstances.classAttribute()
				.numValues()];

		for (Instance inst : m_Data) {
			class_names[(int) inst.classValue()] = String.valueOf(inst
					.classValue());
			class_counts[(int) inst.classValue()]++;
		}

		return class_names[Utils.maxIndex(class_counts)];
	}

	/**
	 * Returns a description of the classifier.
	 *
	 * @return a description of the classifier
	 */
	@Override
	public String toString() {    
		int i=0;
		String result="";	
		result += "Property: "+getDataSetInstances().attribute(getDataSetInstances().classIndex()).name()+" = "+getValueClass()+"\n";
		
		result += "Random seed: " + String.valueOf(getRandomSeed()) + "\nThe created rules are:\n\n";
		if (getRules()!=null) {
			for (i = 0; i < getRules().size(); i++) {
											
				result += "Rule " + (i + 1) + ":\n" + getRules().get(i).toString(isUseRefined(),getDataSetInstances(),m_RuleMaxMin) + "\n" +Metrics.getInstance().calculateEvaluation(getRules().get(i),m_aux)+ "\n\n";													
			}                                                                   
		} else {
			result += "Empty rule set!.";
		}

		return result;
	}

	public Crossover getCrossover() {
		return m_Crossover;
	}

	public void setCrossover(Crossover m_Crossover) {
		this.m_Crossover = m_Crossover;
	}

	public Mutation getMutation() {
		return m_Mutation;
	}

	public void setMutation(Mutation m_Mutation) {
		this.m_Mutation = m_Mutation;
	}

	public Selection getSelection() {
		return m_Selection;
	}

	public void setSelection(Selection m_Selection) {
		this.m_Selection = m_Selection;
	}

	public Fitness getFitness() {
		return m_Fitness;
	}

	public void setFitness(Fitness m_Fitness) {
		this.m_Fitness = m_Fitness;
	}

	/**
	 * Returns the error percentage.
	 * 
	 * @return Returns the error percentage.
	 */
	public double getErrorPercentage() {
		return m_ErrorPercentage;
	}

	/**
	 * Sets the error percentage.
	 * 
	 * @param m_ErrorPercentage
	 */
	public void setErrorPercentage(double m_ErrorPercentage) {
		this.m_ErrorPercentage = m_ErrorPercentage;
	}

	/**
	 * Tip text for error percentage.
	 * 
	 * @return Tip text for error percentage.
	 */
	public String errorPercentageTipText() {
		return "Percentage (expressed with a number between 0 and 1) of allowed incorrectly classified instances by each rule.";
	}

	/**
	 * Tip text for scaling.
	 * 
	 * @return Tip text for scaling.
	 */
	public String scaleEnabledTipText() {
		return "Enables/disables scaling instances.";
	}

	/**
	 * Returns the random seed.
	 * 
	 * @return Returns the random seed.
	 */
	public long getRandomSeed() {
		return m_RandomSeed;
	}

	/**
	 * Sets the random seed.
	 * 
	 * @param m_RandomSeed
	 */
	public void setRandomSeed(long m_RandomSeed) {
		this.m_RandomSeed = m_RandomSeed;
	}

	/**
	 * Tip text for number of copied individuals.
	 * 
	 * @return Tip text for number of copied individuals.
	 */
	public String numCopiedIndividualsTipText() {
		return "Number of individuals to be copied to the next generation.";
	}

	/**
	 * Get the number of copied individuals from one generation to the next one.
	 * 
	 * @return Number of copied individuals from one generation to the next one.
	 */
	public int getNumCopiedIndividuals() {
		return m_NumCopiedIndividuals;
	}

	/**
	 * Set the number of copied individuals from one generation to the next one.
	 * 
	 * @param m_NumCopiedIndividuals
	 *            Number of individuals to be copied.
	 */
	public void setNumCopiedIndividuals(int m_NumCopiedIndividuals) {
		this.m_NumCopiedIndividuals = m_NumCopiedIndividuals;
	}

	/**
	 * Tip text for number of generations.
	 * 
	 * @return Tip text for number of generations.
	 */
	public String numGenerationsTipText() {
		return "Number of generations for the evolutionary algorithm.";
	}

	/**
	 * Get the number of generations for the evolutionary algorithm.
	 * 
	 * @return Number of generations for the evolutionary algorithm.
	 */
	public int getNumGenerations() {
		return m_NumGenerations;
	}

	/**
	 * Set the number of generations for the evolutionary algorithm.
	 * 
	 * @param m_NumGenerations
	 *            Number of generations for the evolutionary algorithm.
	 */
	public void setNumGenerations(int m_NumGenerations) {
		this.m_NumGenerations = m_NumGenerations;
	}

	/**
	 * Tip text for using refined rules.
	 * 
	 * @return Tip text for using refined rules.
	 */
	public String useRefinedTipText() {
		return "Use refined rules for classifying?";
	}

	/**
	 * Get whether the classifier will use refined rules or not.
	 * 
	 * @return Whether the classifier will use refined rules or not.
	 */
	public boolean isUseRefined() {
		return m_UseRefined;
	}

	/**
	 * Set whether the classifier will use refined rules or not.
	 * 
	 * @param m_UseRefined
	 *            True for using refined rules.
	 */
	public void setUseRefined(boolean m_UseRefined) {
		this.m_UseRefined = m_UseRefined;
	}

	/**
	 * Tip text for number of individuals.
	 * 
	 * @return Tip text for number of individuals.
	 */
	public String numIndividualsTipText() {
		return "Number of individuals for the initial population.";
	}

	/**
	 * Set the number of individuals for the initial population of the
	 * evolutionary algorithm.
	 * 
	 * @param num
	 *            Number of individuals for the initial population of the
	 *            evolutionary algorithm.
	 */
	public void setNumIndividuals(int num) {
		m_NumIndividuals = num;
	}

	/**
	 * Get the number of individuals for the initial population of the
	 * evolutionary algorithm.
	 * 
	 * @return Number of individuals for the initial population of the
	 *         evolutionary algorithm.
	 */
	public int getNumIndividuals() {
		return m_NumIndividuals;
	}

	/**
	 * Returns scale to normalize
	 * 
	 * @return
	 */
	public double getScale() {
		return m_Scale;
	}

	/**
	 * Sets scale to normalize
	 * 
	 * @param nScale
	 */
	public void setScale(double m_Scale) {
		this.m_Scale = m_Scale;
	}

	/**
	 * Returns translation to normalize
	 * 
	 * @return
	 */
	public double getTranslation() {
		return m_Translation;
	}

	/**
	 * Sets translation to normalize
	 * 
	 * @param nTranslation
	 */
	public void setTranslation(double m_Translation) {
		this.m_Translation = m_Translation;
	}

	
	private String getValueClass() {
		return m_ValueClass;
	}

	private void setValueClass(String m_Class) {
		this.m_ValueClass = m_Class;
	}	
	
	/**
	 * Classifies an instance.
	 * 
	 * @param instance
	 *            the instance to classify
	 * @return the classification for the instance
	 * @throws Exception
	 *             if instance can't be classified successfully
	 */
	@Override
	public double classifyInstance(Instance instance) throws Exception {
		Instance instance_copy = (Instance) instance.copy();

		for (EDERSD_Rule rule : getRules()) {
			boolean is_contained = isUseRefined() ? rule.checkRefined(instance_copy) : rule.check(instance_copy);

			if (is_contained) {
				return rule.getRuleClass();
			}
		}

		throw new Exception("The instance couldn't be classified");
	}

	/**
	 * Returns class probabilities for an instance.
	 * 
	 * @param instance
	 *            the instance to calculate the class probabilities for
	 * @return the class probabilities
	 * @throws Exception
	 *             if distribution can't be computed successfully
	 */
	@Override
	public final double[] distributionForInstance(Instance instance)
			throws Exception {

		if (getRules().isEmpty()) {
			throw new Exception("There are no rules.");
		}

		double num_classes = 0.0;
		double[] dist = new double[instance.classAttribute().numValues()];
		double factor = 1.0;

		Instance instance_copy = (Instance) instance.copy();

		for (EDERSD_Rule rule : getRules()) {
			boolean is_contained = isUseRefined() ? rule.checkRefined(instance_copy) : rule.check(instance_copy);

			if (is_contained) {
				dist[(int) rule.getRuleClass()] += factor;// 1.0;
				num_classes += factor;

				factor *= 0.5;
			}
		}

		if (num_classes > 0.0) {
			for (int i = 0; i < instance.classAttribute().numValues(); i++) {
				dist[i] /= num_classes;
			}
		}

		return dist;
	}

	/**
	 * Parses a given list of options.
	 * 
	 * Valid options are:
	 * <p/>
	 * 
	 * <pre>
	 * -N
	 *  Use not refined rules.
	 * </pre>
	 * 
	 * <pre>
	 * -I &lt;number of individuals&gt;
	 *  Set the number of individuals for the evolutionary algorithm population.
	 *  (default 400)
	 * </pre>
	 * 
	 * <pre>
	 * -C &lt;number of individuals&gt;
	 *  Set the number of individuals that will be copied in the next population.
	 *  (default 40)
	 * </pre>
	 * 
	 * <pre>
	 * -G &lt;number of generations&gt;
	 *  Set the number of generations for the evolutionary algorithm.
	 *  (default 500)
	 * </pre>
	 * 
	 * <pre>
	 * -S
	 *  Enables scalation.
	 *  (default false)
	 * </pre>
	 * 
	 * <pre>
	 * -M &lt;scale minimum&gt;
	 *  Sets the minimum for scaling.
	 *  (default 1)
	 * </pre>
	 * 
	 * <pre>
	 * -X &lt;scale maximum&gt;
	 *  Sets the maximum for scaling.
	 *  (default 20)
	 * </pre>
	 * 
	 * <pre>
	 * -R &lt;random seed&gt;
	 *  Sets the random seed.
	 * </pre>
	 * 
	 * <pre>
	 * -E &lt;error percentage&gt;
	 *  Sets the error percentage.
	 *  (default 0.2)
	 * </pre>
	 * 
	 * <pre>
	 * -D
	 *  If set, classifier is run in debug mode and
	 *  may output additional info to the console
	 * </pre>
	 * 
	 * @param options
	 *            the list of options as an array of strings
	 * @throws Exception
	 *             if an option is not supported
	 */
	@Override
	public void setOptions(String[] options) throws Exception {

		setDebug(Utils.getFlag('D', options));

		String errorPercentage = Utils.getOption("E", options);
		if (errorPercentage.length() != 0)
			setErrorPercentage(Double.parseDouble(errorPercentage));

		String cross = Utils.getOption("X", options);
		if (cross.length() != 0)
			setCrossover((Crossover) Utils.forName(Crossover.class, cross,
					options));

		String mut = Utils.getOption("M", options);
		if (mut.length() != 0)
			setMutation((Mutation) Utils.forName(Mutation.class, mut, options));

		String sel = Utils.getOption("S", options);
		if (sel.length() != 0)
			setSelection((Selection) Utils.forName(Selection.class, sel,
					options));

		String fit = Utils.getOption("F", options);
		if (fit.length() != 0)
			setFitness((Fitness) Utils.forName(Fitness.class, fit, options));

		String numIndividuals = Utils.getOption("I", options);
		if (numIndividuals.length() != 0)
			setNumIndividuals(Integer.parseInt(numIndividuals));

		String numGenerations = Utils.getOption("G", options);
		if (numGenerations.length() != 0)
			setNumGenerations(Integer.parseInt(numGenerations));

		numIndividuals = Utils.getOption("C", options);
		if (numIndividuals.length() != 0)
			setNumCopiedIndividuals(Integer.parseInt(numIndividuals));

		String randomSeed = Utils.getOption("A", options);
		if (randomSeed.length() != 0)
			setRandomSeed(Long.parseLong(randomSeed));

		String scale = Utils.getOption("SC", options);
		if (scale.length() != 0)
			setScale(Double.parseDouble(scale));

		String translation = Utils.getOption("TR", options);
		if (translation.length() != 0)
			setTranslation(Double.parseDouble(translation));

		setUseRefined(Utils.getFlag('R', options));

		// String searchAlgorithmName = Utils.getOption('Q', options);
		// if (searchAlgorithmName.length() != 0) {
		// // setSearchAlgorithm(
		// // (SearchAlgorithm) Utils.forName(
		// // SearchAlgorithm.class,
		// // searchAlgorithmName,
		// // //partitionOptions(options)));
		// }
		// else {
		// //setSearchAlgorithm(new k2());
		// }

	}

	/**
	 * Gets the current settings of the EDERSD classifier.
	 * 
	 * @return an array of strings suitable for passing to setOptions
	 */
	public String[] getOptions() {

		String[] crossOptions = getCrossover().getOptions();
		String[] mutOptions = getMutation().getOptions();
		String[] selOptions = getSelection().getOptions();
		String[] fitOptions = getFitness().getOptions();

		String[] options = new String[50];
		int current = 0;

		if (getDebug()) {
			options[current++] = "-D";
		}

		options[current++] = "-E";
		options[current++] = "" + getErrorPercentage();
		options[current++] = "-X";
		options[current++] = "" + getCrossover().getClass().getName();
		options[current++] = "--";
		for (int iOption = 0; iOption < crossOptions.length; iOption++) {
			options[current++] = crossOptions[iOption];
		}

		options[current++] = "-M";
		options[current++] = "" + getMutation().getClass().getName();
		options[current++] = "--";
		for (int iOption = 0; iOption < mutOptions.length; iOption++) {
			options[current++] = mutOptions[iOption];
		}

		options[current++] = "-S";
		options[current++] = "" + getSelection().getClass().getName();
		options[current++] = "--";
		for (int iOption = 0; iOption < selOptions.length; iOption++) {
			options[current++] = selOptions[iOption];
		}

		options[current++] = "-F";
		options[current++] = "" + getFitness().getClass().getName();
		options[current++] = "--";
		for (int iOption = 0; iOption < fitOptions.length; iOption++) {
			options[current++] = fitOptions[iOption];
		}

		options[current++] = "-I";
		options[current++] = "" + getNumIndividuals();
		options[current++] = "-G";
		options[current++] = "" + getNumGenerations();
		options[current++] = "-C";
		options[current++] = "" + getNumCopiedIndividuals();
		options[current++] = "-A";
		options[current++] = "" + getRandomSeed();
		options[current++] = "-SC";
		options[current++] = "" + getScale();
		options[current++] = "-TR";
		options[current++] = "" + getTranslation();

		if (isUseRefined()) {
			options[current++] = "-R";
		}

		while (current < options.length) {
			options[current++] = "";
		}

		return options;
	}

	/**
	 * Returns an enumeration describing the available options.
	 * 
	 * Valid options are:
	 * <p/>
	 * 
	 * <pre>
	 * -N
	 *  Use not refined rules.
	 * </pre>
	 * 
	 * <pre>
	 * -I &lt;number of individuals&gt;
	 *  Set the number of individuals for the evolutionary algorithm population.
	 *  (default 400)
	 * </pre>
	 * 
	 * <pre>
	 * -C &lt;number of individuals&gt;
	 *  Set the number of individuals that will be copied in the next population.
	 *  (default 40)
	 * </pre>
	 * 
	 * <pre>
	 * -G &lt;number of generations&gt;
	 *  Set the number of generations for the evolutionary algorithm.
	 *  (default 500)
	 * </pre>
	 * 
	 * <pre>
	 * -S
	 *  Enables scalation.
	 *  (default false)
	 * </pre>
	 * 
	 * <pre>
	 * -M &lt;scale minimum&gt;
	 *  Sets the minimum for scaling.
	 *  (default 1)
	 * </pre>
	 * 
	 * <pre>
	 * -X &lt;scale maximum&gt;
	 *  Sets the maximum for scaling.
	 *  (default 20)
	 * </pre>
	 * 
	 * <pre>
	 * -R &lt;random seed&gt;
	 *  Sets the random seed.
	 * </pre>
	 * 
	 * <pre>
	 * -E &lt;error percentage&gt;
	 *  Sets the error percentage.
	 *  (default 0.2)
	 * </pre>
	 * 
	 * <pre>
	 * -D
	 *  If set, classifier is run in debug mode and
	 *  may output additional info to the console
	 * </pre>
	 * 
	 * @return an enumeration of all the available options.
	 */
	@Override
	public Enumeration<Option> listOptions() {

		Vector<Option> newVector = new Vector<Option>();

		newVector.addElement(new Option("\tEnables debug mode.\n"
				+ "\t(default false)", "D", 0, "-D"));
		newVector.addElement(new Option("\tSet the error percentage.\n"
				+ "\t(default 0.2)", "E", 1, "-E <error percentage>"));

		newVector.addElement(new Option("\tSet the crossover operator.\n"
				+ "\t(default Crossover1)", "X", 1, "-E <crossover>"));
		newVector.addElement(new Option("\tSet the mutation operator.\n"
				+ "\t(default Mutate1)", "X", 1, "-E <mutate>"));
		newVector.addElement(new Option("\tSet the selection operator.\n"
				+ "\t(default Select1)", "X", 1, "-E <selection>"));
		newVector.addElement(new Option("\tSet the fitness operator.\n"
				+ "\t(default WRACC)", "X", 1, "-E <fitness>"));
		newVector.addElement(new Option(
				"\tSet the number of individuals for the evolutionary algorithm population.\n"
						+ "\t(default 400)", "I", 1,
				"-I <number of individuals>"));
		newVector.addElement(new Option(
				"\tSet the number of generations for the evolutionary algorithm.\n"
						+ "\t(default 500)", "G", 1,
				"-G <number of generations>"));
		newVector.addElement(new Option(
				"\tSet the number of individuals that will be copied in the next population.\n"
						+ "\t(default 40)", "C", 1,
				"-C <number of individuals>"));
		newVector.addElement(new Option("\tSet the random seed.\n", "R", 1,
				"-R <random seed>"));
		newVector.addElement(new Option("\tSet value of scale.\n"
				+ "\t(default 20.0)", "SC", 0, "-SC"));
		newVector.addElement(new Option("\tSet value of translation.\n"
				+ "\t(default 0)", "M", 1, "-M <translation>"));
		newVector.addElement(new Option("\tUse not refined rules.", "N", 0,
				"-N"));

		return newVector.elements();
	}

	/**
	 * Returns a string describing classifier
	 * 
	 * @return a description suitable for displaying in the
	 *         explorer/experimenter gui
	 */
	public String globalInfo() {

		return "Global information\n\n" + getTechnicalInformation().toString();
	}

	/**
	 * Returns an instance of a TechnicalInformation object, containing detailed
	 * information about the technical background of this class, e.g., paper
	 * reference or book this class is based on.
	 * 
	 * @return the technical information about this class
	 */
	public TechnicalInformation getTechnicalInformation() {
		TechnicalInformation result;

		result = new TechnicalInformation(Type.BOOK);
		result.setValue(Field.AUTHOR, "Marta Hernando San Juan");
		result.setValue(Field.YEAR, "2011");
		result.setValue(Field.TITLE, "EDERSD");
		result.setValue(Field.PDF,
				"Evolutionary Learning of Hierarchical Decision Rules");
		// result.setValue(Field.PUBLISHER, "Morgan Kaufmann Publishers");
		// result.setValue(Field.ADDRESS, "San Mateo, CA");

		return result;
	}

	/**
	 * Returns the revision string.
	 * 
	 * @return the revision
	 */
	public String getRevision() {
		return RevisionUtils.extract("$Revision: 0.10 $");
	}

	/**
	 * Returns the capabilities of the classifier.
	 * 
	 * @return the capabilities
	 */
	@Override
	public Capabilities getCapabilities() {
		Capabilities result = super.getCapabilities(); // returns the object
														// from
														// weka.classifiers.Classifier
		result.disableAll();

		// attributes
		result.enable(Capability.NUMERIC_ATTRIBUTES);
		result.enable(Capability.MISSING_VALUES);
		result.enable(Capability.NOMINAL_ATTRIBUTES);

		// class
		result.enable(Capability.NOMINAL_CLASS);
		result.enable(Capability.MISSING_CLASS_VALUES);
		return result;
	}

	/**
	 * Main method for testing this class
	 * 
	 * @param args
	 *            the commandline parameters
	 */
	public static void main(String[] args) {
		runClassifier(new EDERSD(), args);
	}
	

	/**
	 * Returns set of instances
	 * 
	 * @return
	 */
	public Instances getDataSetInstances() {
		return m_DataSetInstances;
	}

	/**
	 * Sets set of instances
	 * 
	 * @param inst
	 */
	public void setDataSetInstances(Instances inst) {
		m_DataSetInstances = inst;
	}

	/**
	 * Returns set of rules
	 * 
	 * @return
	 */
	public ArrayList<EDERSD_Rule> getRules() {
		return m_Rules;
	}

	/**
	 * Sets rule
	 * 
	 * @param rules
	 */
	public void setRule(EDERSD_Rule rules) {
		m_Rules.add(rules);
	}

	/**
	 * Sets rules
	 * 
	 * @param rules
	 */
	public void setRules(ArrayList<EDERSD_Rule> rules) {
		m_Rules = rules;
	}
	
	
	/**
	 * Returns the decreased percentage.
	 * @return m_DecreasedPercentage
	 */
	public double getDecreasedPercentage() {
		return m_DecreasedPercentage;
	}

	/**
	 * Sets the decreased percentage.
	 * @param m_DecreasedPercentage
	 */
	public void setDecreasedPercentage(double m_DecreasedPercentage) {
		this.m_DecreasedPercentage = m_DecreasedPercentage;
	}	
}
