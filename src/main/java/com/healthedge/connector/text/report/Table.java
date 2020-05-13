/*
 * Copyright © 2001-2018 HealthEdge Software, Inc. All Rights Reserved.
 *
 * This software is proprietary information of HealthEdge Software, Inc.
 * and may not be reproduced or redistributed for any purpose.
 */

package com.healthedge.connector.text.report;

import java.util.ArrayList;
import java.util.List;

public final class Table {
    
    private Panel panel;

    private List<String> headersList;

    private List<List<String>> rowsList;

    private List<Integer> colWidthsList;

    private List<Integer> colAlignsList;

    private int headerHeight;

    private int rowHeight;

    private int gridMode;

    private Cube initialTableCube;

    public static final int GRID_NON = 13;

    public static final int GRID_FULL = 14;

    public static final int GRID_COLUMN = 15;

    public Table(Panel panel, int tableWidth, List<String> headersList, List<List<String>> rowsList) {
        this.panel = panel;
        if (tableWidth <= 0) {
            throw new ReportException("Board width must be large than zero. " + tableWidth + " given.");
        }

        if (headersList.isEmpty()) {
            throw new ReportException("Header size must be large than zero. " + headersList.size() + " found.");
        } else {
            this.headersList = headersList;
        }
        for (int i = 0; i < rowsList.size(); i++) {
            List<String> row = rowsList.get(i);
            if (row.size() != headersList.size()) {
                throw new ReportException("Size(" + row.size() + ") of the row(" + i + ") and header size(" + headersList.size() + ") are not equal");
            }
        }
        this.rowsList = rowsList;
        this.colWidthsList = new ArrayList<>();
        int avgWidthOfCol = (tableWidth - (gridMode == GRID_NON ? 0 : headersList.size() + 1)) / headersList.size();
        int availableForExtend = (tableWidth - (gridMode == GRID_NON ? 0 : headersList.size() + 1)) % headersList.size();
        for (int i = 0; i < headersList.size(); i++, availableForExtend--) {
            int finalWidth = avgWidthOfCol + (availableForExtend > 0 ? 1 : 0);
            this.colWidthsList.add(finalWidth);
        }
        this.colAlignsList = new ArrayList<>();
        List<String> firstRow = rowsList.get(0);
        for (String cell : firstRow) {
            int alignMode;
            try {
                Long.parseLong(cell);
                alignMode = Cube.DATA_MIDDLE_RIGHT;
            } catch (NumberFormatException e0) {
                try {
                    Integer.parseInt(cell);
                    alignMode = Cube.DATA_MIDDLE_RIGHT;
                } catch (NumberFormatException e1) {
                    try {
                        Double.parseDouble(cell);
                        alignMode = Cube.DATA_MIDDLE_RIGHT;
                    } catch (NumberFormatException e2) {
                        alignMode = Cube.DATA_MIDDLE_LEFT;
                    }
                }
            }
            this.colAlignsList.add(alignMode);
        }
        headerHeight = 1;
        rowHeight = 1;
        gridMode = GRID_COLUMN;
    }

    public Table(Panel panel, int tableWidth, List<String> headersList, List<List<String>> rowsList, List<Integer> colWidthsList) {
        this(panel, tableWidth, headersList, rowsList);
        if (colWidthsList.size() != headersList.size()) {
            throw new ReportException("Column width count(" + colWidthsList.size() + ") and header size(" + headersList.size() + ") are not equal");
        } else {
            this.colWidthsList = colWidthsList;
        }
    }

    public Table(Panel panel, int tableWidth, List<String> headersList, List<List<String>> rowsList, List<Integer> colWidthsList, List<Integer> colAlignsList) {
        this(panel, tableWidth, headersList, rowsList, colWidthsList);
        if (colAlignsList.size() != headersList.size()) {
            throw new ReportException("Column align count(" + colAlignsList.size() + ") and header size(" + headersList.size() + ") are not equal");
        } else {
            this.colAlignsList = colAlignsList;
        }
    }

    public List<String> getHeadersList() {
        return headersList;
    }

    public Table setHeadersList(List<String> headersList) {
        this.headersList = headersList;
        return this;
    }

    public List<List<String>> getRowsList() {
        return rowsList;
    }

    public Table setRowsList(List<List<String>> rowsList) {
        this.rowsList = rowsList;
        return this;
    }

    public List<Integer> getColWidthsList() {
        return colWidthsList;
    }

    public Table setColWidthsList(List<Integer> colWidthsList) {
        if (colWidthsList.size() != headersList.size()) {
            throw new ReportException("Column width count(" + colWidthsList.size() + ") and header size(" + headersList.size() + ") are not equal");
        } else {
            this.colWidthsList = colWidthsList;
        }
        return this;
    }

    public List<Integer> getColAlignsList() {
        return colAlignsList;
    }

    public Table setColAlignsList(List<Integer> colAlignsList) {
        if (colAlignsList.size() != headersList.size()) {
            throw new ReportException("Column align count(" + colAlignsList.size() + ") and header size(" + headersList.size() + ") are not equal");
        } else {
            this.colAlignsList = colAlignsList;
        }
        return this;
    }

    public int getHeaderHeight() {
        return headerHeight;
    }

    public Table setHeaderHeight(int headerHeight) {
        this.headerHeight = headerHeight;
        return this;
    }

    public int getRowHeight() {
        return rowHeight;
    }

    public Table setRowHeight(int rowHeight) {
        this.rowHeight = rowHeight;
        return this;
    }

    public int getGridMode() {
        return gridMode;
    }

    public Table setGridMode(int gridMode) {
        if (gridMode == GRID_NON || gridMode == GRID_FULL || gridMode == GRID_COLUMN) {
            this.gridMode = gridMode;
        } else {
            throw new ReportException("Invalid grid mode. " + gridMode + " given.");
        }
        return this;
    }

    public Cube tableToCubes() {
        for (int i = 0; i < headersList.size(); i++) {
            String headerValue = headersList.get(i);
            int columnWidth = colWidthsList.get(i);
            Cube block = new Cube(panel, columnWidth, headerHeight, headerValue);
            if (getGridMode() == GRID_NON) {
                block.allowGrid(false);
            } else {
                block.allowGrid(true);
            }
            int alignIndex = colAlignsList.get(i);
            block.setDataAlign(alignIndex);
            if (initialTableCube == null) {
                initialTableCube = block;
            } else {
                initialTableCube.getMostRightCube().setRightCube(block);
            }
        }
        if (getGridMode() != GRID_COLUMN) {
            for (int i = 0; i < rowsList.size(); i++) {
                List<String> row = rowsList.get(i);
                Cube rowStartingCube = initialTableCube.getMostBelowCube();
                for (int j = 0; j < row.size(); j++) {
                    String rowValue = row.get(j);
                    int columnWidth = colWidthsList.get(j);
                    Cube block = new Cube(panel, columnWidth, rowHeight, rowValue);
                    if (getGridMode() == GRID_NON) {
                        block.allowGrid(false);
                    } else {
                        block.allowGrid(true);
                    }
                    int alignIndex = colAlignsList.get(j);
                    block.setDataAlign(alignIndex);

                    if (rowStartingCube.getBelowCube() == null) {
                        rowStartingCube.setBelowCube(block);
                    } else {
                        rowStartingCube.getBelowCube().getMostRightCube().setRightCube(block);
                    }
                }
            }
        } else {            
            for (int i = 0; i < headersList.size(); i++) {
                String columnData = "";
                for (int j = 0; j < rowsList.size(); j++) {
                    String rowData = rowsList.get(j).get(i);
                    columnData = columnData.concat(rowData).concat("\n");
                }
                Cube block = new Cube(panel, colWidthsList.get(i), rowsList.size(),columnData);
                int alignIndex = colAlignsList.get(i);
                    block.setDataAlign(alignIndex);
                if (initialTableCube.getBelowCube() == null) {
                    initialTableCube.setBelowCube(block);
                } else {
                    initialTableCube.getBelowCube().getMostRightCube().setRightCube(block);
                }
            }
        }
        return initialTableCube;
    }
    
    public Table invalidate(){
        initialTableCube = null;
        return this;
    }
}
