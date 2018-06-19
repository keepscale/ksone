import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Booking } from './domain/booking.model';

@Injectable({
  providedIn: 'root'
})
export class BookingService {

  constructor(private http: HttpClient) { }

  
 
  findAllPastBooking(page: number = 1){
    return this.http.get<Booking[]>("/api/bookings/pastbookings", {
        params: new HttpParams().set("page", page.toString())
      }
    );
  }
}
