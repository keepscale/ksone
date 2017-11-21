package org.crossfit.app.service;
 
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.lang3.StringUtils;
import org.crossfit.app.domain.Bill;
import org.crossfit.app.domain.BillLine;
import org.crossfit.app.domain.CrossFitBox;
import org.joda.time.LocalDate;
import org.xml.sax.SAXException;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.xmp.XMPException;
 
 
/**
 * Reads bill data from a test database and creates ZUGFeRD bills
 * (Basic profile).
 * @author Bruno Lowagie
 */
public class PdfBill {

	private static final BaseColor TAB_HEADER_COLOR = new BaseColor(87,113,138);

    protected Font fontCFN;
    protected Font fontCFN30;
    protected Font font10;
    protected Font font10b;
    protected Font font12;
    protected Font font12b;
    protected Font font14;
 
    private PdfBill() throws DocumentException, IOException {
        BaseFont bf = BaseFont.createFont();
        BaseFont bfCFN = BaseFont.createFont("fonts/cfn.ttf", BaseFont.WINANSI, BaseFont.EMBEDDED);
        BaseFont bfb = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.WINANSI, BaseFont.EMBEDDED);
        fontCFN = new Font(bfCFN, 18);
        fontCFN30 = new Font(bfCFN, 30);
        font10 = new Font(bf, 10);
        font10b = new Font(bfb, 10);
        font12 = new Font(bf, 12);
        font12b = new Font(bfb, 12);
        font14 = new Font(bf, 14);
    }
    
    public static PdfBill getBuilder() throws DocumentException, IOException {
    	return new PdfBill();
    }
 
    public String getI18n(ResourceBundle bundle, String key) throws UnsupportedEncodingException {
    	return new String(bundle.getString(key).getBytes("ISO-8859-1"), "UTF-8");
    }
    public void createPdf(Bill bill, OutputStream os, ResourceBundle i18n) throws ParserConfigurationException, SAXException, TransformerException, IOException, DocumentException, XMPException, ParseException {

        CrossFitBox box = bill.getBox();
        
        // step 1
        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        // step 2
        PdfWriter writer = PdfWriter.getInstance(document, os);
        writer.setPdfVersion(PdfWriter.VERSION_1_7);
        // step 3
        document.open();

 
        // header
        PdfPCell cFactNumberl = new PdfPCell(new Paragraph(getI18n(i18n, "bill.pdf.label.number") + bill.getNumber(), font14));
        cFactNumberl.setPaddingTop(13);
        cFactNumberl.setBorder(PdfPCell.NO_BORDER);
        cFactNumberl.setHorizontalAlignment(Element.ALIGN_RIGHT);
        
        PdfPCell seller = new PdfPCell();
        seller.setBorder(PdfPCell.NO_BORDER);
		seller.addElement(new Paragraph(box.getName(), fontCFN));
        seller.addElement(new Paragraph(box.getBillAddress(), font12));

        
        // Address seller / buyer
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.addCell(seller);
        table.addCell(cFactNumberl);
        
        document.add(table);

        document.add(new Phrase("\n"));
        
        table = new PdfPTable(2);
        table.setWidthPercentage(100);
        PdfPCell cDest = new PdfPCell();
        cDest.setBorder(PdfPCell.NO_BORDER);
        cDest.addElement(new Paragraph(bill.getDisplayName(), font12b));
        cDest.addElement(new Paragraph(bill.getDisplayAddress(), font12));
        table.addCell(cDest);
        

        PdfPTable tableInfo = new PdfPTable(2);
        
        addLineInfo(tableInfo, getI18n(i18n, "bill.pdf.label.effectiveDate"), formatDate(bill.getEffectiveDate(), i18n));
        addLineInfo(tableInfo, getI18n(i18n, "bill.pdf.label.ref"), bill.getNumber());
        addLineInfo(tableInfo, getI18n(i18n, "bill.pdf.label.memberId"), bill.getMember().getId()+"");
        addLineInfo(tableInfo, getI18n(i18n, "bill.pdf.label.paymentMethod"), getI18n(i18n, "bill.pdf.label.paymentMethod."+bill.getPaymentMethod()));
        addLineInfo(tableInfo, getI18n(i18n, "bill.pdf.label.payAtDate"), formatDate(bill.getPayAtDate(), i18n));

        table.addCell(tableInfo);
        
        document.add(table);

        
        if (StringUtils.isNotBlank(bill.getComments())) {
	        document.add(new Paragraph(getI18n(i18n, "bill.pdf.label.comments"), font12b));
	        document.add(new Paragraph(bill.getComments(), font12));
        }

        document.add(new Phrase("\n"));
        // line items
        table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        table.setSpacingAfter(10);
        table.setWidths(new int[]{7, 1, 2, 2, 2, 2});
        table.addCell(getCell(getI18n(i18n, "bill.pdf.label.line.label"), Element.ALIGN_LEFT, font12b, TAB_HEADER_COLOR));
        table.addCell(getCell(getI18n(i18n, "bill.pdf.label.line.quantity"), Element.ALIGN_CENTER, font12b, TAB_HEADER_COLOR));
        table.addCell(getCell(getI18n(i18n, "bill.pdf.label.line.priceTaxExcl"), Element.ALIGN_CENTER, font12b, TAB_HEADER_COLOR));
        table.addCell(getCell(getI18n(i18n, "bill.pdf.label.line.taxPerCent"), Element.ALIGN_CENTER, font12b, TAB_HEADER_COLOR));
        table.addCell(getCell(getI18n(i18n, "bill.pdf.label.line.totalTaxExcl"), Element.ALIGN_CENTER, font12b, TAB_HEADER_COLOR));
        table.addCell(getCell(getI18n(i18n, "bill.pdf.label.line.totalTaxIncl"), Element.ALIGN_CENTER, font12b, TAB_HEADER_COLOR));
        for (BillLine line : bill.getLines()) {
            table.addCell(getCell(line.getLabel(), Element.ALIGN_LEFT, font12));
            table.addCell(getCell(String.valueOf(line.getQuantity()), Element.ALIGN_RIGHT, font12));
            table.addCell(getCell(formatPrice(line.getPriceTaxExcl()), Element.ALIGN_RIGHT, font12));
            table.addCell(getCell(formatPerCent(line.getTaxPerCent()), Element.ALIGN_RIGHT, font12));
            table.addCell(getCell(formatPrice(line.getTotalTaxExcl()), Element.ALIGN_RIGHT, font12));
            table.addCell(getCell(formatPrice(line.getTotalTaxIncl()), Element.ALIGN_RIGHT, font12));
        }

        table.addCell(getCell("", Element.ALIGN_RIGHT, font12b, 4, PdfPCell.NO_BORDER));
        table.addCell(getCell(getI18n(i18n, "bill.pdf.label.totalTaxExcl"), Element.ALIGN_RIGHT, font12b, TAB_HEADER_COLOR));
        table.addCell(getCell(formatPrice(bill.getTotalTaxExcl()), Element.ALIGN_RIGHT, font12));

        table.addCell(getCell("", Element.ALIGN_RIGHT, font12b, 4, PdfPCell.NO_BORDER));
        table.addCell(getCell(getI18n(i18n, "bill.pdf.label.totalTax"), Element.ALIGN_RIGHT, font12b, TAB_HEADER_COLOR));
        table.addCell(getCell(formatPrice(bill.getTotalTax()), Element.ALIGN_RIGHT, font12));        

        table.addCell(getCell("", Element.ALIGN_RIGHT, font12b, 4, PdfPCell.NO_BORDER));
        table.addCell(getCell(getI18n(i18n, "bill.pdf.label.totalTaxIncl"), Element.ALIGN_RIGHT, font12b, TAB_HEADER_COLOR));
        table.addCell(getCell(formatPrice(bill.getTotalTaxIncl()), Element.ALIGN_RIGHT, font12));
        document.add(table);

        Image img = Image.getInstance(new URL(bill.getBox().getLogoUrl()));
        img.setAlignment(Element.ALIGN_CENTER);
        document.add(img);

        // step 5
        document.close();
    }

	private String formatDate(LocalDate date, ResourceBundle i18n) throws UnsupportedEncodingException {
		return date == null ? "" : date.toString(getI18n(i18n, "bill.pdf.label.date.format"));
	}

	private void addLineInfo(PdfPTable tableInfo, String label, String value) {
		PdfPCell cell = new PdfPCell();
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		cell.addElement(new Paragraph(label, font12));        
		cell.setPadding(5);
        tableInfo.addCell(cell);

        cell = new PdfPCell();
        cell.setBorder(PdfPCell.NO_BORDER);        
		cell.setPadding(5);
		cell.addElement(new Paragraph(value, font12));        
        tableInfo.addCell(cell);
	}
 
	private String formatPrice(double value) {
		return NumberFormat.getCurrencyInstance(Locale.FRANCE).format(value);
	}
    private String formatPerCent(double value) {
    	return value + "%";
	}

    public PdfPCell getCell(String value, int alignment, Font font, BaseColor backgroundColor) {
    	return getCell(value, alignment, font, 0, backgroundColor, -1);
    }
    public PdfPCell getCell(String value, int alignment, Font font, int colspan, int border) {
    	return getCell(value, alignment, font, colspan, null, border);
    }
    public PdfPCell getCell(String value, int alignment, Font font) {
    	return getCell(value, alignment, font, 0, null, -1);
    }
 
    public PdfPCell getCell(String value, int alignment, Font font, int colspan, BaseColor backgroundColor, int border) {
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
 
 
    public String convertDate(Date d, String newFormat) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(newFormat);
        return sdf.format(d);
    }
}