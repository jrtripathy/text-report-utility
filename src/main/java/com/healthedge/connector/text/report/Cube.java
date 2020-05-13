/*
 * Copyright © 2001-2018 HealthEdge Software, Inc. All Rights Reserved.
 *
 * This software is proprietary information of HealthEdge Software, Inc.
 * and may not be reproduced or redistributed for any purpose.
 */

package com.healthedge.connector.text.report;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Cube {

    protected static int nextIndex = 0;

    private Panel panel;

    private final int index;

    private int width;

    private int height;

    private boolean allowGrid;

    private int blockAlign;

    public static final int BLOCK_LEFT = 1;

    public static final int BLOCK_CENTRE = 2;

    public static final int BLOCK_RIGHT = 3;

    private String data;

    private int dataAlign;

    public static final int DATA_TOP_LEFT = 4;

    public static final int DATA_TOP_MIDDLE = 5;

    public static final int DATA_TOP_RIGHT = 6;

    public static final int DATA_MIDDLE_LEFT = 7;

    public static final int DATA_CENTER = 8;

    public static final int DATA_MIDDLE_RIGHT = 9;

    public static final int DATA_BOTTOM_LEFT = 10;

    public static final int DATA_BOTTOM_MIDDLE = 11;

    public static final int DATA_BOTTOM_RIGHT = 12;

    private int x;

    private int y;

    private Cube rightCube;

    private Cube belowCube;

    private List<Chart> chartsList;

    private String preview;

    public Cube(Panel panel, int width, int height) {
        this.panel = panel;
        if (width <= panel.panelWidth) {
            this.width = width;
        } else {
            throw new ReportException("Cube " + toString() + " exceeded the panel width " + panel.panelWidth);
        }
        this.height = height;
        this.allowGrid = true;
        this.blockAlign = BLOCK_LEFT;
        this.data = null;
        this.dataAlign = DATA_TOP_LEFT;
        this.x = 0;
        this.y = 0;
        this.rightCube = null;
        this.belowCube = null;
        this.chartsList = new ArrayList<>();
        this.preview = "";
        this.index = nextIndex;
        Cube.nextIndex++;
    }

    public Cube(Panel panel, int width, int height, String data) {
        this(panel, width, height);
        this.data = data;
    }

    public Cube(Panel panel, int width, int height, String data, Cube rightCube, Cube belowCube) {
        this(panel, width, height, data);
        if (rightCube != null) {
            rightCube.setX(getX() + getWidth() + (isGridAllowed() ? 1 : 0));
            rightCube.setY(getY());
            this.rightCube = rightCube;
        }
        if (belowCube != null) {
            belowCube.setX(getX());
            belowCube.setY(getY() + getHeight() + (isGridAllowed() ? 1 : 0));
            this.belowCube = belowCube;
        }
    }

    protected int getIndex() {
        return index;
    }

    public int getWidth() {
        return width;
    }

    public Cube setWidth(int width) {
        this.width = width;
        return this;
    }

    public int getHeight() {
        return height;
    }

    public Cube setHeight(int height) {
        this.height = height;
        return this;
    }

    public boolean isGridAllowed() {
        return allowGrid;
    }

    public Cube allowGrid(boolean allowGrid) {
        this.allowGrid = allowGrid;
        return this;
    }

    public int getCubeAlign() {
        return blockAlign;
    }

    public Cube setCubeAlign(int blockAlign) {
        if (blockAlign == BLOCK_LEFT || blockAlign == BLOCK_CENTRE || blockAlign == BLOCK_RIGHT) {
            this.blockAlign = blockAlign;
        } else {
            throw new ReportException("Invalid block align mode. " + dataAlign + " given.");
        }
        return this;
    }

    public String getData() {
        return data;
    }

    public Cube setData(String data) {
        this.data = data;
        return this;
    }

    public int getDataAlign() {
        return dataAlign;
    }

    public Cube setDataAlign(int dataAlign) {
        if (dataAlign == DATA_TOP_LEFT || dataAlign == DATA_TOP_MIDDLE || dataAlign == DATA_TOP_RIGHT
                || dataAlign == DATA_MIDDLE_LEFT || dataAlign == DATA_CENTER || dataAlign == DATA_MIDDLE_RIGHT
                || dataAlign == DATA_BOTTOM_LEFT || dataAlign == DATA_BOTTOM_MIDDLE || dataAlign == DATA_BOTTOM_RIGHT) {
            this.dataAlign = dataAlign;
        } else {
            throw new ReportException("Invalid data align mode. " + dataAlign + " given.");
        }
        return this;
    }

    protected int getX() {
        return x;
    }

    protected Cube setX(int x) {
        if (x + getWidth() + (isGridAllowed() ? 2 : 0) <= panel.panelWidth) {
            this.x = x;
        } else {
            throw new ReportException("Cube " + toString() + " exceeded the panel width " + panel.panelWidth);
        }
        return this;
    }

    protected int getY() {
        return y;
    }

    protected Cube setY(int y) {
        this.y = y;
        return this;
    }

    public Cube getRightCube() {
        return rightCube;
    }

    public Cube setRightCube(Cube rightCube) {
        if (rightCube != null) {
            rightCube.setX(getX() + getWidth() + (isGridAllowed() ? 1 : 0));
            rightCube.setY(getY());
            this.rightCube = rightCube;
        }
        return this;
    }

    public Cube getBelowCube() {
        return belowCube;
    }

    public Cube setBelowCube(Cube belowCube) {
        if (belowCube != null) {
            belowCube.setX(getX());
            belowCube.setY(getY() + getHeight() + (isGridAllowed() ? 1 : 0));
            this.belowCube = belowCube;
        }
        return this;
    }

    protected Cube invalidate() {
        chartsList = new ArrayList<>();
        preview = "";
        return this;
    }

    protected Cube build() {
        if (chartsList.isEmpty()) {
            int ix = x;
            int iy = y;
            int blockLeftSideSpaces = -1;
            int additionalWidth = (isGridAllowed() ? 2 : 0);
            switch (getCubeAlign()) {
                case BLOCK_LEFT:
                    blockLeftSideSpaces = 0;
                    break;
                case BLOCK_CENTRE:
                    blockLeftSideSpaces = (panel.panelWidth - (ix + getWidth() + additionalWidth)) / 2 + (panel.panelWidth - (ix + getWidth() + additionalWidth)) % 2;
                    break;
                case BLOCK_RIGHT:
                    blockLeftSideSpaces = panel.panelWidth - (ix + getWidth() + additionalWidth);
                    break;
                default:
            }
            ix += blockLeftSideSpaces;
            if (data == null) {
                data = toString();
            }
            String[] lines = data.split("\n");
            List<String> dataInLines = new ArrayList<>();
            if (panel.showCubeIndex) {
                dataInLines.add("i = " + index);
            }
            for (String line : lines) {
                if (getHeight() > dataInLines.size()) {
                    dataInLines.add(line);
                } else {
                    break;
                }
            }
            for (int i = dataInLines.size(); i < getHeight(); i++) {
                dataInLines.add("");
            }
            for (int i = 0; i < dataInLines.size(); i++) {
                String dataLine = dataInLines.get(i);
                if (dataLine.length() > getWidth()) {
                    dataInLines.set(i, dataLine.substring(0, getWidth()));
                    if (i + 1 != dataInLines.size()) {
                        String prifix = dataLine.substring(getWidth(), dataLine.length());
                        String suffix = dataInLines.get(i + 1);
                        String combinedValue = prifix.concat((suffix.length() > 0 ? String.valueOf(Chart.S) : "")).concat(suffix);
                        dataInLines.set(i + 1, combinedValue);
                    }
                }
            }

            for (int i = 0; i < dataInLines.size(); i++) {
                if (dataInLines.remove("")) {
                    i--;
                }
            }

            int givenAlign = getDataAlign();
            int dataStartingLineIndex = -1;
            int additionalHeight = (isGridAllowed() ? 1 : 0);
            if (givenAlign == DATA_TOP_LEFT || givenAlign == DATA_TOP_MIDDLE || givenAlign == DATA_TOP_RIGHT) {
                dataStartingLineIndex = iy + additionalHeight;
            } else if (givenAlign == DATA_MIDDLE_LEFT || givenAlign == DATA_CENTER || givenAlign == DATA_MIDDLE_RIGHT) {
                dataStartingLineIndex = iy + additionalHeight + ((getHeight() - dataInLines.size()) / 2 + (getHeight() - dataInLines.size()) % 2);
            } else if (givenAlign == DATA_BOTTOM_LEFT || givenAlign == DATA_BOTTOM_MIDDLE || givenAlign == DATA_BOTTOM_RIGHT) {
                dataStartingLineIndex = iy + additionalHeight + (getHeight() - dataInLines.size());
            }
            int dataEndingLineIndex = dataStartingLineIndex + dataInLines.size();

            int extendedIX = ix + getWidth() + (isGridAllowed() ? 2 : 0);
            int extendedIY = iy + getHeight() + (isGridAllowed() ? 2 : 0);
            int startingIX = ix;
            int startingIY = iy;
            for (; iy < extendedIY; iy++) {
                for (; ix < extendedIX; ix++) {
                    boolean writeData;
                    if (isGridAllowed()) {
                        if ((iy == startingIY) || (iy == extendedIY - 1)) {
                            if ((ix == startingIX) || (ix == extendedIX - 1)) {
                                chartsList.add(new Chart(ix, iy, Chart.P));
                                writeData = false;
                            } else {
                                chartsList.add(new Chart(ix, iy, Chart.D));
                                writeData = false;
                            }
                        } else {
                            if ((ix == startingIX) || (ix == extendedIX - 1)) {
                                chartsList.add(new Chart(ix, iy, Chart.VL));
                                writeData = false;
                            } else {
                                writeData = true;
                            }
                        }
                    } else {
                        writeData = true;
                    }
                    if (writeData && (iy >= dataStartingLineIndex && iy < dataEndingLineIndex)) {
                        int dataLineIndex = iy - dataStartingLineIndex;
                        String lineData = dataInLines.get(dataLineIndex);
                        if (!lineData.isEmpty()) {
                            int dataLeftSideSpaces = -1;
                            if (givenAlign == DATA_TOP_LEFT || givenAlign == DATA_MIDDLE_LEFT || givenAlign == DATA_BOTTOM_LEFT) {
                                dataLeftSideSpaces = 0;
                            } else if (givenAlign == DATA_TOP_MIDDLE || givenAlign == DATA_CENTER || givenAlign == DATA_BOTTOM_MIDDLE) {
                                dataLeftSideSpaces = (getWidth() - lineData.length()) / 2 + (getWidth() - lineData.length()) % 2;
                            } else if (givenAlign == DATA_TOP_RIGHT || givenAlign == DATA_MIDDLE_RIGHT || givenAlign == DATA_BOTTOM_RIGHT) {
                                dataLeftSideSpaces = getWidth() - lineData.length();
                            }
                            int dataStartingIndex = (startingIX + dataLeftSideSpaces + (isGridAllowed() ? 1 : 0));
                            int dataEndingIndex = (startingIX + dataLeftSideSpaces + lineData.length() - (isGridAllowed() ? 0 : 1));
                            if (ix >= dataStartingIndex && ix <= dataEndingIndex) {
                                char charData = lineData.charAt(ix - dataStartingIndex);
                                chartsList.add(new Chart(ix, iy, charData));
                            }
                        }
                    }
                }
                ix = startingIX;
            }
        }
        return this;
    }

    protected List<Chart> getChars() {
        return this.chartsList;
    }

    public String getPreview() {
        build();
        if (preview.isEmpty()) {
            int maxY = -1;
            int maxX = -1;
            for (Chart charr : chartsList) {
                int testY = charr.getY();
                int testX = charr.getX();
                if (maxY < testY) {
                    maxY = testY;
                }
                if (maxX < testX) {
                    maxX = testX;
                }
            }
            String[][] dataPoints = new String[maxY + 1][panel.panelWidth];
            for (Chart charr : chartsList) {
                dataPoints[charr.getY()][charr.getX()] = String.valueOf(charr.getC());
            }

            for (String[] dataPoint : dataPoints) {
                for (String point : dataPoint) {
                    if (point == null) {
                        point = String.valueOf(Chart.S);
                    }
                    preview = preview.concat(point);
                }
                preview = preview.concat(String.valueOf(Chart.NL));
            }
        }
        return preview;
    }

    public Cube getMostRightCube() {
        return getMostRightCube(this);
    }

    private Cube getMostRightCube(Cube block) {
        if (block.getRightCube() == null) {
            return block;
        } else {
            return getMostRightCube(block.getRightCube());
        }
    }

    public Cube getMostBelowCube() {
        return getMostBelowCube(this);
    }

    private Cube getMostBelowCube(Cube block) {
        if (block.getBelowCube() == null) {
            return block;
        } else {
            return getMostBelowCube(block.getBelowCube());
        }
    }

    @Override
    public String toString() {
        return index + " = [" + x + "," + y + "," + width + "," + height + "]";
    }

    @Override
    public boolean equals(Object block) {
        if (block == null) {
            return false;
        }
        if (!(block instanceof Cube)) {
            return false;
        }
        Cube b = (Cube) block;
        return b.getIndex() == getIndex() && b.getX() == getX() && b.getY() == getY();
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 43 * hash + this.index;
        hash = 43 * hash + this.width;
        hash = 43 * hash + this.height;
        hash = 43 * hash + (this.allowGrid ? 1 : 0);
        hash = 43 * hash + this.blockAlign;
        hash = 43 * hash + Objects.hashCode(this.data);
        hash = 43 * hash + this.dataAlign;
        hash = 43 * hash + this.x;
        hash = 43 * hash + this.y;
        hash = 43 * hash + Objects.hashCode(this.rightCube);
        hash = 43 * hash + Objects.hashCode(this.belowCube);
        return hash;
    }
}
