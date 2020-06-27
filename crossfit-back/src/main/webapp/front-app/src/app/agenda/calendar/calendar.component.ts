import { Component, OnInit, EventEmitter, Output, Input } from '@angular/core';
import { Day } from '../event';
import * as moment from 'moment';

@Component({
  selector: 'calendar',
  templateUrl: './calendar.component.html',
  styleUrls: ['./calendar.component.scss']
})
export class CalendarComponent implements OnInit {

  @Input("days")
  days: Day[] = [];
  @Input()
  totalDaysByRow: number;
  rows: number[] = [];

  @Input() addEventMenu;
  @Output() onAddEvent = new EventEmitter<Day>();
  @Output() onEditEvent = new EventEmitter<Event>();
  
  constructor() { }

  ngOnInit() {
    
    let firstDay = this.days[0].date;
    let lastDay = this.days[this.days.length-1].date;
    let totalDaysToDisplay = Math.ceil(moment(lastDay).diff(firstDay, 'd', true));
    let totalRows = Math.max(Math.ceil(totalDaysToDisplay / this.totalDaysByRow), 1);
    console.log("deb: "+moment(firstDay)+" - fin:" + moment(lastDay) + " - totalDaysToDisplay:"+totalDaysToDisplay+"/totalDaysByRow:"+this.totalDaysByRow+" =>totalrow:"+totalRows);
    this.rows = Array.apply(null, {length: totalRows}).map(Number.call, Number);

  }

  editEvent(event:Event){
    this.onEditEvent.emit(event);
  }

  addEvent(day:Day){
    this.onAddEvent.emit(day);
  }
  

  public getDaysOfRowIdx(rowidx: number): Day[] {
    return this.days.slice(rowidx*this.totalDaysByRow, (rowidx+1)*this.totalDaysByRow);
  }
}
