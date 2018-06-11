import { Component, OnInit, Input } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { WodService } from '../../wod.service';
import { WodResultRanking } from '../../domain/wod-result-ranking.model';
import { WodDetailService } from '../wod.detail.service';
import { WodResult } from '../../domain/wod-result.model';
import { Wod } from '../../domain/wod.model';

@Component({
  selector: 'app-ranking',
  templateUrl: './ranking.component.html',
  styleUrls: ['./ranking.component.scss']
})
export class RankingComponent implements OnInit {

  @Input("wod")
  private wod: Wod;
  @Input("myresult")
  private myresult: WodResult;
  rankings: WodResultRanking[];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private service: WodService,
    private wodDetailService: WodDetailService) { }

  ngOnInit() {
    this.loadRanking();
    this.wodDetailService.resultSaved$.subscribe(result=>{
      if (this.myresult == result){
        this.loadRanking();
      }
    });
  }

  loadRanking(){
    this.rankings = null;
    this.service.getRanking(this.wod.id, this.myresult.date+"").subscribe(res=>{
      this.rankings = res;
    })
  }

}
