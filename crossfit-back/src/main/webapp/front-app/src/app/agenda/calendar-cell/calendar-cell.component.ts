import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { Day } from '../event';

@Component({
  selector: 'calendar-cell',
  templateUrl: './calendar-cell.component.html',
  styleUrls: ['./calendar-cell.component.scss']
})
export class CalendarCellComponent implements OnInit {

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
