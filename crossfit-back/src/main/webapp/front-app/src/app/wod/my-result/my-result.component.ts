import { Component, OnInit } from '@angular/core';
import { WodService } from '../wod.service';
import { ToolBarService } from '../../toolbar/toolbar.service';
import { Wod } from '../domain/wod.model';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-my-result',
  templateUrl: './my-result.component.html',
  styleUrls: ['./my-result.component.scss']
})
export class MyResultComponent implements OnInit {

  private status: string;
  private error: string;
  private wod: Wod;

  private result: any = {};
  
  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private service: WodService,
    private toolbar: ToolBarService) { }

  ngOnInit() {
    this.service.get(this.route.snapshot.paramMap.get('id')).subscribe(w=>{
      this.wod = w;
      this.toolbar.setTitle("Mon résultat");
    },
    err=>{
      this.router.navigate(["wod"]);
    }
  )
  }

}
