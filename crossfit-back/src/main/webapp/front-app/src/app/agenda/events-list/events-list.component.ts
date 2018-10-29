import { Component, OnInit, Input, EventEmitter, Output } from '@angular/core';
import { Day } from '../event';

@Component({
  selector: 'events-list',
  templateUrl: './events-list.component.html',
  styleUrls: ['./events-list.component.scss']
})
export class EventsListComponent implements OnInit {

  @Input() day: Day;

  @Output() onEditEvent = new EventEmitter<Event>();

  constructor() { }

  ngOnInit() {
  }

  editEvent(event: Event){
    this.onEditEvent.emit(event);
  }

}
