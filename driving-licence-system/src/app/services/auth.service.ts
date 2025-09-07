import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { BehaviorSubject, Observable } from 'rxjs';
import { Login } from '../models/adminInterfaces';
import { User } from '../models/user.model';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private api = environment.apiBaseUrl;   // FIXED
  private userURL = environment.userBaseUrl; // FIXED
  currentUser: any = null;
   private userSubject = new BehaviorSubject<any>(null);
   currentUser$ = this.userSubject.asObservable();

  constructor(private http: HttpClient) {}

  login(data: { email: string; password: string }, role?: string) : Observable<any> {
    console.log("auth");
    console.log(data);
    return this.http.post<any>(`${this.userURL}/login`, data,{
    headers: {
      'Content-Type': 'application/json'
    },
     responseType: 'text' as 'json'
    });
  }

  registerUser(data: User) : Observable<any> {
    return this.http.post(`${this.userURL}/register`, data);
  }
   
   
  logout() { this.currentUser = null; }

  isAuthenticated() { return !!this.currentUser; }
  isAdmin() { return this.currentUser?.role === 'admin'; }
}
