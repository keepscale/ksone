package org.crossfit.app.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.xmp.XMPException;
import org.apache.commons.lang3.StringUtils;
import org.crossfit.app.domain.Bill;
import org.crossfit.app.domain.BillLine;
import org.crossfit.app.domain.CrossFitBox;
import org.crossfit.app.domain.Subscription;
import org.crossfit.app.service.util.PdfUtils;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class PdfSubscription {

    private static final String FONTS_CFN_TTF = "fonts/cfn.ttf";
    private static final String I18N_MESSAGES = "i18n/messages-subscription";
    private static final BaseColor HEADER_COLOR = new BaseColor(87,113,138);

    protected Font fontCFN;
    protected Font fontCFN30;
    protected Font font10;
    protected Font font10b;
    protected Font font12;
    protected Font font12b;
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
    }

    public static PdfSubscription getBuilder() throws DocumentException, IOException {
        return new PdfSubscription();
    }

    public String getI18n(String key) throws UnsupportedEncodingException {
    	return new String(i18n.getString(key).getBytes("ISO-8859-1"), "UTF-8");
//        return i18n.getString(key);
    }

    public void createPdf(SubcriptionLegalText data, Subscription sub, OutputStream os) throws ParserConfigurationException, SAXException, TransformerException, IOException, DocumentException, XMPException, ParseException {


        // step 1
        Document document = new Document(PageSize.A4, 20, 20, 20, 20);
        // step 2
        PdfWriter writer = PdfWriter.getInstance(document, os);
        writer.setPdfVersion(PdfWriter.VERSION_1_7);
        // step 3
        document.open();

        PdfPCell cSubNumber = new PdfPCell();
        cSubNumber.setBorder(PdfPCell.NO_BORDER);
        cSubNumber.setHorizontalAlignment(Element.ALIGN_RIGHT);

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

        document.add(table);

        document.add(new Paragraph(getI18n("sub.pdf.label.preambule"), font10));

        // step 5
        document.close();
    }

    public static class SubcriptionLegalText{
        public String logoUrl;
        public String preambuleText;
        public String designationBeneficiaireText;
        public List<String> cgv = new ArrayList<>();
        public String signatureText;
        public List<String> signatureInformationText = new ArrayList<>();
    }
}
