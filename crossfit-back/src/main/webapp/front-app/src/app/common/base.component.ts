import { Component, OnInit } from '@angular/core';
import { ToolBarService } from '../toolbar/toolbar.service';
import { RunnerService } from './runner.service';

export class BaseComponent {

  constructor(
    protected toolbar: ToolBarService,
    protected runner: RunnerService<any>) { 
  }

  set title(value: string){
    this.toolbar.setTitle(value)
  }

}
