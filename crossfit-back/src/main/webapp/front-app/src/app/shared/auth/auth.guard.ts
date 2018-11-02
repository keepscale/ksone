import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Observable, Subject } from 'rxjs';
import { Router } from '@angular/router';
import { Principal } from '../auth/principal.service';

@Injectable()
export class AuthGuard implements CanActivate {

  private authenticationState = new Subject<boolean>();
  protected authorities: string[];

  constructor(protected router: Router, protected principal: Principal) { }

  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean> | Promise<boolean> | boolean {
    
      if (this.principal.isAuthenticated()){
        if (this.checkAuthority(this.principal)){
          return true;
        }
        else{
          console.log("Vous n'avez pas le role "+this.authorities);
          return false;
        }
      }


      this.principal.identity(true).subscribe(
        res=>this.checkAfterIdentityResolve(),err=>this.checkAfterIdentityResolve()
      );  
      
      return this.authenticationState;
  }

  checkAfterIdentityResolve(){    
    if (!this.principal.isAuthenticated()){
      this.router.navigate(['/login']);
    }
    if (this.principal.isAuthenticated()){
      if (this.checkAuthority(this.principal)){
        this.authenticationState.next(true);
        return;
      }
      else{
        console.log("Vous n'avez pas le role "+this.authorities);
      }
    }
    this.authenticationState.next(false);
  }

  checkAuthority(principal: Principal): boolean{
    return this.authorities == null || principal.hasAnyAuthority(this.authorities);
  }
}


@Injectable()
export class RoleManagerGuard extends AuthGuard{
  
  constructor(protected router: Router, protected principal: Principal) {
    super(router, principal);
    this.authorities = ['ROLE_MANAGER'];
   }
}


@Injectable()
export class RoleAdminGuard extends AuthGuard{
  
  constructor(protected router: Router, protected principal: Principal) {
    super(router, principal);
    this.authorities = ['ROLE_ADMIN'];
   }
}


@Injectable()
export class RoleCoachGuard extends AuthGuard{
  
  constructor(protected router: Router, protected principal: Principal) {
    super(router, principal);
    this.authorities = ['ROLE_COACH'];
   }
}