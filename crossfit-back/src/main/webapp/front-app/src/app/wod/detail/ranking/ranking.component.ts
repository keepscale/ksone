import { Component, OnInit, Input, OnChanges, SimpleChanges } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { WodService } from '../../wod.service';
import { WodResultRanking } from '../../domain/wod-result-ranking.model';
import { WodResult } from '../../domain/wod-result.model';
import { Wod } from '../../domain/wod.model';

@Component({
  selector: 'app-ranking',
  templateUrl: './ranking.component.html',
  styleUrls: ['./ranking.component.scss']
})
export class RankingComponent implements OnInit, OnChanges {

  @Input("wod")
  private wod: Wod;
  @Input("myresult")
  private myresult: WodResult;
  rankings: WodResultRanking[];
  rankingsInCategory: WodResultRanking[];
  @Input("mode")
  private mode:string = "COMPATE_TO_ME";

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private service: WodService) { }

  ngOnInit() {
    this.loadRanking();
  }

  ngOnChanges(changes: SimpleChanges) {
    console.log("ngOnChanges");
    this.loadRanking();
  }

  loadRanking(){
    this.rankings = null;
    this.service.getRanking(this.wod.id, this.myresult.date+"").subscribe(res=>{
      this.rankings = res;
      this.rankingsInCategory = [];
      this.rankings.forEach(r => {
        if (r.category==this.myresult.category && r.title == this.myresult.title){
          this.rankingsInCategory.push(r);
          r.orderInCategory = this.rankingsInCategory.length;
        }
      });
    })
  }

}
