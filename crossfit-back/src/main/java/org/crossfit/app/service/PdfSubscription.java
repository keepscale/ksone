package org.crossfit.app.service;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.crossfit.app.domain.Subscription;
import org.crossfit.app.domain.enumeration.PaymentMethod;
import org.crossfit.app.service.util.PdfUtils;
import org.xml.sax.SAXException;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.ListItem;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.xmp.XMPException;

public class PdfSubscription {

    private static final String FONTS_CFN_TTF = "fonts/cfn.ttf";
    private static final String I18N_MESSAGES = "i18n/messages-subscription";
    private static final BaseColor HEADER_COLOR = new BaseColor(87,113,138);

    protected Font fontCFN;
    protected Font fontCFN30;
    protected Font font10;
    protected Font font10White;
    protected Font font10b;
    protected Font font12;
    protected Font font12White;
    protected Font font12b;
    protected Font font12bWhite;
    protected Font font14;

    private ResourceBundle i18n;

    private PdfSubscription() throws DocumentException, IOException {
        i18n = ResourceBundle.getBundle(I18N_MESSAGES);
        BaseFont bf = BaseFont.createFont();
        BaseFont bfCFN = BaseFont.createFont(FONTS_CFN_TTF, BaseFont.WINANSI, BaseFont.EMBEDDED);
        BaseFont bfb = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.WINANSI, BaseFont.EMBEDDED);
        fontCFN = new Font(bfCFN, 18);
        fontCFN30 = new Font(bfCFN, 30);
        font10 = new Font(bf, 10);
        font10b = new Font(bfb, 10);
        font12 = new Font(bf, 12);
        font12b = new Font(bfb, 12);
        font14 = new Font(bf, 14);

        font10White = new Font(font10);
        font10White.setColor(BaseColor.WHITE);
        font12bWhite = new Font(font12b);
        font12bWhite.setColor(BaseColor.WHITE);
        font12White = new Font(font12);
        font12White.setColor(BaseColor.WHITE);
    }

    public static PdfSubscription getBuilder() throws DocumentException, IOException {
        return new PdfSubscription();
    }

    public String getI18n(String key) throws UnsupportedEncodingException {
    	return new String(i18n.getString(key).getBytes("ISO-8859-1"), "UTF-8");
    }

    public void createPdf(SubcriptionLegalText data, Subscription sub, OutputStream os) throws ParserConfigurationException, SAXException, TransformerException, IOException, DocumentException, XMPException, ParseException {


        // step 1
        Document document = new Document(PageSize.A4, 0, 0, 20, 20);
        // step 2
        PdfWriter writer = PdfWriter.getInstance(document, os);
        writer.setPdfVersion(PdfWriter.VERSION_1_7);
        // step 3
        document.open();

        createBlock(document, null,  createPreambule(data, sub));
        createBlock(document, getI18n("sub.pdf.designation.header"), createDesignation(data, sub));
        createBlock(document, getI18n("sub.pdf.subscription.header"), createSubscription(data, sub));
        createBlock(document, getI18n("sub.pdf.resiliation.header"), createResiliation(data, sub));
        createBlock(document, getI18n("sub.pdf.cgv.header"), createCGV(data, sub));
        createBlock(document, getI18n("sub.pdf.signature.header"), createSignature(data, sub));

        // step 5
        document.close();
    }

	private List<Element> createPreambule(SubcriptionLegalText data, Subscription sub)
			throws UnsupportedEncodingException, BadElementException, MalformedURLException, IOException,
			DocumentException {
		PdfPCell cSubNumber = new PdfPCell();
        cSubNumber.setBorder(PdfPCell.NO_BORDER);

        cSubNumber.addElement(new Paragraph(getI18n("sub.pdf.label.number") + sub.getId(), font14));
        cSubNumber.addElement(new Paragraph(getI18n("sub.pdf.label.indetermine"), font12));


        Image imgLogo = Image.getInstance(new URL(data.logoUrl));
        imgLogo.scaleToFit(100, 100);
        PdfPCell cLogo = new PdfPCell(imgLogo);
        cLogo.setBorder(PdfPCell.NO_BORDER);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new int[]{30, 100});
        table.addCell(cLogo);
        table.addCell(cSubNumber);

        List<Element> pp = new ArrayList<>();
        pp.add(table);
        pp.add(new Paragraph(getI18n("sub.pdf.label.preambule"), font10));
        
        return pp;
	}


    private List<Element> createSignature(SubcriptionLegalText data, Subscription sub) throws BadElementException, MalformedURLException, IOException {
        List<Element> elements = new ArrayList<>();
        elements.add(createParagraph(getI18n("sub.pdf.signature.declare.label"), font10));
        
        com.itextpdf.text.List romanList = new com.itextpdf.text.List(com.itextpdf.text.List.UNORDERED);
        for (String c : data.signatureInformationText) {
        	ListItem li = new ListItem(c, font10);
        	li.setAlignment(Element.ALIGN_JUSTIFIED);
			romanList.add(li);
		}
        elements.add(romanList);
        
        String base64Image = sub.getSignatureDataEncoded().split(",")[1];
        byte[] imageBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(base64Image);
        Image img = Image.getInstance(imageBytes);
        img.scaleToFit(568, 220);
		elements.add(img);
        elements.add(createParagraph(sub.getMember().getFirstName() + " " + sub.getMember().getLastName(), font10));
        elements.add(createParagraph(
        		getI18n("sub.pdf.signature.date.label") + " " + 
				PdfUtils.formatDate(sub.getSignatureDate(), getI18n("sub.pdf.signature.date.format")), font10));
        
        return elements;
    }
    
    private List<Element> createCGV(SubcriptionLegalText data, Subscription sub) {
		List<Element> elements = new ArrayList<>();
    	elements.add(createParagraph(data.cgvText, font10));
    	
        for (String s : data.cgvs) {
        	elements.add(createParagraph(s, font10));
		}
        
        return elements;
	}
	
	private List<Element> createResiliation(SubcriptionLegalText data, Subscription sub) {
		List<Element> elements = new ArrayList<>();
		elements.add(createParagraph(sub.getMembership().getResiliationInformation(), font10));
		return elements;
	}

	private List<Element> createSubscription(SubcriptionLegalText data, Subscription sub) throws DocumentException, UnsupportedEncodingException {
		PdfPTable table = new PdfPTable(3);
        
        table.setWidthPercentage(100);
        table.setWidths(new int[]{20, 20, 11});
        table.addCell(PdfUtils.getCell(getI18n("sub.pdf.subscription.tab.designation.label"), Element.ALIGN_CENTER, font12bWhite, HEADER_COLOR));
        table.addCell(PdfUtils.getCell(getI18n("sub.pdf.subscription.tab.prestation.label"), Element.ALIGN_CENTER, font12bWhite, HEADER_COLOR));
        table.addCell(PdfUtils.getCell(getI18n("sub.pdf.subscription.tab.ttc.label"), Element.ALIGN_CENTER, font12bWhite, HEADER_COLOR));

        table.addCell(PdfUtils.getCell(sub.getMembership().getName(), Element.ALIGN_LEFT, font10));
        table.addCell(PdfUtils.getCell(sub.getMembership().getInformation(), Element.ALIGN_LEFT, font10));
        table.addCell(PdfUtils.getCell(PdfUtils.formatPrice(sub.getMembership().getPriceTaxIncl()), Element.ALIGN_RIGHT, font10));

        if (sub.getDirectDebit() != null && sub.getDirectDebit().getFirstPaymentMethod() != PaymentMethod.NA){

            table.addCell(PdfUtils.getCell(getI18n("sub.pdf.subscription.tab.firstpayment.label")
                    +  getI18n("sub.pdf.subscription.tab.payment." + sub.getDirectDebit().getFirstPaymentMethod()),
                    Element.ALIGN_LEFT, font10White, 2, HEADER_COLOR,-1));
            table.addCell(PdfUtils.getCell(PdfUtils.formatPrice(sub.getDirectDebit().getFirstPaymentTaxIncl()), Element.ALIGN_RIGHT, font10));
        }
        if(sub.getDirectDebit() != null){

            table.addCell(PdfUtils.getCell(getI18n("sub.pdf.subscription.tab.directdebit.afterdate.label") +" "+
                            PdfUtils.formatDate(sub.getDirectDebit().getAfterDate(), getI18n("sub.pdf.subscription.tab.directdebit.afterdate.format")) +" "+
                            getI18n("sub.pdf.subscription.tab.directdebit.atday.before.label") +" "+
                            sub.getDirectDebit().getAtDayOfMonth() +" "+
                            getI18n("sub.pdf.subscription.tab.directdebit.atday.after.label")
                    ,
                    Element.ALIGN_LEFT, font10White, 2, HEADER_COLOR, -1));
            table.addCell(PdfUtils.getCell(PdfUtils.formatPrice(sub.getDirectDebit().getAmount()), Element.ALIGN_RIGHT, font10));
        }
        if (sub.getPaymentMethod() != PaymentMethod.DIRECT_DEBIT){

            table.addCell(PdfUtils.getCell(getI18n("sub.pdf.subscription.tab.payment.label")
                            +  getI18n("sub.pdf.subscription.tab.payment." + sub.getPaymentMethod()),
                    Element.ALIGN_LEFT, font10White, 2, HEADER_COLOR,-1));
            table.addCell(PdfUtils.getCell(PdfUtils.formatPrice(sub.getMembership().getPriceTaxIncl()), Element.ALIGN_RIGHT, font10));
        }
        
		List<Element> elements = new ArrayList<>();
		elements.add(table);
		return elements;
	}

    private List<Element> createDesignation(SubcriptionLegalText data, Subscription sub) throws UnsupportedEncodingException {
        List<Element> elements = new ArrayList<>();
    	
    	elements.add(createParagraph(getI18n("sub.pdf.designation.entre.label"), font10b));

        PdfPTable tEntre = new PdfPTable(2);
        tEntre.setWidthPercentage(100);
        createLine(tEntre, "sub.pdf.designation.civilite.label", getI18n("sub.pdf.designation.civilite."+ sub.getMember().getTitle()));
        createLine(tEntre, "sub.pdf.designation.nom.label", sub.getMember().getLastName());
        createLine(tEntre, "sub.pdf.designation.prenom.label", sub.getMember().getFirstName());
        createLine(tEntre, "sub.pdf.designation.adresse.label", sub.getMember().getAddress());
        createLine(tEntre, "sub.pdf.designation.city.label", sub.getMember().getCity(), "sub.pdf.designation.zipcode.label", sub.getMember().getZipCode());
        createLine(tEntre, "sub.pdf.designation.mail.label", sub.getMember().getLogin(), "sub.pdf.designation.tel.label", sub.getMember().getTelephonNumber());

        elements.add(tEntre);
        elements.add(createParagraph(getI18n("sub.pdf.designation.adherent.label")));
        elements.add(createParagraph(getI18n("sub.pdf.designation.et.label"), font10b));
        elements.add(createParagraph(data.designationBeneficiaireText));
        return elements;
    }

    private Paragraph createParagraph(String text) {
        return createParagraph(text, font10);
    }
    private Paragraph createParagraph(String text, Font font){
        Paragraph p = new Paragraph(text, font);
        p.setAlignment(Paragraph.ALIGN_JUSTIFIED);
        return p;
    }
    private void createLine(PdfPTable table, String label, String value) throws UnsupportedEncodingException {
        this.createLine(table, label, value, null, null);
    }
    private void createLine(PdfPTable table, String label, String value, String label2, String value2) throws UnsupportedEncodingException {
        Phrase p1 = new Phrase(getI18n(label), font10b);
        p1.add(new Chunk(value == null ? "" : value, font10));
        PdfPCell cell = new PdfPCell();
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.setUseAscender(true);
        cell.setUseDescender(true);
        cell.setPaddingLeft(0);
        cell.addElement(p1);
        table.addCell(cell);


        Phrase p2 = new Phrase();

        if (label2 != null){
            p2.add(new Phrase(getI18n(label2), font10b));
            p2.add(new Chunk(value2 == null ? "" : value2, font10));
        }

        PdfPCell cell2 = new PdfPCell();
        cell2.setBorder(PdfPCell.NO_BORDER);
        cell2.setUseAscender(true);
        cell2.setUseDescender(true);
        cell2.addElement(p2);
        table.addCell(cell2);
    }

    private void createBlock(Document document, String header, List<Element> elements) throws UnsupportedEncodingException, DocumentException {

        PdfPTable t = new PdfPTable(1);
        t.setSpacingBefore(10);
        t.setWidthPercentage(100);
        t.setKeepTogether(true);
        
        if (header != null) {
            PdfPCell cell = new PdfPCell();

            cell.setBorder(PdfPCell.NO_BORDER);
            cell.setBackgroundColor(HEADER_COLOR);
            cell.setUseAscender(true);
            cell.setUseDescender(true);
            cell.setPadding(5);
            cell.setPaddingLeft(10);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            cell.addElement(new Paragraph(header, font12bWhite));
            t.addCell(cell);
        }

        for (Element e : elements) {
        	PdfPCell content = new PdfPCell();
        	content.setBorder(PdfPCell.NO_BORDER);
            content.setUseAscender(true);
            content.setUseDescender(true);
            content.setPadding(10);
            content.setPaddingLeft(20);
            content.setPaddingRight(20);
            content.addElement(e);
            t.addCell(content);
		}
        
        document.add(t);

    }

    public static class SubcriptionLegalText{
        public String logoUrl;
        public String preambuleText;
        public String designationBeneficiaireText;
        public List<String> cgvs = new ArrayList<>();
        public List<String> signatureInformationText = new ArrayList<>();
		public String cgvText;
    }
}
