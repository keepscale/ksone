package org.crossfit.app.service.pdf;

import com.itextpdf.text.DocumentException;
import org.crossfit.app.domain.Subscription;
import org.crossfit.app.domain.enumeration.VersionFormatContractSubscription;

import java.io.IOException;
import java.io.OutputStream;

public interface PdfSubscriptionBuilder {
    boolean support(VersionFormatContractSubscription versionFormat);
    void createPdf(OutputStream os) throws IOException, DocumentException;
}
