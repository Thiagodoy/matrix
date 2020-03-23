/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.service;

import com.core.matrix.dto.MeansurementFileResultStatusDTO;
import com.core.matrix.request.MeansurementResultRequest;
import com.core.matrix.utils.ReportConstants;
import java.io.IOException;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author thiag
 */
@Service
public class ReportService {

    private SXSSFWorkbook wb;
    private SXSSFSheet sheet;

    @Autowired
    private MeansurementFileResultService fileResultService;

    public void export(HttpServletResponse response, MeansurementResultRequest request) throws IOException {

        List<MeansurementFileResultStatusDTO> results = fileResultService.getStatusBilling(request.getYear(), request.getMonth());

        final ReportConstants.ReportType type = request.getType();
        List<MeansurementFileResultStatusDTO> report = results
                .stream()
                .filter(t -> request
                .getIds()
                .stream()
                .filter(tt -> tt.equals(t.getId()))
                .findFirst()
                .isPresent())
                .collect(Collectors.toList());

        this.createAndWriteFile(report, type);

        OutputStream out = response.getOutputStream();

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-disposition", "attachment; filename=exportação.xlsx");

        this.wb.write(out);
        out.flush();
        out.close();
        this.wb.dispose();
    }

    private void createAndWriteFile(List<MeansurementFileResultStatusDTO> regs, ReportConstants.ReportType type) {

        String sheetName = "WBC";

        wb = new SXSSFWorkbook();
        wb.setCompressTempFiles(true);

        sheet = wb.createSheet(sheetName);

        CellStyle cellBold = wb.createCellStyle();

        Font font = wb.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setFontName("Arial");
        font.setColor(IndexedColors.BLACK.getIndex());
        font.setBold(true);
        font.setItalic(true);

        int line = 0;
        String[] header = ReportConstants.getHeaders(type);
        SXSSFRow rr = sheet.createRow(0);
        cellBold.setFont(font);

        //Escreve o header do arquivo
        for (int i = 0; i < header.length; i++) {
            SXSSFCell cell = rr.createCell(i);
            cell.setCellValue(header[i]);
        }

        Font fontRecord = wb.createFont();
        fontRecord.setCharSet(XSSFFont.ANSI_CHARSET);

        CellStyle style = wb.createCellStyle();
        style.setFont(fontRecord);

        for (MeansurementFileResultStatusDTO r : regs) {

            line++;
            SXSSFRow rrr = sheet.createRow(line);
            Object[] values = r.export(type);

            DataFormat format = sheet.getWorkbook().createDataFormat();
            NumberFormat numberFormat = NumberFormat.getIntegerInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

            for (int i = 0; i < values.length; i++) {
                SXSSFCell cell = rrr.createCell(i);

                Object value = values[i];
                if (value instanceof Double) {
                    CellStyle cellStyle = cell.getCellStyle();
                    ((XSSFCellStyle) cellStyle).setDataFormat(format.getFormat("#,##0.00000"));
                    cell.setCellValue((Double) value);
                } else if (value instanceof Integer) {
                    cell.setCellValue(numberFormat.format(value));
                } else if (value instanceof Long) {
                    cell.setCellValue(numberFormat.format(value));
                } else if (value instanceof Date) {
                    Date date = (Date) value;
                    cell.setCellValue(dateFormat.format(date));
                } else {
                    cell.setCellValue(String.valueOf(value));
                }

            }

        }
    }

}
