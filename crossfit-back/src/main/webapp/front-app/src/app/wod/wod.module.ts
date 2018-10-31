import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RoleManagerGuard, AuthGuard, RoleCoachGuard } from '../shared/auth/auth.guard';
import { RouterModule, Routes } from '@angular/router';
import { SharedModule } from '../shared/shared.module';
import { EditComponent } from './edit/edit.component';
import { DetailComponent } from './detail/detail.component';
import { MyResultComponent } from './detail/myresult/myresult.component';
import { RankingComponent } from './detail/ranking/ranking.component';
import { DeleteComponent } from './delete/delete.component';
import { DatePublicationDialogComponent } from './edit/date-publication-dialog/date-publication-dialog.component';
import { AgendaModule } from '../agenda/agenda.module';
import { WodCalendarComponent } from './wod-calendar/wod-calendar.component';
import { ListComponent } from './list/list.component';

const wodsRoutes: Routes = [
  { path: 'wod/calendar',     component: WodCalendarComponent, canActivate: [RoleCoachGuard] },
  { path: 'wod/search',       component: ListComponent,     canActivate: [RoleCoachGuard] },
  { path: 'wod/create',       component: EditComponent,     canActivate: [RoleCoachGuard] },
  { path: 'wod/:id/edit',     component: EditComponent,     canActivate: [RoleCoachGuard] },
  { path: 'wod/:id/detail',   component: DetailComponent,   canActivate: [RoleCoachGuard] },
  { path: 'wod/:id/delete',   component: DeleteComponent,   canActivate: [RoleCoachGuard] }
];

@NgModule({
  imports: [
    SharedModule,
    RouterModule.forChild(wodsRoutes),
    CommonModule,
    AgendaModule
  ],
  exports: [
    MyResultComponent, RankingComponent
  ],
  entryComponents: [DatePublicationDialogComponent],
  declarations: [WodCalendarComponent, EditComponent, DetailComponent, 
    MyResultComponent, RankingComponent, DeleteComponent, 
    DatePublicationDialogComponent, ListComponent],
  providers: []
})
export class WodModule { }
