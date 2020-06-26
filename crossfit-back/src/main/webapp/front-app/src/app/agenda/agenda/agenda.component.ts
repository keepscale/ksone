import { Component, OnInit, SimpleChanges, Input, OnChanges, Output, EventEmitter } from '@angular/core';
import * as moment from 'moment';
import { Observable } from 'rxjs';
import { Event, Day } from '../event';
import { EventService, EventRequest } from '../event.service';
import { ActivatedRoute, Router, Params } from '@angular/router';
import { MatSelectChange } from '@angular/material/select';

export interface CalendarMode{
  name: string;
  addValue: number;
  addUnit: moment.DurationInputArg2;
  startOfUnit: moment.unitOfTime.StartOf[];
  endOfUnit: moment.unitOfTime.StartOf | moment.unitOfTime.StartOf[];

  daysByRow: number;
}


@Component({
  selector: 'agenda',
  templateUrl: './agenda.component.html',
  styleUrls: ['./agenda.component.scss']
})
export class AgendaComponent implements OnInit {

  availableModes: CalendarMode[] = [
    {name:"Day",      addValue: 1, addUnit: 'd',  daysByRow: 1,  startOfUnit: ["day"],             endOfUnit: "day"},
    {name:"Week",     addValue: 7, addUnit: 'd',  daysByRow: 7,  startOfUnit: ["isoWeek"],         endOfUnit: "day"},
    {name:"Month",    addValue: 1, addUnit: 'M',  daysByRow: 7,  startOfUnit: ["month", "isoWeek"],endOfUnit: ["month", "isoWeek"]},
    {name:"Planning", addValue: 5, addUnit: 'd',  daysByRow: 1,  startOfUnit: ["day"],             endOfUnit: "day"},
    {name:"4Days",    addValue: 4, addUnit: 'd',  daysByRow: 4,  startOfUnit: ["day"],             endOfUnit: "day"},
  ];

  currentDate: moment.Moment = moment();

  days: Day[] = [];

  mode: CalendarMode = this.availableModes[2];
  
  @Input() addEventMenu;
  @Output() onAddEvent = new EventEmitter<Date>();
  @Output() onEditEvent = new EventEmitter<Event>();


  constructor(private eventService: EventService, private route:ActivatedRoute, private router:Router) { }

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

    this.route.queryParams.subscribe(params=>{
      this.currentDate = params["date"] ? moment(params["date"]) : moment();
      this.mode = this.availableModes.find(p=>p.name===params["mode"]);
      this.mode = this.mode == undefined ? this.availableModes[2] : this.mode;
      this.reCalculateDays();
    })   
    
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
    this.navigateTo(this.currentDate, event.value);
  }

  today(){
    this.navigateTo(moment());
  }

  next(){
    this.navigateTo(this.currentDate.add(this.mode.addValue, this.mode.addUnit));
  }
  
  prev(){
    this.navigateTo(this.currentDate.add(-1*this.mode.addValue, this.mode.addUnit));
  }

  private navigateTo(date, mode?: CalendarMode){
    this.currentDate = moment(date);
    this.mode = mode ? mode : this.mode;
    const queryParams: Params = Object.assign({}, this.route.snapshot.queryParams);
    queryParams['date'] = this.currentDate.format("Y-MM-D")
    queryParams['mode'] = this.mode.name;
    this.router.navigate([], { relativeTo: this.route, queryParams: queryParams });
  }
  
  private reCalculateDays(){
    
    let start = moment(this.currentDate);
    this.mode.startOfUnit.forEach(unit => {
      start.startOf(unit);
    });
    let end = moment(start)
      .add(this.mode.addValue, this.mode.addUnit)
      .add(-1, 'd');
    if (typeof this.mode.endOfUnit === 'string'){
      end.endOf(this.mode.endOfUnit);
    }
    else{
      this.mode.endOfUnit.forEach(unit => {
        end.endOf(unit);
      });
    }
    console.log("end:"+end);
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

  addEvent(day:Day){
    this.onAddEvent.emit(day.date.toDate());
  }
}
