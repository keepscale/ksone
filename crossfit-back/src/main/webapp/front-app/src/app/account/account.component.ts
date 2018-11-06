import { Component, OnInit } from '@angular/core';
import { Principal } from '../shared/auth/principal.service';
import { User } from '../shared/domain/user.model';
import { ToolBarService } from '../toolbar/toolbar.service';
import { BaseComponent } from '../common/base.component';
import { RunnerService } from '../common/runner.service';
import { AbstractComponent } from '../common/abstract.component';

@Component({
  selector: 'app-account',
  templateUrl: './account.component.html',
  styleUrls: ['./account.component.scss']
})
export class AccountComponent extends AbstractComponent implements OnInit {

  account: User;

  constructor(
    protected toolbar: ToolBarService, 
    protected runner: RunnerService<any>, 
    protected principal: Principal) {
      super(toolbar, runner, principal);
  }

  ngOnInit() {
    this.title = "Mes informations";
    this.principal.identity(true).subscribe(res=>{
      this.account=res;
    });
  }

}
