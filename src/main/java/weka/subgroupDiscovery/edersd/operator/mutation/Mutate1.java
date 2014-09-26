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
 *    Mutate1.java
 *    Copyright (C) 2011 Marta Hernando, Daniel Rodriguez. Universidad de Alcala
 *
 */
package weka.subgroupDiscovery.edersd.operator.mutation;

import java.util.*;

import weka.attributeSelection.StartSetHandler;
import weka.subgroupDiscovery.edersd.evaluation.Fitness;
import weka.subgroupDiscovery.edersd.evaluation.FunctionFitnessNormal;
import weka.subgroupDiscovery.edersd.gene.*;
import weka.subgroupDiscovery.edersd.*;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Utils;


/**
 * Class that represents a type of mutation.
 */
public class Mutate1 extends Mutation implements OptionHandler, StartSetHandler
{
	/**
	 * Constructor.
	 */
	public Mutate1(){}

	/**
	 * Mutates a given individual.
	 * @param individual Individual to be mutated.
	 */
	public EDERSD_Rule doMutation(int m_NumAttributos,int classIndex,EDERSD_Rule rule,EDERSD_Rule ruleMaxMin,Random randomGenerator) 
	{	
		int p = classIndex;
		double anterior;

		while (p == classIndex) {
			p = randomGenerator.nextInt(m_NumAttributos);
		}
		
		if (rule.getGene(p).isNumeric())
		{		
			//Hay un 50% de probabilidades de modificar el valor 'par' y un 50% de modificar el 'impar'
			if (randomGenerator.nextFloat() < 0.5) {	//PAR
				anterior = ((Continuous) rule.getGene(p)).getLowerLimit();
				
				((Continuous) rule.getGene(p)).setLowerLimit((anterior + (randomGenerator.nextInt(2) * 2 - 1) * 
						((((Continuous)ruleMaxMin.getGene(p)).getUpperLimit() - 
						((Continuous)ruleMaxMin.getGene(p)).getLowerLimit()) 
						/ 20.) * randomGenerator.nextFloat()));
																						
				//Si el nuevo valor es menor que el m�nimo, nos quedamos con el m�nimo
				if (((Continuous) rule.getGene(p)).getLowerLimit() < ((Continuous)ruleMaxMin.getGene(p)).getLowerLimit()) {
					((Continuous) rule.getGene(p)).setLowerLimit(((Continuous)ruleMaxMin.getGene(p)).getLowerLimit());
				}
				
				//Si el resultado no es 'par' < 'impar' se cancela la mutaci�n
				if (((Continuous) rule.getGene(p)).getLowerLimit() > ((Continuous)rule.getGene(p)).getUpperLimit()) {
				((Continuous) rule.getGene(p)).setLowerLimit(anterior);
				}
				
			} else {	//IMPAR
				anterior = ((Continuous)rule.getGene(p)).getUpperLimit();
				
				((Continuous) rule.getGene(p)).setUpperLimit((anterior + (randomGenerator.nextInt(2) * 2 - 1) * 
						((((Continuous)ruleMaxMin.getGene(p)).getUpperLimit() - 
						((Continuous)ruleMaxMin.getGene(p)).getLowerLimit()) 
						/ 20.) * randomGenerator.nextFloat()));
				
				//Si el nuevo valor es mayor que el m�ximo, nos quedamos con el m�ximo
				if (((Continuous)rule.getGene(p)).getUpperLimit() > ((Continuous)ruleMaxMin.getGene(p)).getUpperLimit()) {
					((Continuous) rule.getGene(p)).setUpperLimit(((Continuous)ruleMaxMin.getGene(p)).getUpperLimit());
				}
				
				//Si el resultado no es 'par' < 'impar' se cancela la mutaci�n
				if (((Continuous)rule.getGene(p)).getUpperLimit() < ((Continuous) rule.getGene(p)).getLowerLimit()) {
					((Continuous) rule.getGene(p)).setUpperLimit(anterior);
				}
			}
		
		}
		if (rule.getGene(p).isNominal())
		{					
			((Discrete) rule.getGene(p)).changeAleatBit();
		}		
							
		return rule;
	}
		
	
	  /**
	   * Executes the operation
	   * @param object An object containing an array of two GASUD_Rules
	   * @return An object containing an array with the offSprings
	   */
	  public Object execute(Object object) 
	  {
	    EDERSD_Rule solution= (EDERSD_Rule) object;
	  
		int m_NumAttributos=(Integer)getParameter("numAttributes");
		int classIndex=(Integer)getParameter("classIndex");
		EDERSD_Rule ruleMaxMin=(EDERSD_Rule)getParameter("ruleMaxMin");

		Random randomGenerator=(Random)getParameter("random");
	    	    	    	    	    	 
	    EDERSD_Rule offSpring;
	    offSpring = doMutation(m_NumAttributos,classIndex,solution,ruleMaxMin,randomGenerator);

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
