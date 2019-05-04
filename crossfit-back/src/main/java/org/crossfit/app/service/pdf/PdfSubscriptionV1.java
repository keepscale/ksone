package org.crossfit.app.service.pdf;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import aQute.bnd.annotation.component.Component;

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

public class PdfSubscriptionV1 extends AbstractPdf {

    private final Long subContractNumber;
    private final String membershipName;


    private final PaymentMethod paymentMethod;
    private final Double priceTaxIncl;
    private final SubscriptionDirectDebit directDebit;

    private final DateTime signatureDate;
    private final String signatureDataEncoded;

    private final String memberLastName;
    private final String memberFirstName;
    private final Title memberTitle;
    private final String memberAddress;
    private final String memberCity;
    private final String memberZipCode;
    private final String memberLogin;
    private final String memberTelephonNumber;


    public PdfSubscriptionV1(String json, Subscription sub) throws IOException, DocumentException {
        super(json);
        this.subContractNumber = sub.getId();
        this.membershipName = sub.getMembership().getName();
        this.paymentMethod = sub.getPaymentMethod();
        this.priceTaxIncl = sub.getPriceTaxIncl();
        this.directDebit = sub.getDirectDebit();
        this.signatureDate = sub.getSignatureDate();
        this.signatureDataEncoded = sub.getSignatureDataEncoded();
        Member m = sub.getMember();
        this.memberLastName = m.getLastName();
        this.memberFirstName = m.getFirstName();
        this.memberTitle = m.getTitle();
        this.memberAddress = m.getAddress();
        this.memberCity = m.getCity();
        this.memberZipCode = m.getZipCode();
        this.memberLogin = m.getLogin();
        this.memberTelephonNumber = m.getTelephonNumber();
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
        table.addCell(PdfUtils.getCell(PdfUtils.formatPrice(priceTaxIncl), Element.ALIGN_RIGHT, font10));

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
            table.addCell(PdfUtils.getCell(PdfUtils.formatPrice(priceTaxIncl), Element.ALIGN_RIGHT, font10));
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

    public String getStringMembership(String pointer){
        return getString("membership."+membershipName+"."+pointer);
    }

}
