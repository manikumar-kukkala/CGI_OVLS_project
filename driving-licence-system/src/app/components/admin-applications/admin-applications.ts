import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import {
  AdminService,
  ReviewStatus,
  Status,
  Stats
} from '../../services/admin.service';
import { ApplicationRow } from '../../models/adminInterfaces';



// ✅ Import the navbar component so the tag <app-admin-navbar> is known
import { AdminNavbar } from '../admin-navbar/admin-navbar';


type ReviewStatusLower = Lowercase<ReviewStatus>; // 'approved' | 'rejected'

@Component({
  selector: 'app-admin-applications',
  standalone: true,
  imports: [CommonModule, RouterModule,AdminNavbar],
  templateUrl: './admin-applications.html',
  styleUrls: ['./admin-applications.scss']
})
export class AdminApplications {
  applications: ApplicationRow[] = [];
  filtered: ApplicationRow[] = [];
  selectedStatus: Status | null = null;

  stats: Stats = { pending: 0, approved: 0, rejected: 0, total: 0 };

  constructor(private adminService: AdminService) {}

  ngOnInit() {
    this.loadAll();
  }

  private toReviewStatus(s: ReviewStatus | ReviewStatusLower): ReviewStatus {
    return (typeof s === 'string' ? s.toUpperCase() : s) as ReviewStatus;
  }

  private loadAll() {
    // apps is a plain array from AdminService
    this.adminService.getAllApplications().subscribe({
      next: (apps) => {
        this.applications = (apps ?? []).map(a => ({
          ...a,
          status: (a.status ?? 'PENDING').toString().toUpperCase() as Status
        }));
        this.applyFilter();
      }
    });

    this.adminService.getStats().subscribe({
      next: (s) => (this.stats = s)
    });
  }

  view(status: Status | null) {
    this.selectedStatus = status;
    this.applyFilter();
  }
   get filteredApplications(): ApplicationRow[] {
    return this.filtered;
  }

  private applyFilter() {
    if (!this.selectedStatus) {
      this.filtered = [...this.applications];
      return;
    }
    this.filtered = this.applications.filter(a => (a.status as string) === this.selectedStatus);
  }

  review(app: ApplicationRow, newStatus: ReviewStatus | ReviewStatusLower) {
    const id = app.applicationId ?? (app as any).id;
    if (!id) return;

    const status = this.toReviewStatus(newStatus); // -> 'APPROVED' | 'REJECTED'

    this.adminService.reviewApplication(id, { status, reviewedBy: 'admin' }).subscribe({
      next: () => {
        app.status = status; // optimistic update
        this.applyFilter();
        this.adminService.getStats().subscribe(s => (this.stats = s));
      }
    });
  }
}
