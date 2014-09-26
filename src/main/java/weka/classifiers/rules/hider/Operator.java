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
 *    Operator.java
 *    Copyright (C) 2011 Marta Hernando, Daniel Rodriguez. Universidad de Alcala
 *
 */
package weka.classifiers.rules.hider;

import java.io.Serializable;
import java.util.*;


/**
 * Represents a genetic operator.
 * @author Marta
 *
 */
public abstract class Operator implements Serializable{

	/**
	 * Stores the current operator parameters. It is defined as a Map of pairs <
	 * <code>String</code>, <code>Object</code>>, and it allow objects to be
	 * accessed by their names, which are specified by the string.
	 */
	protected Map<String, Object> parameters_;

	/**
	 * Constructor.
	 */
	public Operator() {
		parameters_ = new HashMap<String, Object>();
	} // Operator

	/**
	 * Sets a new <code>Object</code> parameter to the operator.
	 * 
	 * @param name The parameter name.
	 * @param value Object representing the parameter.
	 */
	public void setParameter(String name, Object value) {
		parameters_.put(name.toUpperCase(), value);
	} // setParameter

	/**
	 * Returns an object representing a parameter of the <code>Operator</code>
	 * 
	 * @param name The parameter name.
	 * @return the parameter.
	 */
	public Object getParameter(String name) {
		return parameters_.get(name.toUpperCase());
	} // getParameter

	abstract public Object execute(Object object) throws Exception;
}
