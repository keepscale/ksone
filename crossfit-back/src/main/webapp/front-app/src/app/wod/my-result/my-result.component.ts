import { Component, OnInit } from '@angular/core';
import { WodService } from '../wod.service';
import { ToolBarService } from '../../toolbar/toolbar.service';
import { Wod, WodPublication } from '../domain/wod.model';
import { ActivatedRoute, Router } from '@angular/router';
import { WodResult } from '../domain/wod-result.model';

@Component({
  selector: 'app-my-result',
  templateUrl: './my-result.component.html',
  styleUrls: ['./my-result.component.scss']
})
export class MyResultComponent implements OnInit {

  private status: string;
  private error: string;
  private wod: Wod;
  private wodResults: WodResult[];

  private result: any = {};
  
  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private service: WodService,
    private toolbar: ToolBarService) { }

  ngOnInit() {
    this.toolbar.setTitle("Mes résultat");
    let wodId = +this.route.snapshot.paramMap.get('id');
    if (wodId){
      this.service.get(wodId).subscribe(w=>{
          this.wod = w;
          this.service.getMyResult(wodId).subscribe(res=>{
            this.wodResults=res;
            array.forEach(element => {
              
            });
          });
        },
        err=>{
          this.router.navigate(["wod"]);
        }
      );
    }


  }

  findResultForPublication(publication: WodPublication){
    let res = this.wodResults.filter(r=>r.date==publication.date);
    if (!res){
      let result = new WodResult();
      result.date = publication.date;
      this.wodResults.push(result);
      return result;
    }
    return res[0];
  }

}
