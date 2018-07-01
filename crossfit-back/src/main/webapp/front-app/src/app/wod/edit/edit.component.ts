import { Component, OnInit, ViewChild, Input } from '@angular/core';
import { Wod, WodPublication } from '../domain/wod.model';
import { WodService } from '../wod.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Location } from '@angular/common';
import { Movement } from '../domain/movement.model';
import { MatAutocompleteSelectedEvent, MatAutocomplete, MatDatepickerInputEvent } from '@angular/material';
import { OptionSelectedEvent } from '../../shared/text-complete/text-complete.directive';
import { Equipment } from '../domain/equipment.model';
import { Taggable } from '../domain/taggable.model';
import { ToolBarService } from '../../toolbar/toolbar.service';
import * as moment from 'moment';

@Component({
  selector: 'app-edit',
  templateUrl: './edit.component.html',
  styleUrls: ['./edit.component.scss']
})
export class EditComponent implements OnInit {
  
  
  availableWodScore: String[];
  availableWodCategories: String[];
  availableMovements: Movement[] = [];
  availableEquipments: Equipment[] = [];
  status: string;
  error: string;
  wod: Wod;

  constructor(
    private service: WodService,
    private route: ActivatedRoute,
    private router: Router,
    private location: Location, 
    private toolbar: ToolBarService) { }

  ngOnInit() {
    this.service.getCategories().subscribe(res=>this.availableWodCategories=res);
    this.service.getScores().subscribe(res=>this.availableWodScore=res);
    this.service.getMovements().subscribe(res=>this.availableMovements=res);
    this.service.getEquipments().subscribe(res=>this.availableEquipments=res);
    let id = this.route.snapshot.paramMap.get('id');
    this.toolbar.setOnGoBack(()=>{
      this.router.navigate(['wod', this.wod.id, 'detail']);
      }
    );
    if (!id){
      let dateParam = this.route.snapshot.queryParamMap.get("date");
      let date = dateParam == null ? new Date() : new Date(dateParam);

      this.toolbar.setTitle("Plannifier un WOD pour le " + moment(date).format("dddd DD/MM/YYYY"));
      this.wod = new Wod();
      this.wod.category = "CUSTOM";
      this.wod.score = "FOR_TIME";
      this.wod.name = "WOD";
      this.addPublicationDate(date);
    }
    else{
      this.service.get(id).subscribe(w=>{
          this.wod = w;
          this.toolbar.setTitle("Modifier un WOD");
        },
        err=>{
          this.router.navigate(["wod"]);
        }
      )
    }
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
    this.status = "wait";
    this.service.save(this.wod).subscribe(
      success=>{
        this.status = "success";
        this.wod.id = success.id;
        this.toolbar.goBack();
      },
      e=>{
        this.status = "error";
        this.error = e.error;
      }
    );
    
  }

}
