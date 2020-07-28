/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.exceptions;

import lombok.Data;

/**
 *
 * @author thiag
 */
@Data
public class PointWithoutProinfaException extends Exception {

    public PointWithoutProinfaException() {
        super("Point don´t have proinfa ");
    }

}
