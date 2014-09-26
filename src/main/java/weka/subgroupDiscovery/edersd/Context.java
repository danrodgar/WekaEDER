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
 *    Context.java
 *    Copyright (C) 2011 Marta Hernando, Daniel Rodriguez. Universidad de Alcala
 *
 */

package weka.subgroupDiscovery.edersd;

import java.util.ArrayList;

import weka.subgroupDiscovery.edersd.*;
import weka.subgroupDiscovery.edersd.evaluation.Fitness;
import weka.core.Instance;

/**
 * Class that represents context of strategy pattern.
 * @author Marta
 *
 */
public class Context {

	private Fitness m_Strategy;				//Function used.
	private ArrayList<Instance> m_Data;		//Example set.
	private double m_Target_class;			//Value of class.
	
	/**
	 * Constructor.
	 * @param function
	 * @param exampleSet
	 * @param target_class
	 */
	public Context(Fitness function, ArrayList<Instance> exampleSet,double target_class) {
		this.m_Strategy = function;
		this.m_Data = exampleSet;
		this.m_Target_class= target_class;
	}
	
	public void setStrategy(Fitness f) {
		this.m_Strategy = f;
	}
	
	public double executeStrategy(EDERSD_Rule rule) {
		return m_Strategy.evaluate(rule, m_Data, m_Target_class);
	}
}
