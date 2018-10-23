import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '../shared/shared.module';
import { AgendaComponent } from './agenda/agenda.component';
import { CalendarCellComponent } from './calendar-cell/calendar-cell.component';
import { CalendarColumnComponent } from './calendar-column/calendar-column.component';
import { EventsListComponent } from './events-list/events-list.component';

@NgModule({
  imports: [
    CommonModule,
    SharedModule
  ],
  exports: [
    AgendaComponent
  ],
  declarations: [AgendaComponent, CalendarCellComponent, CalendarColumnComponent, EventsListComponent]
})
export class AgendaModule { }
