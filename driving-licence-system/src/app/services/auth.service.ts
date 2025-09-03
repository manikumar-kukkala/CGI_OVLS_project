import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private api = environment.apiBaseUrl;   // FIXED
  currentUser: any = null;
   private userSubject = new BehaviorSubject<any>(null);
   currentUser$ = this.userSubject.asObservable();

  constructor(private http: HttpClient) {}

  login(data: any) : Observable<any> {
    console.log("auth");
    return this.http.post(`${this.api}/auth/login`, data);
  }

  register(data: any) : Observable<any> {
    return this.http.post(`${this.api}/auth/register`, data);
  }
   
   setUser(user: any) {
    this.currentUser = user;
    this.userSubject.next(user);
  }
  logout() { this.currentUser = null; }

  isAuthenticated() { return !!this.currentUser; }
  isAdmin() { return this.currentUser?.role === 'admin'; }
}
