import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Wod } from './domain/wod.model';
import { Movement } from './domain/movement.model';
import { Equipment } from './domain/equipment.model';
import { Observable } from 'rxjs';
import { WodResult } from './domain/wod-result.model';
import { WodResultRanking } from './domain/wod-result-ranking.model';
import { StringifyOptions } from 'querystring';

@Injectable({
  providedIn: 'root'
})
export class WodService {

  constructor(private http: HttpClient) { }

  
 
  findAll(search?: string){
    return this.http.get<Wod[]>("/api/wod", {
        params: new HttpParams()
          .set("query", search)
      }
    );
  }

  save(wod:Wod){
    return this.http.put<Wod>("/api/wod", wod);
  }

  delete(wod: Wod){
    return this.http.delete("/api/wod/" + wod.id);
  }

  get(id){
    return this.http.get<Wod>("/api/wod/" + id);
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
  
  findAllWodAtDateWithMyResult(date: Date){
    return this.http.get<Wod[]>("/api/wod/" + date + "/withMyResult");
  }

  getMyResult(wodId){
    return this.http.get<WodResult[]>("/api/wod/" + wodId + "/results");
  }
  saveOrUpdateResult(wod:Wod, result:WodResult){
    return this.http.put<WodResult>("/api/wod/" + wod.id + "/results", result);
  }
  deleteResult(wod:Wod, result:WodResult){
    return this.http.delete("/api/wod/" + wod.id + "/results/" + result.id);
  }
  getRanking(wodId, date:string){
    return this.http.get<WodResultRanking[]>("/api/wod/" + wodId + "/" + date + "/ranking");
  }
}
