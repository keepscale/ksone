import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Wod } from './domain/wod.model';
import { Movement } from './domain/movement.model';
import { Equipment } from './domain/equipment.model';
import { Observable } from 'rxjs';
import { WodResult } from './domain/wod-result.model';
import { WodResultRanking } from './domain/wod-result-ranking.model';
import * as moment from 'moment';


@Injectable({
  providedIn: 'root'
})
export class WodResultService {

  constructor(private http: HttpClient) { }

  
  
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
  getRanking(wodId){
    return this.http.get<WodResultRanking[]>("/api/wod/" + wodId + "/ranking");
  }
  
}
