import { Component, OnInit } from '@angular/core';
import { ToolBarService } from 'src/app/toolbar/toolbar.service';
import { Principal } from 'src/app/shared/auth/principal.service';
import { User } from '../shared/domain/user.model';
import { Observable, PartialObserver } from 'rxjs';
import { ErrorService } from '../error/error.service';
import { RunnerService } from './runner.service';

export class AbstractComponent implements OnInit {

  protected title: string;
  protected currentUser: User;

  constructor(
    protected toolbar: ToolBarService,
    protected runner: RunnerService<any>,
    protected principal: Principal) { 

      this.principal.identity().subscribe(user=>this.currentUser = user);

  }

  ngOnInit() {
    this.toolbar.setTitle(this.title)
  }

}
