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
 *    GeneticAlgorithm.java
 *    Copyright (C) 2011 Marta Hernando, Daniel Rodriguez. Universidad de Alcala
 *
 */
package weka.classifiers.rules.hider.algorithm;

import java.util.*;
import weka.core.*;
import weka.classifiers.rules.hider.*;
import weka.classifiers.rules.hider.evaluation.*;
import weka.classifiers.rules.hider.gene.*;

public class GeneticAlgorithm extends Algorithm {
	
	/**
	 * Stores the fitness funtion.
	 */
	private Fitness fit;

	/**
	 * Constructor
	 * 
	 * @param fitness
	 */
	public GeneticAlgorithm(Fitness fitness) {
		this.fit = fitness;
	}// GeneticAlgorithm

	@Override
	public HIDER_Rule execute() throws Exception {
		
		int ind, ind1, ind2;
		float prob;
		float m_ProbMutation;

		// Read the parameters
		double class_value = (Integer) getInputParameter("class_value");
		ArrayList<Instance> m_Data = ((ArrayList<Instance>) getInputParameter("m_Data"));
		int m_NumIndividuals = ((Integer) getInputParameter("numIndividuals")).intValue();
		int m_NumGenerations = ((Integer) getInputParameter("numGenerations")).intValue();
		int m_NumCopiedIndividuals = ((Integer) getInputParameter("numCopiedIndividuals")).intValue();
		Instances dataSetInstances = ((Instances) getInputParameter("dataSetInstances"));
		HIDER_Rule m_RuleMaxMin = ((HIDER_Rule) getInputParameter("ruleMaxMin"));
		Random m_RandomGenerator = (Random) getInputParameter("random");

		// Initialize the variables
		int m_NumAttributes = dataSetInstances.numAttributes();
		int m_ClassIndex = dataSetInstances.classIndex();
		double[] m_PopulationFitness = new double[m_NumIndividuals];

		// Read the operators
		Operator sel = operators_.get("selection");
		Operator mut1 = operators_.get("mutation");
		Operator cros1 = operators_.get("crossover");

		// Initialize the populations
		Population m_CurrentPopulation = new Population(m_NumIndividuals,class_value, dataSetInstances);
		Population m_NextPopulation = new Population(m_NumIndividuals,class_value, dataSetInstances);
		m_CurrentPopulation.initPopulation(class_value, m_Data,m_RandomGenerator, m_RuleMaxMin);
		
			
		// Generations ...
		for (int ii = 0; ii < m_NumGenerations; ii++) {
			m_ProbMutation = 10.0f - 0.03f * ii;
			
			// Calculate fitness
			m_PopulationFitness = m_CurrentPopulation.calculatePopulationFitness(m_Data, fit);			

			//Selects the element with more fitness
			int best_individual = m_CurrentPopulation.getBestIndividual();
			
			//The best individual is copied twice initially of the next population
			m_NextPopulation.add(0, m_CurrentPopulation.get(best_individual).clone());		
			m_NextPopulation.add(1, m_CurrentPopulation.get(best_individual).clone());
					
			//The best individual is copied n-times in the next population
			for (int j = 2; j < m_NumCopiedIndividuals; j++) {
				ind = (Integer) sel.execute(m_PopulationFitness);
				m_NextPopulation.add(j, m_CurrentPopulation.get(ind).clone());							
			}

			//---------------CROSSOVER----------------------------------------------
			for (int j = m_NumCopiedIndividuals; j < m_NumIndividuals; j++) {
				HIDER_Rule rul_cross = null;
				do
				{
					
				//Obtain parents
				ind1 = (Integer) sel.execute(m_PopulationFitness);
				ind2 = (Integer) sel.execute(m_PopulationFitness);

				//Generates two parents
				HIDER_Rule[] parent = new HIDER_Rule[3];
				parent[0] = m_NextPopulation.get(j); //The son is copied
				parent[1] = m_CurrentPopulation.get(ind1);
				parent[2] = m_CurrentPopulation.get(ind2);
							
					if (ind1 < m_NumIndividuals && ind2 < m_NumIndividuals && 0 <= ind1 && 0 <= ind2) {
						rul_cross=(HIDER_Rule) cros1.execute(parent);
					} else {
						parent[0] = m_CurrentPopulation.get(0);
						parent[1] = m_CurrentPopulation.get(m_RandomGenerator.nextInt(m_NumIndividuals - 1) + 1);
						
						rul_cross=(HIDER_Rule) cros1.execute(parent);
					}			
				}
				while (!rul_cross.cover(m_Data));
				
				m_NextPopulation.add(j,rul_cross);							
			}

			//--------MUTATION---------------------------------------------					
			for (int j = 1; j < m_NumIndividuals; j++) {

				HIDER_Rule rul_mut = null;
				prob = m_RandomGenerator.nextFloat()*10;
				if (prob < m_ProbMutation) {	
					rul_mut=(HIDER_Rule) mut1.execute(m_NextPopulation.get(j));
					if (rul_mut.cover(m_Data))
					{
						m_NextPopulation.add(j, rul_mut);
					}					
				}												
			}
			
			m_CurrentPopulation = new Population(m_NumIndividuals,class_value, dataSetInstances);
			
			//--------TO ASSIGN NEXT POPULATION TO CURRENT POPULATION-----------------------------
			for (int i = 0; i < m_NumIndividuals; i++) {
				
				if (m_NextPopulation.get(i) == null) {					
					HIDER_Rule hr=new HIDER_Rule(m_CurrentPopulation.get(i).getRuleClass());
					hr.grow(dataSetInstances);
					m_NextPopulation.add(i,hr);
				}
				
				for (int j = 0; j < m_NumAttributes; j++) {									
					if (j != m_ClassIndex) {
						
						if (m_NextPopulation.get(i).getGene(j) instanceof Numeric) {
						
							if (((Continuous) m_NextPopulation.get(i).getGene(j)).getLowerLimit() < ((Continuous) m_RuleMaxMin.getGene(j)).getLowerLimit()) 
							{
								((Continuous) m_CurrentPopulation.get(i).getGene(j)).setLowerLimit(((Continuous) m_RuleMaxMin.getGene(j)).getLowerLimit());
							} else {
								((Continuous) m_CurrentPopulation.get(i).getGene(j)).setLowerLimit(((Continuous) m_NextPopulation.get(i).getGene(j)).getLowerLimit());
							}

							if (((Continuous) m_NextPopulation.get(i).getGene(j)).getUpperLimit() > ((Continuous) m_RuleMaxMin.getGene(j)).getUpperLimit()) 
							{
								((Continuous) m_CurrentPopulation.get(i).getGene(j)).setUpperLimit(((Continuous) m_RuleMaxMin.getGene(j)).getUpperLimit());
							} else {
								((Continuous) m_CurrentPopulation.get(i).getGene(j)).setUpperLimit(((Continuous) m_NextPopulation.get(i).getGene(j)).getUpperLimit());
							}

							if (((Continuous) m_CurrentPopulation.get(i).getGene(j)).getLowerLimit() > ((Continuous) m_CurrentPopulation.get(i).getGene(j)).getUpperLimit()) 
							{							
								System.out.printf("ERROR: %d %d", i, j);

								throw new Exception("Error individual "+ String.valueOf(i) + ", attribute "+ String.valueOf(j));
							}							
		
						} else if (m_NextPopulation.get(i).getGene(j) instanceof Nominal) {																										
							((Discrete) m_CurrentPopulation.get(i).getGene(j)).setBits(((Discrete) m_NextPopulation.get(i).getGene(j)).getBits());		
						}
					}
				}	
				m_CurrentPopulation.get(i).printRule();					
			}
		}
		return m_NextPopulation.get(0);
	}
}
