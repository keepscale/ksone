import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { ToolBarService } from './toolbar.service';
import { Subscription } from 'rxjs';
import { BreakpointObserver } from '@angular/cdk/layout';
import { Router } from '@angular/router';
import { Principal } from '../shared/auth/principal.service';

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

  sideNavOpened: boolean = false;

  title: string;


  showSearchToolBar: boolean;
  showSearchButton: boolean;
  searchPlaceHolder: string;
  searchText: string;

  showGoBackButton: boolean;

  firstSub: Subscription;

  menuItems: MenuItem[] = [];

  @Output() toggleSideNav = new EventEmitter<void>();

  constructor(private breakpointObserver: BreakpointObserver, private toolbar:ToolBarService, private router: Router, private principal: Principal) { }

  ngOnInit() {
    this.router.events.subscribe(event=>{
      this.showSearchToolBar = false;
      this.showSearchButton = false;
      this.showGoBackButton = false;
      this.menuItems = [];
    })
    this.toolbar.getTitle().subscribe(t=>this.title=t); 
    this.toolbar.getSearchPlaceHolder().subscribe(t=>this.searchPlaceHolder=t);
    this.toolbar.getAllowSearch().subscribe(a=>this.showSearchButton=a);
    this.toolbar.getAllowGoBack().subscribe(a=>this.showGoBackButton=a);

  }
  isAuthenticated(){
    return this.principal.isAuthenticated() || false;
  }
  onToggleSideNav(){
    this.toggleSideNav.emit();
  }
  sideNavChange(opened: boolean){
    this.sideNavOpened = opened;
  }
  search(){
    this.toolbar.search(this.searchText);
  }
  goBack(){
    this.toolbar.goBack();
  }
  hideSearch(){
    this.showSearchToolBar=false;
    this.toolbar.search(null);
  }
}
