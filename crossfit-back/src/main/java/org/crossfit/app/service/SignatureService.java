package org.crossfit.app.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.inject.Inject;

import org.apache.commons.codec.binary.Base64OutputStream;
import org.crossfit.app.domain.CrossFitBox;
import org.crossfit.app.domain.Mandate;
import org.crossfit.app.domain.Signable;
import org.crossfit.app.domain.Subscription;
import org.crossfit.app.domain.enumeration.MandateStatus;
import org.crossfit.app.exception.AlreadySignedException;
import org.crossfit.app.repository.MandateRepository;
import org.crossfit.app.repository.SubscriptionRepository;
import org.crossfit.app.service.pdf.PdfSubscriptionProvider;
import org.crossfit.app.web.rest.dto.MandateDTO;
import org.crossfit.app.web.rest.dto.SubscriptionDTO;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itextpdf.text.DocumentException;

/**
 * Service class for managing signature.
 */
@Service
@Transactional
public class SignatureService {

    private final Logger log = LoggerFactory.getLogger(SignatureService.class);

    @Inject
    private CrossFitBoxSerivce boxService;

    @Inject
    private MailService mailService;
    
    @Inject
    private PdfSubscriptionProvider pdfSubscriptionProvider;

	@Inject
	private SubscriptionRepository subscriptionRepository;

	@Inject
	private MandateRepository mandateRepository;

	public Mandate sign(MandateDTO dto) throws AlreadySignedException {
		CrossFitBox currentCrossFitBox = boxService.findCurrentCrossFitBox();
		Mandate mandate = mandateRepository.getOne(dto.getId());
		
		sign(mandate, dto.getSignatureDate(), dto.getSignatureDataEncoded());
		mandate.setStatus(MandateStatus.ACTIVE);
		//TODO: generate pdf && send by mail ?
		
		return mandateRepository.save(mandate);
	}
	

	public Subscription sign(SubscriptionDTO dto) throws AlreadySignedException, IOException, DocumentException {
		CrossFitBox currentCrossFitBox = boxService.findCurrentCrossFitBox();
		Subscription subscription = subscriptionRepository.findOneWithContract(dto.getId());
		
		sign(subscription, dto.getSignatureDate(), dto.getSignatureDataEncoded());

		ByteArrayOutputStream baos = pdfSubscriptionProvider.getOutputStreamPdfForSubscription(subscription);
		mailService.sendSubscription(subscription, baos.toByteArray());		
		baos.close();
		
		return subscriptionRepository.save(subscription);
	}


	private void sign(Signable signable, DateTime signatureDate, String signatureDataEncoded) throws AlreadySignedException {
		if (signable.getSignatureDate() != null) {
			throw new AlreadySignedException(signable);
		}
		
		signable.setSignatureDate(signatureDate);
		signable.setSignatureDataEncoded(signatureDataEncoded);
		

		log.debug("Signature de {} le {}", signable, signatureDate);
	}
}
