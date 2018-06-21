import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivityComponent } from './activity/activity.component';
import { Routes, RouterModule } from '@angular/router';
import { AuthGuard } from '../shared/auth/auth.guard';
import { SharedModule } from '../shared/shared.module';
import { WodModule } from '../wod/wod.module';


const bookingsRoutes: Routes = [
  { path: 'activity',  component: ActivityComponent, canActivate: [AuthGuard] }
];

@NgModule({
  imports: [
    SharedModule,
    RouterModule.forChild(bookingsRoutes),
    CommonModule,
    WodModule
  ],
  declarations: [ActivityComponent],
  providers: []
})
export class BookingModule { }
