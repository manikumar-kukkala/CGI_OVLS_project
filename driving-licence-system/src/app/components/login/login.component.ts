import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { Router ,ActivatedRoute} from '@angular/router';
import { AdminService } from '../../services/admin.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent implements OnInit {
  form!: FormGroup;
  error = '';
  loading = false;
  successMessage = '';
   isAdminLogin = false;
  constructor(private fb: FormBuilder, private auth: AuthService, private router: Router,private route: ActivatedRoute) {}

  ngOnInit() {

    

    this.form = this.fb.group({
      username: ['', Validators.required],
      password: ['', Validators.required],
    });

    const fromParam = this.route.snapshot.queryParams['from'];
    this.isAdminLogin = fromParam === 'admin';

    // Pre-fill admin credentials if it's admin login
    if (this.isAdminLogin) {
      this.form.patchValue({
        username: 'admin',
        password: 'admin@123'
      });
    } 
    
  }

  submit() {
    this.error = '';
    this.successMessage = '';
    console.log("clicked submit");
    if (this.form.invalid) return;

    this.loading = true;
     console.log("enter to login");
    this.auth.login(this.form.value).subscribe({
     
      next: (res: any) => {
        console.log('Login response:', res); // Inspect backend response
        this.auth.currentUser = res.user;
         this.auth.setUser(res.user);    // Store user in AuthService
        this.successMessage = res.message;   // Show backend message
        if (res.user?.role === 'admin') {
          this.router.navigateByUrl('/admin-dashboard');
        } else {
          this.router.navigateByUrl('/home');
        }      // Navigate after login
      },
      error: (e) => {
        console.error('Login error:', e);
        this.error = e?.error?.message || 'Login failed'; // Show error message
      },
      complete: () => {
        this.loading = false;
      }
    });


  }

  logout() {
    this.auth.logout();
    this.router.navigateByUrl('/login'); // Optional redirect after logout
  }
}
