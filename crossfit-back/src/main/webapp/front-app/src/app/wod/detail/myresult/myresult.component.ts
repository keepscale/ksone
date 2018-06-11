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
    this.myresult = this.wodDetailService.getMyResult(resultDate);
  }

}
