import { Component, OnInit, Directive } from '@angular/core';
import { ToolBarService } from 'src/app/toolbar/toolbar.service';
import { Principal } from 'src/app/shared/auth/principal.service';
import { User } from '../shared/domain/user.model';
import { AbstractComponent } from './abstract.component';

@Directive()
export class AbstractDetailComponent extends AbstractComponent implements OnInit {

  ngOnInit() {
    this.toolbar.setAllowGoBack(true);
  }
}
