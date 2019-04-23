package org.crossfit.app.service;

import org.crossfit.app.domain.Subscription;
import org.crossfit.app.domain.VersionContractSubscription;
import org.crossfit.app.domain.enumeration.VersionFormatContractSubscription;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
public class PdfSubscriptionProvider {

    @Autowired
    private ApplicationContext ctx;

    public PdfSubscriptionBuilder getBuilderForSubscription(Subscription sub){
        Map<String, PdfSubscriptionBuilder> beansOfType = ctx.getBeansOfType(PdfSubscriptionBuilder.class);

        Optional<VersionFormatContractSubscription> versionFormat = Optional.ofNullable(sub)
                .map(Subscription::getVersionContractSubscription)
                .map(VersionContractSubscription::getVersionFormat);

        Optional<PdfSubscriptionBuilder> optBuilder = versionFormat.flatMap(
                vFormat -> beansOfType.values().stream().filter(bean -> bean.support(vFormat)).findFirst());

        return optBuilder.orElseThrow(()->new NoSuchBeanDefinitionException(
                        "Impossible de trouver un PdfSubscriptionBuilder supportant " + versionFormat));
    }
}
