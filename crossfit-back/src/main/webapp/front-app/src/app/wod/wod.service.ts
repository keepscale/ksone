import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Wod } from './domain/wod.model';
import { Movement } from './domain/movement.model';
import { Equipment } from './domain/equipment.model';
import { Observable } from 'rxjs';
import { WodResult } from './domain/wod-result.model';
import { WodResultRanking } from './domain/wod-result-ranking.model';
import * as moment from 'moment';

export class WodSearchRequest{
  constructor(public query?: string, public start?: Date, public end?: Date){}
}

@Injectable({
  providedIn: 'root'
})
export class WodService {

  constructor(private http: HttpClient) { }

  
 
  findAll(search?: WodSearchRequest){
    return this.http.get<Wod[]>("/api/manage/wod", {
        params: new HttpParams()
        .set("query", search.query)
        .set("start", moment(search.start).format("YYYY-MM-DD"))
        .set("end", moment(search.end).format("YYYY-MM-DD"))
      }
    );
  }

  save(wod:Wod){
    return this.http.put<Wod>("/api/manage/wod", wod);
  }

  delete(wod: Wod){
    return this.http.delete("/api/manage/wod/" + wod.id);
  }

  get(id){
    return this.http.get<Wod>("/api/manage/wod/" + id);
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
