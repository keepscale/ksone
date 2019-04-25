package org.crossfit.app.pdf;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.Arrays;
import java.util.stream.Collectors;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import com.fasterxml.jackson.core.JsonParser;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.crossfit.app.domain.Member;
import org.crossfit.app.domain.Membership;
import org.crossfit.app.domain.Subscription;
import org.crossfit.app.domain.SubscriptionDirectDebit;
import org.crossfit.app.domain.enumeration.PaymentMethod;
import org.crossfit.app.domain.enumeration.Title;
import org.crossfit.app.service.MemberService;
import org.crossfit.app.service.pdf.PdfSubscriptionV1;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.xml.sax.SAXException;

import com.itextpdf.text.DocumentException;
import com.itextpdf.xmp.XMPException;

/**
 * Test class for the UserResource REST controller.
 *
 * @see MemberService
 */
public class PdfSubsciptionTest {


    public static final void main(String args[]) throws IOException, DocumentException, ParserConfigurationException, XMPException, SAXException, ParseException, TransformerException, net.minidev.json.parser.ParseException {

        String data = Files.readAllLines(Paths.get("/home/loic/dev/github/keepscale/ksone/crossfit-back/src/test/java/org/crossfit/app/pdf/data.json")).stream().collect(Collectors.joining());


        Subscription sub = new Subscription();
        sub.setId(2157521L);
        sub.setMember(new Member());
        sub.getMember().setTitle(Title.MR);
        sub.getMember().setFirstName("Loïc");
        sub.getMember().setLastName("Gangloff");
        sub.getMember().setAddress("1 Rue du Bambou qui fuit");
        sub.getMember().setZipCode("54740");
        sub.getMember().setCity("Marbache");
        sub.getMember().setLogin("loic.gangloff@gmail.com");
        sub.getMember().setTelephonNumber("+33 6 47 87 57 84");
        sub.setMembership(new Membership());
        sub.getMembership().setName("Triplet");
        sub.setPriceTaxIncl(75.0);
        sub.setPaymentMethod(PaymentMethod.DIRECT_DEBIT);
        sub.setDirectDebit(new SubscriptionDirectDebit());
        sub.getDirectDebit().setFirstPaymentTaxIncl(33.5);
        sub.getDirectDebit().setFirstPaymentMethod(PaymentMethod.BANK_CHECK);
        sub.getDirectDebit().setAtDayOfMonth(3);
        sub.getDirectDebit().setAfterDate(new LocalDate());
        sub.getDirectDebit().setAmount(55.0);
        sub.setSignatureDate(new DateTime());
        sub.setSignatureDataEncoded("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABQAAAAUCAMAAAC6V+0/AAAAwFBMVEXm7NK41k3w8fDv7+q01Tyy0zqv0DeqyjOszDWnxjClxC6iwCu11z6y1DvA2WbY4rCAmSXO3JZDTxOiwC3q7tyryzTs7uSqyi6tzTCmxSukwi9aaxkWGga+3FLv8Ozh6MTT36MrMwywyVBziSC01TbT5ZW9z3Xi6Mq2y2Xu8Oioxy7f572qxzvI33Tb6KvR35ilwTmvykiwzzvV36/G2IPw8O++02+btyepyDKvzzifvSmw0TmtzTbw8PAAAADx8fEC59dUAAAA50lEQVQYV13RaXPCIBAG4FiVqlhyX5o23vfVqUq6mvD//1XZJY5T9xPzzLuwgKXKslQvZSG+6UXgCnFePtBE7e/ivXP/nRvUUl7UqNclvO3rpLqofPDAD8xiu2pOntjamqRy/RqZxs81oeVzwpCwfyA8A+8mLKFku9XfI0YnSKXnSYZ7ahSII+AwrqoMmEFKriAeVrqGM4O4Z+ADZIhjg3R6LtMpWuW0ERs5zunKVHdnnnMLNQqaUS0kyKkjE1aE98b8y9x9JYHH8aZXFMKO6JFMEvhucj3Wj0kY2D92HlHbE/9Vk77mD6srRZqmVEAZAAAAAElFTkSuQmCC");

        
        
        File tempFile = File.createTempFile("subscription", ".pdf");
        try(FileOutputStream os = new FileOutputStream(tempFile)){
            new PdfSubscriptionV1(data, sub).createPdf(os);
        }
  
        System.out.println(tempFile.getPath());
        Desktop.getDesktop().open(tempFile);

    }
}
