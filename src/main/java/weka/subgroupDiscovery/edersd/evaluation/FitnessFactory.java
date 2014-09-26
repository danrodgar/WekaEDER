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
 *    FitnessFactory.java
 *    Copyright (C) 2011 Marta Hernando, Daniel Rodriguez. Universidad de Alcala
 *
 */
package weka.subgroupDiscovery.edersd.evaluation;

/**
 * Class that represents a factory for function fitness. Used for the Pattern
 * Factory Method
 */
public class FitnessFactory {

	public static Fitness getFunctionEvaluation(String name) {
		if (name.equalsIgnoreCase("FunctionFitnessNormal"))			
			return new FunctionFitnessNormal();
		if (name.equalsIgnoreCase("WRACC"))
			return new WRACC();
		if (name.equalsIgnoreCase("Coverage"))
			return new Coverage();
		if (name.equalsIgnoreCase("Support"))
			return new Support();
		if (name.equalsIgnoreCase("Specificity"))
			return new Specificity();
		if (name.equalsIgnoreCase("Confidence"))
			return new Confidence();
		return null;
	}
}
