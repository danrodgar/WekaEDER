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
 *    Specificity.java
 *    Copyright (C) 2011 Marta Hernando, Daniel Rodriguez. Universidad de Alcala
 *
 */
package weka.subgroupDiscovery.edersd.evaluation;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;

import weka.attributeSelection.StartSetHandler;
import weka.subgroupDiscovery.edersd.EDERSD_Rule;
import weka.core.Instance;
import weka.core.Option;
import weka.core.OptionHandler;

/**
 * Represents the proportion of negative cases correctly classified. 
 * @author Marta
 *
 */
public class Specificity extends Fitness implements OptionHandler, StartSetHandler{

	/**
	 * Constructor.
	 */
	public Specificity() {}
	
	
	/**
	 * Calculates the fitness value of a given rule.
	 * 
	 * Spec(R) = -n(Cond . Class)/ - n (Class)
	 * 
	 * @param rule Rule.
	 * @return Fitness value of the given rule.
	 */
	private static Double fitness(EDERSD_Rule rule, ArrayList<Instance> exampleSet, Double posClass)
	{
		//Calculates the number of instances covered.
		double notnCondClass = rule.notnCondClass(exampleSet);
		double notnClass = rule.notnClass(exampleSet);
		
		return notnCondClass/notnClass;
	}


	/**
	 * Realizes the evaluation of the function fitness.
	 */
	public double evaluate(EDERSD_Rule r, ArrayList<Instance> exSet, double target_class) {
		ArrayList<Instance> exampleSet = exSet;
		EDERSD_Rule rule = r;
		double class_value = target_class;

		double result;
		result = fitness(rule, exampleSet, class_value);

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
		// TODO Auto-generated method stub
		return null;
	}
}
