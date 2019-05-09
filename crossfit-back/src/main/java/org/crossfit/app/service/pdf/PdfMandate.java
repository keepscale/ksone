package org.crossfit.app.service.pdf;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.crossfit.app.domain.Mandate;
import org.crossfit.app.domain.Member;
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
        Document document = new Document(PageSize.A4, 20, 20, 20, 20);
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

        elements.add(createPreambule());
        elements.add(createDesignation());
        elements.add(createModeEtIBANBIC());
        elements.add(createSignature());

        return elements;
    }

    private Element createPreambule() throws IOException, DocumentException {
        PdfPCell text = new PdfPCell();
        text.setBorder(PdfPCell.NO_BORDER);

        Paragraph p = new Paragraph(getString("preambule.text"), font6);
        p.setAlignment(Paragraph.ALIGN_JUSTIFIED);
        text.addElement(p);


        PdfPCell cLogo = new PdfPCell();
        cLogo.addElement(createImgFromURL(getString("preambule.logo.url"), 100, 100));
        cLogo.setBorder(PdfPCell.NO_BORDER);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new int[]{2, 8});
        table.addCell(cLogo);
        table.addCell(text);
        return table;
    }


    private PdfPTable createDesignation() throws UnsupportedEncodingException {
        PdfPTable tEntre = new PdfPTable(2);
        tEntre.setWidthPercentage(100);
        createLine(tEntre, "designation.nom.label", memberLastName, "designation.prenom.label", memberFirstName);
        Phrase p1 = new Phrase(getString("designation.adresse.label"), font10b);
        p1.add(new Chunk(memberAddress == null ? "" : memberAddress, font10));
        PdfPCell cell = new PdfPCell();
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.setUseAscender(true);
        cell.setUseDescender(true);
        cell.setPaddingLeft(0);
        cell.setColspan(2);
        cell.addElement(p1);
        tEntre.addCell(cell);
        createLine(tEntre, "designation.city.label", memberCity, "designation.zipcode.label", memberZipCode);

        Paragraph pEt = new Paragraph(getString("designation.beneficiaire.text"), font10);
        pEt.setAlignment(Paragraph.ALIGN_CENTER);


        PdfPTable tDesgination = new PdfPTable(2);
        tDesgination.setWidthPercentage(100);
        createLine(tDesgination, tEntre);
        createLine(tDesgination, pEt);

        return tDesgination;
    }


    private PdfPTable createModeEtIBANBIC() throws UnsupportedEncodingException {

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        createLine(table, "info.paiement.label", getString("info.paiement.value"));
        createLine(table, "info.iban.label", iban, "info.bic.label", bic);
        return table;
    }

    private PdfPTable createSignature() throws IOException, BadElementException {

        PdfPTable tLeft = new PdfPTable(1);
        tLeft.setWidthPercentage(100);

        Phrase p1 = new Phrase(getString("info.rum.label"), font10b);
        p1.add(new Chunk(rum, font10));
        createLine(tLeft, p1);

        Phrase p2 = new Phrase(getString("info.ics.label"), font10b);
        p2.add(new Chunk(ics, font10));
        createLine(tLeft, p2);
        createLine(tLeft, new Phrase("\n\n\n\n"));
        createLine(tLeft, createParagraph(
                getString("signature.date.label") + " " +
                        PdfUtils.formatDate(signatureDate, getString("signature.date.format")), font10));


        PdfPCell cellSignature = new PdfPCell();
        cellSignature.setBorderWidth(1);
        cellSignature.addElement(createImgFromB64(signatureDataEncoded, 258, 100));


        PdfPTable tSginature = new PdfPTable(2);
        tSginature.setWidthPercentage(100);
        createLine(tSginature, tLeft);
        tSginature.addCell(cellSignature);

        return tSginature;
    }

    protected void createSepaBlock(Document document, List<Element> elements) throws DocumentException {

        if (elements.isEmpty())
            return;

        PdfPTable t = new PdfPTable(1);
        t.setWidthPercentage(100);
        
        for (Element e : elements) {
            PdfPCell content = new PdfPCell();
            content.setBorder(PdfPCell.NO_BORDER);
            content.setUseAscender(true);
            content.setUseDescender(true);
            content.setPadding(5);
            content.addElement(e);
            t.addCell(content);
        }


        PdfPTable tFinal = new PdfPTable(1);
        tFinal.setWidthPercentage(100);
        PdfPCell content = new PdfPCell();
        content.setBorderWidth(1);
        content.setUseAscender(true);
        content.setUseDescender(true);
        content.addElement(t);
        tFinal.addCell(content);


        document.add(tFinal);

    }

}
