import { Component, OnInit, Input, OnChanges } from '@angular/core';
import { AnimateTimings } from '@angular/animations';

@Component({
  selector: 'app-status-bar',
  templateUrl: './status-bar.component.html',
  styleUrls: ['./status-bar.component.scss']
})
export class StatusBarComponent implements OnInit {

  @Input("status")
  status: string;

  @Input("error")
  error: any;

  constructor() { }

  ngOnInit() {
  }

}
