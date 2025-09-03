import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import {
  Application, InsertApplication, ApplicationStatusRequest
} from '../models/licence.model';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class LicenceService {
  private base = environment.apiBaseUrl;

  constructor(private http: HttpClient) {}

  submitApplication(data: InsertApplication, userId: string): Observable<{ application: Application }> {
    return this.http.post<{ application: Application }>(`${this.base}/api/applications`, { ...data, userId });
  }

  getUserApplications(userId: string): Observable<{ applications: Application[] }> {
    return this.http.get<{ applications: Application[] }>(`${this.base}/api/applications/user/${userId}`);
  }

  checkApplicationStatus(request: ApplicationStatusRequest): Observable<{ application: Application }> {
    return this.http.post<{ application: Application }>(`${this.base}/api/applications/status`, request);
  }
}
