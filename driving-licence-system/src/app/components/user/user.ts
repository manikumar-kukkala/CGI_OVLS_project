import { Component, OnInit } from '@angular/core';
import {
  FormBuilder, FormGroup, Validators,
  ReactiveFormsModule, FormsModule, AbstractControl, ValidationErrors
} from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { of, switchMap, map, forkJoin, Observable, finalize } from 'rxjs';
import { LicenceService } from '../../services/licence.service';
import { Application } from '../../models/licence.model';

type Step = 'choose' | 'llRef' | 'form' | 'appointment' | 'payment' | 'done';

@Component({
  selector: 'app-user',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, HttpClientModule, FormsModule],
  templateUrl: './user.html',
  styleUrls: ['./user.scss']
})
export class User implements OnInit {
  step: Step = 'choose';
  selectedAction: 'learning' | 'permanent' | 'renewal' | 'update' | null = null;
  form!: FormGroup;
  error = '';
  submitted = false;
  timeslot = '';
  selectedPayment: 'online' | 'challan' | null = null;
  paymentDone = false;

  // LL reference (for Permanent flow)
  llIdInput: string = '';
  verifyingLL = false;
  llCheckError: string | null = null;

  private createdApplicationId: number | null = null;
  private createdDocumentId: number | null = null;
  private createdApplicantId: number | null = null;

  private idProofName: string | null = null;
  private photoName: string | null = null;
  processingPayment: boolean | undefined;

  constructor(
    private readonly fb: FormBuilder,
    private readonly router: Router,
    private readonly http: HttpClient,
    private readonly licence: LicenceService
  ) {}

  ngOnInit() { this.initForm(); }

  initForm() {
    this.form = this.fb.group({
      applicantId: [''],
      documentId: [''],

      applicationNumber: ['', Validators.required],
      applicantName: ['', Validators.required],
      fatherName: ['', Validators.required],

      dob: ['', [Validators.required, this.minimumAgeValidator(18)]],
      gender: ['', Validators.required],

      address: ['', Validators.required],
      mobile: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],

      state: [''],
      city: [''],
      house: [''],
      landmark: [''],
      pincode: [''],

      identityProof: [''],
      photo: [''],

      type: ['learning'] // default value; overridden by chooseAction
    });
  }

  chooseAction(a: 'learning' | 'permanent' | 'renewal' | 'update') {
    this.selectedAction = a;
    this.form.patchValue({ type: a });
    this.step = (a === 'permanent') ? 'llRef' : 'form';
  }

  minimumAgeValidator(minAge: number) {
    return (control: AbstractControl): ValidationErrors | null => {
      if (!control.value) return null;
      const dob = new Date(control.value);
      const today = new Date();
      let age = today.getFullYear() - dob.getFullYear();
      const monthDiff = today.getMonth() - dob.getMonth();
      const dayDiff = today.getDate() - dob.getDate();
      if (monthDiff < 0 || (monthDiff === 0 && dayDiff < 0)) age--;
      return age >= minAge ? null : { underage: true };
    };
  }

  verifyLLAndContinue() {
    this.llCheckError = null;
    const raw = (this.llIdInput ?? '').trim();
    if (!raw) {
      this.llCheckError = 'Please enter the LL Application Number (e.g., APP-001).';
      return;
    }
    const appNo = raw;
    this.verifyingLL = true;

    this.licence.getApplicationByNumber(appNo)
      .pipe(finalize(() => (this.verifyingLL = false)))
      .subscribe({
        next: (app: any) => {
          const applicantId = Number(app?.applicant?.applicantId ?? app?.applicantId ?? NaN);
          if (Number.isFinite(applicantId)) this.form.patchValue({ applicantId });
          this.step = 'form';
        },
        error: () => {
          this.verifyingLL = true;
          this.licence.getAllApplications()
            .pipe(finalize(() => (this.verifyingLL = false)))
            .subscribe({
              next: (all: any) => {
                const rows: any[] = Array.isArray(all) ? all : (all?.applications ?? []);
                const llApp = rows.find(a =>
                  String(a?.applicationNumber ?? '').toLowerCase() === appNo.toLowerCase()
                );
                if (!llApp) { this.llCheckError = 'No application found for that LL Application Number.'; return; }
                const applicantId = Number(llApp?.applicant?.applicantId ?? llApp?.applicantId ?? NaN);
                if (Number.isFinite(applicantId)) this.form.patchValue({ applicantId });
                this.step = 'form';
              },
              error: () => { this.llCheckError = 'Failed to verify LL Application Number. Please try again.'; }
            });
        }
      });
  }

  onFileSelected(event: any, controlName: 'identityProof' | 'photo') {
    const f: File | undefined = event?.target?.files?.[0];
    if (!f) return;
    if (controlName === 'identityProof') { this.idProofName = f.name; }
    else { this.photoName = f.name; }
    this.form.get(controlName)?.setValue(f.name);
  }

  submitForm() {
    this.error = '';
    if (this.form.invalid) return;

    const needCreateDocs = !this.form.value.documentId && (this.idProofName || this.photoName);
    const docs$ = needCreateDocs
      ? this.http.post<any>('http://localhost:8080/documents', {
          idProof: this.idProofName ?? '',
          photo: this.photoName ?? '',
          addressProof: ''
        }).pipe(
          map(d => {
            console.log('Created document response:', d);
            const id = Number(d?.documentId ?? d?.id ?? null);
            this.createdDocumentId = id;
            return id;
          })
        )
      : of(this.form.value.documentId ? Number(this.form.value.documentId) : null);

    docs$
      .pipe(
        switchMap((docId: number | null) => {
          // In case your backend expects nested relations:
          const applicantPart = this.form.value.applicantId
            ? { applicant: { applicantId: Number(this.form.value.applicantId) } }
            : null;

          const documentsPart = docId ? { documents: { documentId: docId } } : null;

          const payload: Application = {
            applicationNumber: this.form.value.applicationNumber ?? ('APP-' + Date.now()),
            applicationDate: new Date().toISOString(),
            modeOfPayment: this.selectedPayment ?? '',
            paymentStatus: this.paymentDone ? 'Completed' : 'Pending',
            remarks: '',
            status: 'PENDING',

            applicantName: this.form.value.applicantName,
            address: this.form.value.address,
            type: this.form.value.type,

            ...(applicantPart ?? {}),
            ...(documentsPart ?? {}),
          };

          return this.licence.createApplication(payload);
        })
      )
      .subscribe({
        next: (created) => {
          this.createdApplicationId =
            created?.applicationId ??
            created?.id ??
            created?.application?.applicationId ??
            null;

          this.createdApplicantId =
            created?.applicant?.applicantId ??
            created?.applicantId ??
            (this.form.value.applicantId ? Number(this.form.value.applicantId) : null);

          if (!this.createdDocumentId && this.form.value.documentId) {
            this.createdDocumentId = Number(this.form.value.documentId);
          }

          this.submitted = true;
          this.error = '';
          this.step = 'appointment';
        },
        error: (err) => {
          this.submitted = false;
          this.error = 'There was an error submitting your application.';
          console.error(err);
        }
      });
  }

  selectTimeslot(s: string) { this.timeslot = s; }
  goToPayment() { if (this.timeslot) this.step = 'payment'; }
  selectPayment(t: 'online' | 'challan') { this.selectedPayment = t; }

  makePayment() {
    if (!this.selectedPayment) return;

    this.processingPayment = true;
    this.paymentDone = true;
    this.error = '';

    this.runPostPaymentUpdates()
      .pipe(finalize(() => { this.processingPayment = false; }))
      .subscribe({
        next: () => { this.step = 'done'; },
        error: (err) => {
          console.error('Post-payment updates failed', err);
          this.error = 'Payment recorded, but updating the records failed. Please contact support.';
          this.step = 'done';
        }
      });
  }

  printPage() { window.print(); }
  goHome() { this.router.navigate(['/home']); }

  applyAnother() {
    this.step = 'choose';
    this.selectedAction = null;
    this.form.reset();
    this.submitted = false;
    this.paymentDone = false;
    this.timeslot = '';
    this.selectedPayment = null;
    this.idProofName = null;
    this.photoName = null;
    this.createdApplicationId = null;
    this.createdDocumentId = null;
    this.createdApplicantId = null;
    this.llIdInput = '';
    this.llCheckError = null;
  }

  back() {
    if (this.step === 'form') this.step = 'choose';
    else if (this.step === 'appointment') this.step = 'form';
    else if (this.step === 'payment') this.step = 'appointment';
  }

  private runPostPaymentUpdates(): Observable<any> {
    const tasks: Observable<any>[] = [];

    if (this.createdApplicationId && this.selectedPayment) {
      tasks.push(
        this.licence.updateApplicationPayment(this.createdApplicationId, {
          modeOfPayment: this.selectedPayment,
          paymentStatus: 'Completed'
        })
      );
    }

    const applicantIdToUse =
      (this.form.value.applicantId ? Number(this.form.value.applicantId) : null) ??
      this.createdApplicantId;

    if (applicantIdToUse && (this.selectedAction === 'learning' || this.selectedAction === 'permanent')) {
      const type: 'learning' | 'permanent' = this.selectedAction;
      tasks.push(
        this.licence.updateApplicantLicense(applicantIdToUse, {
          type,
          status: 'APPLIED'
        })
      );
    }

    if (applicantIdToUse) {
      const state = (this.form.value.state ?? '').toString().trim();
      const city = (this.form.value.city ?? '').toString().trim();
      const house = (this.form.value.house ?? '').toString().trim();
      const landmark = (this.form.value.landmark ?? '').toString().trim();
      const pincode = (this.form.value.pincode ?? '').toString().trim();

      const anyProvided = !!(state || city || house || landmark || pincode);
      if (anyProvided) {
        tasks.push(
          this.licence.createApplicantAddress(applicantIdToUse, {
            state, city, house, landmark, pincode
          })
        );
      }
    }

    if (this.createdDocumentId && (this.idProofName || this.photoName)) {
      console.log('Updating docs', this.createdDocumentId, {
        idProof: this.idProofName ?? '',
        photo: this.photoName ?? '',
        addressProof: ''
      });

      tasks.push(
        this.licence.updateDocuments(this.createdDocumentId, {
          idProof: this.idProofName ?? '',
          photo: this.photoName ?? '',
          addressProof: ''
        })
      );
    }

    if (this.createdApplicationId) {
      const today = new Date();
      const isoDate = `${today.getFullYear()}-${String(today.getMonth()+1).padStart(2,'0')}-${String(today.getDate()).padStart(2,'0')}`;
      tasks.push(
        this.licence.createAppointment({
          applicationId: this.createdApplicationId,
          officerId: null,
          testDate: isoDate,
          timeSlot: this.timeslot,
          testNumber: null,
          testResult: 'PENDING'
        })
      );
    }

    return tasks.length ? forkJoin(tasks) : of(null);
  }
}
