import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs';
import { Application, ReviewApplicationRequest } from '../models/licence.model';

@Injectable({ providedIn: 'root' })
export class AdminService {
  private base = environment.apiBaseUrl;

  constructor(private http: HttpClient) {}

  getAllApplications(): Observable<{ applications: Application[] }> {
    return this.http.get<{ applications: Application[] }>(`${this.base}/admin/applications`);
  }

  getStats(): Observable<{ stats: { pending: number; approved: number; rejected: number; total: number } }> {
    return this.http.get<{ stats: { pending: number; approved: number; rejected: number; total: number } }>(`${this.base}/admin/status`);
  }
reviewApplication(id: number, review: { status: 'approved' | 'rejected'; reviewedBy: string }) {
  return this.http.put<{ application: any }>(`${this.base}/applications/${id}`, review);
}
}
