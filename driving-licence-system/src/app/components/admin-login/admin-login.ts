import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router,RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AdminService } from '../../services/admin.service';
import { RTOOfficer } from '../../models/adminInterfaces';
 // adjust path if needed

@Component({
  selector: 'app-admin-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './admin-login.html',
  styleUrls: ['./admin-login.scss']
})
export class AdminLogin implements OnInit {
  form!: FormGroup;
  error: string = '';
  isSubmitting: boolean = false;

  constructor(
    private fb: FormBuilder,
    private adminService: AdminService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  onSubmit() {
    this.error = '';
    if (this.form.invalid) return;

    this.isSubmitting = true;

    const loginData: Pick<RTOOfficer, 'email' | 'password'> = this.form.value;

    this.adminService.login(loginData).subscribe({
      next: (res) => {
        console.log('Admin login successful:', res);
        this.isSubmitting = false;
         this.adminService.currentUser = { email: loginData.email, role: 'admin' };
        // ✅ Navigate after successful authentication
        this.router.navigateByUrl('/admin-dashboard');
      },
      error: (err) => {
        console.error('Login failed:', err);
        this.error = err?.error?.message || 'Invalid credentials';
        this.isSubmitting = false;
      }
    });
  }
}
