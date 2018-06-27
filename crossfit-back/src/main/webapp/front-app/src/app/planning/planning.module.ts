import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { WeekPlanningComponent } from './week-planning/week-planning.component';
import { SharedModule } from '../shared/shared.module';

@NgModule({
  imports: [
    CommonModule,
    SharedModule
  ],
  exports: [
    WeekPlanningComponent
  ],
  declarations: [WeekPlanningComponent]
})
export class PlanningModule { }
