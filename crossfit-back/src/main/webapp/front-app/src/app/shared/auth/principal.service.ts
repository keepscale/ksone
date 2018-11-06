import { Injectable } from '@angular/core';
import { Observable, Subject, of } from 'rxjs';
import { AccountService } from './account.service';
import { User } from '../domain/user.model';
import { map, catchError } from 'rxjs/operators';

@Injectable({
    providedIn: 'root'
})
export class Principal {
    private userIdentity: User;
    private authenticated = false;
    private authenticationState = new Subject<User>();
    authenticationUpdated$ = this.authenticationState.asObservable();

    constructor(
        private account: AccountService
    ) {}

    hasAnyAuthority(authorities: string[]|string): boolean {
        let rolesToHave =  typeof authorities === 'string' ? [authorities] : authorities;
        if (!this.userIdentity || !this.userIdentity.roles) {
            //console.log("hasAnyAuthority("+authorities+") ? false (not connected: "+JSON.stringify(this.userIdentity)+")");
            return false;
        }

        for (let i = 0; i < rolesToHave.length; i++) {
            if (this.userIdentity.roles.indexOf(rolesToHave[i]) !== -1) {
                return true;
            }
        }

        return false;
    }

    identity(force?: boolean): Observable<User>{
        if (force === true) {
            this.userIdentity = undefined;
            this.authenticated = false;
        }

        // check and see if we have retrieved the userIdentity data from the server.
        // if we have, reuse it by immediately resolving
        if (this.userIdentity) {
            //this.authenticationState.next(this.userIdentity);
            return of(this.userIdentity);
        }
        else{
            // retrieve the userIdentity data from the server, update the identity object, and then resolve.
            return this.account.get().pipe(map(
                user=>{
                    if (user) {
                        this.userIdentity = user;
                        this.authenticated = true;
                    } else {
                        this.userIdentity = null;
                        this.authenticated = false;
                    }
                    this.authenticationState.next(this.userIdentity);
                    return user;
                }
            ))
            .pipe(catchError(error=>of(this.userIdentity)));
        }
    }

    isAuthenticated(): boolean {
        return this.authenticated;
    }

    isIdentityResolved(): boolean {
        return this.userIdentity !== undefined;
    }
    logout(){
        this.userIdentity = undefined;
        this.authenticated = false;
        this.authenticationState.next(this.userIdentity);
    }
}