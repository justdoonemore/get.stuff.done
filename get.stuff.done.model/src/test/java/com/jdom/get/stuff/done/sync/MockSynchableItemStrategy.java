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

import java.util.ArrayList;
import java.util.List;

import com.jdom.get.stuff.done.sync.LocalSynchableItem;
import com.jdom.get.stuff.done.sync.RemoteSynchableItem;
import com.jdom.get.stuff.done.sync.SynchableItemStrategy;

public abstract class MockSynchableItemStrategy<LOCAL_TYPE extends LocalSynchableItem, REMOTE_TYPE extends RemoteSynchableItem>
    implements
    SynchableItemStrategy<LOCAL_TYPE, REMOTE_TYPE>
{
    final List<LOCAL_TYPE> savedLocalItems = new ArrayList<LOCAL_TYPE>();
    final List<REMOTE_TYPE> savedRemoteItems = new ArrayList<REMOTE_TYPE>();
    final List<LOCAL_TYPE> deletedLocalItems = new ArrayList<LOCAL_TYPE>();
    final List<REMOTE_TYPE> deletedRemoteItems = new ArrayList<REMOTE_TYPE>();
    final List<LOCAL_TYPE> modifiedLocalItems = new ArrayList<LOCAL_TYPE>();
    final List<REMOTE_TYPE> modifiedRemoteItems = new ArrayList<REMOTE_TYPE>();

    public void saveLocalSynchableItem(LOCAL_TYPE localTaskList)
    {
        this.savedLocalItems.add(localTaskList);
    }

    public void saveRemoteSynchableItem(REMOTE_TYPE remoteTaskList)
    {
        this.savedRemoteItems.add(remoteTaskList);
    }

    public void deleteLocalSynchableItem(LOCAL_TYPE local)
    {
        this.deletedLocalItems.add(local);
    }

    public void deleteRemoteSynchableItem(REMOTE_TYPE remote)
    {
        this.deletedRemoteItems.add(remote);
    }

    public REMOTE_TYPE modifyRemoteItem(LOCAL_TYPE local, REMOTE_TYPE remote)
    {
        remote.setName(local.getName());
        this.modifiedRemoteItems.add(remote);
        return remote;
    }

    public LOCAL_TYPE modifyLocalItem(REMOTE_TYPE remote, LOCAL_TYPE local)
    {
        local.setName(remote.getName());
        this.modifiedLocalItems.add(local);
        return local;
    }

    public boolean itemsAreDifferent(REMOTE_TYPE remote, LOCAL_TYPE local)
    {
        return !remote.getName().equals(local.getName());
    }

}
