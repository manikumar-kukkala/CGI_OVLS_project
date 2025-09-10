import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-application-status',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './application-status.component.html',
  styleUrls: ['./application-status.component.css']
})
export class ApplicationStatusComponent {
  form: FormGroup;
  statusResult: { id: string, name: string, status: string } | null = null;

  constructor(private fb: FormBuilder, private http: HttpClient) {
    this.form = this.fb.group({
      applicationId: ['', Validators.required],
      name: ['', Validators.required]
    });
  }

  // Helper to normalize backend status to frontend-friendly value
  normalizeStatus(status: string): string {
    switch (status.toUpperCase()) {
      case 'APPROVED': return 'Approved';
      case 'REJECTED': return 'Rejected';
      case 'PENDING': return 'Under Review';
      default: return 'Submitted';
    }
  }

  check() {
  if (this.form.invalid) return;

  const { applicationId, name } = this.form.value;

  this.http.get<any>(
    `http://localhost:8080/rto/officer/applications/${applicationId}`
  ).subscribe({
    next: (res) => {
      const applicantName = res?.applicant?.name || res?.name || name;
      this.statusResult = {
        id: res?.id || applicationId,
        name: applicantName,
        status: this.normalizeStatus(res?.status)
      };
    },
    error: (err) => {
      console.error(err);
      this.statusResult = { id: applicationId, name, status: 'Submitted' };
    }
  });
}


  getProgress(): number {
    if (!this.statusResult) return 0;
    switch (this.statusResult.status) {
      case 'Submitted': return 30;
      case 'Under Review': return 60;
      case 'Approved': return 100;
      case 'Rejected': return 100;
      default: return 0;
    }
  }

  getProgressColor(): string {
    if (!this.statusResult) return 'bg-secondary';
    if (this.statusResult.status === 'Approved') return 'bg-success';
    if (this.statusResult.status === 'Rejected') return 'bg-danger';
    if (this.statusResult.status === 'Under Review') return 'bg-warning text-dark';
    return 'bg-info text-dark';
  }
}
