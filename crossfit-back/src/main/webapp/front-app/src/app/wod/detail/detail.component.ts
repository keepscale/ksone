import { Component, OnInit, OnDestroy } from '@angular/core';
import { WodService } from '../wod.service';
import { ToolBarService } from '../../toolbar/toolbar.service';
import { Wod, WodPublication } from '../domain/wod.model';
import { ActivatedRoute, Router } from '@angular/router';
import { WodResult } from '../domain/wod-result.model';
import { Principal } from '../../shared/auth/principal.service';
import * as moment from 'moment';
import { AbstractDetailComponent } from 'src/app/common/abstract-detail.component';
import { RunnerService } from 'src/app/common/runner.service';
import { Observable, Subscription } from 'rxjs';

@Component({
  selector: 'app-detail',
  templateUrl: './detail.component.html',
  styleUrls: ['./detail.component.scss']
})
export class DetailComponent extends AbstractDetailComponent implements OnInit, OnDestroy {

  paramsSubscription: Subscription;
  wod: Wod;
  
  constructor(
    protected toolbar: ToolBarService, 
    protected runner: RunnerService<Wod>, 
    protected principal: Principal,
    protected route: ActivatedRoute,
    protected router: Router,
    protected service: WodService) {

      super(toolbar, runner, principal);
  }

  ngOnInit() {
    super.ngOnInit();
    this.title = "Détail d'un wod";
    this.paramsSubscription = this.route.params.subscribe(params=>{
      this.runner.forkJoin(
        this.loadWod(params["id"]), 
        res=>this.wodLoaded(res));
    });
  }
  ngOnDestroy(){
    super.ngOnDestroy();
    if (this.paramsSubscription)
      this.paramsSubscription.unsubscribe();
  }

  loadWod(id: number): Observable<any>[]{
    return [this.service.get(id)];
  }

  wodLoaded(result: any[]){
    this.wod = result[0];
    this.wod.publications = this.wod.publications.sort((pub1,pub2)=>{return moment(pub2.endAt).diff(moment(pub1.endAt));});
  }
}
