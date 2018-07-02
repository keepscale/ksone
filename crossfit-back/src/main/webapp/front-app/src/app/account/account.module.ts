import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '../shared/shared.module';
import { RouterModule, Routes } from '@angular/router';
import { AccountComponent } from './account.component';
import { AuthGuard } from '../shared/auth/auth.guard';

const accountRoutes: Routes = [
  { path: 'account',  component: AccountComponent, canActivate: [AuthGuard] }
];

@NgModule({
  imports: [
    SharedModule,
    RouterModule.forChild(accountRoutes),
    CommonModule
  ],
  declarations: [AccountComponent],
  providers: []
})
export class AccountModule { }
