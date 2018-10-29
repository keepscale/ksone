import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '../shared/shared.module';
import { AgendaComponent } from './agenda/agenda.component';
import { EventsListComponent } from './events-list/events-list.component';
import { CalendarComponent } from './calendar/calendar.component';
import { CalendarCellComponent } from './calendar-cell/calendar-cell.component';

@NgModule({
  imports: [
    CommonModule,
    SharedModule
  ],
  exports: [
    AgendaComponent
  ],
  declarations: [AgendaComponent, CalendarComponent, EventsListComponent, CalendarCellComponent]
})
export class AgendaModule { }
