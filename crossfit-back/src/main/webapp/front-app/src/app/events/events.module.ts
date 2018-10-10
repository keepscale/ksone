import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '../shared/shared.module';
import { CalendarComponent } from './calendar/calendar.component';
import { EventsTableComponent } from './events-table/events-table.component';
import { EventsCellComponent } from './events-cell/events-cell.component';
import { EventCellComponent } from './event-cell/event-cell.component';

@NgModule({
  imports: [
    CommonModule,
    SharedModule
  ],
  exports: [
    CalendarComponent
  ],
  declarations: [CalendarComponent, EventsTableComponent, EventsCellComponent, EventCellComponent]
})
export class EventsModule { }
