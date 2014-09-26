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
 *    Gene.java
 *    Copyright (C) 2011 Marta Hernando, Daniel Rodriguez. Universidad de Alcala
 *
 */
package weka.classifiers.rules.hider;

import weka.classifiers.rules.hider.gene.*;
import weka.core.Instance;

/**
 * Class that represents the basic element of a rule
 */
public abstract class Gene
{	
	/**
	 * Variables to store the type of comparison.
	 */
	private int mComparisonTypes;
	private int NO_COMPARISON = 0;
		
	/**
	 * Constructor.
	 */
	public Gene(){	
		mComparisonTypes = NO_COMPARISON;	
	}

	/**
	 * Returns type of comparison.
	 * @return
	 */
	public int getComparisonTypes()
	{		
		return mComparisonTypes;
	}
	
	/**
	 * Sets the comparison mode for a specified attribute.
	 * @param comparison
	 */
	public void setAttributeComparison(int comparison) {
		assert (comparison >= 0 && comparison <= 3);
		mComparisonTypes= comparison;
	}		
	
	/**
	 * Method that it allows to verify if a gene is nominal.
	 * @return boolean
	 */
	public boolean isNominal()
	{
		if (this instanceof Nominal) 
		{
			return true;
		}
		return false;
	}
	
	/**
	 * Method that it allows to verify if a gene is numeric.
	 * @return boolean
	 */
	public boolean isNumeric()
	{
		if (this instanceof Numeric) 
		{
			return true;
		}
		return false;
	}		
	
	/**
	 * Abstract methods
	 */
	public abstract boolean contains(Instance inst, int pos);
	public abstract boolean containsRefined(Instance instance, int pos);
	public abstract boolean check(Instance instance, int pos);
	public abstract boolean checkRefined(Instance instance, int pos);
	public abstract boolean equal(Gene gen);
	public abstract double coverage();
	public abstract String toString(String att_name, Gene genMaxMin);
}
