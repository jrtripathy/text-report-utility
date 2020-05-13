/*
 * Copyright © 2001-2018 HealthEdge Software, Inc. All Rights Reserved.
 *
 * This software is proprietary information of HealthEdge Software, Inc.
 * and may not be reproduced or redistributed for any purpose.
 */

package com.healthedge.connector.text.report;

import java.util.ArrayList;
import java.util.List;

public class Panel {
    
    protected boolean showCubeIndex;

    protected int panelWidth;

    private Cube initialCube;

    private List<Chart> charts;

    private String preview;

    public static final int APPEND_RIGHT = 16;

    public static final int APPEND_BELOW = 17;

    public Panel(int panelWidth) {
        this.panelWidth = panelWidth;
        this.charts = new ArrayList<>();
        this.preview = "";
        this.showCubeIndex = false;
        Cube.nextIndex = 0;
    }

    public Panel setInitialCube(Cube initialCube) {
        this.initialCube = initialCube;
        return this;
    }

    public boolean isCubeIndexShowing() {
        return showCubeIndex;
    }

    public void showCubeIndex(boolean showCubeIndex) {
        this.showCubeIndex = showCubeIndex;
    }        

    public Panel appendTableTo(int appendableCubeIndex, int appendableDirection, Table table) {
        Cube tableCube = table.tableToCubes();
        Cube cube = getCube(appendableCubeIndex);
        if (appendableDirection == APPEND_RIGHT) {
            cube.setRightCube(tableCube);
            rearranegCoordinates(cube);
        } else if (appendableDirection == APPEND_BELOW) {
            cube.setBelowCube(tableCube);
            rearranegCoordinates(cube);
        } else {
            throw new ReportException("Invalid cube appending direction given");
        }
        return this;
    }

    private void rearranegCoordinates(Cube cube) {
        Cube rightCube = cube.getRightCube();
        Cube belowCube = cube.getBelowCube();
        if (rightCube != null && belowCube == null) {
            cube.setRightCube(rightCube);
            rearranegCoordinates(rightCube);
        } else if (rightCube == null && belowCube != null) {
            cube.setBelowCube(belowCube);
            rearranegCoordinates(belowCube);
        } else if (rightCube != null && belowCube != null) {
            int rightIndex = rightCube.getIndex();
            int belowIndex = belowCube.getIndex();
            int cubeIdDiff = rightIndex - belowIndex;
            if (cubeIdDiff > 0) {
                if (cubeIdDiff == 1) {
                    cube.setRightCube(rightCube);
                    cube.setBelowCube(belowCube);
                    rearranegCoordinates(rightCube);
                    rearranegCoordinates(belowCube);
                } else {
                    cube.setRightCube(rightCube);
                    rearranegCoordinates(rightCube);
                    cube.setBelowCube(belowCube);
                    rearranegCoordinates(belowCube);
                }
            } else if (cubeIdDiff < 0) {
                cubeIdDiff *= -1;
                if (cubeIdDiff == 1) {
                    cube.setBelowCube(belowCube);
                    cube.setRightCube(rightCube);
                    rearranegCoordinates(belowCube);
                    rearranegCoordinates(rightCube);
                } else {
                    cube.setBelowCube(belowCube);
                    rearranegCoordinates(belowCube);
                    cube.setRightCube(rightCube);
                    rearranegCoordinates(rightCube);
                }
            }
        }
    }

    public Cube getCube(int cubeIndex) {
        if (cubeIndex >= 0) {
            return getCube(cubeIndex, initialCube);
        } else {
            throw new ReportException("Cube index cannot be negative. " + cubeIndex + " given.");
        }
    }

    private Cube getCube(int cubeIndex, Cube cube) {
        Cube foundCube = null;
        if (cube.getIndex() == cubeIndex) {
            return cube;
        } else {
            if (cube.getRightCube() != null) {
                foundCube = getCube(cubeIndex, cube.getRightCube());
            }
            if (foundCube != null) {
                return foundCube;
            }
            if (cube.getBelowCube() != null) {
                foundCube = getCube(cubeIndex, cube.getBelowCube());
            }
            if (foundCube != null) {
                return foundCube;
            }
        }
        return foundCube;
    }

    public Panel build() {
        if (charts.isEmpty()) {
            buildCube(initialCube);
            dumpchartsFromCube(initialCube);

            int maxY = -1;
            int maxX = -1;
            for (Chart charr : charts) {
                int testY = charr.getY();
                int testX = charr.getX();
                if (maxY < testY) {
                    maxY = testY;
                }
                if (maxX < testX) {
                    maxX = testX;
                }
            }
            String[][] dataPoints = new String[maxY + 1][panelWidth];
            for (Chart charr : charts) {
                String currentValue = dataPoints[charr.getY()][charr.getX()];
                String newValue = String.valueOf(charr.getC());
                if (currentValue == null || !currentValue.equals("+")) {
                    dataPoints[charr.getY()][charr.getX()] = newValue;
                }
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

        return this;
    }

    public String getPreview() {
        build();
        return preview;
    }

    public Panel invalidate() {
        invalidateCube(initialCube);
        charts = new ArrayList<>();
        preview = "";
        return this;
    }

    private void buildCube(Cube cube) {
        if (cube != null) {
            cube.build();
            buildCube(cube.getRightCube());
            buildCube(cube.getBelowCube());
        }
    }

    private void dumpchartsFromCube(Cube cube) {
        if (cube != null) {
            charts.addAll(cube.getChars());
            dumpchartsFromCube(cube.getRightCube());
            dumpchartsFromCube(cube.getBelowCube());
        }
    }

    private void invalidateCube(Cube cube) {
        if (cube != null) {
            cube.invalidate();
            invalidateCube(cube.getRightCube());
            invalidateCube(cube.getBelowCube());
        }
    }

}
