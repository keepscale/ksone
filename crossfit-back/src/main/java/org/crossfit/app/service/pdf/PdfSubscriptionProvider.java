package org.crossfit.app.service.pdf;

import com.itextpdf.text.DocumentException;
import groovy.util.logging.Slf4j;
import org.crossfit.app.domain.Subscription;
import org.crossfit.app.domain.SubscriptionContractModel;
import org.crossfit.app.domain.enumeration.VersionFormatContractSubscription;
import org.crossfit.app.service.MemberService;
import org.crossfit.app.service.SubscriptionContractModelService;
import org.crossfit.app.web.rest.api.ContractModelResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.Optional;

@Component
public class PdfSubscriptionProvider {

    private final Logger log = LoggerFactory.getLogger(PdfSubscriptionProvider.class);

    @Inject
    private SubscriptionContractModelService contractModelService;


    public ByteArrayOutputStream getOutputStreamPdfForSubscription(Subscription sub){

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        SubscriptionContractModel contractModel = sub.getContractModel();
        try{
            switch (contractModel.getVersionFormat()){
                case V_1:
                    PdfSubscriptionV1 v1 = new PdfSubscriptionV1(contractModelService.getJsonById(contractModel.getId()), sub);
                    break;
                default:
                    throw new RuntimeException("Version " + contractModel.getVersionFormat() + " non supportée.");

            }
        }
        catch (IOException | DocumentException e){
            log.error("Erreur a la generation du pdf", e);
            throw new RuntimeException("Impossible de générer le pdf: " + e.getMessage());
        }

        return baos;
    }
}
