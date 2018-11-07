import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { PaginateList } from 'src/app/common/paginate-list.model';
import { Wod } from '../domain/wod.model';
import { WodSearchRequest } from '../wod.service';


export interface WokDialogPickerComponentData {
  date: Date
}

@Component({
  selector: 'app-wod-dialog-picker',
  templateUrl: './wod-dialog-picker.component.html',
  styleUrls: ['./wod-dialog-picker.component.scss']
})
export class WoDDialogPickerComponent implements OnInit {

  list:PaginateList<Wod>;
  search = new WodSearchRequest();
  
  constructor(
    public dialogRef: MatDialogRef<WoDDialogPickerComponent>,
    @Inject(MAT_DIALOG_DATA) public data: WokDialogPickerComponentData) {}

  ngOnInit() {
  }

}
