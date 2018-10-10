import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'event-cell',
  templateUrl: './event-cell.component.html',
  styleUrls: ['./event-cell.component.scss']
})
export class EventCellComponent implements OnInit {

  @Input() event: Event;

  constructor() { }

  ngOnInit() {
  }

}
