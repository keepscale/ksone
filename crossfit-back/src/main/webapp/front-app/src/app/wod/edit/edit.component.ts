import { Component, OnInit, ViewChild, Input } from '@angular/core';
import { Wod, WodPublication } from '../domain/wod.model';
import { WodService } from '../wod.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Location } from '@angular/common';
import { Movement } from '../domain/movement.model';
import { FormControl } from '@angular/forms';
import { MatAutocompleteSelectedEvent, MatAutocomplete, MatDatepickerInputEvent } from '@angular/material';
import { OptionSelectedEvent } from '../../shared/text-complete/text-complete.directive';
import { Equipment } from '../domain/equipment.model';
import { Taggable } from '../domain/taggable.model';
import { ToolBarService } from '../../toolbar/toolbar.service';

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
      let goBack = this.route.snapshot.paramMap.get("goBack");
      let path = goBack == null ? 'wod' : goBack;
      let adate = this.wod.publications.length == 0 ? null : this.wod.publications[0].date.toISOString().slice(0,10);
      this.router.navigate([path, {'date': adate}])
    }
    );
    if (!id){
      this.toolbar.setTitle("Proposer un WOD");
      this.wod = new Wod();
      this.wod.category = "CUSTOM";
      this.wod.score = "FOR_TIME";
      this.wod.name = "WOD";
      let date = this.route.snapshot.paramMap.get("date");
      this.addPublicationDate(date == null ? new Date() : new Date(date));
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
    console.log( typeof event.option);
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
        this.toolbar.goBack();
      },
      e=>{
        this.status = "error";
        this.error = e.error;
      }
    );
    
  }

}
