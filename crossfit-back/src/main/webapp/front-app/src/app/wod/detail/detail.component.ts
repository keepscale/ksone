import { Component, OnInit } from '@angular/core';
import { WodService } from '../wod.service';
import { ToolBarService } from '../../toolbar/toolbar.service';
import { Wod, WodPublication } from '../domain/wod.model';
import { ActivatedRoute, Router } from '@angular/router';
import { WodResult } from '../domain/wod-result.model';
import { Principal } from '../../shared/auth/principal.service';

@Component({
  selector: 'app-detail',
  templateUrl: './detail.component.html',
  styleUrls: ['./detail.component.scss']
})
export class DetailComponent implements OnInit {

  status: string;
  error: string;
  wod: Wod;
  wodResults: WodResult[];

  result: any = {};
  
  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private service: WodService,
    private toolbar: ToolBarService, 
    private principal: Principal) { }

  ngOnInit() {
    this.toolbar.setTitle("Mes résultat");
    this.toolbar.setOnGoBack(this.goToSearch.bind(this));

    let wodId = +this.route.snapshot.paramMap.get('id');
    if (wodId){
      this.service.get(wodId).subscribe(w=>{
          this.wod = w;
          this.service.getMyResult(wodId).subscribe(res=>{
            this.wodResults=res;
            this.wodResults.sort((r1,r2)=>new Date(r2.date).getDate() - new Date(r1.date).getDate());
          });
        },
        err=>{
          this.router.navigate(["wod"]);
        }
      );
    }
  }

  resultsEditing:WodResult[] = [];
  onEdit(editMode:boolean, resultEdited:WodResult){
    if (editMode){
      this.resultsEditing.push(resultEdited);
    }
    else{
      this.resultsEditing.splice(this.resultsEditing.indexOf(resultEdited), 1);
    }
  }

  isEditing(result:WodResult){
    return this.resultsEditing.indexOf(result) != -1;
  }

  goToSearch(){
    this.router.navigate(['wod']);
    return;
  }
}
