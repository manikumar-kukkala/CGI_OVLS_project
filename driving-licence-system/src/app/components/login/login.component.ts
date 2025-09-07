import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';
import { RouterModule } from '@angular/router';
import { User } from '../../models/user.model';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  form!: FormGroup;
  error: string = '';
  isSubmitting: boolean = false;
  role: 'user' | 'admin' = 'user';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Use queryParams if your URL is /login?role=admin
   this.form = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }
onSubmit() {
   this.error = '';
  if (this.form.invalid) return;

  this.isSubmitting = true;

  const loginData: Pick<User, 'email' | 'password'> = this.form.value;
  
      this.authService.login(loginData).subscribe({
        next: (res) => {
          console.log('user login successful:', res);
          this.isSubmitting = false;
           this.authService.currentUser = { email: loginData.email, role: 'user' };
          // ✅ Navigate after successful authentication
          this.router.navigateByUrl('/home');
        },
        error: (err) => {
          console.error('Login failed:', err);
          this.error = err?.error?.message || 'Invalid credentials';
          this.isSubmitting = false;
        }
      });
    }
  }
  

