import { Component, OnInit, Input } from '@angular/core';
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
import { SelectionModel } from '@angular/cdk/collections';

@Component({
  selector: 'app-wod-list',
  templateUrl: './list.component.html',
  styleUrls: ['./list.component.scss']
})
export class ListComponent  extends AbstractComponent implements OnInit {

  list:PaginateList<Wod>;

  @Input("columns")
  displayedColumns: string[] = [/*'id', */'name', 'description', 'publications', 'category', 'score'];

  search = new WodSearchRequest();

  defaultPageSize: number = 10;

  
  @Input("selection")
  selection;

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

    this.toolbar.addMenuItem('ROLE_ADMIN', this._importWods.bind(this), "cloud_upload", "Importer les wods");

    this.route.queryParams.subscribe(params=>{
      this.search.query = params["query"];
      this.search.pageIndex = params["pageIndex"] ? params["pageIndex"] : 0;
      this.search.pageSize = params["pageSize"] ? params["pageSize"] : this.defaultPageSize;
      this._refreshData();   
    });
  }

  _refreshData(){
    this.runner.run(
      this.wodService.findAll(this.search),
      result=>this.searchResult(result));     
  }
  _importWods(){
    this.runner.run(
      this.wodService.importWods(),
      result=>this.importWodsDone()
    )
  }

  
  submitSearchForm(){
    const queryParams: Params = Object.assign({}, this.route.snapshot.queryParams);
    if (this.search.query === queryParams['query']){
      this._refreshData();
    }
    else{
      queryParams['refresh'] = null;
    }

    queryParams['query'] = this.search.query;
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

  importWodsDone(){
    this.router.navigate([], { relativeTo: this.route, queryParams: {'import':'done'} });
  }

  searchResult(result: PaginateList<Wod>){
    this.list = result;
  }

  /** Whether the number of selected elements matches the total number of rows. */
  isAllSelected() {
    const numSelected = this.selection.selected.length;
    const numRows = this.list !=null ? this.list.length : 0;
    return numSelected == numRows;
  }

  /** Selects all rows if they are not all selected; otherwise clear selection. */
  masterToggle() {
    this.isAllSelected() ?
        this.selection.clear() :
        (this.list !=null ? this.list.results : []).forEach(row => this.selection.select(row));
  }
}
