import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';


import { LoginComponent } from './login/login.component';
import { LogoutComponent } from './login/logout.component';
import { AuthService } from './auth/auth.service';
import { AccountService } from './auth/account.service';
import { Principal } from './auth/principal.service';
import { AuthGuard, RoleManagerGuard, RoleAdminGuard, RoleCoachGuard } from './auth/auth.guard';
import { SubmitButtonComponent } from './submit-button/submit-button.component';
import { TranslateModule } from '@ngx-translate/core';

import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatButtonModule } from '@angular/material/button';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatCardModule } from '@angular/material/card';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatChipsModule } from '@angular/material/chips';
import { NativeDateModule, MatNativeDateModule } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatDialogModule } from '@angular/material/dialog';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatListModule } from '@angular/material/list';
import { MatMenuModule } from '@angular/material/menu';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatRadioModule } from '@angular/material/radio';
import { MatSelectModule } from '@angular/material/select';
import { MatTableModule } from '@angular/material/table';
import { MatTabsModule } from '@angular/material/tabs';
import { TextCompleteModule } from './text-complete/text-complete.module';
import { StatusBarComponent } from './status-bar/status-bar.component';
import { IfHasAnyRoleDirective } from './auth/if-has-any-role.directive';
import { IfIsAuthenticatedDirective } from './auth/if-is-authenticated.directive';
import { TruncatePipe } from './pipes/truncate.pipe';
import { HttpClientModule } from '@angular/common/http';

const loginRoutes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'logout', component: LogoutComponent },
];

@NgModule({
  imports: [
    RouterModule.forChild(loginRoutes),
    CommonModule, HttpClientModule, FormsModule, ReactiveFormsModule, RouterModule,
    TranslateModule,
    MatButtonModule,
    MatIconModule,
    MatListModule, MatFormFieldModule, MatInputModule, MatCheckboxModule, MatProgressBarModule, MatRadioModule, MatChipsModule, MatAutocompleteModule,
    ReactiveFormsModule, MatSelectModule, TextCompleteModule, MatMenuModule, MatCardModule, MatGridListModule, MatDatepickerModule, MatNativeDateModule,
    MatExpansionModule, MatTabsModule, MatTableModule, MatButtonToggleModule, MatDialogModule, MatPaginatorModule
  ],
  exports: [TranslateModule,
    CommonModule, HttpClientModule, FormsModule, ReactiveFormsModule, RouterModule, SubmitButtonComponent, MatButtonModule,
    MatIconModule,
    MatListModule, MatFormFieldModule, MatInputModule, MatCheckboxModule,
    MatProgressBarModule, MatRadioModule, MatChipsModule, MatAutocompleteModule, ReactiveFormsModule, MatSelectModule, TextCompleteModule,
    MatMenuModule, MatCardModule, MatGridListModule, MatDatepickerModule, MatNativeDateModule,
    MatExpansionModule, MatTabsModule, MatTableModule, MatButtonToggleModule, MatDialogModule,
    StatusBarComponent, MatPaginatorModule, IfHasAnyRoleDirective, IfIsAuthenticatedDirective, TruncatePipe
  ],
  providers:[
	  AuthGuard,
	  RoleManagerGuard,
    RoleAdminGuard,
    RoleCoachGuard,
	  AccountService,
	  AuthService,
	  Principal,
  ],
  declarations: [LoginComponent, LogoutComponent, SubmitButtonComponent, StatusBarComponent, 
    IfIsAuthenticatedDirective, IfHasAnyRoleDirective, TruncatePipe]
})
export class SharedModule { }
