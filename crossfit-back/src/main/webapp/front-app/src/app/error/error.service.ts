import { Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class ErrorService {

  constructor(public snackBar: MatSnackBar) {}

  manage(error: any, overrideMessage?: string){

    console.log(error);

    if (overrideMessage != null){
      this.showLongMessage(overrideMessage);
    }
    else if (error instanceof HttpErrorResponse) {
              
      this.showLongMessage(error.message, error.statusText);

      if (error.status == 401){

      }
      else if (error.status == 403){
  
      }
      else if (error.status == 404){
  
      }
      else if (error.status == 400){
        //Parse response...
      }
    }
    else if (error instanceof HttpResponse) {
      this.showLongMessage(error.body, error.statusText);
    }
    else{
      
      this.showLongMessage("Erreur non géré: " + typeof error);
    }
  }

  private showLongMessage(message: string, buttonText?: string){    
    this.snackBar.open(message, buttonText?buttonText:"Ok", {
      duration: 5000
    });
  }
}
