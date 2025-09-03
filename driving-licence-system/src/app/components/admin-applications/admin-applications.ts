import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AdminService } from '../../services/admin.service';

@Component({
  selector: 'app-admin-applications',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './admin-applications.html',
  styleUrls: ['./admin-applications.scss']  // Fixed typo from styleUrl to styleUrls
})
export class AdminApplications {
  applications: any[] = [];
  stats = {
    pending: 0,
    approved: 0,
    rejected: 0,
    total: 0
  };
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
}



review(app: any, newStatus: 'approved' | 'rejected') {
  this.adminService.reviewApplication(app.id, { status: newStatus, reviewedBy: 'admin' }).subscribe(res => {
    console.log("Status updated:", res.application);
    this.loadStats();
    this.loadApplications();
    // Reapply current filter
    if (this.selectedStatus) {
      this.view(this.selectedStatus);
    }
  });
}

}
