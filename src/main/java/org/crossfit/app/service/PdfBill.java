package org.crossfit.app.service;
 
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.crossfit.app.domain.Bill;
import org.crossfit.app.domain.BillLine;
import org.xml.sax.SAXException;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
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
    public static final String FONT = "resources/fonts/OpenSans-Regular.ttf";
    public static final String FONTB = "resources/fonts/OpenSans-Bold.ttf";
 
    protected Font font10;
    protected Font font10b;
    protected Font font12;
    protected Font font12b;
    protected Font font14;
 
    private PdfBill() throws DocumentException, IOException {
        BaseFont bf = BaseFont.createFont();
        BaseFont bfb = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.WINANSI, BaseFont.EMBEDDED);
        font10 = new Font(bf, 10);
        font10b = new Font(bfb, 10);
        font12 = new Font(bf, 12);
        font12b = new Font(bfb, 12);
        font14 = new Font(bf, 14);
    }
    
    public static PdfBill getBuilder() throws DocumentException, IOException {
    	return new PdfBill();
    }
 
    public void createPdf(Bill bill, OutputStream os) throws ParserConfigurationException, SAXException, TransformerException, IOException, DocumentException, XMPException, ParseException {
      
        // step 1
        Document document = new Document();
        // step 2
        PdfWriter writer = PdfWriter.getInstance(document, os);
        writer.setPdfVersion(PdfWriter.VERSION_1_7);
        // step 3
        document.open();

 
        // header
        Paragraph p;
        p = new Paragraph(bill.getNumber(), font14);
        p.setAlignment(Element.ALIGN_RIGHT);
        document.add(p);
        p = new Paragraph(convertDate(bill.getEffectiveDate().toDate(), "dd/MM/yyyy"), font12);
        p.setAlignment(Element.ALIGN_RIGHT);
        document.add(p);
 
        // Address seller / buyer
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        PdfPCell seller = getPartyAddress(
        		"CrossFit Nancy",
        		"Régiment \n"+
        		"54000 Nancy");
        table.addCell(seller);
        PdfPCell buyer = getPartyAddress(
        		bill.getDisplayName(),
        		bill.getDisplayAddress());
        table.addCell(buyer);

        table.addCell(buyer);
        document.add(table);
 
        // line items
        table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        table.setSpacingAfter(10);
        table.setWidths(new int[]{7, 2, 1, 2, 2, 2});
        table.addCell(getCell("Item:", Element.ALIGN_LEFT, font12b));
        table.addCell(getCell("Qty:", Element.ALIGN_LEFT, font12b));
        table.addCell(getCell("Price:", Element.ALIGN_LEFT, font12b));
        table.addCell(getCell("Subtotal:", Element.ALIGN_LEFT, font12b));
        table.addCell(getCell("VAT:", Element.ALIGN_LEFT, font12b));
        table.addCell(getCell("Total:", Element.ALIGN_LEFT, font12b));
        for (BillLine line : bill.getLines()) {
            table.addCell(getCell(line.getLabel(), Element.ALIGN_LEFT, font12));
            table.addCell(getCell(String.valueOf(line.getQuantity()), Element.ALIGN_RIGHT, font12));
            table.addCell(getCell(format2dec(line.getPriceTaxExcl()), Element.ALIGN_RIGHT, font12));
            table.addCell(getCell(format2dec(line.getTotalTaxExcl()), Element.ALIGN_RIGHT, font12));
            table.addCell(getCell(format2dec(line.getTaxPerCent()), Element.ALIGN_RIGHT, font12));
            table.addCell(getCell(format2dec(line.getTotalTaxIncl()), Element.ALIGN_RIGHT, font12));
        }
        table.addCell(getCell("Total", Element.ALIGN_LEFT, font12));
        table.addCell(getCell("", Element.ALIGN_RIGHT, font12));
        table.addCell(getCell("", Element.ALIGN_RIGHT, font12));
        table.addCell(getCell(format2dec(bill.getTotalTaxExcl()), Element.ALIGN_RIGHT, font12));
        table.addCell(getCell("", Element.ALIGN_RIGHT, font12));
        table.addCell(getCell(format2dec(bill.getTotalTaxIncl()), Element.ALIGN_RIGHT, font12));
        document.add(table);
 
        Paragraph paiment = new Paragraph(String.format("Moyen de paiment: %s", bill.getPaymentMethod()), font12);
        document.add(paiment);
        // step 5
        document.close();
    }
 
    private String format2dec(double value) {

    	return value + "";
	}

	public PdfPCell getPartyAddress( String name, String address) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.addElement(new Paragraph(name, font12));
        cell.addElement(new Paragraph(address, font12));
        return cell;
    }
    public PdfPCell getPartyTax(String[] taxId, String[] taxSchema) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.addElement(new Paragraph("Tax ID(s):", font10b));
        if (taxId.length == 0) {
            cell.addElement(new Paragraph("Not applicable", font10));
        }
        else {
            int n = taxId.length;
            for (int i = 0; i < n; i++) {
                cell.addElement(new Paragraph(String.format("%s: %s", taxSchema[i], taxId[i]), font10));
            }
        }
        return cell;
    }
 
 
    public PdfPCell getCell(String value, int alignment, Font font) {
        PdfPCell cell = new PdfPCell();
        cell.setUseAscender(true);
        cell.setUseDescender(true);
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