package org.crossfit.app.service.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import org.joda.time.LocalDate;

import java.io.UnsupportedEncodingException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;


public class PdfUtils {

    public static PdfPCell getCell(String value, int alignment, Font font, BaseColor backgroundColor) {
        return getCell(value, alignment, font, 0, backgroundColor, -1);
    }
    public static PdfPCell getCell(String value, int alignment, Font font, int colspan, int border) {
        return getCell(value, alignment, font, colspan, null, border);
    }
    public static PdfPCell getCell(String value, int alignment, Font font) {
        return getCell(value, alignment, font, 0, null, -1);
    }

    public static PdfPCell getCell(String value, int alignment, Font font, int colspan, BaseColor backgroundColor, int border) {
        PdfPCell cell = new PdfPCell();
        cell.setUseAscender(true);
        cell.setUseDescender(true);
        cell.setPadding(5);
        cell.setColspan(colspan);
        if (border != -1)
            cell.setBorder(border);
        if (backgroundColor !=null)
            cell.setBackgroundColor(backgroundColor);
        cell.setHorizontalAlignment(alignment);
        Paragraph p = new Paragraph(value, font);
        p.setAlignment(alignment);
        cell.addElement(p);
        return cell;
    }



    public static String formatDate(LocalDate date, String format) throws UnsupportedEncodingException {
        return date == null ? "" : date.toString(format);
    }
    public static String formatPrice(double value) {
        return NumberFormat.getCurrencyInstance(Locale.FRANCE).format(value);
    }
    public static String formatPerCent(double value) {
        return value + "%";
    }
    public static String convertDate(Date d, String newFormat) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(newFormat);
        return sdf.format(d);
    }
}
