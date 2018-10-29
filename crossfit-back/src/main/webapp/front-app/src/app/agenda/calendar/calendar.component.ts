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
  @Input()
  totalDaysByRow: number;
  rows: number[] = [];

  @Output() onAddEvent = new EventEmitter<Day>();
  @Output() onEditEvent = new EventEmitter<Event>();
  
  constructor() { }

  ngOnInit() {
  }

  editEvent(event:Event){
    this.onEditEvent.emit(event);
  }

  addEvent(day:Day){
    this.onAddEvent.emit(day);
  }
  
  @Input()
  set days(days: Day[]) {
    let firstDay = days[0].date;
    let lastDay = days[days.length-1].date;
    let totalDaysToDisplay = Math.ceil(moment(lastDay).diff(firstDay, 'd', true));
    let totalRows = Math.max(Math.ceil(totalDaysToDisplay / this.totalDaysByRow), 1);
    console.log("deb: "+moment(firstDay)+" - fin:" + moment(lastDay) + " - totalDaysToDisplay:"+totalDaysToDisplay+"/totalDaysByRow:"+this.totalDaysByRow+" =>totalrow:"+totalRows);
    this._days = days;
    this.rows = Array.apply(null, {length: totalRows}).map(Number.call, Number);
  }

  get days() {
    return this._days;
  }

  public getDaysOfRowIdx(rowidx: number): Day[] {
    return this.days.slice(rowidx*this.totalDaysByRow, (rowidx+1)*this.totalDaysByRow);
  }
}
