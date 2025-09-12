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

      // Default pattern for LL (APP-###); updated dynamically per flow
      applicationNumber: ['', [Validators.required, Validators.pattern(/^APP-\d{3,}$/)]],
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

      // UI-only; DO NOT POST this
      type: ['learning']
    });
  }

  private setValidatorsForFlow(flow: 'learning' | 'permanent' | 'renewal' | 'update') {
    const set = (c: string, v: any[]) => this.form.get(c)?.setValidators(v);
    const req = () => [Validators.required];

    // applicationNumber: LL uses APP-###, DL uses DL-APP-###
    if (flow === 'permanent') {
      set('applicationNumber', [Validators.required, Validators.pattern(/^DL-\s*APP-\d{3,}$/)]);
    } else {
      set('applicationNumber', [Validators.required, Validators.pattern(/^APP-\d{3,}$/)]);
    }

    // Always required:
    set('applicantName', req());
    set('address', req());

    if (flow === 'permanent') {
      // DL reuses LL; make these optional
      set('fatherName', []);
      set('mobile', []);
      set('email', [Validators.email]);   // format-only check
      set('dob', []);                      // optional in DL flow
      set('gender', []);                   // optional in DL flow
    } else {
      set('fatherName', req());
      set('mobile', req());
      set('email', [Validators.required, Validators.email]);
      set('dob', [Validators.required, this.minimumAgeValidator(18)]);
      set('gender', req());
    }

    [
      'applicationNumber','applicantName','address','fatherName',
      'mobile','email','dob','gender'
    ].forEach(c => this.form.get(c)?.updateValueAndValidity());
  }

  chooseAction(a: 'learning' | 'permanent' | 'renewal' | 'update') {
    this.selectedAction = a;
    this.form.patchValue({ type: a });   // UI-only; not posted
    this.setValidatorsForFlow(a);
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

    const handleFound = (app: any) => {
      // LL must be APPROVED (frontend hint; backend enforces too)
      const status = String(app?.status ?? '').toUpperCase();
      if (status !== 'APPROVED') {
        this.llCheckError = 'Your Learning Licence application is not APPROVED yet.';
        return;
      }

      // Reuse Applicant & Documents
      const applicantId = Number(app?.applicant?.applicantId ?? app?.applicantId ?? NaN);
      const documentId  = Number(app?.documents?.documentId ?? app?.documentId ?? NaN);
      const llAppNo     = String(app?.applicationNumber ?? '').trim();

      // ✅ DL application number rule: "DL-<LL application number>"
      const dlAppNo = `DL-${llAppNo}`;

      this.form.patchValue({
        applicantId: Number.isFinite(applicantId) ? applicantId : '',
        documentId:  Number.isFinite(documentId)  ? documentId  : '',
        applicationNumber: dlAppNo,
        applicantName: app?.applicantName ?? app?.applicant?.user?.name ?? '',
        address: app?.address ?? ''
      });

      // Make sure validators are set for DL and rechecked
      this.setValidatorsForFlow('permanent');
      this.form.updateValueAndValidity();

      this.step = 'form';
    };

    this.licence.getApplicationByNumber(appNo)
      .pipe(finalize(() => (this.verifyingLL = false)))
      .subscribe({
        next: (app: any) => handleFound(app),
        error: () => {
          // fallback: search all apps
          this.verifyingLL = true;
          this.licence.getAllApplications()
            .pipe(finalize(() => (this.verifyingLL = false)))
            .subscribe({
              next: (all: any) => {
                const rows: any[] = Array.isArray(all) ? all : (all?.applications ?? []);
                const llApp = rows.find(a =>
                  String(a?.applicationNumber ?? '').toLowerCase() === appNo.toLowerCase()
                );
                if (!llApp) {
                  this.llCheckError = 'No application found for that LL Application Number.';
                  return;
                }
                handleFound(llApp);
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

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.focusFirstInvalid();
      this.error = 'Please fix the highlighted fields.';
      return;
    }

    // Only create docs if user didn’t supply a Document ID and selected files
    const needCreateDocs = !this.form.value.documentId && (this.idProofName || this.photoName);
    const docs$ = needCreateDocs
      ? this.http.post<any>('http://localhost:8080/documents', {
          idProof: this.idProofName ?? '',
          photo: this.photoName ?? '',
          addressProof: ''
        }).pipe(
          map(d => {
            const id = Number(d?.documentId ?? d?.id ?? null);
            this.createdDocumentId = id;
            return id;
          })
        )
      : of(this.form.value.documentId ? Number(this.form.value.documentId) : null);

    docs$
      .pipe(
        switchMap((docId: number | null) => {
          const applicantPart = this.form.value.applicantId
            ? { applicant: { applicantId: Number(this.form.value.applicantId) } }
            : null;

          const documentsPart = docId ? { documents: { documentId: docId } } : null;

          const licenceType = this.mapToLicenceType(this.selectedAction ?? 'learning');

          // IMPORTANT: use the form's applicationNumber (DL flow builds DL-<LL no>)
          const payload: Application = {
            applicationNumber: this.form.value.applicationNumber,
            applicationDate: new Date().toISOString(),
            modeOfPayment: this.selectedPayment ?? '',
            paymentStatus: this.paymentDone ? 'Completed' : 'Pending',
            remarks: '',
            status: 'PENDING',

            // required by backend rules
            licenceType,

            // optional, stored on Application entity
            applicantName: this.form.value.applicantName,
            address: this.form.value.address,

            ...(applicantPart ?? {}),
            ...(documentsPart ?? {})
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
          const serverMsg =
            err?.error?.message ||
            err?.error?.error ||
            (typeof err?.error === 'string' ? err.error : '') ||
            err?.message;

          this.submitted = false;
          this.error = serverMsg || 'Bad Request. Please check the inputs.';
          console.error('Create application failed:', err);
        }
      });
  }

  private mapToLicenceType(a: 'learning' | 'permanent' | 'renewal' | 'update'): 'LL' | 'DL' {
    return a === 'permanent' ? 'DL' : 'LL';
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
    // reset default validators to LL flow defaults
    this.setValidatorsForFlow('learning');
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

  private focusFirstInvalid() {
    const firstInvalidKey = Object.keys(this.form.controls)
      .find(k => this.form.get(k)?.invalid);
    if (firstInvalidKey) {
      const el = document.querySelector(`[formControlName="${firstInvalidKey}"]`) as HTMLElement | null;
      el?.focus?.();
      el?.scrollIntoView?.({ behavior: 'smooth', block: 'center' });
    }
  }
}
