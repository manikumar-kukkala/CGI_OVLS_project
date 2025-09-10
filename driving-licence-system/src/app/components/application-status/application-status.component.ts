import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { Router, RouterModule } from '@angular/router';

type UiStatus = 'Submitted' | 'Under Review' | 'Approved' | 'Rejected';

interface AppView {
  applicationId: number | null;
  applicationNumber: string;
  applicantName: string | null;
  applicationDate: string | null;
  modeOfPayment: string | null;
  paymentStatus: string | null;
  remarks: string | null;
  rawStatus: string | null;
  statusLabel: UiStatus;
  applicantId?: number | null;
  documentId?: number | null;
}

@Component({
  selector: 'app-application-status',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, HttpClientModule, RouterModule],
  templateUrl: './application-status.component.html',
  styleUrls: ['./application-status.component.css']
})
export class ApplicationStatusComponent {
  form: FormGroup;
  details: AppView | null = null;
  errorMsg: string | null = null;

  private readonly API = 'http://localhost:8080';

  constructor(private fb: FormBuilder, private http: HttpClient, private router: Router) {
    this.form = this.fb.group({
      applicationId: ['', Validators.required], // APP-001
      name: ['', Validators.required]
    });
  }

  private norm(s: string | null | undefined) {
    return (s ?? '').toString().trim().replace(/\s+/g, ' ').toLowerCase();
  }
  private namesMatch(a: string | null | undefined, b: string | null | undefined) {
    const A = this.norm(a);
    const B = this.norm(b);
    return !!A && !!B && A === B;
  }
  private normalizeStatus(status: string | null | undefined): UiStatus {
    if (!status) return 'Submitted';
    switch (status.toUpperCase()) {
      case 'APPROVED': return 'Approved';
      case 'REJECTED': return 'Rejected';
      case 'PENDING':
      case 'UNDER_REVIEW':
      case 'IN_REVIEW': return 'Under Review';
      default: return 'Submitted';
    }
  }

  check() {
    this.errorMsg = null;
    this.details = null;
    if (this.form.invalid) return;

    const { applicationId, name } = this.form.value;

    // Backend route you already have
    this.http.get<any>(`${this.API}/applications/by-number/${encodeURIComponent(applicationId)}`)
      .subscribe({
        next: (res) => {
          const dbName =
            res?.applicantName ??
            res?.applicant?.user?.name ??
            res?.applicant?.name ??
            res?.name ??
            null;

          // ❗ Require name to match
          if (!this.namesMatch(name, dbName)) {
            this.errorMsg = `Applicant name does not match for ${applicationId}.`;
            this.details = null;
            return;
          }

          this.details = {
            applicationId: res?.applicationId ?? null,
            applicationNumber: res?.applicationNumber ?? applicationId,
            applicantName: dbName,
            applicationDate: res?.applicationDate ?? null,
            modeOfPayment: res?.modeOfPayment ?? null,
            paymentStatus: res?.paymentStatus ?? null,
            remarks: res?.remarks ?? null,
            rawStatus: res?.status ?? null,
            statusLabel: this.normalizeStatus(res?.status),
            applicantId: res?.applicant?.applicantId ?? res?.applicantId ?? null,
            documentId: res?.documents?.documentId ?? res?.documentId ?? null
          };
        },
        error: (err) => {
          console.error('Lookup failed', err);
          this.errorMsg = `No application found for ${applicationId}.`;
        }
      });
  }

  getProgress(): number {
    if (!this.details) return 0;
    switch (this.details.statusLabel) {
      case 'Submitted': return 30;
      case 'Under Review': return 60;
      case 'Approved':
      case 'Rejected': return 100;
      default: return 0;
    }
  }

  getProgressColor(): string {
    if (!this.details) return 'bg-secondary';
    if (this.details.statusLabel === 'Approved') return 'bg-success';
    if (this.details.statusLabel === 'Rejected') return 'bg-danger';
    if (this.details.statusLabel === 'Under Review') return 'bg-warning text-dark';
    return 'bg-info text-dark';
  }

  print() { window.print(); }
  close() { this.router.navigate(['/home']); }
}
