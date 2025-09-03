import { Component } from '@angular/core';
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-admin-navbar',
  imports: [RouterModule],
  templateUrl: './admin-navbar.html',
  styleUrl: './admin-navbar.scss'
})
export class AdminNavbar {
   officerName: string = 'RTO Officer';

   constructor(private readonly router: Router) {}

  logout() {
    // Clear token/session here
    localStorage.removeItem('authToken'); // Or use a service
    this.router.navigate(['/home']);
  }
}
