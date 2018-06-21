import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { WodListComponent } from './wod-list/wod-list.component';
import { RoleManagerGuard, AuthGuard } from '../shared/auth/auth.guard';
import { RouterModule, Routes } from '@angular/router';
import { SharedModule } from '../shared/shared.module';
import { EditComponent } from './edit/edit.component';
import { DetailComponent } from './detail/detail.component';
import { MyResultComponent } from './detail/myresult/myresult.component';
import { RankingComponent } from './detail/ranking/ranking.component';

const wodsRoutes: Routes = [
  { path: 'wod',  component: WodListComponent, canActivate: [AuthGuard] },
  { path: 'wod/new',  component: EditComponent, canActivate: [AuthGuard] },
  { path: 'wod/:id/edit',  component: EditComponent, canActivate: [AuthGuard] },
  { path: 'wod/:id/detail',  component: DetailComponent, canActivate: [AuthGuard] }
];

@NgModule({
  imports: [
    SharedModule,
    RouterModule.forChild(wodsRoutes),
    CommonModule
  ],
  exports: [
    MyResultComponent
  ],
  declarations: [WodListComponent, EditComponent, DetailComponent, MyResultComponent, RankingComponent],
  providers: []
})
export class WodModule { }
