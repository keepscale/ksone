import { Component, OnInit, SimpleChanges, Input, OnChanges, Output, EventEmitter } from '@angular/core';
import * as moment from 'moment';
import { Observable } from 'rxjs';
import { Event, Day } from '../event';
import { EventService, EventRequest } from '../event.service';
import { ActivatedRoute } from '@angular/router';


@Component({
  selector: 'week-events',
  templateUrl: './week-planning.component.html',
  styleUrls: ['./week-planning.component.scss']
})
export class WeekComponent implements OnInit {

  currentDate: moment.Moment;

  days: Day[] = [];

  numberOfDayToDisplay: number = 7;
  
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
  }

  today(){
    this.goTo(moment());
  }

  next(){
    this.goTo(this.currentDate.add(this.numberOfDayToDisplay, 'd'));
  }
  
  prev(){
    this.goTo(this.currentDate.add(-1*this.numberOfDayToDisplay, 'd'));
  }
  
  private goTo(date){
    this.currentDate = moment(date);
    
    this.days = [];
    let start = moment(this.currentDate);
    let end = moment(start.startOf('isoWeek')).add(this.numberOfDayToDisplay,'d');

    
    let curday = moment(start);
    do{
      this.days.push(new Day(moment(curday)));
      curday.add(1, 'd');
    }
    while(curday.isBefore(end));

    this.eventService.sendEventRequest(new EventRequest(start.toDate(), end.toDate()));
  }

  editEvent(event:Event){
    this.onEditEvent.emit(event);
  }

  addEvent(date:moment.Moment){
    this.onAddEvent.emit(date.toDate());
  }
}
