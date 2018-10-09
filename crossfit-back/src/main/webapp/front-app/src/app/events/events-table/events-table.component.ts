import { Component, OnInit, EventEmitter, Output, Input } from '@angular/core';
import { Day } from '../event';
import * as moment from 'moment';

@Component({
  selector: 'events-table',
  templateUrl: './events-table.component.html',
  styleUrls: ['./events-table.component.scss']
})
export class EventsTableComponent implements OnInit {

  @Input() days: Day[] = [];

  @Output() onAddEvent = new EventEmitter<Date>();
  @Output() onEditEvent = new EventEmitter<Event>();
  
  constructor() { }

  ngOnInit() {
  }

  editEvent(event:Event){
    this.onEditEvent.emit(event);
  }

  addEvent(date:moment.Moment){
    this.onAddEvent.emit(date.toDate());
  }
}
