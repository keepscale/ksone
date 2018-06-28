import { Component, OnInit, SimpleChanges, Input, OnChanges } from '@angular/core';
import * as moment from 'moment';
import { Observable } from 'rxjs';
import { Event, Day } from '../event';

@Component({
  selector: 'app-week-planning',
  templateUrl: './week-planning.component.html',
  styleUrls: ['./week-planning.component.scss']
})
export class WeekPlanningComponent implements OnInit, OnChanges {

  now = moment();
  currentDate: moment.Moment;


  days: Day[] = [];


  @Input("events")
  events: Event[] = [];



  constructor() { }

  ngOnInit() {
    this.today();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['events']) {
      this.refreshEvent();
    }
}

  buildCalendar(){
    this.days = [];
    let start = moment(this.currentDate).startOf('isoWeek').add(-1, 'd');
    for (let day = 0; day < 7; day++) {
      this.days.push(new Day(moment(start.add(1, 'd'))));
    }
    this.refreshEvent();
  }

  refreshEvent(){
    this.days.forEach(day => {
      day.events = this.events.filter(e=>e.date.isSame(day.date, 'd'));
    });
  }

  today(){
    this.currentDate = moment(this.now);
    this.buildCalendar();
  }

  add(amout, unit: string){
    this.currentDate.add(amout, unit);
    this.buildCalendar();
  }

  editEvent(event:Event){
    console.log(event);
  }
}
