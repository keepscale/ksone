package org.crossfit.app.service;
 
import java.io.IOException;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.lang3.StringUtils;
import org.crossfit.app.domain.Bill;
import org.crossfit.app.domain.BillLine;
import org.xml.sax.SAXException;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
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
        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        // step 2
        PdfWriter writer = PdfWriter.getInstance(document, os);
        writer.setPdfVersion(PdfWriter.VERSION_1_7);
        // step 3
        document.open();

 
        // header
        PdfPCell cFactNumberl = new PdfPCell(new Paragraph("Facture N°" + bill.getNumber(), font14));
        cFactNumberl.setBorder(PdfPCell.NO_BORDER);
        cFactNumberl.setHorizontalAlignment(Element.ALIGN_RIGHT);
        
        PdfPCell seller = getPartyAddress(
        		bill.getBox().getName(),
        		"Régiment \n"+
        		"54000 Nancy");
        
        // Address seller / buyer
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.addCell(seller);
        table.addCell(cFactNumberl);
        
        document.add(table);

        document.add(new Phrase("\n"));
        document.add(new Phrase("\n"));
        
        table = new PdfPTable(2);
        table.setWidthPercentage(100);
        PdfPCell cDest = new PdfPCell();
        cDest.setBorder(PdfPCell.NO_BORDER);
        addPartyAddress(cDest, bill.getDisplayName(), bill.getDisplayAddress());
        table.addCell(cDest);
        

        PdfPTable tableInfo = new PdfPTable(2);
        
        addLineInfo(tableInfo, "Date de la facture", bill.getEffectiveDate().toString("dd/MM/yyyy"));
        addLineInfo(tableInfo, "Référence", bill.getNumber());
        addLineInfo(tableInfo, "N° Client", bill.getMember().getId()+"");
        addLineInfo(tableInfo, "Moyen de paiement", bill.getPaymentMethod()+"");

        table.addCell(tableInfo);
        
        document.add(table);

        
        if (StringUtils.isNotBlank(bill.getComments())) {
            document.add(new Phrase("\n"));
	        document.add(new Paragraph("Informations complémentaires:", font12b));
	        document.add(new Paragraph(bill.getComments(), font12));
        }

        document.add(new Phrase("\n"));
        document.add(new Phrase("\n"));        
        // line items
        table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        table.setSpacingAfter(10);
        table.setWidths(new int[]{7, 1, 2, 2, 2, 2});
        table.addCell(getCell("Description", Element.ALIGN_LEFT, font12b, BaseColor.LIGHT_GRAY));
        table.addCell(getCell("Q.", Element.ALIGN_CENTER, font12b, BaseColor.LIGHT_GRAY));
        table.addCell(getCell("PU HT", Element.ALIGN_CENTER, font12b, BaseColor.LIGHT_GRAY));
        table.addCell(getCell("% TVA", Element.ALIGN_CENTER, font12b, BaseColor.LIGHT_GRAY));
        table.addCell(getCell("Tot. TVA", Element.ALIGN_CENTER, font12b, BaseColor.LIGHT_GRAY));
        table.addCell(getCell("Tot. HT", Element.ALIGN_CENTER, font12b, BaseColor.LIGHT_GRAY));
        for (BillLine line : bill.getLines()) {
            table.addCell(getCell(line.getLabel(), Element.ALIGN_LEFT, font12));
            table.addCell(getCell(String.valueOf(line.getQuantity()), Element.ALIGN_RIGHT, font12));
            table.addCell(getCell(formatPrice(line.getPriceTaxExcl()), Element.ALIGN_RIGHT, font12));
            table.addCell(getCell(formatPerCent(line.getTaxPerCent()), Element.ALIGN_RIGHT, font12));
            table.addCell(getCell(formatPrice(0.0), Element.ALIGN_RIGHT, font12));
            table.addCell(getCell(formatPrice(line.getTotalTaxExcl()), Element.ALIGN_RIGHT, font12));
        }

        table.addCell(getCell("", Element.ALIGN_RIGHT, font12b, 4, PdfPCell.NO_BORDER));
        table.addCell(getCell("Tot. HT", Element.ALIGN_RIGHT, font12b, BaseColor.LIGHT_GRAY));
        table.addCell(getCell(formatPrice(bill.getTotalTaxExcl()), Element.ALIGN_RIGHT, font12));

        table.addCell(getCell("", Element.ALIGN_RIGHT, font12b, 4, PdfPCell.NO_BORDER));
        table.addCell(getCell("Tot. TVA", Element.ALIGN_RIGHT, font12b, BaseColor.LIGHT_GRAY));
        table.addCell(getCell(formatPrice(152.0), Element.ALIGN_RIGHT, font12));        

        table.addCell(getCell("", Element.ALIGN_RIGHT, font12b, 4, PdfPCell.NO_BORDER));
        table.addCell(getCell("Tot. TTC", Element.ALIGN_RIGHT, font12b, BaseColor.LIGHT_GRAY));
        table.addCell(getCell(formatPrice(bill.getTotalTaxIncl()), Element.ALIGN_RIGHT, font12));
        document.add(table);
 

        // step 5
        document.close();
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

	public PdfPCell addPartyAddress(PdfPCell cell, String name, String address) {
        cell.addElement(new Paragraph(name, font12b));
        cell.addElement(new Paragraph(address, font12));
        return cell;
    }

	public PdfPCell getPartyAddress( String name, String address) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(PdfPCell.NO_BORDER);
        addPartyAddress(cell, name, address);
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