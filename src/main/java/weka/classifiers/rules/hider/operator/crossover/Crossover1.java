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
 *    Crossover1.java
 *    Copyright (C) 2011 Marta Hernando, Daniel Rodriguez. Universidad de Alcala
 *
 */
package weka.classifiers.rules.hider.operator.crossover;

import java.util.Enumeration;
import java.util.Vector;

import weka.attributeSelection.StartSetHandler;
import weka.classifiers.rules.hider.gene.*;
import weka.classifiers.rules.hider.*;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.SelectedTag;
import weka.core.Utils;

/**
 * Class that represents a type of crossover.
 */
public class Crossover1 extends Crossover implements OptionHandler, StartSetHandler{


	/**
	 * Constructor.
	 */
	public Crossover1() {}


	/**
	 * Makes crossover between 2 individuals.
	 * 
	 * @param new_individual New rule to be created.
	 * @param parent1 Parent rule.
	 * @param parent2 Parent rule.
	 * @throws Exception 
	 */
	public HIDER_Rule doCrossover(Instances dataSetInstances,
			HIDER_Rule parent1, HIDER_Rule parent2, HIDER_Rule son) throws Exception {

		int m_NumAttributes = dataSetInstances.numAttributes();
		int m_ClassIndex = dataSetInstances.classIndex();

		//If both individuals to crossing have the same class...
		if (parent1.getRuleClass() == parent2.getRuleClass()) {
			HIDER_Rule rule = new HIDER_Rule(parent1.getRuleClass());
			rule.grow(dataSetInstances);
			son = rule;

			//The values of the son are the average of the parents
			for (int i = 0; i < m_NumAttributes; i++) {
				if (i != m_ClassIndex) {
					if (son.getGene(i).isNumeric()) {
						((Continuous) son.getGene(i)).setLowerLimit((((Continuous) parent1.getGene(i)).getLowerLimit() + ((Continuous) parent2.getGene(i)).getLowerLimit()) / 2.0f);										
						((Continuous) son.getGene(i)).setUpperLimit((((Continuous) parent1.getGene(i)).getUpperLimit() + ((Continuous) parent2.getGene(i)).getUpperLimit()) / 2.0f);						
					} else if (son.getGene(i).isNominal()) {
						((Discrete) son.getGene(i)).setBits(((Discrete) parent1.getGene(i)).getBits());
					}
				}
			}
		} else { //If they don't have the same class...
			//The son has the values of the first father
			for (int i = 0; i < m_NumAttributes; i++) {
				if (i != m_ClassIndex) {
					if (son.getGene(i).isNumeric()) {
						((Continuous) son.getGene(i)).setLowerLimit(((Continuous) parent1.getGene(i)).getLowerLimit());
						((Continuous) son.getGene(i)).setUpperLimit(((Continuous) parent1.getGene(i)).getUpperLimit());
					} else if (son.getGene(i).isNominal()) {
						/*-------------IMPLEMENTAR BINARIO----------------------*/
					}
				}
			}
		}

		return son;
	} // doCrossover

	/**
	 * Executes the operation
	 * 
	 * @param object An object containing an array of two GASUD_Rules
	 * @return An object containing an array with the offSprings
	 * @throws Exception 
	 */
	public Object execute(Object object) throws Exception {
		HIDER_Rule[] parents = (HIDER_Rule[]) object;
		HIDER_Rule son = parents[0];

		Instances dataSetInstances = (Instances) getParameter("instances");

		HIDER_Rule offSpring;
		offSpring = doCrossover(dataSetInstances, parents[1], parents[2], son);

		return offSpring;
	} // execute















	@Override
	public Enumeration listOptions() {
		Vector<Option> newVector = new Vector<Option>();

		return newVector.elements();
	}

	@Override
	public void setOptions(String[] options) throws Exception {

	}

	@Override
	public String[] getOptions() {
		String[] options = new String[0];
		return options;
	}

	@Override
	public void setStartSet(String startSet) throws Exception {
	}

	@Override
	//Cuando se pulsa sobre el operador se muestra esta ventana para cambiar los parametros
	public String getStartSet() {
		return null;
	}

	@Override
	public String getRevision() {
		// TODO Auto-generated method stub
		return null;
	}





}
