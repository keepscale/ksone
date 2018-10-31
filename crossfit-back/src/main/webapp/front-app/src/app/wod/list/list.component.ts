import { Component, OnInit } from '@angular/core';
import { AbstractComponent } from 'src/app/common/abstract.component';
import { Wod } from '../domain/wod.model';
import { ToolBarService } from 'src/app/toolbar/toolbar.service';
import { RunnerService } from 'src/app/common/runner.service';
import { Principal } from 'src/app/shared/auth/principal.service';
import { WodService, WodSearchRequest } from '../wod.service';
import { Router, ActivatedRoute, Params } from '@angular/router';
import * as moment from 'moment';
import { PaginateList } from 'src/app/common/paginate-list.model';
import { PageEvent } from '@angular/material';

@Component({
  selector: 'app-list',
  templateUrl: './list.component.html',
  styleUrls: ['./list.component.scss']
})
export class ListComponent  extends AbstractComponent implements OnInit {

  list:PaginateList<Wod>;
  displayedColumns: string[] = ['id', 'name', 'category', 'score'];

  query: string;

  defaultPageSize: number = 10;

  constructor(
    protected toolbar: ToolBarService, 
    protected runner: RunnerService<any>, 
    protected principal: Principal,
    protected wodService: WodService,
    protected route: ActivatedRoute,
    protected router: Router) {
      super(toolbar, runner, principal);
  }

  ngOnInit() {
    this.title = "Liste des wods";
    if (this.principal.hasAnyAuthority(['ROLE_ADMIN'])){
      this.toolbar.addMenuItem(this.importWods.bind(this), "cloud_upload", "Importer les wods");
    }
    
    this.route.queryParams.subscribe(params=>{
      let search = new WodSearchRequest();
      search.query = params["query"];
      search.pageEvent = new PageEvent();
      search.pageEvent.pageIndex = params["pageIndex"] ? params["pageIndex"] : 0;
      search.pageEvent.pageSize = params["pageSize"] ? params["pageSize"] : this.defaultPageSize;
      
      this.runner.run(
        this.wodService.findAll(search),
        result=>this.searchResult(result));             
    })   
  }

  
  submitSearchForm(){
    const queryParams: Params = Object.assign({}, this.route.snapshot.queryParams);
    queryParams['query'] = this.query;
    queryParams['pageIndex'] = null;
    queryParams['pageSize'] = null;
    this.router.navigate([], { relativeTo: this.route, queryParams: queryParams });
  }
  
  paginate(pageEvent: PageEvent){
    const queryParams: Params = Object.assign({}, this.route.snapshot.queryParams);
    queryParams['pageIndex'] = pageEvent.pageIndex;
    queryParams['pageSize'] = pageEvent.pageSize;
    this.router.navigate([], { relativeTo: this.route, queryParams: queryParams });
  }

  clearSearch(){
    this.router.navigate([], { relativeTo: this.route, queryParams: {} });
  }

  searchResult(result: PaginateList<Wod>){
    this.list = result;
  }

  importWods(){
    this.runner.run(
      this.wodService.importWods(),
      res=>this.clearSearch()
    )
  }
}
