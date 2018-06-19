import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Wod } from '../../domain/wod.model';
import { WodResult } from '../../domain/wod-result.model';
import { WodService } from '../../wod.service';

@Component({
  selector: 'app-myresult',
  templateUrl: './myresult.component.html',
  styleUrls: ['./myresult.component.scss']
})
export class MyResultComponent implements OnInit {

  @Input("wod")
  wod: Wod;
  @Input("myresult")
  myresult: WodResult;
  @Input("editable")
  editable: boolean = true;
  
  mode:string = "READ";
  status: string;
  error: string;

  @Output() onEditMode = new EventEmitter<boolean>();

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private service: WodService) { }

  ngOnInit() {
    if (!this.myresult.id && this.editable){
      this.editMode();
    }
  }

  onSubmit(){
    this.status = "wait";
    this.service.saveOrUpdateResult(this.wod, this.myresult).subscribe(
      success=>{
          this.status = "success";
          Object.assign(this.myresult, success);
          this.readMode();
      },
      e=>{
        this.status = "error";
        this.error = e.error;
      }
    );

  }

  editMode(){
    this.mode = "EDIT";
    this.onEditMode.emit(true);
  }

  readMode(){
    this.mode = "READ";
    this.onEditMode.emit(false);
  }
}
