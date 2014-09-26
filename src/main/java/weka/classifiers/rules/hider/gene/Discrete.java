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
 *    Discrete.java
 *    Copyright (C) 2011 Marta Hernando, Daniel Rodriguez. Universidad de Alcala
 *
 */
package weka.classifiers.rules.hider.gene;

import java.io.*;
import java.util.*;

import weka.classifiers.rules.hider.Gene;
import weka.core.Instance;

/**
 * Class that represents a type of discrete gene.
 */
public class Discrete extends Gene implements Nominal, Serializable {
	
	private final int EQUAL = 4;
	
	/**
	 * Stores the bits constituting the binary string. It is implemented using a
	 * BitSet object
	 */
	private BitSet bits;

	/**
	 * Store the length of the binary string
	 */
	private int numberOfBits;
	
	/**
	 * Values that the discrete gene can take.
	 */
	private String[] values;

	/**
	 * Constructor
	 */
	public Discrete(int namesLen, Enumeration<Object> list) {
		bits = new BitSet(namesLen);
		numberOfBits = namesLen;
		values = new String[numberOfBits];

		for (int i = 0; i < numberOfBits; i++) {
			if (list.hasMoreElements()) {
				values[i] = list.nextElement().toString();
			}
		}
	}

	/**
	 * Returns the length of the binary string.
	 * 
	 * @return The length
	 */
	public int getNumberOfBits() {
		return numberOfBits;
	} // getNumberOfBits

	/**
	 * Returns the value of the ith bit.
	 * 
	 * @param bit The bit to retrieve
	 * @return The ith bit
	 */
	public boolean getIth(int bit) {
		return bits.get(bit);
	} // getNumberOfBits

	/**
	 * Sets the value of the ith bit.
	 * 
	 * @param bit The bit to set
	 */
	public void setIth(int bit, boolean value) {
		bits.set(bit, value);
	} // getNumberOfBits

	/**
	 * Sets the value set of bits.
	 * 
	 * @param b
	 */
	public void setBits(BitSet b) {
		bits = b;
	}

	/**
	 * Returns the value set of bits.
	 * @return bits
	 */
	public BitSet getBits() {
		return bits;
	}

	/**
	 * From a given value, it puts in the gene a bit to 1 for this value, 
	 * and the rest of the positions with probability of 50%, puts the value to 1 or not.
	 * 
	 * @param value
	 */
	public void setNominalValue(String value) {
		int p = 0;

		long m_RandomSeed = System.currentTimeMillis();
		Random randomGenerator = new Random(m_RandomSeed);

		for (int i = 0; i < values.length; i++) {
			p = randomGenerator.nextInt();

			if (values[i].equals(value)) {
				bits.set(i, true);
			} else {
				if (p < 0.1) {
					bits.set(i, true);
				} else {
					bits.set(i, false);
				}
			}
		}
	}

	/**
	 * From a random position it changes to the opposite value the bit.
	 */
	public void changeAleatBit() {
		int aleatBit = 0;
		long m_RandomSeed = System.currentTimeMillis();
		Random randomGenerator = new Random(m_RandomSeed);
		aleatBit = randomGenerator.nextInt(numberOfBits);
		bits.set(aleatBit, true);		
	}

	/**
	 * Initializes the gene.
	 */
	public void setEmpty() {
		for (int bit = 0; bit < getNumberOfBits(); bit++) {
			bits.set(bit, false);
		}
	}

	/**
	 * Puts all the values of the gene to 1.
	 */
	public void setFull() {
		for (int bit = 0; bit < getNumberOfBits(); bit++) {
			bits.set(bit, true);
		}
	}

	
	/**
	 * Returns true if all values are 1.
	 */
	public boolean isFull() {
	
		for (int bit = 0; bit < getNumberOfBits(); bit++) {
			if (bits.get(bit)==false)
				return false;
		}
		return true;
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
		if (getComparisonTypes() == EQUAL) {
			return contains(instance, pos);
		}
		return false;
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
		if (getComparisonTypes() == EQUAL) {
			return contains(instance, pos);
		}
		return false;
	}

	@Override
	/**
	 * Verifies if a discrete gene is equal to other one.
	 */
	public boolean equal(Gene gen) {
		return ((Discrete) gen).equals(getBits());
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
		for (int i = 0; i < numberOfBits; i++) {
			if (getIth(i)) {
				if (values[i].equals(inst.stringValue(pos)))
					return true;
			}
		}
		return false;
	}
	
	@Override
	/**
	 * Verifies if this one inside the values.
	 * 	
	 * @param instance,pos
	 * @return true if the instance is contained, false if not
	 */	
	public boolean containsRefined(Instance instance, int pos) {
		return contains(instance, pos);
	}	

	@Override
	/**
	 * Returns the representation of a discrete attribute.
	 *
	 * @param att_name
	 * @return String
	 */
	public String toString(String att_name,Gene genMaxMin) {
		int num = 0;
		String result = "";
		
		if (!isFull())
		{
			for (int i = 0; i < numberOfBits; i++) {
				if (getIth(i)) {
					if (num != 0) {
						result += " or ";
					}
					result += values[i];
					num++;
				}
			}
			return att_name + " = " + result + "\n   ";
		}
		else
			return "";
	}

	@Override
	/**
	 * Coverage. Returns number of active bits.
	 */
	public double coverage() {
		int count = 0;
		for (int i = 0; i < numberOfBits; i++) {
			if (bits.get(i)) {
				count++;
			}
		}
		return count;	
	}

	public String printDiscrete()
	{
		int num = 0;
		String result = "";
		for (int i = 0; i < numberOfBits; i++) {
			if (getIth(i)) {
				if (num != 0) {
					result += " or ";
				}
				result += values[i];
				num++;
			}
		}
		return result;		
	}
}
