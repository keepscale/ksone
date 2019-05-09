package org.crossfit.app.service.pdf;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import aQute.bnd.annotation.component.Component;

import org.crossfit.app.domain.Mandate;
import org.crossfit.app.domain.Member;
import org.crossfit.app.domain.Subscription;
import org.crossfit.app.domain.SubscriptionDirectDebit;
import org.crossfit.app.domain.enumeration.PaymentMethod;
import org.crossfit.app.domain.enumeration.Title;
import org.crossfit.app.domain.enumeration.VersionFormatContractSubscription;
import org.crossfit.app.service.util.PdfUtils;
import org.joda.time.DateTime;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PdfMandate extends AbstractPdf {

    private final String iban;
    private final String bic;
    private final String rum;
    private final String ics;

    private final DateTime signatureDate;
    private final String signatureDataEncoded;

    private final String memberLastName;
    private final String memberFirstName;
    private final String memberAddress;
    private final String memberCity;
    private final String memberZipCode;


    public PdfMandate(String json, Mandate mandate) throws IOException, DocumentException {
        super(json);
        this.iban = mandate.getIban();
        this.bic = mandate.getBic();
        this.rum = mandate.getRum();
        this.ics = mandate.getIcs();
        
        this.signatureDate = mandate.getSignatureDate();
        this.signatureDataEncoded = mandate.getSignatureDataEncoded();
        
        Member m = mandate.getMember();
        this.memberLastName = m.getLastName();
        this.memberFirstName = m.getFirstName();
        this.memberAddress = m.getAddress();
        this.memberCity = m.getCity();
        this.memberZipCode = m.getZipCode();
        
    }

    public void createPdf(OutputStream os) throws IOException, DocumentException {


        // step 1
        Document document = new Document(PageSize.A4, 0, 0, 20, 20);
        // step 2
        PdfWriter writer = PdfWriter.getInstance(document, os);
        writer.setPdfVersion(PdfWriter.VERSION_1_7);
        // step 3
        document.open();

        createSepaBlock(document, createSepa());

        // step 5
        document.close();
    }




    private List<Element> createSepa() throws MalformedURLException, IOException, DocumentException {
        List<Element> elements = new ArrayList<>();
        
        
        PdfPCell cSubNumber = new PdfPCell();
        cSubNumber.setBorder(PdfPCell.NO_BORDER);

        cSubNumber.addElement(new Paragraph(getString("preambule.text"), font12));


        Image imgLogo = Image.getInstance(new URL(getString("preambule.logo.url")));
        imgLogo.scaleToFit(100, 100);
        PdfPCell cLogo = new PdfPCell(imgLogo);
        cLogo.setBorder(PdfPCell.NO_BORDER);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new int[]{30, 100});
        table.addCell(cLogo);
        table.addCell(cSubNumber);

        elements.add(table);
        
        PdfPTable tableAdresse = new PdfPTable(2);
        tableAdresse.setWidthPercentage(100);
        table.addCell(createDesignation());

        PdfPCell addressTo = new PdfPCell();
        addressTo.setBorder(PdfPCell.NO_BORDER);
        addressTo.addElement(new Paragraph(getString("address.to"), font12));
        table.addCell(addressTo);
        
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
        img.scaleToFit(258, 100);
        elements.add(img);
        elements.add(createParagraph(memberFirstName + " " + memberLastName, font10));
        elements.add(createParagraph(
                getString("signature.date.label") + " " +
                        PdfUtils.formatDate(signatureDate, getString("signature.date.format")), font10));

        return elements;
    }
    

    private PdfPCell createDesignation() throws UnsupportedEncodingException {
    	PdfPCell cell = new PdfPCell();
    	cell.setBorder(PdfPCell.NO_BORDER);
    	

        PdfPTable tEntre = new PdfPTable(2);
        tEntre.setWidthPercentage(100);
        createLine(tEntre, "designation.nom.label", memberLastName, "designation.prenom.label", memberFirstName);
        createLine(tEntre, "designation.adresse.label", memberAddress);
        createLine(tEntre, "designation.city.label", memberCity, "designation.zipcode.label", memberZipCode);

        elements.add(createParagraph(getString("designation.adherent.label")));
        elements.add(createParagraph(getString("designation.et.label"), font10b));
        elements.add(createParagraph(getString("designation.beneficiaire.text")));
        return elements;
    }

    protected void createSepaBlock(Document document, List<Element> elements) throws UnsupportedEncodingException, DocumentException {

        if (elements.isEmpty())
            return;

        PdfPTable t = new PdfPTable(1);
        t.setSpacingBefore(10);
        t.setWidthPercentage(100);
        
        for (Element e : elements) {
            PdfPCell content = new PdfPCell();
            content.setBorder(1);
            content.setUseAscender(true);
            content.setUseDescender(true);
            content.setPadding(10);
            content.addElement(e);
            t.addCell(content);
        }

        document.add(t);

    }

}
