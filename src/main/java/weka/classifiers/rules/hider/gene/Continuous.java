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
 *    Continuous.java
 *    Copyright (C) 2011 Marta Hernando, Daniel Rodriguez. Universidad de Alcala
 *
 */
package weka.classifiers.rules.hider.gene;

import java.io.*;
import weka.classifiers.rules.hider.*;
import weka.core.*;

/**
 * Class that represents a type of continuous gene.
 */
public class Continuous extends Gene implements Numeric, Serializable {
	
	/**
	 * Represents the limits of the continuous gene
	 */
	private double[] mLimits;

	/**
	 * Represents the RefinedLimits of the continuous gene
	 */
	private double[] mRefinedLimits;
	private final int LESS_THAN = 1;
	private final int GREATER_THAN = 2;
	private final int BETWEEN = 3;

	/**
	 * Constructor
	 */
	public Continuous() {
		mLimits = new double[2];
		mLimits[0]=-9999.9;
		mLimits[1]=-9999.9;
		mRefinedLimits = new double[2];
	}

	/**
	 * Returns limits of the continuous gene.
	 */
	public double[] getLimits() {
		return mLimits;
	}

	/**
	 * Returns refinedLimits of the continuous gene.
	 */
	public double[] getRefinedLimits() {
		return mRefinedLimits;
	}

	/**
	 * Returns the lower limit for an attribute from the non-refined version of
	 * the rule.
	 * 
	 * @return Returns the lower limit for an attribute from the non-refined
	 *         version of the rule.
	 */
	public double getLowerLimit() {
		return mLimits[0];
	}

	/**
	 * Returns the upper limit for an attribute from the non-refined version of
	 * the rule.
	 * 
	 * @return Returns the upper limit for an attribute from the non-refined
	 *         version of the rule.
	 */
	public double getUpperLimit() {
		return mLimits[1];
	}

	/**
	 * Sets the lower limit for an attribute from the non-refined version of the
	 * rule.
	 * 
	 * @param value
	 */
	public void setLowerLimit(double value) {
		if (mLimits == null) {
			System.out.println("Vacio");
		}
		mLimits[0] = value;
	}

	/**
	 * Sets the upper limit for an attribute from the non-refined version of the
	 * rule.
	 * 
	 * @param value
	 */
	public void setUpperLimit(double value) {
		mLimits[1] = value;
	}

	/**
	 * Returns the lower limit for an attribute from the refined version of the
	 * rule.
	 * 
	 * @return Returns the lower limit for an attribute from the refined version
	 *         of the rule.
	 */
	public double getLowerRefinedLimit() {
		return mRefinedLimits[0];
	}

	/**
	 * Returns the upper limit for an attribute from the refined version of the
	 * rule.
	 * 
	 * @return Returns the upperlimit for an attribute from the refined version
	 *         of the rule.
	 */
	public double getUpperRefinedLimit() {
		return mRefinedLimits[1];
	}

	/**
	 * Sets the lower limit for an attribute from the refined version of the
	 * rule.
	 * 
	 * @param value
	 */
	public void setLowerRefinedLimit(double value) {
		mRefinedLimits[0] = value;
	}

	/**
	 * Sets the upper limit for an attribute from the refined version of the
	 * rule.
	 * 
	 * @param value
	 */
	public void setUpperRefinedLimit(double value) {
		mRefinedLimits[1] = value;
	}

	@Override
	/**
	 * Returns the representation of a continuous attribute.
	 */
	
	public String toString(String att_name,Gene genMaxMin) {
		String result = "";

		switch (getComparisonTypes()) {
		case LESS_THAN:
//			
//			if (((Continuous)genMaxMin).getLowerLimit() >= getLowerLimit()) 
//			{
//				result="";
//			}
//			else
//			{
				result += att_name + " <= "+ String.valueOf(redondearOne(getUpperLimit()))+ "\n   ";				
//			}			
			break;
		case GREATER_THAN:
			
//			if(getUpperLimit() >= ((Continuous)genMaxMin).getUpperLimit())
//			{
//				result = "";
//			}
//			else
//			{
				result += att_name + " >= " + String.valueOf(redondearOne(getLowerLimit()))+ "\n   ";
//			}			
			break;
		case BETWEEN:
			
//			if (((Continuous)genMaxMin).getLowerLimit() >= getLowerLimit() && 
//			(getUpperLimit() >= ((Continuous)genMaxMin).getUpperLimit()))
//			{
//				result = "";
//			}
//			else
//			if(((Continuous)genMaxMin).getLowerLimit() >= getLowerLimit())
//			{
//				result += att_name + " <= "+ String.valueOf(redondearOne(getUpperLimit()))+ "\n   ";
//			}
//			else
//			if((getUpperLimit() >= ((Continuous)genMaxMin).getUpperLimit()))
//			{
//				result += String.valueOf(redondearOne(getLowerLimit()))+ " <= "+ att_name + "\n   ";
//			}
//			else
//			if (String.valueOf(redondearOne(getUpperLimit())).equals(String.valueOf(redondearOne(getLowerLimit()))))
//			{
//				result += att_name + " = "+ String.valueOf(redondearOne(getUpperLimit()))+ "\n   ";
//			}			
//			else
//			{
//				if (getLowerLimit()!=getUpperLimit())
//				{
					result += String.valueOf(redondearOne(getLowerLimit()))+ " <= "+ att_name + " <= "+ String.valueOf(redondearOne(getUpperLimit()))+ "\n   ";
//				}
//				else
//				{				
//					result += att_name + " = "+getUpperLimit()+ "\n   ";											
//				}			
//			}				
			break;
		default:
		}				
		return result;
	}
	
	
	
	

	@Override
	/**
	 * Checks if the given value is contained by the non-refined version of
	 * the rule according to the comparison modes.
	 * 
	 * @param instance,pos
	 * @return true if the instance is contained, false if not
	 */
	public boolean check(Instance instance, int pos) {
		switch (getComparisonTypes()) {
		case LESS_THAN:
			if (instance.value(pos) >= getUpperLimit()) {
				return false;
			}
			break;
		case GREATER_THAN:
			if (instance.value(pos) <= getLowerLimit()) {
				return false;
			}
			break;
		case BETWEEN:
			if (instance.value(pos) < getLowerLimit() || instance.value(pos) > getUpperLimit()) {
				return false;
			}
			break;
		}
		return true;
	}

	@Override
	/**
	 * Checks if the given value is contained by the refined version of the
	 * rule according to the comparison modes.
	 * 
	 * @param instance,pos
	 * @return true if the instance is contained, false if not
	 */	
	public boolean checkRefined(Instance instance, int pos) {
		switch (getComparisonTypes()) {
		case LESS_THAN:
			if (instance.value(pos) > getUpperRefinedLimit()) {
				return false;
			}
			break;
		case GREATER_THAN:
			if (instance.value(pos) < getLowerRefinedLimit()) {
				return false;
			}
			break;
		case BETWEEN:
			if (instance.value(pos) < getLowerRefinedLimit()
					|| instance.value(pos) > getUpperRefinedLimit()) {
				return false;
			}
			break;
		}
		return true;
	}

	@Override
	/**
	 * Verifies if a continuous gene is equal to other one.
	 */	
	public boolean equal(Gene gen) {
		if (redondearOne(((Continuous) gen).getLowerLimit()) != (redondearOne(getLowerLimit()))) {
			return false;
		}
		if (redondearOne(((Continuous) gen).getUpperLimit()) != (redondearOne(getUpperLimit()))) {
			return false;
		}
		if (redondearOne(((Continuous) gen).getLowerRefinedLimit()) != (redondearOne(getLowerRefinedLimit()))) {
			return false;
		}
		if (redondearOne(((Continuous) gen).getUpperRefinedLimit()) != (redondearOne(getUpperRefinedLimit()))) {
			return false;
		}
		return true;
	}

	/**
	 * To round a value to two decimals.
	 * @param d
	 * @return double
	 */
	private double redondearOne(double d) {
		return Math.rint(d * 10000) / 10000;
	}

	@Override
	/**
	 * Checks if the given value is contained by the non-refined version of
	 * the rule.
	 * 
	 * @param inst, pos
	 * @return true if the instance is contained, false if not
	 */
	public boolean contains(Instance inst, int pos) {
			return (inst.value(pos) >= getLowerLimit() && inst.value(pos) <= getUpperLimit());	
	}

	@Override
	/**
	 * Checks if the given value is contained by the refined version of
	 * the rule.
	 * 
	 * @param inst, pos
	 * @return true if the instance is contained, false if not
	 */
	public boolean containsRefined(Instance inst, int pos) {
			return (inst.value(pos) >= getLowerRefinedLimit() && inst.value(pos) <= getUpperRefinedLimit());			
	}
	
	
	/**
	 * Returns the representation of a continuous attribute with RefinedLimits.
	 *
	 * @param att_name
	 * @return
	 */
	public String toStringRefined(String att_name,Gene genMaxMin) {
		String result = "";

		switch (getComparisonTypes()) {
		case LESS_THAN:
			
			if (((Continuous)genMaxMin).getLowerRefinedLimit() >= getLowerRefinedLimit()) 
			{
				result="";
			}
			else
			{
				result += att_name + " <= "+ String.valueOf(redondearOne(getUpperRefinedLimit()))+ "\n   ";				
			}			
			break;
		case GREATER_THAN:
			
			if(getUpperRefinedLimit() >= ((Continuous)genMaxMin).getUpperRefinedLimit())
			{
				result = "";
			}
			else
			{
				result += att_name + " >= " + String.valueOf(redondearOne(getLowerRefinedLimit()))+ "\n   ";
			}			
			break;
		case BETWEEN:
			
			if (((Continuous)genMaxMin).getLowerRefinedLimit() >= getLowerRefinedLimit() && 
			(getUpperRefinedLimit() >= ((Continuous)genMaxMin).getUpperRefinedLimit()))
			{
				result = "";
			}
			else
			if(((Continuous)genMaxMin).getLowerRefinedLimit() >= getLowerRefinedLimit())
			{
				result += att_name + " <= "+ String.valueOf(redondearOne(getUpperRefinedLimit()))+ "\n   ";
			}
			else
			if((getUpperRefinedLimit() >= ((Continuous)genMaxMin).getUpperRefinedLimit()))
			{
				result += String.valueOf(redondearOne(getLowerRefinedLimit()))+ " <= "+ att_name + "\n   ";
			}
			else
			if (String.valueOf(redondearOne(getUpperRefinedLimit())).equals(String.valueOf(redondearOne(getLowerRefinedLimit()))))
			{
				result += att_name + " = "+ String.valueOf(redondearOne(getUpperRefinedLimit()))+ "\n   ";
			}	
			else
			{
				if (getLowerRefinedLimit()!=getUpperRefinedLimit())
				{
					result += String.valueOf(redondearOne(getLowerRefinedLimit()))+ " <= "+ att_name + " <= "+ String.valueOf(redondearOne(getUpperRefinedLimit()))+ "\n   ";
				}
				else
				{				
					result += att_name + " = "+getUpperRefinedLimit()+ "\n   ";											
				}			
			}				
			break;
		default:
		}				
		return result;
	}

	@Override
	/**
	 * Coverage.
	 */
	public double coverage() {
		return Math.abs(getUpperLimit() - getLowerLimit());		
	}


	public String printContinuous()
	{
		return "["+(getLowerLimit())+"-"+(getUpperLimit())+"]";	
		//return "["+Math.round(getLowerLimit())+"-"+Math.round(getUpperLimit())+"]";			
	}
	
}
