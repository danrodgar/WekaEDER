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
 *    FunctionFitnessNormal.java
 *   Copyright (C) 2011 Marta Hernando, Daniel Rodriguez. Universidad de Alcala
 *
 */
package weka.classifiers.rules.hider.evaluation;

import java.util.*;

import weka.attributeSelection.StartSetHandler;
import weka.classifiers.rules.hider.*;
import weka.classifiers.rules.hider.operator.crossover.Crossover;
import weka.classifiers.rules.hider.operator.crossover.Crossover1;
import weka.core.*;

public class FunctionFitnessNormal extends Fitness implements OptionHandler, StartSetHandler{

	
	/**
	 * Constructor.
	 */
	public FunctionFitnessNormal() {}
    

	/**
	 * Calculates the fitness value of a given rule.
	 * 
	 * @param rule Rule.
	 * @return Fitness value of the given rule.
	 */
	public double fitness(ArrayList<Instance> exampleSet,
			HIDER_Rule rule, Random randomGenerator,double totalVolume, double errorPercentage) {
		
		double result = 0.0;
	
		//Calculates the number of instances covered or not
		double error = rule.notnCond(exampleSet); 
		double nCond = rule.nCond(exampleSet);		
		
		//Coverage of the rule
		double coverage =rule.coverage(); 

		if (nCond > 0.0) {
			// if(error < 0.05*acierto)
			if (error < errorPercentage * nCond) {
				result = error - nCond - coverage / totalVolume;
			} else {
				result = 3.0 * error - nCond - coverage / totalVolume;
			}
		} else {
			result = -1.0 * coverage / totalVolume;
		}

		return result;
	}

	/**
	 * Realizes the evaluation of the function fitness.
	 */
	@Override
	public double evaluate(HIDER_Rule r, ArrayList<Instance> exSet, double target_class) {
		ArrayList<Instance> exampleSet = exSet;
		HIDER_Rule rule = r;
		

		Random m_RandomGenerator = (Random) getParameter("random");
		double m_TotalVolume = (Double) getParameter("totalVolume");
		double m_ErrorPercentage = (Double) getParameter("errorPercentage");

		double result;
		result = fitness(exampleSet, rule, m_RandomGenerator,m_TotalVolume, m_ErrorPercentage);

		return result;
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
