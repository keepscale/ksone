package org.crossfit.app.service.pdf;

import com.itextpdf.text.DocumentException;
import org.crossfit.app.domain.Mandate;
import org.crossfit.app.domain.Subscription;
import org.crossfit.app.domain.SubscriptionContractModel;
import org.crossfit.app.service.CrossFitBoxSerivce;
import org.crossfit.app.service.SubscriptionContractModelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.IOException;
import java.io.OutputStream;

@Component
public class PdfProvider {

    private final Logger log = LoggerFactory.getLogger(PdfProvider.class);

    @Inject
    private SubscriptionContractModelService contractModelService;
    @Inject
    private CrossFitBoxSerivce crossFitBoxSerivce;


    public void writePdfForSubscription(Subscription sub, OutputStream os){

        SubscriptionContractModel contractModel = sub.getContractModel();
        try{
            switch (contractModel.getVersionFormat()){
                case V_1:
                    PdfSubscriptionV1 v1 = new PdfSubscriptionV1(contractModelService.getJsonById(contractModel.getId()), sub);
                    v1.createPdf(os);
                    break;
                default:
                    throw new RuntimeException("Version " + contractModel.getVersionFormat() + " non supportée.");

            }
        }
        catch (IOException | DocumentException e){
            log.error("Erreur a la generation du pdf", e);
            throw new RuntimeException("Impossible de générer le pdf: " + e.getMessage());
        }

    }


    public void writePdfForMandate(Mandate m, OutputStream os){

        String json = crossFitBoxSerivce.findCurrentCrossFitBox().getJsonMandate();

        try {
            PdfMandate pdf = new PdfMandate(json, m);
            pdf.createPdf(os);
        }
        catch (IOException | DocumentException e){
            log.error("Erreur a la generation du pdf", e);
            throw new RuntimeException("Impossible de générer le pdf: " + e.getMessage());
        }
    }
}
