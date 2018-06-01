import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Wod } from './domain/wod.model';
import { Movement } from './domain/movement.model';
import { Equipment } from './domain/equipment.model';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class WodService {

  constructor(private http: HttpClient) { }

  
 
  findAll(search: string){
    return this.http.get<Wod[]>("api/wods", {
        params: new HttpParams().set("query", search)
      }
    );
  }

  save(wod:Wod){
    return this.http.put<Wod>("api/wods", wod);
  }

  delete(wod: Wod){
    return this.http.delete("api/wods/" + wod.id);
  }

  get(id){
    return this.http.get<Wod>("api/wods/" + id);
  }

  getScores(){
    return this.http.get<string[]>("/api/wod/scores");
  }
  getCategories(){
    return this.http.get<string[]>("/api/wod/categories");
  }
  getMovements(){
    return this.http.get<Movement[]>("/api/wod/movements");
  }
  getEquipments():Observable<Equipment[]>{
    return this.http.get<Equipment[]>("/api/wod/equipments");
  }
}
