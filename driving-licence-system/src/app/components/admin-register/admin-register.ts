import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule ,ActivatedRoute} from '@angular/router';
import { AdminService } from '../../services/admin.service';
import { RTOOfficer } from '../../models/adminInterfaces';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-admin-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './admin-register.html',
  styleUrl: './admin-register.scss'
})
export class AdminRegister implements OnInit {

   registerForm!: FormGroup;
  isSubmitting = false;
  error = '';

  constructor(private fb: FormBuilder, private router: Router, private adminService: AdminService,  private route: ActivatedRoute,) {}

  ngOnInit(): void {
    this.initForm();
  }

  initForm() {
  this.registerForm = this.fb.group({
    username: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]],
   // rtoId: ['', Validators.required]  // ✅ flatten, not nested
  });
}


onSubmit() {
  this.error = '';
  if (this.registerForm.invalid) return;

  this.isSubmitting = true;

  const officerData: RTOOfficer = this.registerForm.value; // now matches backend

  this.adminService.addOfficer(officerData).subscribe({
    next: (response) => {
      console.log('Admin Registered:', response);
      this.isSubmitting = false;
      this.router.navigateByUrl('/admin-login');
    },
    error: (err) => {
      console.error('Error during registration:', err);
      this.error = 'Registration failed. Please try again.';
      this.isSubmitting = false;
    }
  });
}



}

