package org.crossfit.app.service;

import org.crossfit.app.domain.CrossFitBox;
import org.crossfit.app.domain.Membership;
import org.crossfit.app.domain.MembershipRules;
import org.crossfit.app.domain.SubscriptionContractModel;
import org.crossfit.app.repository.MembershipRepository;
import org.crossfit.app.repository.SubscriptionContractModelRepository;
import org.crossfit.app.web.rest.dto.SubscriptionContractModelDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;

@Service
@Transactional
public class SubscriptionContractModelService {

    @Inject
    private CrossFitBoxSerivce boxService;

    @Inject
    private SubscriptionContractModelRepository contractModelRepository;

    public SubscriptionContractModel save(SubscriptionContractModelDTO dto){

        CrossFitBox currentCrossFitBox = boxService.findCurrentCrossFitBox();
        SubscriptionContractModel model = dto.getId() == null ? null : contractModelRepository.getOne(dto.getId());
        if(model != null && !model.getBox().equals(currentCrossFitBox)){
            model = null;
        }
        if (model == null){
            model = new SubscriptionContractModel();
        }
        model.setBox(currentCrossFitBox);
        model.setVersionData(dto.getVersionData());
        model.setVersionFormat(dto.getVersionFormat());
        model.setJsonValue(dto.getJsonValue());
        SubscriptionContractModel result = contractModelRepository.save(model);
        return result;
    }


    public List<SubscriptionContractModel> findAllOfCurrentBox(){
        return contractModelRepository.findAllByBox(boxService.findCurrentCrossFitBox());
    }
}
