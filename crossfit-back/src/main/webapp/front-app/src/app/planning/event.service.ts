import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { Event } from './event';

export class EventRequest{
  constructor(private start: Date, private end: Date){}
}
@Injectable({
  providedIn: 'root'
})
export class EventService {

  private eventSource = new Subject<Event[]>();
  private eventRequest = new Subject<EventRequest>();
  
  eventSource$ = this.eventSource.asObservable();
  eventRequested$ = this.eventSource.asObservable();

  constructor() { }

  sendEventRequest(request: EventRequest){
    this.eventRequest.next(request);
  }
  setEvents(events: Event[]){
    this.eventSource.next(events);
  }
}
