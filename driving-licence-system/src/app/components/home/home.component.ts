import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import {  RouterModule } from '@angular/router';
import { Navbar } from '../navbar/navbar';


@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterModule,Navbar],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent {
  
  stats = [
    { title: 'Licenses Issued', value: '50,000+', icon: '📝' },
    { title: 'Processing Time', value: '48 Hours', icon: '⏱️' },
    { title: 'Customer Satisfaction', value: '98%', icon: '⭐' }
  ];

  steps = [
    { number: 1, title: 'Register/Login', desc: 'Create an account or log in to start applying.' },
    { number: 2, title: 'Fill Application', desc: 'Complete your license application online.' },
    { number: 3, title: 'Upload Documents', desc: 'Provide required documents for verification.' },
    { number: 4, title: 'Processing', desc: 'Our team verifies and processes your application.' },
    { number: 5, title: 'Receive License', desc: 'Get your license delivered or ready for pickup.' }
  ];

   
}
