import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { LicenceService } from '../../services/licence.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-apply-licence',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './apply-licence.component.html'
})
export class ApplyLicenceComponent implements OnInit {
  form!: FormGroup;
  submitted = false;
  error = '';

  constructor(private fb: FormBuilder, private licence: LicenceService, private auth: AuthService) {}

  ngOnInit() {
    this.form = this.fb.group({
      type: ['learning', Validators.required],
      address: ['', Validators.required],
      identityProof: ['', Validators.required],
      addressProof: ['', Validators.required],
      photo: ['', Validators.required],
    });
  }

  submit() {
    this.error = '';
    if (this.form.invalid || !this.auth.currentUser) return;
    this.licence.submitApplication(this.form.value as any, this.auth.currentUser.id).subscribe({
      next: () => { 
        this.submitted = true; 
        this.form.reset({ type: 'learning' }); 
      },
      error: (e) => this.error = e?.error || 'Submission failed',
    });
  }
}
