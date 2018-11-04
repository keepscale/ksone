import { Directive, ViewContainerRef, TemplateRef, Input, OnDestroy, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { tap, filter } from 'rxjs/operators';
import { Principal } from './principal.service';

@Directive({
  selector: '[hasAnyRole]'
})
export class IfHasAnyRoleDirective implements OnInit, OnDestroy {

  auth$: Subscription;

  @Input('hasAnyRole') roles: string[]|string;

  constructor(
    private templateRef: TemplateRef<any>,
    private viewContainer: ViewContainerRef,
    private principal: Principal) {
      
      this.auth$ = this.principal.authenticationUpdated$.subscribe(res=>{
        this._refresh();
      });
  }
  
  ngOnInit() {
    this.principal.identity().subscribe(user=>{}, error=>{this._refresh()});
  }
  
  ngOnDestroy() {
    if(this.auth$)
      this.auth$.unsubscribe();
  }

  _refresh(){
    if (this.principal.hasAnyAuthority(this.roles)){
      this.viewContainer.createEmbeddedView(this.templateRef);
    }
    else{
      this.viewContainer.clear();
    }
  }
}
