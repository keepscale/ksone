import { Component, OnInit, ViewChild, Input } from '@angular/core';
import { Wod, WodPublication } from '../domain/wod.model';
import { WodService } from '../wod.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Movement } from '../domain/movement.model';
import {  MatDatepickerInputEvent, MatDialog } from '@angular/material';
import { OptionSelectedEvent } from '../../shared/text-complete/text-complete.directive';
import { Equipment } from '../domain/equipment.model';
import { Taggable } from '../domain/taggable.model';
import { ToolBarService } from '../../toolbar/toolbar.service';
import * as moment from 'moment';
import { DatePublicationDialogComponent } from './date-publication-dialog/date-publication-dialog.component';
import { DetailComponent } from '../detail/detail.component';
import { Principal } from 'src/app/shared/auth/principal.service';
import { RunnerService } from 'src/app/common/runner.service';
import { Observable, of } from 'rxjs';


@Component({
  selector: 'app-edit',
  templateUrl: './edit.component.html',
  styleUrls: ['./edit.component.scss']
})
export class EditComponent extends DetailComponent {
  
  
  availableWodScore: String[];
  availableWodCategories: String[];
  availableMovements: Movement[] = [];
  availableEquipments: Equipment[] = [];

  constructor(
    protected toolbar: ToolBarService, 
    protected runner: RunnerService<Wod>, 
    protected principal: Principal,
    protected route: ActivatedRoute,
    protected router: Router,
    protected service: WodService,
    private dialog: MatDialog) { 
      super(toolbar, runner, principal, route, router, service);
    }

  ngOnInit() {
    super.ngOnInit();
  }

  loadWod(id: number): Observable<any>[]{
    let observables:Observable<any>[] = [];
    if (!id){

      let dateParam = this.route.snapshot.queryParamMap.get("date");
      let date = dateParam == null ? new Date() : new Date(dateParam);

      this.title = "Plannifier un WOD pour le " + moment(date).format("DD/MM/YYYY");

      let w = new Wod();
      w.category = "CUSTOM";
      w.score = "FOR_TIME";
      w.name = "WOD";
      w.publications.push(new WodPublication(date));
      observables[0] = of(w);
    }
    else{
      this.title = "Modfier un WOD";
      observables[0] = this.service.get(id);
    }

    observables[1] = this.service.getCategories();
    observables[2] = this.service.getScores();
    observables[3] = this.service.getMovements();
    observables[4] = this.service.getEquipments();
    return observables;
  }

  wodLoaded(result: any[]){
    super.wodLoaded(result);
    
    this.availableWodCategories = result[1];
    this.availableWodScore = result[2];
    this.availableMovements = result[3];
    this.availableEquipments = result[4];
  }

  showAddPublicationDate(){
    const dialogRef = this.dialog.open(DatePublicationDialogComponent, {
      width: '250px',
      data: new WodPublication()
    });

    dialogRef.afterClosed().subscribe(result => {      
      if(result != null){
        this.wod.publications.push(result);
      }
    });
  }
  

  onSelectPublicationDate(event: MatDatepickerInputEvent<Date>){
    this.addPublicationDate(event.value);
  }
  addPublicationDate(date: Date){    
    this.wod.publications.push(new WodPublication(date));
  }
  removePublicationAtIndex(index: number){
    this.wod.publications.splice(index, 1);
  }

  completeFilter(searchText: string) {
    console.log(searchText);
    if (searchText.length === 0){
      return [];
    }
    var data: Taggable[] = (this.availableMovements as Taggable[])/*.concat(this.availableEquipments)*/;
    return data
      .filter(m=>
              m.fullname.toLowerCase().includes(searchText.toLowerCase())
      ).sort((a,b)=>{
        return a.fullname.toLowerCase().startsWith(searchText.toLowerCase()) ? 
            -1 : b.fullname.toLowerCase().startsWith(searchText.toLowerCase()) ? 1 :
            a.fullname.toLowerCase().localeCompare(b.fullname.toLowerCase());
      });
  }

  displayOptionComplete(option: Taggable) {
    return option.fullname;
  }

  onSelectOption(event: OptionSelectedEvent){
    if (event.option.type){
      this.wod.taggedMovements.push(event.option);
    }
    else{
      this.wod.taggedEquipments.push(event.option);
    }
  }

  removeMovement(m: Movement){
    this.wod.taggedMovements.splice(this.wod.taggedMovements.indexOf(m),1);
  }

  onSubmit() {
    this.runner.run(
      this.service.save(this.wod),
      res=>this.toolbar.goBack()
    )
  }

}
