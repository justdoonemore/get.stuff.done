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
package com.jdom.util.patterns.mvc.base;

import com.jdom.util.patterns.mvc.AbstractView;
import com.jdom.util.patterns.mvc.Controller;
import com.jdom.util.patterns.observer.base.AbstractModel;

public abstract class AbstractController<MODEL extends AbstractModel, VIEW extends AbstractView<MODEL, Controller<VIEW, MODEL>>>
        implements Controller<VIEW, MODEL> {

    protected abstract MODEL getModel();

    protected abstract VIEW getView();
}
