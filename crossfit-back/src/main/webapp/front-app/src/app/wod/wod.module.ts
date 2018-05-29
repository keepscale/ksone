import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { WodListComponent } from './wod-list/wod-list.component';
import { RoleManagerGuard } from '../shared/auth/auth.guard';
import { RouterModule, Routes } from '@angular/router';
import { SharedModule } from '../shared/shared.module';
import { DetailComponent } from './detail/detail.component';

const wodsRoutes: Routes = [
  { path: 'wod',  component: WodListComponent, /*canActivate: [RoleManagerGuard]*/ },
  { path: 'wod/detail',  component: DetailComponent, /*canActivate: [RoleManagerGuard]*/ }
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
