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
 *    SelectionFitnessWeight.java
 *    Copyright (C) 2011 Marta Hernando, Daniel Rodriguez. Universidad de Alcala
 *
 */
package weka.subgroupDiscovery.edersd.operator.selection;

import java.util.*;

import weka.attributeSelection.StartSetHandler;
import weka.classifiers.rules.hider.operator.mutation.Mutate1;
import weka.classifiers.rules.hider.operator.mutation.Mutation;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Utils;

/**
 * Class that represents a type of selection.
 */
public class SelectionFitnessWeight extends Selection implements OptionHandler, StartSetHandler{
	
	/**
	 * Constructor.
	 */
	public SelectionFitnessWeight() {}


	/**
	 * Fitness-Weight selection.
	 * 
	 * @return The index of the selected individual.
	 * @throws Exception 
	 */
	public int doSelect(double[] populationFitness, int m_NumIndividuals,Random randomGenerator) throws Exception {
		
		int valor = 0;
		double prob = 0.0, tot = 0.0;

		prob = randomGenerator.nextDouble();
		tot = 0.0;
		for (int i = 0; i < m_NumIndividuals; i++) {
			tot = tot + populationFitness[i];
			if (tot > prob) {
				valor = i;
				break;
			}
		}

		if (valor >= m_NumIndividuals) {
			System.out.printf("Error escogido valor mayor de N %d", valor);
			 throw new
			 Exception("Error: selected value greater than number of individuals: "
			 + String.valueOf(valor));
		}

		return valor;
	}

	/**
	 * Executes the operation.
	 * 
	 * @param object An object containing an array of two HIDER_Rule
	 * @return An object containing an array with the offSprings
	 * @throws Exception 
	 */
	public Object execute(Object object) throws Exception {
		double[] population = (double[]) object;

		int m_NumIndividuals = (Integer) getParameter("numIndividuals");
		Random randomGenerator = (Random) getParameter("random");

		int offSpring;
		offSpring = doSelect(population, m_NumIndividuals, randomGenerator);

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