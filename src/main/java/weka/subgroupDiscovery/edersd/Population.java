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
 *    Population.java
 *    Copyright (C) 2011 Marta Hernando, Daniel Rodriguez. Universidad de Alcala
 *
 */
package weka.subgroupDiscovery.edersd;

import java.io.*;
import java.util.*;

import weka.classifiers.rules.hider.HIDER_Rule;
import weka.core.*;
import weka.subgroupDiscovery.edersd.evaluation.*;
import weka.subgroupDiscovery.edersd.gene.*;

public class Population implements Serializable {

	private static final long serialVersionUID = 1L;
	private EDERSD_Rule[] EDERSD_RulesList; 	// Stores a list of <code>edersd_Rule</code> objects.
	private int m_NumIndividuals; 			// Maximum size of the population
	private double m_Target_class;			// Value of class was selected.
	private double[] m_PopulationFitness;	// Value of fitness.
	private Instances dataSetInstances;

	public Population(int numIndividuals, double target_class, Instances dataSetInstances) throws Exception {

		this.dataSetInstances=dataSetInstances;
		EDERSD_RulesList = new EDERSD_Rule[numIndividuals];
		
		for (int i=0;i<EDERSD_RulesList.length;i++)
		{
			EDERSD_RulesList[i]= new EDERSD_Rule(target_class);
			EDERSD_RulesList[i].grow(dataSetInstances);
		}
		
		m_NumIndividuals = numIndividuals;
		m_Target_class = target_class;
		m_PopulationFitness = new double[m_NumIndividuals];
	}

	/**
	 * Inits the population according to a target class.
	 * 
	 * @param target_class Target class to build the rules.
	 * @throws Exception 
	 */
	public void initPopulation(double target_class, ArrayList<Instance> m_Data,Random m_RandomGenerator, EDERSD_Rule ruleMaxMin) throws Exception {
		
		int q = 0;
		double l, u,range,max,min;
		Instance instance;
		int m_NumAttributes = dataSetInstances.numAttributes();
		int m_ClassIndex = dataSetInstances.classIndex();

		double value=0.0;
		for (int j = 0; j < m_NumIndividuals; j++) 
		{		
			//To select an instance corresponding to the value of the selected class
			do {
				q = m_RandomGenerator.nextInt(m_Data.size());
				instance = m_Data.get(q);			
			} while ((instance.classValue() != (target_class)));

			EDERSD_RulesList[j] = new EDERSD_Rule(instance.classValue());
			EDERSD_RulesList[j].grow(dataSetInstances);
			
			for (int a = 0; a < m_NumAttributes; a++) {
				if (a != m_ClassIndex) {					
					if (EDERSD_RulesList[j].getGene(a).isNumeric()) 
					{
						max=((Continuous) ruleMaxMin.getGene(a)).getUpperLimit();
						min=((Continuous) ruleMaxMin.getGene(a)).getLowerLimit();
						range=max - min;
						l = instance.value(a)- range/ (100.0f) * m_RandomGenerator.nextFloat();
									
						if (l < min) {
							l = min;
						}

						u = instance.value(a)+ range/ (100.0f) * m_RandomGenerator.nextFloat();
						if (u > max) {
							u = max;
						}

						if (l < u) {
							((Continuous) EDERSD_RulesList[j].getGene(a)).setLowerLimit(l);
							((Continuous) EDERSD_RulesList[j].getGene(a)).setUpperLimit(u);
						} else {
							((Continuous) EDERSD_RulesList[j].getGene(a)).setLowerLimit(u);
							((Continuous) EDERSD_RulesList[j].getGene(a)).setUpperLimit(l);
						}
					} else if (EDERSD_RulesList[j].getGene(a).isNominal()) {
						/*The same attributes that are generated by the aleatory instance are placed 
						and the other bits are mutated with certain probability*/
						((Discrete) EDERSD_RulesList[j].getGene(a)).setNominalValue(instance.stringValue(a));
					}
				}
			}
		}
	}

	/**
	 * Calculates the fitness value of the current population.
	 */
	public double[] calculatePopulationFitness(ArrayList<Instance> m_Data,Fitness fit) {
		
		double suma, a;
		suma = 0.0;
		int aux=1;
		for (int i = 0; i < m_NumIndividuals; i++) {
			
		a=0.0;	
			
			//Patron Strategy			
			Context ctx= new Context(fit,m_Data,m_Target_class);
			ctx.setStrategy(fit);		
			a = ctx.executeStrategy(EDERSD_RulesList[i]);			
		
			if (a != -1.0) {				
				m_PopulationFitness[i] = 1.0 / (1.0 + a);				
			} else {
				m_PopulationFitness[i] = 0.0;
			}
			suma += m_PopulationFitness[i];
		
		}

		for (int i = 0; i < m_NumIndividuals; i++) {
			m_PopulationFitness[i] /= suma;
			EDERSD_RulesList[i].setFitness(m_PopulationFitness[i]);
		}

		return m_PopulationFitness;
	}

	/**
	 * Returns the index in the current population that indicates the best individual (rule).
	 * 
	 * @return Returns the index in the current population that indicates the
	 *         best individual (rule).
	 */
	public int getBestIndividual() {	
		
		//System.out.println("The best element of population: "+Utils.maxIndex(this.m_PopulationFitness));
		return Utils.maxIndex(m_PopulationFitness);
	}

	/**
	 * Returns an individual of the population.
	 * @param index
	 * @param rule
	 */
	public void add(int index, EDERSD_Rule rule) {
		EDERSD_RulesList[index] = rule;
	}

	/**
	 * Sets an individual of the population.
	 * @param i
	 * @return edersd_Rule
	 */
	public EDERSD_Rule get(int i) {
		return EDERSD_RulesList[i];
	}

}// Population