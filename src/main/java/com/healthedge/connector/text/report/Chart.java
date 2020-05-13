/*
 * Copyright © 2001-2018 HealthEdge Software, Inc. All Rights Reserved.
 *
 * This software is proprietary information of HealthEdge Software, Inc.
 * and may not be reproduced or redistributed for any purpose.
 */

package com.healthedge.connector.text.report;

public class Chart {
    
    protected static final char S = ' ';
    
    protected static final char NL = '\n';
    
    protected static final char P = '+';
    
    protected static final char D = '-';
    
    protected static final char VL = '|';

    private final int x;

    private final int y;

    private final char c;

    protected Chart(int x, int y, char c) {
        this.x = x;
        this.y = y;
        this.c = c;
    }

    protected int getX() {
        return x;
    }

    protected int getY() {
        return y;
    }

    protected char getC() {
        return c;
    }

}
