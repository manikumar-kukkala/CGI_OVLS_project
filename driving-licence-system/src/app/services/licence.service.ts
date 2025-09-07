
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import {
  Application, InsertApplication, ApplicationStatusRequest,
  ApplicationSummary
} from '../models/licence.model';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class LicenceService {
  private base = environment.apiBaseUrl;

  constructor(private http: HttpClient) {}

  getLatestStatusByUser(userId: number): Observable<ApplicationSummary> {
    return this.http.get<ApplicationSummary>(`${this.base}/applications/status/latest/by-user/${userId}`);
  }

  submitApplication(data: InsertApplication, userId: string): Observable<{ application: Application }> {
    return this.http.post<{ application: Application }>(`${this.base}/api/applications`, { ...data, userId });
  }

  getUserApplications(userId: string): Observable<{ applications: Application[] }> {
    return this.http.get<{ applications: Application[] }>(`${this.base}/api/applications/user/${userId}`);
  }

  checkApplicationStatus(request: ApplicationStatusRequest): Observable<{ application: Application }> {
    return this.http.post<{ application: Application }>(`${this.base}/api/applications/status`, request);
  }

  getAllApplications(): Observable<{ applications: Application[] }> {
    return this.http.get<{ applications: Application[] }>(`${this.base}/applicants`);
  }

  getApplicationsForUserEmail(email: string) {
    return this.http.get<any[]>(`${this.base}/applications/user/email`, { params: { email } });
  }

  getApplicationByNumber(appNumber: string) {
    return this.http.get<any>(`${this.base}/applications/by-number/${encodeURIComponent(appNumber)}`);
  }

  // --- Documents ---
  updateDocuments(id: number, body: { addressProof?: string; idProof?: string; photo?: string }): Observable<any> {
    return this.http.put<any>(`${this.base}/documents/update/${id}`, body);
  }

  // --- Appointments ---
  updateAppointment(id: number, body: any): Observable<any> {
    return this.http.put<any>(`${this.base}/appointments/update/${id}`, body);
  }

  createAppointment(body: {
    applicationId: number;
    officerId?: number | null;
    testDate: string;   // yyyy-MM-dd
    timeSlot: string;
    testNumber?: string | null;
    testResult?: string | null;
  }): Observable<any> {
    return this.http.post<any>(`${this.base}/appointments`, body);
  }

  updateApplicationPayment(
  applicationId: number,
  body: { modeOfPayment: string; paymentStatus: string }
) {
  return this.http.patch<any>(`${this.base}/applications/${applicationId}/payment`, body);
}
updateApplicantLicense(
  applicantId: number,
  body: { type: 'learning' | 'permanent'; status?: string }
) {
  return this.http.patch<any>(`${this.base}/applicants/${applicantId}/license`, body);
}

createApplicantAddress(
  applicantId: number,
  body: { state?: string; city?: string; house?: string; landmark?: string; pincode?: string }
) {
  return this.http.post<any>(`${this.base}/applicants/${applicantId}/address`, body);
}
}
