import { Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { ApplyLicenceComponent } from './components/apply-licence/apply-licence.component';
import { ApplicationStatusComponent } from './components/application-status/application-status.component';
import { AdminDashboardComponent } from './components/admin-dashboard/admin-dashboard.component';
import { AuthGuard } from './guards/auth.guard';
import { AdminGuard } from './guards/admin.guard';
import { AdminApplications } from './components/admin-applications/admin-applications';
import { User } from './components/user/user';
import { AdminRegister } from './components/admin-register/admin-register';
import { AdminLogin } from './components/admin-login/admin-login';

export const routes: Routes = [
   { path: '', redirectTo: 'home', pathMatch: 'full' },
  { path: 'home', component: HomeComponent },

  // Separate User Routes
  { path: 'login', component: LoginComponent }, // user login
  { path: 'register', component: RegisterComponent },

  { path: 'apply-licence', component: ApplyLicenceComponent, canActivate: [AuthGuard] },
  { path: 'application-status', component: ApplicationStatusComponent, canActivate: [AuthGuard] },
  { path: 'user', component: User, canActivate: [AuthGuard] },

  // Separate Admin Routes
  { path: 'admin-login', component: AdminLogin },        // Admin login component
  { path: 'admin-register', component: AdminRegister },  // Admin register component
  { path: 'admin-dashboard', component: AdminDashboardComponent, canActivate: [AdminGuard] },
  { path: 'admin-applications', component: AdminApplications ,canActivate: [AdminGuard] },
];