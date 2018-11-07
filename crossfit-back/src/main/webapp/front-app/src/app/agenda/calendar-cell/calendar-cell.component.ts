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

  
  @Input() showDayName: boolean = false;  
  @Input() showMonth: boolean = true;
  
  @Input() showBigCenterDayNumber: boolean = false;
  
  @Input() addEventMenu;
  @Output() onAddEvent = new EventEmitter<Day>();

  constructor() { }

  ngOnInit() {
  }

  addEvent(day:Day){
    this.onAddEvent.emit(day);
  }
}
