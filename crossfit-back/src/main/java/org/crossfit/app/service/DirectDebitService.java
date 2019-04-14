package org.crossfit.app.service;

import org.crossfit.app.domain.Mandate;
import org.crossfit.app.domain.Member;
import org.crossfit.app.repository.MandateRepository;
import org.crossfit.app.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DirectDebitService {

    @Autowired
    private MandateRepository mandateRepository;
    @Autowired
    private CrossFitBoxSerivce crossFitBoxSerivce;

    @Autowired
    private MemberRepository memberRepository;


    public List<Mandate> findAllMandateByMember(Member member){
        return mandateRepository.findAllByMember(member);
    }

}
