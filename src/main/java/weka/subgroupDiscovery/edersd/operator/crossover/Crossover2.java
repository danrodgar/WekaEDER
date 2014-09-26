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
 *    Crossover2.java
 *    Copyright (C) 2011 Marta Hernando, Daniel Rodriguez. Universidad de Alcala
 *
 */
package weka.subgroupDiscovery.edersd.operator.crossover;

import java.util.*;

import weka.attributeSelection.StartSetHandler;
import weka.subgroupDiscovery.edersd.gene.*;
import weka.subgroupDiscovery.edersd.*;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Utils;

/**
 * Class that represents a type of crossover.
 */
public class Crossover2 extends Crossover implements OptionHandler, StartSetHandler{
    
	/**
	 * Constructor.
	 */
	public Crossover2() {}
    
	/**
	 * Makes crossover between 2 individuals.
	 * 
	 * @param new_individual New rule to be created.
	 * @param parent1 Parent rule.
	 * @param parent2 Parent rule.
	 * @throws Exception 
	 */
	public EDERSD_Rule doCrossover(Instances dataSetInstances,
			EDERSD_Rule parent1, EDERSD_Rule parent2, Random randomGenerator) throws Exception {
		
		float p;
		double anterior;
		int m_NumAttributes = dataSetInstances.numAttributes();
		int m_ClassIndex = dataSetInstances.classIndex();

		EDERSD_Rule son = new EDERSD_Rule(parent1.getRuleClass());
		son.grow(dataSetInstances);

		//The values of the new individual will be selected each one of random form of a father
		for (int i = 0; i < m_NumAttributes; i++) {
			if (i != m_ClassIndex) {
				if (son.getGene(i).isNumeric()) {

					p = randomGenerator.nextFloat();
					if (p < 0.5) {
						((Continuous) son.getGene(i)).setLowerLimit(((Continuous) parent1.getGene(i)).getLowerLimit());
					} else {
						((Continuous) son.getGene(i)).setLowerLimit(((Continuous) parent2.getGene(i)).getLowerLimit());
					}

					p = randomGenerator.nextFloat();
					if (p < 0.5) {
						((Continuous) son.getGene(i)).setUpperLimit(((Continuous) parent1.getGene(i)).getUpperLimit());
					} else {
						((Continuous) son.getGene(i)).setUpperLimit(((Continuous) parent2.getGene(i)).getUpperLimit());
					}
				} else if (son.getGene(i).isNominal()) {

					p = randomGenerator.nextFloat();
					if (p < 0.5) {
						((Discrete) son.getGene(i)).setBits(((Discrete) parent1.getGene(i)).getBits());
					} else {
						((Discrete) son.getGene(i)).setBits(((Discrete) parent2.getGene(i)).getBits());
					}
				}
			}
		}

		//
		for (int j = 0; j < m_NumAttributes; j++) {
			if (j != m_ClassIndex) {
				if (son.getGene(j).isNumeric()) {
					if (((Continuous) son.getGene(j)).getUpperLimit() < ((Continuous) son.getGene(j)).getLowerLimit()) {
						anterior = ((Continuous) son.getGene(j)).getLowerLimit();
						((Continuous) son.getGene(j)).setLowerLimit(((Continuous) son.getGene(j)).getUpperLimit());
						((Continuous) son.getGene(j)).setUpperLimit(anterior);
					}
				} else if (son.getGene(j).isNominal()) {
					/*-------------IMPLEMENTAR BINARIO----------------------*/
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
		EDERSD_Rule[] parents = (EDERSD_Rule[]) object;

		Instances dataSetInstances = (Instances) getParameter("instances");
		Random randomGenerator = (Random) getParameter("random");

		EDERSD_Rule offSpring;
		offSpring = doCrossover(dataSetInstances, parents[1], parents[2],randomGenerator);

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
		return null;
	}


}
