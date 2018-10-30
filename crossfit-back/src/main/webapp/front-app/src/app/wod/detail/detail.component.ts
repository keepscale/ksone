import { Component, OnInit } from '@angular/core';
import { WodService } from '../wod.service';
import { ToolBarService } from '../../toolbar/toolbar.service';
import { Wod, WodPublication } from '../domain/wod.model';
import { ActivatedRoute, Router } from '@angular/router';
import { WodResult } from '../domain/wod-result.model';
import { Principal } from '../../shared/auth/principal.service';
import * as moment from 'moment';
import { AbstractDetailComponent } from 'src/app/common/abstract-detail.component';
import { RunnerService } from 'src/app/common/runner.service';

@Component({
  selector: 'app-detail',
  templateUrl: './detail.component.html',
  styleUrls: ['./detail.component.scss']
})
export class DetailComponent extends AbstractDetailComponent implements OnInit {

  wod: Wod;
  
  constructor(
    protected toolbar: ToolBarService, 
    protected runner: RunnerService<Wod>, 
    protected principal: Principal,
    private route: ActivatedRoute,
    private router: Router,
    private service: WodService) {
      super(toolbar, runner, principal);
     }

  ngOnInit() {
    this.title = "Détail d'un wod";
    this.route.params.subscribe(params=>{
      this.runner.run(
        this.service.get(params["id"]), 
        res=>this.wodLoaded(res));
    });
    super.ngOnInit();
  }

  wodLoaded(result: Wod){
    this.wod = result;
    this.wod.publications = this.wod.publications.sort((pub1,pub2)=>{return moment(pub2.endAt).diff(moment(pub1.endAt));});
  }
}
