import { Component, OnInit } from '@angular/core';
import { Wod } from '../domain/wod.model';
import { ActivatedRoute, Router } from '@angular/router';
import { WodService } from '../wod.service';
import { ToolBarService } from '../../toolbar/toolbar.service';
import * as moment from 'moment';
import { WodResultService } from '../wod-result.service';
import { DetailComponent } from '../detail/detail.component';
import { RunnerService } from 'src/app/common/runner.service';
import { Principal } from 'src/app/shared/auth/principal.service';

@Component({
  selector: 'app-delete',
  templateUrl: './delete.component.html',
  styleUrls: ['./delete.component.scss']
})
export class DeleteComponent extends DetailComponent implements OnInit {

  wod: Wod;
  enabledDeleteButton: boolean = true;

  constructor(
    protected toolbar: ToolBarService, 
    protected runner: RunnerService<any>, 
    protected principal: Principal,
    protected route: ActivatedRoute,
    protected router: Router,
    protected service: WodService,
    private wodResultService: WodResultService) {

      super(toolbar, runner, principal, route, router, service);
  }

  ngOnInit() {
    super.ngOnInit();
    this.title = "Supprimer le wod";
  }

  onDelete(){
    this.runner.run(
      this.service.delete(this.wod),
      res=>this.router.navigate(["wod/calendar"])
    )
  }

}
