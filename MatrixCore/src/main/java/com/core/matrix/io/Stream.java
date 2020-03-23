/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.io;

import com.core.matrix.utils.MeansurementFileType;

/**
 *
 * @author thiag
 */
public enum Stream {

    FILE_LAYOUT_A_PARSER("file-matrix", "static/FILE_LAYOUT_A.xml"),
    FILE_LAYOUT("file-matrix", "static/FILE_LAYOUT.xml"),
    FILE_LAYOUT_B_PARSER("file", "static/FILE_LAYOUT_B.xml"),
    FILE_LAYOUT_C_PARSER("file-matrix", "static/FILE_LAYOUT_C.xml"),
    FILE_LAYOUT_C_1_PARSER("file-matrix", "static/FILE_LAYOUT_C_1.xml"),
    CHECK_LAYOUT_PARSER("file", "static/CHECK_LAYOUT.xml");
    

    private String streamId;
    private String streamFile;

    private Stream(String streamId, String streamFile) {
        this.streamId = streamId;
        this.streamFile = streamFile;
    }

    public String getStreamFile() {
        return this.streamFile;
    }

    public String getStreamId() {
        return this.streamId;
    }

    public static Stream getByLayoutFile(MeansurementFileType type) throws Exception {

        switch (type) {
            case LAYOUT_A:
                return FILE_LAYOUT_A_PARSER;
            case LAYOUT_B:
                return FILE_LAYOUT_B_PARSER;
            case LAYOUT_C:
                return FILE_LAYOUT_C_PARSER;
            case LAYOUT_C_1:
                return FILE_LAYOUT_C_1_PARSER;
            default:
                throw new Exception("Não fopi possivel determinar o layout do arquivo");
        }

    }
}
