import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { WodListComponent } from './wod-list/wod-list.component';
import { RoleManagerGuard, AuthGuard } from '../shared/auth/auth.guard';
import { RouterModule, Routes } from '@angular/router';
import { SharedModule } from '../shared/shared.module';
import { DetailComponent } from './detail/detail.component';

const wodsRoutes: Routes = [
  { path: 'wod',  component: WodListComponent, canActivate: [AuthGuard] },
  { path: 'wod/new',  component: DetailComponent, canActivate: [AuthGuard] },
  { path: 'wod/:id',  component: DetailComponent, canActivate: [AuthGuard] }
];

@NgModule({
  imports: [
    SharedModule,
    RouterModule.forChild(wodsRoutes),
    CommonModule
  ],
  declarations: [WodListComponent, DetailComponent]
})
export class WodModule { }
