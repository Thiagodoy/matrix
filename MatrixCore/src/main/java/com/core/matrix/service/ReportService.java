/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.service;

import com.core.matrix.annotation.ReportColumn;
import com.core.matrix.dto.MeansurementFileResultStatusDTO;
import com.core.matrix.request.MeansurementResultRequest;
import com.core.matrix.utils.Report;
import com.core.matrix.utils.ReportConstants;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    @Deprecated
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

        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-disposition", "attachment; filename=exportação.xlsx");

        this.wb.write(out);
        out.flush();
        out.close();
        this.wb.dispose();

        results.stream()
                .forEach(r -> {
                    fileResultService.updateToExported(r.getId());
                });

    }

    public <T> void export(HttpServletResponse response, List<T> data, ReportConstants.ReportType type) throws IOException {

        this.<T>write(data, type);

        OutputStream out = response.getOutputStream();
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-disposition", "attachment; filename=exportação.xlsx");

        this.wb.write(out);
        out.flush();
        out.close();
        this.wb.dispose();
    }

    public <T> String[] mountHeader(List<T> data, ReportConstants.ReportType type) {

        return Arrays.asList(data.get(0)
                .getClass()
                .getDeclaredFields())
                .stream()
                .filter(field -> field.isAnnotationPresent(ReportColumn.class))
                .map(field -> field.getAnnotation(ReportColumn.class))
                .filter(an -> Arrays.binarySearch(an.typeReport(), type.toString()) >= 0)
                .sorted(Comparator.comparing(ReportColumn::position))
                .map(a -> a.name())
                .collect(Collectors.joining(";")).split(";");

    }

    public <T> List<Field> getField(Class<T> cl, ReportConstants.ReportType type) {

        return Arrays.asList(cl.getDeclaredFields())
                .stream()
                .filter(field -> {

                    if (field.isAnnotationPresent(ReportColumn.class)) {

                        ReportColumn annotation = field.getAnnotation(ReportColumn.class);

                        return Arrays.binarySearch(annotation.typeReport(), type.toString()) >= 0;

                    } else {
                        return false;
                    }

                })
                .sorted((a, b) -> {

                    ReportColumn annotationA = a.getAnnotation(ReportColumn.class);
                    ReportColumn annotationB = b.getAnnotation(ReportColumn.class);

                    return Integer.compare(annotationA.position(), annotationB.position());

                })
                .collect(Collectors.toList());

    }

    public <T> Object[] getData(List<Field> fields, T data) {

        Object[] out = new Object[fields.size()];

        for (int i = 0; i < out.length; i++) {
            try {
                out[i] = fields.get(i).get(data);
            } catch (Exception ex) {
                Logger.getLogger(ReportService.class.getName()).log(Level.SEVERE, "", ex);
            }
        }

        return out;
    }

    private <T> void write(List<T> regs, ReportConstants.ReportType type) {

        String sheetName = "Informação";

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
        String[] header = this.mountHeader(regs, type);
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

        List<Field> fields = this.getField(regs.get(0).getClass(), type);

        for (T register : regs) {

            line++;
            SXSSFRow rrr = sheet.createRow(line);
            Object[] values = this.getData(fields, register);

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

                    String v = Optional.ofNullable(value).isPresent() ? String.valueOf(value) : "";

                    cell.setCellValue(v);
                }

            }

        }
    }

    @Deprecated
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
