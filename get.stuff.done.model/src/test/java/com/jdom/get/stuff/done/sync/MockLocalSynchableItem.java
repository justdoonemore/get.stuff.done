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

public class MockLocalSynchableItem extends MockSynchableItem
{
    private String remoteId;
    private long lastSyncTime = -1L;
    private long lastUpdatedTime = -1L;
    private boolean deleted;

    public MockLocalSynchableItem(String name, String remoteId)
    {
        super(name);
        this.remoteId = remoteId;
    }

    public void setRemoteIdentifier(String id)
    {
        this.remoteId = id;
    }

    public String getRemoteIdentifier()
    {
        return remoteId;
    }

    public long getLastSyncTime()
    {
        return lastSyncTime;
    }

    public void setLastSyncTime(long time)
    {
        this.lastSyncTime = time;
    }

    public long getLastUpdatedTime()
    {
        return lastUpdatedTime;
    }

    public void setLastUpdatedTime(long time)
    {
        this.lastUpdatedTime = time;
    }

    public boolean isDeleted()
    {
        return deleted;
    }

    public void setDeleted(boolean deleted)
    {
        this.deleted = deleted;
    }
}
