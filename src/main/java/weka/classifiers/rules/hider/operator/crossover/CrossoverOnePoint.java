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
 *    CrossoverOnePoint.java
 *    Copyright (C) 2011 Marta Hernando, Daniel Rodriguez. Universidad de Alcala
 *
 */
package weka.classifiers.rules.hider.operator.crossover;

import java.util.*;

import weka.attributeSelection.StartSetHandler;
import weka.classifiers.rules.hider.*;
import weka.core.*;

/**
 * Class that represents a type of crossover.
 */
public class CrossoverOnePoint extends Crossover implements OptionHandler, StartSetHandler
{
	/**
	 * Constructor.
	 */
	public CrossoverOnePoint(){}
    
	/**
	 * Makes crossover between 2 individuals.
	 * @param new_individual New rule to be created.
	 * @param parent1 Parent rule.
	 * @param parent2 Parent rule.
	 * @throws Exception 
	 */
	  public HIDER_Rule doCrossover(Instances dataSetInstances, HIDER_Rule parent1,HIDER_Rule parent2,Random randomGenerator) throws Exception 
	  {
		  int m_NumAttributes=dataSetInstances.numAttributes();
		  int puntoCruce;   //Point of crossover
		  
		  //To generate two HIDER_Rule children	       
		  HIDER_Rule son=new HIDER_Rule(parent1.getRuleClass());		
		  son.grow(dataSetInstances);

		  //To select a point of crossoverz
		  do{
			  puntoCruce = (int) (Math.random() * m_NumAttributes-1);
		  }while((puntoCruce==0)|| (puntoCruce==(m_NumAttributes-1)));
		  		  
		  //A beginning is assigned to every son, of his father or his mother.
		  for (int cont=0;cont<puntoCruce;cont++)
		  {
			  son.setGene(cont, (Gene)parent1.getGene(cont));			  					  
		  }
		  
		  for (int cont=puntoCruce;cont<m_NumAttributes;cont++)
		  {
			  son.setGene(cont,(Gene)parent2.getGene(cont));
		  }		  

		  return son; 
		  
	  } // doCrossover
		  	
	  /**
	   * Executes the operation
	   * @param object An object containing an array of two GASUD_Rules
	   * @return An object containing an array with the offSprings
	 * @throws Exception 
	   */
	  public Object execute(Object object) throws Exception {
	    HIDER_Rule[] parents = (HIDER_Rule[]) object;
	  
		Instances dataSetInstances=(Instances)getParameter("instances");
		Random randomGenerator=(Random)getParameter("random");
	    	    	    	    	    	 
	    HIDER_Rule offSpring;
	    offSpring = doCrossover(dataSetInstances,parents[1],parents[2],randomGenerator);

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