import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { finalize } from 'rxjs/operators';

import { Navbar } from '../navbar/navbar';
import { LicenceService } from '../../services/licence.service';
import { AuthService } from '../../services/auth.service';

// Reuse your existing types
import { ApplicationSummary, AppStatus } from '../../models/licence.model';



@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, Navbar],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  latest?: ApplicationSummary | null;
  loadingLatest = false;

  // === Search UI state (NEW) ===
  searchBy: 'number' | 'email' = 'number';
  query = '';
  searching = false;
  searched = false;
  searchError: string | null = null;
  searchResult: ApplicationSummary | null = null;

  constructor(
    private readonly licence: LicenceService,
    private readonly auth: AuthService,
    private readonly http: HttpClient
  ) {}

  ngOnInit(): void {
    // Get logged-in user (from AuthService or localStorage fallback)
    const u = this.auth.currentUser || JSON.parse(localStorage.getItem('user') || 'null');
    const userId: number | undefined = u?.id ?? u?.userId;

    // Load "latest" status for the top alert
    if (userId) {
      this.loadingLatest = true;
      this.licence.getLatestStatusByUser(userId).subscribe({
        next: (s) => { this.latest = s; this.loadingLatest = false; },
        error: () => { this.latest = null; this.loadingLatest = false; }
      });
    }
  }

  // ===== Search handlers =====
  runSearch() {
    this.searchError = null;
    this.searched = true;
    this.searchResult = null;

    const q = (this.query || '').trim();
    if (!q) {
      this.searchError = 'Please enter a value to search.';
      return;
    }

    this.searching = true;

    if (this.searchBy === 'number') {
      // Try a dedicated endpoint first; if not present, fall back to client-side filter
      this.http.get<any>(`http://localhost:8080/applications/by-number/${encodeURIComponent(q)}`)
        .subscribe({
          next: (res) => {
            const a = Array.isArray(res) ? res[0] : res;
            this.searchResult = a ? this.toSummary(a) : null;
            this.searching = false;
          },
          error: () => this.searchFallbackByNumber(q)
        });
    } else {
      // email search
      this.http.get<any[]>(`http://localhost:8080/applications/user`, { params: { email: q } })
        .subscribe({
          next: (rows) => {
            const top = (rows ?? []).sort((x: any, y: any) => {
              const dx = new Date(x?.applicationDate || 0).getTime();
              const dy = new Date(y?.applicationDate || 0).getTime();
              return dy - dx;
            })[0];
            this.searchResult = top ? this.toSummary(top) : null;
            this.searching = false;
          },
          error: () => this.searchFallbackByEmail(q)
        });
    }
  }

  private searchFallbackByNumber(appNo: string) {
    this.http.get<any[]>(`http://localhost:8080/applications`).subscribe({
      next: (all) => {
        const a = (all ?? []).find(
          (x) => (x?.applicationNumber || '').toString().toLowerCase() === appNo.toLowerCase()
        );
        this.searchResult = a ? this.toSummary(a) : null;
        this.searching = false;
      },
      error: () => {
        this.searchError = 'Search failed. Please try again.';
        this.searching = false;
      }
    });
  }

  private searchFallbackByEmail(email: string) {
    this.http.get<any[]>(`http://localhost:8080/applications`).subscribe({
      next: (all) => {
        const mine = (all ?? []).filter(
          (a) => a?.applicant?.user?.email?.toLowerCase() === email.toLowerCase()
        );
        const top = mine.sort((x: any, y: any) => {
          const dx = new Date(x?.applicationDate || 0).getTime();
          const dy = new Date(y?.applicationDate || 0).getTime();
          return dy - dx;
        })[0];
        this.searchResult = top ? this.toSummary(top) : null;
        this.searching = false;
      },
      error: () => {
        this.searchError = 'Search failed. Please try again.';
        this.searching = false;
      }
    });
  }

  // ===== Normalizer -> ApplicationSummary =====
 private readonly toSummary = (a: any): ApplicationSummary => ({
  applicationId: a.applicationId,
  applicationNumber: a.applicationNumber,
  applicantName: a.applicantName,  // <-- add this
  status: a.status,
  paymentStatus: a.paymentStatus,
  modeOfPayment: a.modeOfPayment,
  applicationDate: a.applicationDate
});


  // ---- Your existing static content ----
  stats = [
    { title: 'Licenses Issued', value: '50,000+', icon: '📝' },
    { title: 'Processing Time', value: '48 Hours', icon: '⏱️' },
    { title: 'Customer Satisfaction', value: '98%', icon: '⭐' }
  ];

  steps = [
    { number: 1, title: 'Register/Login', desc: 'Create an account or log in to start applying.' },
    { number: 2, title: 'Fill Application', desc: 'Complete your license application online.' },
    { number: 3, title: 'Upload Documents', desc: 'Provide required documents for verification.' },
    { number: 4, title: 'Processing', desc: 'Our team verifies and processes your application.' },
    { number: 5, title: 'Receive License', desc: 'Get your license delivered or ready for pickup.' }
  ];
}
