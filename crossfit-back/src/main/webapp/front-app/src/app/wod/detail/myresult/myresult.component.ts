import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Wod } from '../../domain/wod.model';
import { WodResult } from '../../domain/wod-result.model';
import { WodService } from '../../wod.service';
import { Principal } from '../../../shared/auth/principal.service';

@Component({
  selector: 'app-myresult',
  templateUrl: './myresult.component.html',
  styleUrls: ['./myresult.component.scss']
})
export class MyResultComponent implements OnInit {

  @Input("wod")
  wod: Wod;
  @Input("result")
  result: WodResult;

  @Input("defaultDate")
  defaultDate: Date;

  @Input("editable")
  editable: boolean = true;
  @Input("removable")
  removable: boolean = false;
  
  
  editedResult: WodResult;
  mode:string = "READ";
  status: string;
  error: string;

  @Output() onDelete = new EventEmitter<void>();
  @Output() onSaved = new EventEmitter<WodResult>();
  @Output() onEditMode = new EventEmitter<boolean>();

  constructor(
    private principal: Principal,
    private service: WodService) { }

  ngOnInit() {
  }

  delete(){
    this.status = "wait";
    this.service.deleteResult(this.wod, this.result).subscribe(
      success=>{
          this.status = "success";
          this.result = null;
          this.readMode();
          this.onDelete.emit();
      },
      e=>{
        this.status = "error";
        this.error = e.error;
    });
  }
  submit(){
    this.status = "wait";
    this.service.saveOrUpdateResult(this.wod, this.editedResult).subscribe(
      success=>{
          this.status = "success";
          this.readMode();
          this.onSaved.emit(success);
      },
      e=>{
        this.status = "error";
        this.error = e.error;
      }
    );

  }

  editMode(){
    this.editedResult = new WodResult();
    if (this.result == null){
      this.editedResult.category = "RX";
      this.principal.identity().subscribe(principal=>this.editedResult.title=principal.title);
      this.editedResult.date = this.defaultDate;
    }
    else{
      Object.assign(this.editedResult, this.result);
    }

    this.mode = "EDIT";
    this.onEditMode.emit(true);
  }

  readMode(){
    this.mode = "READ";
    this.onEditMode.emit(false);
  }
}
