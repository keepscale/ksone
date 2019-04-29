package org.crossfit.app.service;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.crossfit.app.domain.CrossFitBox;
import org.crossfit.app.domain.SubscriptionContractModel;
import org.crossfit.app.repository.SubscriptionContractModelRepository;
import org.crossfit.app.web.rest.dto.SubscriptionContractModelDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    
    public Optional<SubscriptionContractModel> findById(Long id) {
    	return this.contractModelRepository.findById(id);
    }
}
