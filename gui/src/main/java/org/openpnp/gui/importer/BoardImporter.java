/*
    Copyright (C) 2011 Jason von Nieda <jason@vonnieda.org>
    
    This file is part of OpenPnP.
    
    OpenPnP is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    OpenPnP is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with OpenPnP.  If not, see <http://www.gnu.org/licenses/>.
    
    For more information about OpenPnP visit http://openpnp.org
 */

package org.openpnp.gui.importer;

import java.awt.Frame;

import org.openpnp.model.Board;

/**
 * A simple interface describing a class that can import a Board definition.
 */
public interface BoardImporter {
    /**
     * Called by the controlling program to allow the importer to do it's work.
     * This method should return a Board with as much information as the
     * importer can supply populated. It should not set the File property of the
     * Board.
     * 
     * If the importer requires a user interface it is suggested that the user
     * interface be implemented as an application modal dialog and that this
     * method blocks until the dialog is complete. See the setOwner(Frame)
     * method for more information about implementing a user interface.
     * 
     * @return
     * @throws Exception
     */
    public Board importBoard() throws Exception;

    /**
     * Sets the owning Frame for the importer. This method will be called by the
     * application before importBoard() is called. If the importer requires a
     * user interface it is suggested that the user interface be implemented as
     * an application modal dialog and that this method blocks until the dialog
     * is complete. The Frame passed to this method can be used as the owner of
     * the dialog. If the importer does not require a user interface it can
     * ignore this method.
     * 
     * @param frame
     */
    public void setOwner(Frame frame);
}
