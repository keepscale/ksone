import { Directive, Input, ViewContainerRef, TemplateRef, OnInit, OnDestroy } from '@angular/core';
import { Principal } from './principal.service';
import { tap, filter } from 'rxjs/operators';
import { Subscription } from 'rxjs';


@Directive({
  selector: '[appAuthenticated]'
})
export class IfIsAuthenticatedDirective implements OnInit, OnDestroy {

  @Input('appAuthenticated') appAuthenticated: boolean = true;
  auth$: Subscription;

  viewCreated = false;

  constructor(
    private templateRef: TemplateRef<any>,
    private viewContainer: ViewContainerRef,
    private principal: Principal) {
      
    this.auth$ = this.principal.authenticationUpdated$.subscribe(res=>{ 
      this._refresh();
    });
  }

  ngOnInit() {
    this.principal.identity().subscribe(user=>this._refresh());
  }

  ngOnDestroy() {
    if(this.auth$)
      this.auth$.unsubscribe();
  }

  _refresh(){
    if (this.appAuthenticated == this.principal.isAuthenticated()){
      if (!this.viewCreated ){
        this.viewCreated = true;
        this.viewContainer.createEmbeddedView(this.templateRef);
      }
    }
    else{
      this.viewCreated = false;
      this.viewContainer.clear();
    }
  }
}
