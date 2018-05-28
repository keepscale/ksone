import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { WodListComponent } from './wod-list/wod-list.component';
import { RoleManagerGuard } from '../shared/auth/auth.guard';
import { RouterModule, Routes } from '@angular/router';
import { SharedModule } from '../shared/shared.module';

const wodsRoutes: Routes = [
  { path: 'wod',  component: WodListComponent, /*canActivate: [RoleManagerGuard]*/ }
];

@NgModule({
  imports: [
    RouterModule.forChild(wodsRoutes),
    CommonModule,
    SharedModule
  ],
  declarations: [WodListComponent]
})
export class WodModule { }
