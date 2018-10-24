import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { Day } from '../event';

@Component({
  selector: 'calendar-cell-header',
  templateUrl: './calendar-cell-header.component.html',
  styleUrls: ['./calendar-cell-header.component.scss']
})
export class CalendarCellHeaderComponent implements OnInit {

  @Input() rowIdx: number;
  @Input() colIdx: number;
  @Input() day: Day;
  
  @Output() onAddEvent = new EventEmitter<Date>();

  constructor() { }

  ngOnInit() {
  }

  addEvent(date:Date){
    this.onAddEvent.emit(date);
  }
}
