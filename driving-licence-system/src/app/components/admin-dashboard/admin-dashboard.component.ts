import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminNavbar } from '../admin-navbar/admin-navbar';
import { RouterModule } from '@angular/router';
import { AdminService } from '../../services/admin.service';

@Component({
  selector: 'app-admin-dashboard',
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css'], // create empty file if needed
  standalone: true,
  imports: [CommonModule,AdminNavbar,RouterModule]
})
export class AdminDashboardComponent {
  stats = {
    pending: 0,
    approved: 0,
    rejected: 0,
    total: 0
  };

  applications: any[] = [];

  selectedStatus: 'pending' | 'approved' | 'rejected' | null = null;

  constructor(private adminService: AdminService) {}

  ngOnInit() {
    this.loadStats();
    this.loadApplications();
  }

  loadStats() {
    console.log("Loading stats...");
    this.adminService.getStats().subscribe(res => {
      this.stats = res.stats;
    });
  }

  loadApplications() {
    this.adminService.getAllApplications().subscribe(res => {
      this.applications = res.applications;
      
    });
  }

  view(status: 'pending' | 'approved' | 'rejected') {
    this.selectedStatus = status;
    // no need to filter here; filteredApplications getter will handle that
  }

review(app: any, newStatus: 'approved' | 'rejected') {
  this.adminService.reviewApplication(app.id, { status: newStatus, reviewedBy: 'admin' }).subscribe(res => {
    console.log("Status updated:", res.application);
    this.loadStats();
    this.loadApplications();
  });
}


  get filteredApplications() {
    if (!this.selectedStatus) return [];
    return this.applications.filter(app => app.status === this.selectedStatus);
  }
}
