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
 *    SelectionFactory.java
 *    Copyright (C) 2011 Marta Hernando, Daniel Rodriguez. Universidad de Alcala
 *
 */
package weka.classifiers.rules.hider.operator.selection;

import weka.classifiers.rules.hider.evaluation.WRACC;

/**
 * Class that represents a factory for operator selection. Used for the Pattern
 * Factory Method
 */
public class SelectionFactory {

	/**
	 * Gets a selection operator through its name.
	 * 
	 * @param name of the operator
	 * @return Selection
	 */
	public static Selection getSelectionOperator(String name) {
		if (name.equalsIgnoreCase("RouletteWheel"))
			return new RouletteWheel();
		return null;
	} // getSelectionOperator
	
} // SelectionFactory
