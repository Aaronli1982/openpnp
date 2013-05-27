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

package org.openpnp.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import org.openpnp.gui.support.CameraItem;
import org.openpnp.gui.support.MessageBoxes;
import org.openpnp.model.Configuration;
import org.openpnp.model.Location;
import org.openpnp.spi.Camera;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class MachineCalibration extends JDialog implements Runnable {
	private final static Logger logger = LoggerFactory.getLogger(MachineCalibration.class);
	
	private final Frame frame;
	
	private JTextField txtStartX;
	private JTextField txtStartY;
	private JTextField txtEndX;
	private JTextField txtEndY;
	private JProgressBar progressBar;
	private JButton btnStart;
	
	private boolean cancelled = false;
	
	public MachineCalibration(Frame frame) {
		super(frame, "Machine Calibrator");
		
		this.frame = frame;
		
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.SOUTH);
		panel.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_2 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_2.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		panel.add(panel_2);
		
		btnStart = new JButton(startAction);
		panel_2.add(btnStart);
		
		progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		panel.add(progressBar, BorderLayout.NORTH);
		
		JPanel panel_1 = new JPanel();
		getContentPane().add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,}));
		
		JLabel lblSelectThe = new JLabel("<html>\n1. Select the camera to be used.<br/>\n2. Select an empty output directory for the resulting images.<br/>\n3. Jog the camera to the bottom left corner of the area to be scanned, then press the Start Position Set button.<br/>\n4. Jog the camera to the upper right corner of the area to be scanned, then press the End Position Set button.<br/>\nThe End Position values should be greater than the Start Position values.<br/>\n5. Press the Start button.<br/>\n</html>\n");
		lblSelectThe.setFont(new Font("Lucida Grande", Font.PLAIN, 14));
		panel_1.add(lblSelectThe, "2, 2, 7, 1");
		
		JLabel lblMinX = new JLabel("Minimum");
		panel_1.add(lblMinX, "4, 4, center, default");
		
		JLabel lblEndPosition = new JLabel("Maximum");
		panel_1.add(lblEndPosition, "6, 4, center, default");
		
		JLabel lblX = new JLabel("X");
		panel_1.add(lblX, "2, 6, right, default");
		
		txtStartX = new JTextField();
		txtStartX.setText("0.000");
		panel_1.add(txtStartX, "4, 6, fill, default");
		txtStartX.setColumns(9);
		
		txtEndX = new JTextField();
		txtEndX.setText("0.000");
		panel_1.add(txtEndX, "6, 6, fill, default");
		txtEndX.setColumns(9);
		
		JLabel lblY = new JLabel("Y");
		panel_1.add(lblY, "2, 8, right, default");
		
		txtStartY = new JTextField();
		txtStartY.setText("0.000");
		panel_1.add(txtStartY, "4, 8, fill, default");
		txtStartY.setColumns(9);
		
		txtEndY = new JTextField();
		txtEndY.setText("0.000");
		panel_1.add(txtEndY, "6, 8, fill, default");
		txtEndY.setColumns(9);
		
		JSeparator separator = new JSeparator();
		panel_1.add(separator, "2, 10, 5, 1");
		
		JLabel lblFinishX = new JLabel("Finish X");
		panel_1.add(lblFinishX, "2, 12, right, default");
		
		finishX = new JTextField();
		finishX.setText("0.000");
		panel_1.add(finishX, "4, 12, fill, default");
		finishX.setColumns(10);
		
		JLabel lblFinishY = new JLabel("Finish Y");
		panel_1.add(lblFinishY, "2, 14, right, default");
		
		finishY = new JTextField();
		finishY.setText("0.000");
		panel_1.add(finishY, "4, 14, fill, default");
		finishY.setColumns(10);
		
		JLabel lblNumberOfRandom = new JLabel("Number of Random Moves");
		panel_1.add(lblNumberOfRandom, "2, 16, right, default");
		
		txtNumMoves = new JTextField();
		txtNumMoves.setText("1000");
		panel_1.add(txtNumMoves, "4, 16, fill, default");
		txtNumMoves.setColumns(10);
		
	}
	
	public void run() {
		try {
			double minX = Double.parseDouble(txtStartX.getText());
			double minY = Double.parseDouble(txtStartY.getText());
			double maxX = Double.parseDouble(txtEndX.getText());
			double maxY = Double.parseDouble(txtEndY.getText());
			double finishX = Double.parseDouble(this.finishX.getText());
			double finishY = Double.parseDouble(this.finishY.getText());
			int numMoves = Integer.parseInt(txtNumMoves.getText());
			
			progressBar.setMinimum(0);
			progressBar.setMaximum(numMoves - 1);
			progressBar.setValue(0);
			for (int i = 0; i < numMoves && !cancelled; i++) {
				Location l = new Location(
						Configuration.get().getSystemUnits(),
						getBoundedRandomDouble(minX, maxX),
						getBoundedRandomDouble(minY, maxY),
						0,
						0);
				MainFrame.machineControlsPanel.getSelectedNozzle().moveTo(l, 1.0);
				progressBar.setValue(i);
			}
			Location l = new Location(
					Configuration.get().getSystemUnits(),
					finishX,
					finishY,
					0,
					0);
			MainFrame.machineControlsPanel.getSelectedNozzle().moveTo(l, 1.0);
		}
		catch (Exception e) {
			MessageBoxes.errorBox(frame, "Scan Error", e.getMessage());
			e.printStackTrace();
		}
		btnStart.setAction(startAction);
	}
	
	private double getBoundedRandomDouble(double min, double max) {
		return (Math.random() * (max - min)) + min;
	}
	
	private Action startAction = new AbstractAction("Start") {
		@Override
		public void actionPerformed(ActionEvent e) {
			btnStart.setAction(stopAction);
			cancelled = false;
			new Thread(MachineCalibration.this).start();
		}
	};
	
	private Action stopAction = new AbstractAction("Cancel") {
		@Override
		public void actionPerformed(ActionEvent e) {
			cancelled = true;
			btnStart.setAction(startAction);
		}
	};
	private JTextField txtNumMoves;
	private JTextField finishX;
	private JTextField finishY;
}
