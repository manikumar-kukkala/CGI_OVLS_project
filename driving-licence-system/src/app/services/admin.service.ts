
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { BehaviorSubject, Observable } from 'rxjs';
import { Application, ReviewApplicationRequest } from '../models/licence.model';
import { ApplicationRow, DrivingLicense, RTOOfficer } from '../models/adminInterfaces';


export type Status = 'PENDING' | 'APPROVED' | 'REJECTED';
export type ReviewStatus = 'APPROVED' | 'REJECTED';

export interface Stats {
  pending: number;
  approved: number;
  rejected: number;
  total: number;
}


@Injectable({ providedIn: 'root' })
export class AdminService {
  private url = environment.adminBaseUrl   // FIXED
   currentUser: any = null;
  private userSubject = new BehaviorSubject<any>(null);
  currentUser$ = this.userSubject.asObservable();

  constructor(private http: HttpClient) {}

  login(data: { email: string; password: string }): Observable<any> {
   return this.http.post(`${this.url}/login`, data, {
  headers: { 'Content-Type': 'application/json' },
  responseType: 'text' as 'json'  // ✅ force Angular to treat plain text as response
});
  }

  addOfficer(data: RTOOfficer): Observable<any> {
    return this.http.post(`${this.url}/add`, data);
  }
   
   logout() { this.currentUser = null; }

  isAuthenticated(): boolean {
    return !!this.currentUser;
  }

  isAdmin(): boolean {
    return this.currentUser?.role === 'admin';
  }

  getAllApplications(): Observable<ApplicationRow[]> {
    return this.http.get<ApplicationRow[]>(`${this.url}/applications`);
  }

  /** Dashboard stats (not wrapped) */
  getStats(): Observable<Stats> {
    return this.http.get<Stats>(`${this.url}/stats`);
  }

  /** Approve / Reject */
  reviewApplication(
    id: number,
    body: { status: ReviewStatus; reviewedBy: string }
  ): Observable<ApplicationRow> {
    return this.http.put<ApplicationRow>(`${this.url}/applications/${id}/review`, body);
  }
  // View pending applications
  getPendingApplications(): Observable<Application[]> {
    return this.http.get<Application[]>(`${this.url}/applications/pending`);
  }

  // View approved applications
  getApprovedApplications(): Observable<Application[]> {
    return this.http.get<Application[]>(`${this.url}/applications/approved`);
  }

  // View rejected applications
  getRejectedApplications(): Observable<Application[]> {
    return this.http.get<Application[]>(`${this.url}/applications/rejected`);
  }

  // Get application by ID
  getApplicationById(id: string): Observable<Application> {
    return this.http.get<Application>(`${this.url}/applications/${id}`);
  }

  // Modify test result by ID
  modifyTestResult(id: string): Observable<Application> {
    return this.http.put<Application>(`${this.url}/applications/${id}/test-result`, {});
  }

  // Generate Learner License
  generateLearnerLicense(id: string): Observable<Application> {
    return this.http.put<Application>(`${this.url}/applications/${id}/generate-ll`, {});
  }

  // Generate Driving License
  generateDrivingLicense(id: string): Observable<DrivingLicense> {
    return this.http.put<DrivingLicense>(`${this.url}/applications/${id}/generate-dl`, {});
  }

  // Email Driving License
  emailLicense(license: DrivingLicense): Observable<string> {
    return this.http.post(`${this.url}/license/email`, license, { responseType: 'text' });
  }

  // login(data: any) : Observable<any> {
  //   console.log("auth");
  //   return this.http.post(`${this.api}/rto/officer/login`, data);
  // }

  


}
