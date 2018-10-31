import { Component, OnInit } from '@angular/core';
import { AbstractComponent } from 'src/app/common/abstract.component';
import { Wod } from '../domain/wod.model';
import { ToolBarService } from 'src/app/toolbar/toolbar.service';
import { RunnerService } from 'src/app/common/runner.service';
import { Principal } from 'src/app/shared/auth/principal.service';
import { WodService, WodSearchRequest } from '../wod.service';
import { Router, ActivatedRoute, Params } from '@angular/router';
import * as moment from 'moment';

@Component({
  selector: 'app-list',
  templateUrl: './list.component.html',
  styleUrls: ['./list.component.scss']
})
export class ListComponent  extends AbstractComponent implements OnInit {

  wods:Wod[] = [];
  search: WodSearchRequest = new WodSearchRequest();
  displayedColumns: string[] = ['id', 'name', 'category', 'score'];


  constructor(
    protected toolbar: ToolBarService, 
    protected runner: RunnerService<any>, 
    protected principal: Principal,
    private wodService: WodService,
    private route: ActivatedRoute,
    private router: Router) {
      super(toolbar, runner, principal);
  }

  ngOnInit() {
    this.title = "Liste des wods";
    if (this.principal.hasAnyAuthority(['ROLE_ADMIN'])){
      this.toolbar.addMenuItem(this.importWods.bind(this), "cloud_upload", "Importer les wods");
    }
    
    this.route.queryParams.subscribe(params=>{
      this.search = new WodSearchRequest();
      this.search.query = params["query"];
      this.search.start = params["start"] ? moment(params["start"]).toDate() : null;
      this.search.end = params["end"] ? moment(params["end"]).toDate() : null;
      
      this.runner.run(
        this.wodService.findAll(this.search),
        result=>this.searchResult(result));
    })   
  }

  
  private updateQueryParam(){
    const queryParams: Params = Object.assign({}, this.route.snapshot.queryParams);
    queryParams['query'] = this.search.query;
    queryParams['start'] = this.search.start ? moment(this.search.start).format("Y-MM-D") : null;
    queryParams['end'] = this.search.end ? moment(this.search.end).format("Y-MM-D") : null;
    this.router.navigate([], { relativeTo: this.route, queryParams: queryParams });
  }

  clearSearch(){
    this.search = new WodSearchRequest();
    this.updateQueryParam();
  }

  onSearch(){
    this.updateQueryParam();
  }
  
  searchResult(result: Wod[]){
    this.wods = result;
  }

  importWods(){
    this.runner.run(
      this.wodService.importWods(),
      res=>this.clearSearch()
    )
  }
}
