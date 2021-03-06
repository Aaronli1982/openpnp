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

package org.openpnp.gui.tablemodel;

import javax.swing.table.AbstractTableModel;

import org.openpnp.gui.support.LengthCellValue;
import org.openpnp.model.Board.Side;
import org.openpnp.model.BoardLocation;
import org.openpnp.model.Configuration;
import org.openpnp.model.Job;
import org.openpnp.model.Length;
import org.openpnp.model.Location;

public class BoardLocationsTableModel extends AbstractTableModel {
	private final Configuration configuration;
	
	private String[] columnNames = new String[] { 
			"Board", 
			"Side", 
			"X",
			"Y", 
			"Z", 
			"ø" 
	};
	
	private Class[] columnTypes = new Class[] {
			String.class,
			Side.class,
			LengthCellValue.class,
			LengthCellValue.class,
			LengthCellValue.class,
			String.class
	};
	
	private Job job;
	
	public BoardLocationsTableModel(Configuration configuration) {
		this.configuration = configuration;
	}

	public void setJob(Job job) {
		this.job = job;
		fireTableDataChanged();
	}

	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}

	public int getColumnCount() {
		return columnNames.length;
	}

	public int getRowCount() {
		if (job == null) {
			return 0;
		}
		return job.getBoardLocations().size();
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return columnTypes[columnIndex];
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return (columnIndex != 0);
	}
	
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		try {
			BoardLocation boardLocation = job.getBoardLocations().get(rowIndex);
			if (columnIndex == 0) {
				boardLocation.getBoard().setName((String) aValue);
			}
			else if (columnIndex == 1) {
				boardLocation.setSide((Side) aValue);
				fireTableCellUpdated(rowIndex, columnIndex);
			}
			else if (columnIndex == 2) {
				LengthCellValue value = (LengthCellValue) aValue;
				Length length = value.getLength();
				Location location = boardLocation.getLocation();
				location = Length.setLocationField(configuration, location, length, Length.Field.X);
				boardLocation.setLocation(location);
			}
			else if (columnIndex == 3) {
				LengthCellValue value = (LengthCellValue) aValue;
				Length length = value.getLength();
				Location location = boardLocation.getLocation();
				location = Length.setLocationField(configuration, location, length, Length.Field.Y);
				boardLocation.setLocation(location);
			}
			else if (columnIndex == 4) {
				LengthCellValue value = (LengthCellValue) aValue;
				Length length = value.getLength();
				Location location = boardLocation.getLocation();
				location = Length.setLocationField(configuration, location, length, Length.Field.Z);
				boardLocation.setLocation(location);
			}
			else if (columnIndex == 5) {
				boardLocation.getLocation().setRotation(Double.parseDouble(aValue.toString()));
			}
		}
		catch (Exception e) {
			// TODO: dialog, bad input
		}
	}

	public Object getValueAt(int row, int col) {
		BoardLocation boardLocation = job.getBoardLocations().get(row);
		Location loc = boardLocation.getLocation();
		switch (col) {
		case 0:
			return boardLocation.getBoard().getName();
		case 1:
			return boardLocation.getSide();
		case 2:
			return new LengthCellValue(loc.getLengthX());
		case 3:
			return new LengthCellValue(loc.getLengthY());
		case 4:
			return new LengthCellValue(loc.getLengthZ());
		case 5:
			return String.format(configuration.getLengthDisplayFormat(), loc.getRotation(), "");
		default:
			return null;
		}
	}
}