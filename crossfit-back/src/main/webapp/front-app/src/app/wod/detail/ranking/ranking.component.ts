import { Component, OnInit } from '@angular/core';
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

  private wod: Wod;
  private myresult: WodResult;
  rankings: WodResultRanking[];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private service: WodService,
    private wodDetailService: WodDetailService) { }

  ngOnInit() {
    this.rankings = null;
    let dateParam = this.route.snapshot.paramMap.get('date');
    let resultDate = new Date(dateParam);
    
    this.wod = this.wodDetailService.wod;
    this.myresult = this.wodDetailService.getMyResult(resultDate);

    this.service.getRanking(this.wod.id, dateParam).subscribe(res=>{
      this.rankings = res;
    })
      
  }

}
