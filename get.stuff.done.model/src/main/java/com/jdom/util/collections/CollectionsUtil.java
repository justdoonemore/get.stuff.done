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
package com.jdom.util.collections;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

public class CollectionsUtil {

	public static <T> Set<T> asSet(T... items) {
		return new HashSet<T>(Arrays.asList(items));
	}

	public static Set<String> asSetFromLine(String line, char separator) {
		List<String> list = (StringUtils.isEmpty(line)) ? Collections
				.<String> emptyList() : Arrays.asList(StringUtils.split(line,
				separator));
		return new HashSet<String>(list);
	}

}
