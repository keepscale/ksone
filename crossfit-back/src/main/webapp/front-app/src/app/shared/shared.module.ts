import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { HttpModule } from '@angular/http';
import { RouterModule, Routes } from '@angular/router';


import { LoginComponent } from './login/login.component';
import { LogoutComponent } from './login/logout.component';
import { AuthService } from './auth/auth.service';
import { AccountService } from './auth/account.service';
import { Principal } from './auth/principal.service';
import { AuthGuard, RoleManagerGuard, RoleAdminGuard } from './auth/auth.guard';
import { SubmitButtonComponent } from './submit-button/submit-button.component';
import { TranslateModule } from '@ngx-translate/core';

import { MatButtonModule, MatIconModule, MatListModule, MatFormFieldModule, MatInputModule, MatCheckboxModule, 
  MatProgressBarModule, MatRadioModule, MatChipsModule, MatAutocompleteModule, MatSelectModule, MatMenuModule, MatExpansionModule, MatCardModule, MatGridListModule, MatDatepickerModule, NativeDateModule, MatNativeDateModule } from '@angular/material';
import { TextCompleteModule } from './text-complete/text-complete.module';
  

const loginRoutes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'logout', component: LogoutComponent },
];

@NgModule({
  imports: [
    RouterModule.forChild(loginRoutes),
    CommonModule, HttpModule, FormsModule, ReactiveFormsModule, RouterModule,
    TranslateModule,
    MatButtonModule,
    MatIconModule,
    MatListModule, MatFormFieldModule, MatInputModule, MatCheckboxModule, MatProgressBarModule, MatRadioModule, MatChipsModule, MatAutocompleteModule,
    ReactiveFormsModule, MatSelectModule, TextCompleteModule, MatMenuModule, MatCardModule, MatGridListModule, MatDatepickerModule, MatNativeDateModule,
    MatExpansionModule
  ],
  exports: [TranslateModule,
    CommonModule, HttpModule, FormsModule, ReactiveFormsModule, RouterModule, SubmitButtonComponent, MatButtonModule,
    MatIconModule,
    MatListModule, MatFormFieldModule, MatInputModule, MatCheckboxModule,
    MatProgressBarModule, MatRadioModule, MatChipsModule, MatAutocompleteModule, ReactiveFormsModule, MatSelectModule, TextCompleteModule,
    MatMenuModule, MatCardModule, MatGridListModule, MatDatepickerModule, MatNativeDateModule,
    MatExpansionModule
  ],
  providers:[
	  AuthGuard,
	  RoleManagerGuard,
	  RoleAdminGuard,
	  AccountService,
	  AuthService,
	  Principal,
  ],
  declarations: [LoginComponent, LogoutComponent, SubmitButtonComponent]
})
export class SharedModule { }
