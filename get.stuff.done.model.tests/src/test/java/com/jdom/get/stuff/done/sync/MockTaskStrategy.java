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

public class MockTaskStrategy extends MockSynchableItemStrategy<MockLocalTask, MockRemoteTask>
{
    static final String TASK_ID = "remoteTaskId";

    public MockLocalTask createLocalItem(MockRemoteTask remote)
    {
        return new MockLocalTask(remote.getName());
    }

    public MockRemoteTask createRemoteItem(MockLocalTask local)
    {
        return new MockRemoteTask(TASK_ID, local.getName());
    }

    @Override
    public MockRemoteTask modifyRemoteItem(MockLocalTask local, MockRemoteTask remote)
    {
        MockRemoteTask remoteTask = super.modifyRemoteItem(local, remote);
        remoteTask.setListName(local.getListName());
        return remoteTask;
    }

    @Override
    public MockLocalTask modifyLocalItem(MockRemoteTask remote, MockLocalTask local)
    {
        MockLocalTask localTask = super.modifyLocalItem(remote, local);
        localTask.setListName(remote.getListName());
        return localTask;
    }

    @Override
    public boolean itemsAreDifferent(MockRemoteTask remote, MockLocalTask local)
    {
        boolean itemsDifferent = super.itemsAreDifferent(remote, local);

        if (!itemsDifferent)
        {
            itemsDifferent = !remote.getListName().equals(local.getListName());
        }

        return itemsDifferent;
    }
}
