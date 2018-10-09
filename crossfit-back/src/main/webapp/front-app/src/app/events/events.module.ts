import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '../shared/shared.module';
import { CalendarComponent } from './calendar/calendar.component';
import { EventsTableComponent } from './events-table/events-table.component';

@NgModule({
  imports: [
    CommonModule,
    SharedModule
  ],
  exports: [
    CalendarComponent
  ],
  declarations: [CalendarComponent, EventsTableComponent]
})
export class EventsModule { }
