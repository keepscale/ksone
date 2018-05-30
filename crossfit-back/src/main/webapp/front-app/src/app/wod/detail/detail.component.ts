import { Component, OnInit, ViewChild, Input } from '@angular/core';
import { Wod } from '../domain/wod.model';
import { WodService } from '../wod.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Location } from '@angular/common';
import { Movement } from '../domain/movement.model';
import { FormControl } from '@angular/forms';
import { MatAutocompleteSelectedEvent, MatAutocomplete } from '@angular/material';

@Component({
  selector: 'app-detail',
  templateUrl: './detail.component.html',
  styleUrls: ['./detail.component.scss']
})
export class DetailComponent implements OnInit {
  
  
  private availableWodScore: String[];
  private availableWodCategories: String[];
  private availableMovements: Movement[];
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
    return this.availableMovements.filter(mov =>
      mov.fullname.toLowerCase().includes(searchText.toLowerCase())
    );
  }

  displayOptionComplete(option: any) {
    return option.fullname;
  }

  onSelectOption(option: any){
    console.log(option);
    if (option instanceof Movement){
      this.wod.movements.push(option);
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
