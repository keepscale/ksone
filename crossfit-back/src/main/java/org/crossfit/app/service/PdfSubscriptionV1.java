package org.crossfit.app.service;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.crossfit.app.domain.Subscription;
import org.crossfit.app.domain.SubscriptionDirectDebit;
import org.crossfit.app.domain.enumeration.PaymentMethod;
import org.crossfit.app.domain.enumeration.VersionFormatContractSubscription;
import org.crossfit.app.service.util.PdfUtils;

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
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;

public class PdfSubscriptionV1 implements PdfSubscriptionBuilder {

    private static final String FONTS_CFN_TTF = "fonts/cfn.ttf";
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

    private JSONObject json;

    Long subContractNumber;
    String membershipId;
    private String membershipName;
    private Double membershipPriceTaxIncl;

    private SubscriptionDirectDebit directDebit;

    private DateTime signatureDate;
    private String signatureDataEncoded;

    private String memberLastName;
    private String memberFirstName;
    private String memberTitle;
    private String memberAddress;
    private String memberCity;
    private String memberZipCode;
    private String memberLogin;
    private String memberTelephonNumber;
    private PaymentMethod paymentMethod;


    private PdfSubscriptionV1(Long subId) throws DocumentException, IOException {
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


    public String getStringMembership(String pointer){
        return getString("membership."+membershipId+"."+pointer);
    }

    public List<String> getStrings(String pointer){
        return ((JSONArray)json.query(pointer)).toList().stream().map(Object::toString).collect(Collectors.toList());
    }
    public String getString(String pointer){
        return json.query(pointer).toString();
    }

    @Override
    public boolean support(VersionFormatContractSubscription versionFormat) {
        return versionFormat == VersionFormatContractSubscription.V_1;
    }

    public void createPdf(OutputStream os) throws IOException, DocumentException {


        // step 1
        Document document = new Document(PageSize.A4, 0, 0, 20, 20);
        // step 2
        PdfWriter writer = PdfWriter.getInstance(document, os);
        writer.setPdfVersion(PdfWriter.VERSION_1_7);
        // step 3
        document.open();

        createBlock(document, null,  createPreambule());
        createBlock(document, getString("designation.header.text"), createDesignation());
        createBlock(document, getString("subscription.header.text"), createSubscription());
        createBlock(document, getString("resiliation.header.text"), createResiliation());
        createBlock(document, getString("cgv.header.text"), createCGV());
        createBlock(document, getString("signature.header.text"), createSignature());

        // step 5
        document.close();
    }

	private List<Element> createPreambule() throws IOException, DocumentException {
		PdfPCell cSubNumber = new PdfPCell();
        cSubNumber.setBorder(PdfPCell.NO_BORDER);

        cSubNumber.addElement(new Paragraph(getString("preambule.number.label") + subContractNumber, font14));
        cSubNumber.addElement(new Paragraph(getString("preambule.indetermine.label"), font12));


        Image imgLogo = Image.getInstance(new URL(getString("preambule.logo.url")));
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
        pp.add(new Paragraph(getString("preambule.text"), font10));
        
        return pp;
	}




    private List<Element> createDesignation() throws UnsupportedEncodingException {
        List<Element> elements = new ArrayList<>();
    	
    	elements.add(createParagraph(getString("designation.entre.label"), font10b));

        PdfPTable tEntre = new PdfPTable(2);
        tEntre.setWidthPercentage(100);
        createLine(tEntre, "designation.civilite.label", getString("designation.civilite."+ memberTitle));
        createLine(tEntre, "designation.nom.label", memberLastName);
        createLine(tEntre, "designation.prenom.label", memberFirstName);
        createLine(tEntre, "designation.adresse.label", memberAddress);
        createLine(tEntre, "designation.city.label", memberCity, "designation.zipcode.label", memberZipCode);
        createLine(tEntre, "designation.mail.label", memberLogin, "designation.tel.label", memberTelephonNumber);

        elements.add(tEntre);
        elements.add(createParagraph(getString("designation.adherent.label")));
        elements.add(createParagraph(getString("designation.et.label"), font10b));
        elements.add(createParagraph(getString("designation.beneficiaire.text")));
        return elements;
    }
    private List<Element> createSubscription() throws DocumentException, UnsupportedEncodingException {
        PdfPTable table = new PdfPTable(3);

        table.setWidthPercentage(100);
        table.setWidths(new int[]{20, 20, 11});
        table.addCell(PdfUtils.getCell(getString("subscription.tab.designation.label"), Element.ALIGN_CENTER, font12bWhite, HEADER_COLOR));
        table.addCell(PdfUtils.getCell(getString("subscription.tab.prestation.label"), Element.ALIGN_CENTER, font12bWhite, HEADER_COLOR));
        table.addCell(PdfUtils.getCell(getString("subscription.tab.ttc.label"), Element.ALIGN_CENTER, font12bWhite, HEADER_COLOR));

        table.addCell(PdfUtils.getCell(getStringMembership("designation") , Element.ALIGN_LEFT, font10));
        table.addCell(PdfUtils.getCell(getStringMembership("prestation") , Element.ALIGN_LEFT, font10));
        table.addCell(PdfUtils.getCell(PdfUtils.formatPrice(membershipPriceTaxIncl), Element.ALIGN_RIGHT, font10));

        if (directDebit != null && directDebit.getFirstPaymentMethod() != PaymentMethod.NA){

            table.addCell(PdfUtils.getCell(getString("subscription.tab.firstpayment.label")
                            +  getString("subscription.tab.payment." + directDebit.getFirstPaymentMethod()),
                    Element.ALIGN_LEFT, font10White, 2, HEADER_COLOR,-1));
            table.addCell(PdfUtils.getCell(PdfUtils.formatPrice(directDebit.getFirstPaymentTaxIncl()), Element.ALIGN_RIGHT, font10));
        }
        if(directDebit != null){

            table.addCell(PdfUtils.getCell(getString("subscription.tab.directdebit.afterdate.label") +" "+
                            PdfUtils.formatDate(directDebit.getAfterDate(), getString("subscription.tab.directdebit.afterdate.format")) +" "+
                            getString("subscription.tab.directdebit.atday.before.label") +" "+
                            directDebit.getAtDayOfMonth() +" "+
                            getString("subscription.tab.directdebit.atday.after.label")
                    ,
                    Element.ALIGN_LEFT, font10White, 2, HEADER_COLOR, -1));
            table.addCell(PdfUtils.getCell(PdfUtils.formatPrice(directDebit.getAmount()), Element.ALIGN_RIGHT, font10));
        }
        if (paymentMethod != PaymentMethod.DIRECT_DEBIT){

            table.addCell(PdfUtils.getCell(getString("subscription.tab.payment.label")
                            +  getString("subscription.tab.payment." + paymentMethod),
                    Element.ALIGN_LEFT, font10White, 2, HEADER_COLOR,-1));
            table.addCell(PdfUtils.getCell(PdfUtils.formatPrice(membershipPriceTaxIncl), Element.ALIGN_RIGHT, font10));
        }

        List<Element> elements = new ArrayList<>();
        elements.add(table);
        return elements;
    }

    private List<Element> createResiliation() {
        List<Element> elements = new ArrayList<>();
        if (getStringMembership("resiliation") != null)
            elements.add(createParagraph(getStringMembership("resiliation") , font10));
        return elements;
    }


    private List<Element> createCGV() {
        List<Element> elements = new ArrayList<>();
        elements.add(createParagraph(getString("cgv.text"), font10));

        for (String s : getStrings("cgv.paragraph")) {
            elements.add(createParagraph(s, font10));
        }

        return elements;
    }

    private List<Element> createSignature() throws BadElementException, MalformedURLException, IOException {
        List<Element> elements = new ArrayList<>();
        elements.add(createParagraph(getString("signature.declare.label"), font10));

        com.itextpdf.text.List romanList = new com.itextpdf.text.List(com.itextpdf.text.List.UNORDERED);
        for (String c : getStrings("signature.information.text")) {
            ListItem li = new ListItem(c, font10);
            li.setAlignment(Element.ALIGN_JUSTIFIED);
            romanList.add(li);
        }
        elements.add(romanList);

        String base64Image = signatureDataEncoded.split(",")[1];
        byte[] imageBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(base64Image);
        Image img = Image.getInstance(imageBytes);
        img.scaleToFit(568, 220);
        elements.add(img);
        elements.add(createParagraph(memberFirstName + " " + memberLastName, font10));
        elements.add(createParagraph(
                getString("signature.date.label") + " " +
                        PdfUtils.formatDate(signatureDate, getString("signature.date.format")), font10));

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
        Phrase p1 = new Phrase(getString(label), font10b);
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
            p2.add(new Phrase(getString(label2), font10b));
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

        if (elements.isEmpty())
            return;

        PdfPTable t = new PdfPTable(1);
        t.setSpacingBefore(10);
        t.setWidthPercentage(100);
        //t.setKeepTogether(true);
        
        if (header != null) {
            PdfPCell cell = new PdfPCell();

            cell.setBorder(PdfPCell.NO_BORDER);
            cell.setBackgroundColor(HEADER_COLOR);
            cell.setUseAscender(true);
            cell.setUseDescender(true);
            cell.setPadding(5);
            cell.setPaddingLeft(20);
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
            content.setPaddingLeft(30);
            content.setPaddingRight(30);
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
