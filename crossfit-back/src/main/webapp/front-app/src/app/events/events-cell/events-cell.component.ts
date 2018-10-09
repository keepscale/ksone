import { Component, OnInit, EventEmitter, Output, Input } from '@angular/core';
import { Day } from '../event';
import * as moment from 'moment';

@Component({
  selector: 'events-cell',
  templateUrl: './events-cell.component.html',
  styleUrls: ['./events-cell.component.scss']
})
export class EventsCellComponent implements OnInit {

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

  public getWeeks(){
    let totalWeek = moment(this.days[this.days.length-1].date).diff(this.days[0].date, 'week');
    return new Array(totalWeek+1).keys();
  }
  public getDaysOfWeek(week: number): Day[] {
    return this.days.slice((week-1)*7, (week*7)-1);
  }
}
