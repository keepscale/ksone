import { Component, OnInit, OnDestroy } from '@angular/core';
import { ToolBarService } from 'src/app/toolbar/toolbar.service';
import { Principal } from 'src/app/shared/auth/principal.service';
import { User } from '../shared/domain/user.model';
import { Observable, PartialObserver, Subscription } from 'rxjs';
import { ErrorService } from '../error/error.service';
import { RunnerService } from './runner.service';
import { BaseComponent } from './base.component';

export class AbstractComponent extends BaseComponent {

  constructor(
    protected toolbar: ToolBarService,
    protected runner: RunnerService<any>,
    protected principal: Principal) { 
      super(toolbar, runner);

  }
}
