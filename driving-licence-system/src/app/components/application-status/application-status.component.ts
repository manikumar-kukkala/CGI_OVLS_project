import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { LicenceService } from '../../services/licence.service';
import { AuthService } from '../../services/auth.service';
import { Application } from '../../models/licence.model';

@Component({
  selector: 'app-application-status',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './application-status.component.html'
})
export class ApplicationStatusComponent implements OnInit {
  form!: FormGroup;
  searchResult?: Application;
  userApps: Application[] = [];
  error = '';

  constructor(private fb: FormBuilder, private licence: LicenceService, private auth: AuthService) {}

  ngOnInit() {
    this.form = this.fb.group({
      applicationNumber: ['', Validators.required],
    });

    const user = this.auth.currentUser;
    if (user) {
      this.licence.getUserApplications(user.id).subscribe({
        next: (res) => this.userApps = res.applications || [],
        error: () => {}
      });
    }
  }

  check() {
    this.error = '';
    if (this.form.invalid) return;
    this.licence.checkApplicationStatus(this.form.value as any).subscribe({
      next: (res) => this.searchResult = res.application,
      error: (e) => this.error = e?.error || 'Not found',
    });
  }
}
