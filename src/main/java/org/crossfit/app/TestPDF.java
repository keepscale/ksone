package org.crossfit.app;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.crossfit.app.domain.Bill;
import org.crossfit.app.domain.BillLine;
import org.crossfit.app.domain.CrossFitBox;
import org.crossfit.app.domain.Member;
import org.crossfit.app.domain.enumeration.BillStatus;
import org.crossfit.app.domain.enumeration.PaymentMethod;
import org.crossfit.app.service.PdfBill;
import org.joda.time.LocalDate;
import org.xml.sax.SAXException;

import com.itextpdf.text.DocumentException;
import com.itextpdf.xmp.XMPException;

public class TestPDF {

	public static void main(String[] args) throws FileNotFoundException, ParserConfigurationException, SAXException, TransformerException, IOException, DocumentException, XMPException, ParseException {


		CrossFitBox box = new CrossFitBox();
		box.setName("CrossFit Nancy");

		Member member = new Member();
		member.setId(12L);
		
		Bill bill = new Bill();
		bill.setBox(box);
		bill.setMember(member);
		bill.setDisplayAddress("11 Rue du bambou qui fuit\n54000 Nancy");
		bill.setDisplayName("M Georges De La Tour");
		bill.setEffectiveDate(LocalDate.parse("2017-11-02"));
		bill.setId(5L);
		bill.setLines(new ArrayList<>());
		bill.setNumber("2017-11-000125");
		bill.setPaymentMethod(PaymentMethod.CREDIT_CARD);
		bill.setStatus(BillStatus.VALIDE);
		bill.getLines().add(new BillLine(bill, "Produit 1", 1, 50, 60, 50, 20, 60));
		bill.getLines().add(new BillLine(bill, "Prdzdez", 3, 50, 60, 150, 20, 180));
		bill.getLines().add(new BillLine(bill, "dzedezdezdez 1", 2, 50, 60, 100, 20, 120));
		bill.setTotalTaxExcl(300);
		bill.setTotalTaxIncl(360);
		
		PdfBill.getBuilder().createPdf(bill , new FileOutputStream("test.pdf"));
	}

}
