import { Component, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import {
  AdminService,
  ReviewStatus,
  Status,
  Stats
} from '../../services/admin.service';
import { ApplicationRow } from '../../models/adminInterfaces';
import { AdminNavbar } from '../admin-navbar/admin-navbar';
import { FormsModule } from '@angular/forms';

type ReviewStatusLower = Lowercase<ReviewStatus>; // 'approved' | 'rejected'

@Component({
  selector: 'app-admin-applications',
  standalone: true,
  imports: [CommonModule, RouterModule, AdminNavbar,FormsModule],
  templateUrl: './admin-applications.html',
  styleUrls: ['./admin-applications.scss']
})
export class AdminApplications {
  applications: ApplicationRow[] = [];
  filtered: ApplicationRow[] = [];
  selectedStatus: Status | null = null;

  stats: Stats = { pending: 0, approved: 0, rejected: 0, total: 0 };

  // ===== Details modal =====
  showModal = false;
  selectedApp: ApplicationRow | null = null;

  // ===== Review (Approve/Reject) modal =====
  showReviewModal = false;
  reviewRemarks = '';
  pendingAction:
    | { app: ApplicationRow; status: ReviewStatus }
    | null = null;

  constructor(private adminService: AdminService) {}

  ngOnInit() {
    this.loadAll();
  }

  private toReviewStatus(s: ReviewStatus | ReviewStatusLower): ReviewStatus {
    return (typeof s === 'string' ? s.toUpperCase() : s) as ReviewStatus;
  }

  /** Make LL/DL pretty for UI */
  prettyLicenceType(lt?: string | null): string {
    const v = (lt ?? '').toString().toUpperCase();
    if (v === 'DL') return 'Driving Licence';
    if (v === 'LL') return 'Learning Licence';
    return '—';
  }

  private loadAll() {
    this.adminService.getAllApplications().subscribe({
      next: (apps) => {
        this.applications = (apps ?? []).map(a => {
          // normalise licenceType from either `licenceType` or DB field `licence_type`
          const rawLT = (a as any).licenceType ?? (a as any).licence_type ?? (a as any)['licenseType'];
          const licenceType = (rawLT ? String(rawLT).toUpperCase() : '') as 'DL' | 'LL' | '';

          return {
            ...a,
            // name fallback
            applicantName: a.applicantName ?? a.applicant?.user?.name ?? 'Unknown',
            status: (a.status ?? 'PENDING').toString().toUpperCase() as Status,

            // expose licenceType field for UI
            licenceType,

            // ensure modal fields exist
            modeOfPayment: a.modeOfPayment ?? (a as any)['modeOfPayment'],
            paymentStatus: a.paymentStatus ?? (a as any)['paymentStatus'],
            applicationDate: a.applicationDate ?? (a as any)['applicationDate'],
            remarks: a.remarks ?? (a as any)['remarks'],

            // applicant
            applicant: a.applicant
              ? {
                  ...a.applicant,
                  applicantId:
                    (a.applicant as any).applicantId ??
                    (a.applicant as any)['applicantId'],
                  learnerLicenseStatus:
                    (a.applicant as any).learnerLicenseStatus ??
                    (a.applicant as any)['learnerLicenseStatus'],
                  drivingLicenseStatus:
                    (a.applicant as any).drivingLicenseStatus ??
                    (a.applicant as any)['drivingLicenseStatus'],
                  user: a.applicant.user
                }
              : undefined,

            // documents
            documents: (a as any).documents
              ? {
                  documentId: (a as any).documents.documentId,
                  photo: (a as any).documents.photo,
                  idProof: (a as any).documents.idProof,
                  addressProof: (a as any).documents.addressProof
                }
              : undefined
          } as ApplicationRow & { licenceType?: 'DL' | 'LL' | '' };
        });

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
    this.filtered = this.applications.filter(
      a => (a.status as string) === this.selectedStatus
    );
  }

  // ===== Details modal =====
  openModal(app: ApplicationRow) {
    this.selectedApp = app;
    this.showModal = true;
    document.body.classList.add('no-scroll');
  }

  closeModal() {
    this.showModal = false;
    this.selectedApp = null;
    document.body.classList.remove('no-scroll');
  }

  onBackdrop(evt: MouseEvent) {
    this.closeModal();
  }

  // ===== Review flow (force remarks) =====
  startReview(app: ApplicationRow, newStatus: ReviewStatusLower) {
    const status = this.toReviewStatus(newStatus);
    this.pendingAction = { app, status };
    this.reviewRemarks = '';
    this.showReviewModal = true;
    document.body.classList.add('no-scroll');
  }

  submitReview() {
    if (!this.pendingAction) return;
    const { app, status } = this.pendingAction;
    const id = (app as any).applicationId ?? (app as any).id;
    if (!id) return;

    // Require at least a couple of words
    if (!this.reviewRemarks || this.reviewRemarks.trim().length < 5) {
      alert('Please enter a short, helpful remark for the applicant (min 5 characters).');
      return;
    }

    this.adminService
      .reviewApplication(id, {
        status,
        reviewedBy: 'rto_officer' + this.reviewRemarks.trim(),
        //remarks: this.reviewRemarks.trim()
      })
      .subscribe({
        next: () => {
          // optimistic update
          (app as any).status = status;
          (app as any).remarks = this.reviewRemarks.trim();
          this.applyFilter();
          this.adminService.getStats().subscribe(s => (this.stats = s));
          this.cancelReview();
        }
      });
  }

  cancelReview() {
    this.showReviewModal = false;
    this.pendingAction = null;
    this.reviewRemarks = '';
    document.body.classList.remove('no-scroll');
  }

  // Close on ESC for either modal
  @HostListener('window:keydown', ['$event'])
  onKeyDown(e: KeyboardEvent) {
    if (e.key !== 'Escape') return;
    if (this.showModal) this.closeModal();
    if (this.showReviewModal) this.cancelReview();
  }
}
