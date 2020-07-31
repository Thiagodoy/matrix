/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.utils;

import java.io.Serializable;

/**
 *
 * @author thiag
 */
public enum MeansurementFileStatus implements Serializable{
    FILE_PENDING,
    FILE_ERROR,
    POINT_ERROR,
    DATA_HOUR_ERROR,
    DATA_DAY_ERROR,
    DATA_CALENDAR_ERROR,
    LAYOUT_ERROR,
    APPROVED,
    FILE_MISSING_ALL_HOURS,
    FILE_CHECKED,
    SUCCESS;    
}
