import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { Event } from './event';

export class EventRequest{
  constructor(public start: Date, public end: Date){}
}
@Injectable()
export class EventService {

  private eventSource = new Subject<Event[]>();
  private eventRequest = new Subject<EventRequest>();
  
  private lastRequest;
  eventSource$ = this.eventSource.asObservable();
  eventRequested$ = this.eventRequest.asObservable();

  constructor() { }

  setEvents(events: Event[]){
    this.eventSource.next(events);
  }

  sendEventRequest(request: EventRequest){
    this.lastRequest = request;
    this.eventRequest.next(request);
  }

  refresh(){
    if (this.lastRequest != null)
      this.sendEventRequest(this.lastRequest);
  }
}
