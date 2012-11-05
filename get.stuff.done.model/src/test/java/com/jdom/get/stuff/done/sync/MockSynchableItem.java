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
package com.jdom.get.stuff.done.sync;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class MockSynchableItem
{
    private String name;

    public MockSynchableItem(String name)
    {
        this.name = name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof MockSynchableItem)
        {
            MockSynchableItem other = (MockSynchableItem) obj;
            EqualsBuilder eqBuilder = new EqualsBuilder();
            eqBuilder.append(this.getName(), other.getName());
            return eqBuilder.isEquals();
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        HashCodeBuilder hcBuilder = new HashCodeBuilder();
        hcBuilder.append(this.getName());

        return hcBuilder.toHashCode();
    }

    @Override
    public String toString()
    {
        return this.getName();
    }
}
