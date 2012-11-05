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

import java.io.IOException;

public interface SynchableItemStrategy<LOCAL_TYPE extends LocalSynchableItem, REMOTE_TYPE extends RemoteSynchableItem> {
	void saveLocalSynchableItem(LOCAL_TYPE localTaskList);

	void saveRemoteSynchableItem(REMOTE_TYPE remoteTaskList) throws IOException;

	void deleteLocalSynchableItem(LOCAL_TYPE local);

	void deleteRemoteSynchableItem(REMOTE_TYPE remote) throws IOException;

	LOCAL_TYPE createLocalItem(REMOTE_TYPE remote);

	REMOTE_TYPE createRemoteItem(LOCAL_TYPE local) throws IOException;

	REMOTE_TYPE modifyRemoteItem(LOCAL_TYPE local, REMOTE_TYPE remote);

	LOCAL_TYPE modifyLocalItem(REMOTE_TYPE remote, LOCAL_TYPE local);

	boolean itemsAreDifferent(REMOTE_TYPE remote, LOCAL_TYPE local);
}
