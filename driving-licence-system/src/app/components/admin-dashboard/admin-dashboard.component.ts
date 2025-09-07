import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminNavbar } from '../admin-navbar/admin-navbar';
import { RouterModule } from '@angular/router';
import { AdminService } from '../../services/admin.service';

type Status = 'PENDING' | 'APPROVED' | 'REJECTED';
type LowerStatus = 'pending' | 'approved' | 'rejected';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, AdminNavbar, RouterModule],
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css'],
})
export class AdminDashboardComponent implements OnInit {
  stats = { pending: 0, approved: 0, rejected: 0, total: 0 };
  applications: any[] = [];
  selectedStatus: Status | null = null;

  constructor(private readonly adminService: AdminService) {}

  ngOnInit() {
    this.loadApplications();
    this.loadStats(); // keep if your backend exposes stats; otherwise compute locally
  }

  loadApplications() {
    this.adminService.getAllApplications().subscribe({
      next: (apps: any[]) => {
        // Normalize status to UPPERCASE for consistent filtering and badges
        this.applications = (apps ?? []).map(a => ({
          ...a,
          status: (a.status || 'PENDING').toString().toUpperCase() as Status
        }));
        this.computeStatsFromApps(); // fallback/local stats
      },
      error: (e) => console.error('getAllApplications error', e)
    });
  }

  loadStats() {
    this.adminService.getStats().subscribe({
      next: (res: any) => {
        // Accept either {pending,...} or {stats:{...}}
        this.stats = res?.stats ?? res ?? this.stats;
      },
      error: (e) => console.warn('getStats error (using local stats)', e)
    });
  }

  private computeStatsFromApps() {
    const p = this.applications.filter(a => a.status === 'PENDING').length;
    const a = this.applications.filter(a => a.status === 'APPROVED').length;
    const r = this.applications.filter(a => a.status === 'REJECTED').length;
    this.stats = { pending: p, approved: a, rejected: r, total: this.applications.length };
  }

  // Accept lower or upper, store as UPPERCASE
  view(status: Status | LowerStatus) {
    this.selectedStatus = (status as string).toUpperCase() as Status;
  }

  get filteredApplications() {
    if (!this.selectedStatus) return [];
    return this.applications.filter(a => a.status === this.selectedStatus);
  }

  review(app: any, newStatusLower: 'approved' | 'rejected') {
    const id = app.applicationId ?? app.id;
    const status = newStatusLower.toUpperCase() as 'APPROVED' | 'REJECTED';
    this.adminService
      .reviewApplication(id, { status, reviewedBy: 'admin' })
      .subscribe({
        next: () => {
          app.status = status;                // optimistic update
          this.computeStatsFromApps();        // keep cards in sync
        },
        error: (e) => console.error('review error', e)
      });
  }
}
