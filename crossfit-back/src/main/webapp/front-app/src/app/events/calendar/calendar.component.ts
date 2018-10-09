import { Component, OnInit, SimpleChanges, Input, OnChanges, Output, EventEmitter } from '@angular/core';
import * as moment from 'moment';
import { Observable } from 'rxjs';
import { Event, Day } from '../event';
import { EventService, EventRequest } from '../event.service';
import { ActivatedRoute } from '@angular/router';
import { MatSelectChange } from '@angular/material';

export interface CalendarMode{
  name: string;
  addValue: number;
  addUnit: moment.DurationInputArg2;
  startOfUnit: moment.unitOfTime.StartOf[];

  display: string;
}


@Component({
  selector: 'calendar-events',
  templateUrl: './calendar.component.html',
  styleUrls: ['./calendar.component.scss']
})
export class CalendarComponent implements OnInit {

  availableModes: CalendarMode[] = [
    {name:"Day",      addValue: 1, addUnit: 'd',  display: 'column',  startOfUnit: ["day"]               },
    {name:"Week",     addValue: 7, addUnit: 'd',  display: 'column',  startOfUnit: ["isoWeek"]           },
    {name:"Month",    addValue: 1, addUnit: 'M',  display: 'cell',    startOfUnit: ["month", "isoWeek"]  },
    {name:"Planning", addValue: 7, addUnit: 'd',  display: 'row',     startOfUnit: ["day"]               },
    {name:"4Days",    addValue: 4, addUnit: 'd',  display: 'column',  startOfUnit: ["day"]               },
  ];

  currentDate: moment.Moment;

  days: Day[] = [];

  mode: CalendarMode = this.availableModes[2];
  
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


  onSelectView(event:MatSelectChange){
    this.mode = event.value;
    this.goTo(this.currentDate);
  }

  today(){
    this.goTo(moment());
  }

  next(){
    this.goTo(this.currentDate.add(this.mode.addValue, this.mode.addUnit));
  }
  
  prev(){
    this.goTo(this.currentDate.add(-1*this.mode.addValue, this.mode.addUnit));
  }
  
  private goTo(date){
    this.currentDate = moment(date);
    
    this.days = [];
    let start = moment(this.currentDate);
    let end = moment(this.currentDate);
    this.mode.startOfUnit.forEach(unit => {
      start.startOf(unit);
      end.endOf(unit);
    });
    
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

  addEvent(date:Date){
    this.onAddEvent.emit(date);
  }
}
