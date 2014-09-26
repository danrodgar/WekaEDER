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
 *    Algorithm.java
 *    Copyright (C) 2011 Marta Hernando, Daniel Rodriguez. Universidad de Alcala
 *
 */
package weka.classifiers.rules.hider.algorithm;

import java.io.Serializable;
import java.util.*;

import weka.classifiers.rules.hider.HIDER_Rule;
import weka.classifiers.rules.hider.Operator;

/**
 * This class implements a generic template for the algorithms. Every algorithm
 * must have a mapping between the parameters and and their names, and another
 * mapping between the operators and their names. The class declares an abstract
 * method called <code>execute</code>, which defines the behavior of the
 * algorithm.
 */
public abstract class Algorithm implements Serializable {

	/**
	 * Stores the operators used by the algorithm, such as selection, crossover,etc.
	 */
	protected Map<String, Operator> operators_ = null;

	/**
	 * Stores algorithm specific parameters. For example, in GeneticAlgorith these
	 * parameters include the numIndividuals or numGenerations.
	 */
	protected Map<String, Object> inputParameters_ = null;

	/**
	 * Offers facilities for add new operators for the algorithm. To use an
	 * operator, an algorithm has to obtain it through the
	 * <code>getOperator</code> method.
	 * 
	 * @param name The operator name
	 * @param operator The operator
	 */
	public void addOperator(String name, Operator operator) {
		if (operators_ == null) {
			operators_ = new HashMap<String, Operator>();
		}
		operators_.put(name, operator);
	} // addOperator

	/**
	 * Gets an operator through his name. If the operator doesn't exist or the
	 * name is wrong this method returns null. The client of this method have to
	 * check the result of the method.
	 * 
	 * @param name The operator name
	 * @return The operator if exists, null in another case.
	 */
	public Operator getOperator(String name) {
		return operators_.get(name);
	} // getOperator

	/**
	 * Sets an input parameter to an algorithm. The parameters have to been
	 * inserted using their name to access them through the
	 * <code>getInputParameter</code> method.
	 * 
	 * @param name The parameter name
	 * @param object  Object that represent a parameter for the algorithm.
	 */
	public void setInputParameter(String name, Object object) {
		if (inputParameters_ == null) {
			inputParameters_ = new HashMap<String, Object>();
		}
		inputParameters_.put(name, object);
	} // setInputParameter

	/**
	 * Gets an input parameter through its name. 
	 * 
	 * @param name The parameter name
	 * @return Object representing the parameter or null if the parameter
	 *         doesn't exist or the name is wrong
	 */
	public Object getInputParameter(String name) {
		return inputParameters_.get(name);
	} // getInputParameter

	/**
	 * Launches the execution of an specific algorithm.
	 * 
	 * @return a <code>HIDER_Rule</code> that is a set of
	 *         solutions as a result of the algorithm execution
	 * @throws Exception
	 */
	public abstract HIDER_Rule execute() throws Exception;

} // Algorithm
