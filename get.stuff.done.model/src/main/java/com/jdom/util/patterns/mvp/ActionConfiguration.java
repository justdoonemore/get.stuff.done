/** 
 *  Copyright (C) 2012  Just Do One More
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jdom.util.patterns.mvp;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class ActionConfiguration {
	private final String displayText;
	private final Runnable clickAction;

	public ActionConfiguration(String displayText, Runnable clickAction) {
		this.displayText = displayText;
		this.clickAction = clickAction;
	}

	public String getDisplayText() {
		return displayText;
	}

	public Runnable getClickAction() {
		return clickAction;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ActionConfiguration) {
			ActionConfiguration other = (ActionConfiguration) obj;
			EqualsBuilder eqBuilder = new EqualsBuilder();
			eqBuilder.append(this.getDisplayText(), other.getDisplayText());
			eqBuilder.append(this.getClickAction(), other.getClickAction());
			return eqBuilder.isEquals();
		}
		return false;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder hcBuilder = new HashCodeBuilder();
		hcBuilder.append(this.getDisplayText());
		hcBuilder.append(this.getClickAction());

		return hcBuilder.toHashCode();
	}
}
