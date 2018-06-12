import { Component, OnInit, OnDestroy, Output, EventEmitter } from '@angular/core';
import { ToolBarService } from './toolbar.service';
import { Subscription, Observable } from 'rxjs';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { map } from 'rxjs/operators';
import { Router, RouterState, ActivatedRoute } from '@angular/router';

export class MenuItem{

  action: Function;
  icon: string;
  text: string;
}

@Component({
  selector: 'app-toolbar',
  templateUrl: './toolbar.component.html',
  styleUrls: ['./toolbar.component.scss']
})
export class ToolbarComponent implements OnInit {

  title: string;


  showSearchToolBar: boolean;
  showSearchButton: boolean;
  searchPlaceHolder: string;
  searchText: string;

  firstSub: Subscription;

  menuItems: MenuItem[] = [];

  @Output() toggleSideNav = new EventEmitter<void>();

  constructor(private breakpointObserver: BreakpointObserver, private toolbar:ToolBarService, private router: Router) { }

  ngOnInit() {
    this.router.events.subscribe(event=>{
      this.showSearchToolBar = false;
      this.showSearchButton = false;
      this.menuItems = [];
    })
    this.toolbar.getTitle().subscribe(t=>this.title=t); 
    this.toolbar.getSearchPlaceHolder().subscribe(t=>this.searchPlaceHolder=t);
    this.toolbar.getAllowSearch().subscribe(a=>this.showSearchButton=a)


  }
  onToggleSideNav(){
    this.toggleSideNav.emit();
  }
  search(){
    this.toolbar.search(this.searchText);
  }
  hideSearch(){
    this.showSearchToolBar=false;
    this.toolbar.search(null);
  }
}
