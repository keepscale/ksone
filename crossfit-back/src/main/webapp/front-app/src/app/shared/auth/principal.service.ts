import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { AccountService } from './account.service';
import { User } from '../domain/user.model';

@Injectable()
export class Principal {
    private userIdentity: User;
    private authenticated = false;
    private authenticationState = new Subject<any>();

    constructor(
        private account: AccountService
    ) {}

    hasAnyAuthority(authorities: string[]): boolean {
        if (!this.authenticated || !this.userIdentity || !this.userIdentity.roles) {
            //console.log("hasAnyAuthority("+authorities+") ? false (not connected: "+JSON.stringify(this.userIdentity)+")");
            return false;
        }

        for (let i = 0; i < authorities.length; i++) {
            if (this.userIdentity.roles.indexOf(authorities[i]) !== -1) {
                //console.log("hasAnyAuthority("+authorities+") ? true");
                return true;
            }
        }

        //console.log("hasAnyAuthority("+authorities+") ? false (actual authorities="+JSON.stringify(this.userIdentity.roles)+")");
        return false;
    }

    identity(force?: boolean): Subject<User>{
        if (force === true) {
            this.userIdentity = undefined;
        }

        // check and see if we have retrieved the userIdentity data from the server.
        // if we have, reuse it by immediately resolving
        if (this.userIdentity) {
           this.authenticationState.next(this.userIdentity);
        }

        // retrieve the userIdentity data from the server, update the identity object, and then resolve.
        this.account.get().subscribe(
            user=>{
                if (user) {
                    this.userIdentity = user;
                    this.authenticated = true;
                } else {
                    this.userIdentity = null;
                    this.authenticated = false;
                }
                this.authenticationState.next(this.userIdentity);
            },

            error=>{
                this.userIdentity = null;
                this.authenticated = false;
                this.authenticationState.next(this.userIdentity);
            }
        );
        return this.authenticationState;
    }

    isAuthenticated(): boolean {
        return this.authenticated;
    }

    isIdentityResolved(): boolean {
        return this.userIdentity !== undefined;
    }

    getAuthenticationState(): Observable<any> {
        return this.authenticationState.asObservable();
    }
}