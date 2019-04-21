package org.crossfit.app.pdf;

import com.itextpdf.text.DocumentException;
import com.itextpdf.xmp.XMPException;
import org.crossfit.app.domain.Member;
import org.crossfit.app.domain.PersistentToken;
import org.crossfit.app.domain.Subscription;
import org.crossfit.app.repository.MemberRepository;
import org.crossfit.app.repository.PersistentTokenRepository;
import org.crossfit.app.service.CrossFitBoxSerivce;
import org.crossfit.app.service.MemberService;
import org.crossfit.app.service.PdfSubscription;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.xml.sax.SAXException;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for the UserResource REST controller.
 *
 * @see MemberService
 */
public class PdfSubsciptionTest {


    public static final void main(String args[]) throws IOException, DocumentException, ParserConfigurationException, XMPException, SAXException, ParseException, TransformerException {

        PdfSubscription.SubcriptionLegalText legalText = new PdfSubscription.SubcriptionLegalText();
        legalText.logoUrl = "http://www.crossfit-nancy.fr/img/logo_web.png";

        Subscription sub = new Subscription();
        sub.setId(2157521L);


        File tempFile = File.createTempFile("subscription", ".pdf");
        try(FileOutputStream os = new FileOutputStream(tempFile)){
            PdfSubscription.getBuilder().createPdf(legalText, sub, os);
        }

        System.out.println(tempFile.getPath());
        Desktop.getDesktop().open(tempFile);

    }
}
