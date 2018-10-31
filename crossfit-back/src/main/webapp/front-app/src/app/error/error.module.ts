import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ErrorComponent } from './error.component';
import { MatSnackBarModule } from '@angular/material';

@NgModule({
  imports: [
    CommonModule,
    MatSnackBarModule
  ],
  exports: [
    MatSnackBarModule
  ],
  declarations: [ErrorComponent]
})
export class ErrorModule { }
