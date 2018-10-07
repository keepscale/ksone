import { Component, OnInit, Input, OnChanges, SimpleChanges } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { WodService } from '../../wod.service';
import { WodResultRanking } from '../../domain/wod-result-ranking.model';
import { WodResult } from '../../domain/wod-result.model';
import { Wod } from '../../domain/wod.model';
import { Principal } from '../../../shared/auth/principal.service';
import { WodResultService } from '../../wod-result.service';

@Component({
  selector: 'app-ranking',
  templateUrl: './ranking.component.html',
  styleUrls: ['./ranking.component.scss']
})
export class RankingComponent implements OnInit, OnChanges {

  @Input("wod") wod: Wod;
  @Input("myresult")  myresult: WodResult;
  currentMemberId: number;
  rankings: WodResultRanking[];


  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private principal: Principal,
    private service: WodResultService) { }

  ngOnInit() {
    this.loadRanking();
    this.principal.identity().subscribe(res=>this.currentMemberId=res.id);
  }

  ngOnChanges(changes: SimpleChanges) {
    console.log("ngOnChanges");
    this.loadRanking();
  }

  loadRanking(){
    this.rankings = null;
    this.service.getRanking(this.wod.id).subscribe(res=>{
      this.rankings = res;
    })
  }

  isMyResult(aResult: WodResultRanking){
    return aResult.memberId == this.currentMemberId;
  }

}
