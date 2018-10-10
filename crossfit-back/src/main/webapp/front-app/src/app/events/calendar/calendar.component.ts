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

        day.events.sort((a,b)=>a.compateTo(b)).forEach(event=>{
          if (event.isFirstEventOccurence //new start event
            || day.date.isSame(moment(this.days[0].date)) // debut du calendar
            || day.date.isSame(moment(day.date).startOf('isoWeek'))){ // ou debut de semaine 
            event.positionInDay = this.findMinPosition(day.events); // calculate position
          }
          else{
            event.positionInDay = event.positionOfPreviousEvent;
          }
          return event;
        });
      });
    })
    let date = this.route.snapshot.queryParamMap.get("date");
   
    this.calculateDays(date ? moment(date) : moment());
  }

  findMinPosition(events: Event[]) : number{
    let pos = 0;
    while(pos < 100 && !this.isPositionFree(pos, events)){
      pos++;
    }
    return pos;
  }
  isPositionFree(position:number, events: Event[]){
    return events.filter(e=>e.positionInDay===position).length === 0;
  }


  onSelectView(event:MatSelectChange){
    this.mode = event.value;
    this.calculateDays(this.currentDate);
  }

  today(){
    this.calculateDays(moment());
  }

  next(){
    this.calculateDays(this.currentDate.add(this.mode.addValue, this.mode.addUnit));
  }
  
  prev(){
    this.calculateDays(this.currentDate.add(-1*this.mode.addValue, this.mode.addUnit));
  }
  
  private calculateDays(date){
    this.currentDate = moment(date);
    
    let start = moment(this.currentDate);
    let end = moment(this.currentDate);
    this.mode.startOfUnit.forEach(unit => {
      start.startOf(unit);
      end.endOf(unit);
    });
    
    let newDays = [];
    let curday = moment(start);
    do{
      newDays.push(new Day(moment(curday)));
      curday.add(1, 'd');
    }
    while(curday.isBefore(end));

    this.days = newDays;
    this.eventService.sendEventRequest(new EventRequest(start.toDate(), end.toDate()));
  }

  editEvent(event:Event){
    this.onEditEvent.emit(event);
  }

  addEvent(date:Date){
    this.onAddEvent.emit(date);
  }
}
