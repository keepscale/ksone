import { Component, OnInit, Input, OnChanges, SimpleChanges } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { WodService } from '../../wod.service';
import { WodResultRanking } from '../../domain/wod-result-ranking.model';
import { WodResult } from '../../domain/wod-result.model';
import { Wod } from '../../domain/wod.model';
import { Principal } from '../../../shared/auth/principal.service';
import { WodResultService } from '../../wod-result.service';

export class RankingTab{
  
  constructor(
    private title: string, 
    private icon?: string, 
    public filter?: (value: WodResultRanking) => boolean){
  }
}

@Component({
  selector: 'app-ranking',
  templateUrl: './ranking.component.html',
  styleUrls: ['./ranking.component.scss']
})
export class RankingComponent implements OnInit, OnChanges {

  @Input("wod") wod: Wod;
  @Input("date") date: Date;
  @Input("myresult")  myresult: WodResult;
  currentMemberId: number;
  rankings: WodResultRanking[];
  filteredrankings: WodResultRanking[];

  @Input("columns")
  displayedColumns: string[] = ['order', 'name', 'result', 'category', 'division', 'date'];

  tabs: RankingTab[];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private principal: Principal,
    private service: WodResultService) { }

  ngOnInit() {
    this.loadRanking();
    this.tabs = [new RankingTab('', 'bar_chart', ()=>true)];
    this.service.getResultDivisions().subscribe(divisions=>{
      this.tabs = this.tabs.concat(divisions.map(
        division=>new RankingTab(division, null, (r: WodResultRanking) => r.division == division)
      ));
    })
    this.principal.identity().subscribe(res=>this.currentMemberId=res.id);
  }

  ngOnChanges(changes: SimpleChanges) {
    /*console.log("ngOnChanges");
    this.loadRanking();*/
  }

  loadRanking(){
    this.rankings = null;
    this.service.getRanking(this.wod.id, this.date).subscribe(res=>{
      this.rankings = res;
      this.filteredrankings = res;
    })
  }

  isMyResult(aResult: WodResultRanking){
    return aResult.memberId == this.currentMemberId;
  }

  onSelectTab(index){
    this.filteredrankings = this.rankings.filter(this.tabs[index].filter);
  }
}
