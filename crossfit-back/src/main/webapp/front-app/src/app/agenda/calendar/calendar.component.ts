import { Component, OnInit, EventEmitter, Output, Input } from '@angular/core';
import { Day } from '../event';
import * as moment from 'moment';

@Component({
  selector: 'calendar',
  templateUrl: './calendar.component.html',
  styleUrls: ['./calendar.component.scss']
})
export class CalendarComponent implements OnInit {

  _days: Day[] = [];
  weeks: number[] = [];

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
  
  @Input()
  set days(days: Day[]) {
    let firstDay = days[0].date;
    let lastDay = days[days.length-1].date;
    let totalWeek = Math.ceil(moment(lastDay).diff(firstDay, 'week', true));

    this._days = days;
    this.weeks = Array.apply(null, {length: totalWeek}).map(Number.call, Number);
    console.log(this.days);
  }

  get days() {
    return this._days;
  }

  public getDaysOfWeek(week: number): Day[] {
    return this.days.slice((week)*7, ((week+1)*7));
  }
}
