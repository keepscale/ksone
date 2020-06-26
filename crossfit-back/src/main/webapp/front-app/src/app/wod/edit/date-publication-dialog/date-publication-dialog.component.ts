import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { WodPublication } from '../../domain/wod.model';

@Component({
  templateUrl: './date-publication-dialog.component.html'
})
export class DatePublicationDialogComponent {

  constructor(
    public dialogRef: MatDialogRef<DatePublicationDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public pub: WodPublication) {
    }

  onNoClick(): void {
    this.dialogRef.close();
  }

  addPublication(){
    this.dialogRef.close(this.pub);
  }

}
