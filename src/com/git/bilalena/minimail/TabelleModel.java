package com.git.bilalena.minimail;

import javax.swing.table.DefaultTableModel;

public class TabelleModel extends DefaultTableModel {
	private static final long serialVersionUID = 1L;

	public TabelleModel(Object[] spaltenName, int zeileCount) {
        super(spaltenName, zeileCount);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columnIndex == 0 ? Integer.class : String.class;
    }

    // Editing ist verboten
    @Override
    public boolean isCellEditable(int row, int column) {
        return false; 
    }

}
