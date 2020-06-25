import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { PaginateList } from 'src/app/common/paginate-list.model';
import { RunnerService } from 'src/app/common/runner.service';
import { Wod, WodPublication } from '../domain/wod.model';
import { WodSearchRequest, WodService } from '../wod.service';
import { SelectionModel } from '@angular/cdk/collections';


export interface WokDialogPickerComponentData {
  date: Date
}

@Component({
  selector: 'app-wod-dialog-picker',
  templateUrl: './wod-dialog-picker.component.html',
  styleUrls: ['./wod-dialog-picker.component.scss']
})
export class WoDDialogPickerComponent implements OnInit {

  selection = new SelectionModel<Wod>(false, []);

  constructor(
    public dialogRef: MatDialogRef<WoDDialogPickerComponent>,
    @Inject(MAT_DIALOG_DATA) public data: WokDialogPickerComponentData,
    protected runner: RunnerService<Wod>,
    protected service: WodService) {}

  ngOnInit() {
  }

  close() {
    this.dialogRef.close();
  }

  addPublication(){
    let wod = this.selection.selected[0];
    wod.publications.push(new WodPublication(this.data.date));
    
    this.runner.run(
      this.service.save(wod),
      res=>this.close()
    )
  }
}
