import { Component, OnInit, OnDestroy } from '@angular/core';
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

  isHandset$: Observable<boolean> = this.breakpointObserver.observe(Breakpoints.Handset)
  .pipe(
    map(result => result.matches)
  );

  title: string;


  showSearchToolBar: boolean;
  showSearchButton: boolean;
  searchPlaceHolder: string;
  searchText: string;

  firstSub: Subscription;

  menuItems: MenuItem[] = [];

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


    this.firstSub = this.isHandset$.subscribe(o=>{
      if (o) this.toggleSideNav();
      if (this.firstSub) this.firstSub.unsubscribe();
    })
    this.isHandset$.subscribe(o=>{
      this.toolbar.toggleSideNav();
    })
  }
  toggleSideNav(){
    this.toolbar.toggleSideNav();
  }
  search(){
    this.toolbar.search(this.searchText);
  }
  hideSearch(){
    this.showSearchToolBar=false;
    this.toolbar.search(null);
  }
}
