import { Component, OnInit, SimpleChanges, Input, OnChanges, Output, EventEmitter } from '@angular/core';
import * as moment from 'moment';
import { Observable } from 'rxjs';
import { Event, Day } from '../event';
import { EventService, EventRequest } from '../event.service';
import { ActivatedRoute } from '@angular/router';

enum Mode{
  WEEK,
  DAY
}
@Component({
  selector: 'app-week-planning',
  templateUrl: './week-planning.component.html',
  styleUrls: ['./week-planning.component.scss']
})
export class WeekPlanningComponent implements OnInit {

  mode = Mode.WEEK;
  currentDate: moment.Moment;

  days: Day[] = [];

  
  @Output() onAddEvent = new EventEmitter<Date>();
  @Output() onEditEvent = new EventEmitter<Event>();


  constructor(private eventService: EventService, private route:ActivatedRoute) { }

  ngOnInit() {
    this.eventService.eventSource$.subscribe(events=>{
      this.days.forEach(day => {
        day.events = events.filter(e=>e.date.isSame(day.date, 'd'));
      });
    })
    let date = this.route.snapshot.queryParamMap.get("date");
   
    this.goTo(date ? moment(date) : moment());
  }


  buildCalendar(){
    this.days = [];
    let start = moment(this.currentDate);
    let end = moment(start);

    if (this.mode == Mode.WEEK){
      start.startOf('isoWeek');
      end = moment(start).add(7,'d');
    }
    
    let curday = moment(start);
    do{
      this.days.push(new Day(moment(curday)));
      curday.add(1, 'd');
    }
    while(curday.isBefore(end));

    this.eventService.sendEventRequest(new EventRequest(start.toDate(), end.toDate()));
  }

  today(){
    this.goTo(moment());
  }
  
  goTo(date){
    this.currentDate = moment(date);
    this.buildCalendar();
  }

  add(amout, unit: string){
    this.currentDate.add(amout, unit);
    this.buildCalendar();
  }

  editEvent(event:Event){
    this.onEditEvent.emit(event);
  }
  addEvent(date:moment.Moment){
    this.onAddEvent.emit(date.toDate());
  }
}
