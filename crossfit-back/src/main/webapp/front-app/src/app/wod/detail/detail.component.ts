import { Component, OnInit, ViewChild, Input } from '@angular/core';
import { Wod } from '../domain/wod.model';
import { WodService } from '../wod.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Location } from '@angular/common';
import { Movement } from '../domain/movement.model';
import { FormControl } from '@angular/forms';
import { MatAutocompleteSelectedEvent, MatAutocomplete } from '@angular/material';
import { OptionSelectedEvent } from '../../shared/text-complete/text-complete.directive';
import { Equipment } from '../domain/equipment.model';
import { Taggable } from '../domain/taggable.model';

@Component({
  selector: 'app-detail',
  templateUrl: './detail.component.html',
  styleUrls: ['./detail.component.scss']
})
export class DetailComponent implements OnInit {
  
  
  private availableWodScore: String[];
  private availableWodCategories: String[];
  private availableMovements: Movement[] = [];
  private availableEquipments: Equipment[] = [];
  private status: string;
  private error: string;
  private wod: Wod;

  constructor(
    private service: WodService,
    private route: ActivatedRoute,
    private router: Router,
    private location: Location) { }

  ngOnInit() {
    this.service.getCategories().subscribe(res=>this.availableWodCategories=res);
    this.service.getScores().subscribe(res=>this.availableWodScore=res);
    this.service.getMovements().subscribe(res=>this.availableMovements=res);
    this.service.getEquipments().subscribe(res=>this.availableEquipments=res);
    let id = this.route.snapshot.paramMap.get('id');
    if (!id){
      this.wod = new Wod();
    }
    else{
      this.service.get(id).subscribe(w=>{
          this.wod = w;
        },
        err=>{
          this.router.navigate(["wod"]);
        }
      )
    }
  }

  completeFilter(searchText: string) {
    console.log(searchText);
    if (searchText.length === 0){
      return [];
    }
    return (this.availableMovements).map(m=>m as Taggable).concat(this.availableEquipments.map(m=>m as Taggable)).filter(m=>{
        return searchText.length === 1 ?
        m.fullname.toLowerCase().startsWith(searchText.toLowerCase()) : m.fullname.toLowerCase().includes(searchText.toLowerCase());
      });
  }

  displayOptionComplete(option: Taggable) {
    return option.fullname;
  }

  onSelectOption(event: OptionSelectedEvent){
    console.log( typeof event.option);
    if (event.option.type){
      this.wod.movements.push(event.option);
    }
    else{
      this.wod.equipments.push(event.option);
    }
  }

  onSubmit() {
    this.status = "wait";
    this.service.save(this.wod).subscribe(
      success=>{
        this.status = "success";
        setTimeout (() => {
          this.router.navigate(["wod"]);
        }, 1000)
      },
      e=>{
        this.status = "error";
        this.error = e.error;
      }
    );
    
  }

}
