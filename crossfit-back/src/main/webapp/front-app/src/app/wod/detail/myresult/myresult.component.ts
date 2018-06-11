import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { WodDetailService } from '../wod.detail.service';
import { Wod } from '../../domain/wod.model';
import { WodResult } from '../../domain/wod-result.model';

@Component({
  selector: 'app-myresult',
  templateUrl: './myresult.component.html',
  styleUrls: ['./myresult.component.scss']
})
export class MyResultComponent implements OnInit {

  private wod: Wod;
  private myresult: WodResult;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private wodDetailService: WodDetailService) { }

  ngOnInit() {
    let resultDate = new Date(this.route.snapshot.paramMap.get('date'));

    this.wod = this.wodDetailService.wod;
    let resultsMatch = this.wodDetailService.wodResults.filter(r=>{
      let dateR = new Date(r.date);
      return dateR.getDate() == resultDate.getDate();
    });
    if (resultsMatch.length == 1){
      this.myresult = resultsMatch[0];
    }
  }

}
