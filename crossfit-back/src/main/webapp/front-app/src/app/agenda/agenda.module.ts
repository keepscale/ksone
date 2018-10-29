import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '../shared/shared.module';
import { AgendaComponent } from './agenda/agenda.component';
import { EventsListComponent } from './events-list/events-list.component';
import { CalendarComponent } from './calendar/calendar.component';

@NgModule({
  imports: [
    CommonModule,
    SharedModule
  ],
  exports: [
    AgendaComponent
  ],
  declarations: [AgendaComponent, CalendarComponent, EventsListComponent]
})
export class AgendaModule { }
