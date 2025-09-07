
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule ,ActivatedRoute} from '@angular/router';
import { AuthService } from '../../services/auth.service';
//import { RTOOfficer } from '../../models/adminInterfaces';
import { CommonModule } from '@angular/common';
import { User } from '../../models/user.model';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {
 registerForm!: FormGroup;
  isSubmitting = false;
  error = '';

  constructor(private fb: FormBuilder, private router: Router, private authService: AuthService,  private route: ActivatedRoute,) {}

  ngOnInit(): void {

    this.initForm();
  }

  
  initForm() {
    this.registerForm = this.fb.group({
      name: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }
onSubmit() {
  this.error = '';
  if (this.registerForm.invalid) return;

  this.isSubmitting = true;

  const userData : User= this.registerForm.value;

  this.authService.registerUser(userData).subscribe({
    next: (response) => {
      console.log('User Registered (saved to DB):', response);
      this.isSubmitting = false;
      this.router.navigateByUrl('/login'); // Navigate after successful registration
    },
    error: (err) => {
      console.error('Error during registration:', err);
      this.error = 'Registration failed. Please try again.';
      this.isSubmitting = false;
    }
  });
}
}