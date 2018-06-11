import { Component, OnInit } from '@angular/core';
import { WodService } from '../wod.service';
import { ToolBarService } from '../../toolbar/toolbar.service';
import { Wod, WodPublication } from '../domain/wod.model';
import { ActivatedRoute, Router } from '@angular/router';
import { WodResult } from '../domain/wod-result.model';
import { WodDetailService } from './wod.detail.service';

@Component({
  selector: 'app-detail',
  templateUrl: './detail.component.html',
  styleUrls: ['./detail.component.scss']
})
export class DetailComponent implements OnInit {

  private status: string;
  private error: string;
  private wod: Wod;
  private wodResults: WodResult[];

  private result: any = {};
  
  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private service: WodService,
    private wodDetailService: WodDetailService,
    private toolbar: ToolBarService) { }

  ngOnInit() {
    this.toolbar.setTitle("Mes résultat");
    let wodId = +this.route.snapshot.paramMap.get('id');
    if (wodId){
      this.service.get(wodId).subscribe(w=>{
          this.wod = w;
          this.wodDetailService.wod = this.wod;
          this.service.getMyResult(wodId).subscribe(res=>{
            this.wodResults=res;
            this.wod.publications.forEach(publi => {              
              if (this.wodResults.filter(r=>r.date==publi.date).length===0){
                let result = new WodResult();
                result.date = publi.date;
                this.wodResults.push(result);
                return result;
              }
            });
            this.wodResults.sort((r1,r2)=>new Date(r1.date).getDate() - new Date(r2.date).getDate());
            this.wodDetailService.wodResults = this.wodResults;
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
}
