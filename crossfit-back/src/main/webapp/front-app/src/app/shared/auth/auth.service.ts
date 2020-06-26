import { Injectable } from '@angular/core';
import { Principal } from '../auth/principal.service';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Injectable()
export class AuthService {

  constructor(private http: HttpClient, private principal: Principal) { }

  login(username: string, password: string, rememberMe: boolean){
     var data = 'j_username=' + encodeURIComponent(username) +
                    '&j_password=' + encodeURIComponent(password) +
                    '&remember-me=' + rememberMe + '&submit=Login';

    return this.http.post("/api/authentication", data, {
        headers: new HttpHeaders({
          "Content-Type": "application/x-www-form-urlencoded"
        })
      }
    );
  }


  logout() {
    return this.http.post('/api/logout', "");
  }
}
