import { Component, OnInit, EventEmitter, Output, Input } from '@angular/core';
import { Day } from '../event';
import * as moment from 'moment';
import { AbstractCalendarComponent } from '../abstract-calendar.component';

@Component({
  selector: 'calendar-column',
  templateUrl: './calendar-column.component.html',
  styleUrls: ['./calendar-column.component.scss']
})
export class CalendarColumnComponent extends AbstractCalendarComponent {

}
