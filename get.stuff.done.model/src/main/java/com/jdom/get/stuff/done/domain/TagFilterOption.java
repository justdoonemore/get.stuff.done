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
package com.jdom.get.stuff.done.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class TagFilterOption implements FilterOption {

	static final String TAG_FILTER_PREFIX = "Tag Filter: ";
	private final String tagName;

	public TagFilterOption(String tagName) {
		this.tagName = tagName;
	}

	public String getTagName() {
		return tagName;
	}

	@Override
	public String toString() {
		return TAG_FILTER_PREFIX + tagName;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TagFilterOption) {
			TagFilterOption other = (TagFilterOption) obj;
			EqualsBuilder eqBuilder = new EqualsBuilder();
			eqBuilder.append(this.getTagName(), other.getTagName());
			return eqBuilder.isEquals();
		}
		return false;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder hcBuilder = new HashCodeBuilder();
		hcBuilder.append(this.getTagName());

		return hcBuilder.toHashCode();
	}

	public static FilterOption parse(String string) {
		String tagName = string.replaceAll(TAG_FILTER_PREFIX, "");
		return new TagFilterOption(tagName);
	}

	public boolean accept(Task task) {
		return task.getTags().contains(tagName);
	}
}
